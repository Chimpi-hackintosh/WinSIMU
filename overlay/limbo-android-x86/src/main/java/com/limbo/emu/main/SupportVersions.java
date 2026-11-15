// No Android dependencies: this logic is exercised by ordinary JVM unit tests.
package com.limbo.emu.main;

import java.math.BigInteger;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class SupportVersions {
    private static final Pattern VERSION = Pattern.compile(
        "^v?(\\d+)\\.(\\d+)\\.(\\d+)(?:-([0-9A-Za-z.-]+))?(?:\\+[0-9A-Za-z.-]+)?$");

    private SupportVersions() {}

    static int[] parse(String raw) {
        Version value = parseVersion(raw);
        return value == null ? null : new int[]{value.major, value.minor, value.patch};
    }

    static boolean older(String current, String minimum) {
        Version left = parseVersion(current), right = parseVersion(minimum);
        return left != null && right != null && compare(left, right) < 0;
    }

    private static Version parseVersion(String raw) {
        if (raw == null || raw.length() > 100) return null;
        Matcher matcher = VERSION.matcher(raw);
        if (!matcher.matches()) return null;
        try {
            int major = Integer.parseInt(matcher.group(1));
            int minor = Integer.parseInt(matcher.group(2));
            int patch = Integer.parseInt(matcher.group(3));
            String pre = matcher.group(4);
            // Existing package tags use -build-N as a build label, not a prerelease.
            if (pre != null && pre.toLowerCase(Locale.ROOT).startsWith("build-")) pre = null;
            if (pre != null) {
                String[] identifiers = pre.split("\\.", -1);
                for (String item : identifiers) {
                    if (item.isEmpty() || !item.matches("[0-9A-Za-z-]+")) return null;
                    if (item.matches("\\d+") && item.length() > 1 && item.startsWith("0")) return null;
                }
            }
            return new Version(major, minor, patch, pre);
        } catch (NumberFormatException error) {
            return null;
        }
    }

    private static int compare(Version left, Version right) {
        int core = Integer.compare(left.major, right.major);
        if (core == 0) core = Integer.compare(left.minor, right.minor);
        if (core == 0) core = Integer.compare(left.patch, right.patch);
        if (core != 0) return core;
        if (left.pre == null && right.pre == null) return 0;
        if (left.pre == null) return 1;
        if (right.pre == null) return -1;
        String[] a = left.pre.split("\\.");
        String[] b = right.pre.split("\\.");
        for (int index = 0; index < Math.min(a.length, b.length); index++) {
            if (a[index].equals(b[index])) continue;
            boolean an = a[index].matches("\\d+");
            boolean bn = b[index].matches("\\d+");
            if (an && bn) return new BigInteger(a[index]).compareTo(new BigInteger(b[index]));
            if (an != bn) return an ? -1 : 1;
            return a[index].compareTo(b[index]);
        }
        return Integer.compare(a.length, b.length);
    }

    private static final class Version {
        final int major, minor, patch;
        final String pre;
        Version(int major, int minor, int patch, String pre) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.pre = pre;
        }
    }

    static boolean ended(long now) { return now >= 1793750400000L; } // 2026-11-04 00:00 UTC
}
