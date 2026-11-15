#!/usr/bin/env python3
"""Reconstruct the owner's pinned original source and apply the reviewed overlay.

Native engine libraries and the archive's development signing material stay out
of git. This creates build-source/ only; it never publishes an APK or release.
"""
import argparse
import hashlib
from pathlib import Path, PurePosixPath
import shutil
import tempfile
import urllib.request
import zipfile

ROOT = Path(__file__).resolve().parents[1]
URL = "https://github.com/Chimpi-hackintosh/WinSIMU/releases/download/v0.1.0-alpha/WinSIMU-0.1.0-alpha-source.zip"
SHA256 = "4627f50ea532e59bf4199cbaff56884beaace18acfced8dd85c5fb116049648c"


def prepare(archive, destination):
    if hashlib.sha256(archive.read_bytes()).hexdigest() != SHA256:
        raise ValueError("Original source SHA-256 mismatch — no source was installed")
    if destination.exists():
        raise ValueError("Build destination already exists — use a fresh directory")
    with zipfile.ZipFile(archive) as source:
        for info in source.infolist():
            path = PurePosixPath(info.filename)
            if path.is_absolute() or ".." in path.parts or not path.parts or path.parts[0] != "WinSIMU":
                raise ValueError("Unsafe archive path")
            if (info.external_attr >> 16) & 0o170000 == 0o120000:
                raise ValueError("Archive symlink is not supported")
        destination.mkdir(parents=True)
        for info in source.infolist():
            path = PurePosixPath(info.filename)
            if info.is_dir() or len(path.parts) == 1:
                continue
            target = destination.joinpath(*path.parts[1:])
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_bytes(source.read(info))
    for item in (ROOT / "overlay").rglob("*"):
        if item.is_file():
            target = destination / item.relative_to(ROOT / "overlay")
            target.parent.mkdir(parents=True, exist_ok=True)
            shutil.copyfile(item, target)
    print("Verified original source and applied support overlay")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--archive", type=Path)
    parser.add_argument("--destination", type=Path, default=ROOT / "build-source")
    args = parser.parse_args()
    if args.archive:
        prepare(args.archive, args.destination)
        return
    with tempfile.TemporaryDirectory(prefix="winsimu-support-") as directory:
        archive = Path(directory) / "source.zip"
        request = urllib.request.Request(URL, headers={"User-Agent": "WinSIMU-support-build/1"})
        with urllib.request.urlopen(request, timeout=60) as response, archive.open("wb") as output:
            shutil.copyfileobj(response, output)
        prepare(archive, args.destination)


if __name__ == "__main__":
    main()
