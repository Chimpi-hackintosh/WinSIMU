/* WinSIMU, 2026. SPDX-License-Identifier: GPL-2.0-or-later */
package com.limbo.emu.main;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.max2idea.android.limbo.machine.Machine;
import com.max2idea.android.limbo.machine.MachineController;
import com.max2idea.android.limbo.machine.MachineOpenHelper;
import com.max2idea.android.limbo.machine.WinSimuMachines;
import com.max2idea.android.limbo.machine.WindowsProfile;
import com.max2idea.android.limbo.main.Config;
import com.max2idea.android.limbo.main.LimboApplication;

public final class WinSimuHomeActivity extends AppCompatActivity {
    @Override public void onCreate(Bundle state) {
        super.onCreate(state); LimboApplication.arch=Config.Arch.x86_64;
        SupportNotice.check(this);
    }
    @Override public void onResume() { super.onResume(); render(); }
    private void render() {
        LinearLayout page=WinSimuUi.page(this,R.string.ws_home);
        WinSimuUi.label(this,page,R.string.ws_tagline);
        WinSimuUi.button(this,page,R.string.ws_new,v -> startActivity(new Intent(this,NewMachineActivity.class)));
        Button diagnostic=WinSimuUi.button(this,page,R.string.ws_diagnostic,v -> {});
        diagnostic.setOnClickListener(v -> runDiagnostic(diagnostic));
        WinSimuUi.button(this,page,R.string.ws_advanced,v -> openMachine(null));
        WinSimuUi.label(this,page,R.string.ws_machines);
        java.util.ArrayList<String> names=MachineOpenHelper.getInstance().getMachineNames();
        if(names.isEmpty()) WinSimuUi.label(this,page,R.string.ws_empty);
        for(String name:names) {
            Machine m=MachineOpenHelper.getInstance().getMachine(name);
            if(m==null) continue;
            LinearLayout card=WinSimuUi.card(this,page);
            card.addView(WinSimuUi.text(this,name,20,WinSimuUi.TEXT));
            card.addView(WinSimuUi.text(this,getString(R.string.ws_machine_summary,m.getMemory(),m.getCpuNum(),m.getCpu()),13,WinSimuUi.MUTED));
            WinSimuUi.button(this,card,R.string.ws_open,v -> openMachine(name));
            card.setOnLongClickListener(v -> { removeMachine(m); return true; });
        }
        WinSimuUi.button(this,page,R.string.ws_about,v -> new AlertDialog.Builder(this)
                .setTitle("WinSIMU 0.1.0-alpha").setMessage(R.string.ws_about_text)
                .setPositiveButton(android.R.string.ok,null).show());
    }
    private void openMachine(String name) {
        if(MachineController.getInstance().isRunning()) {
            Machine current=MachineController.getInstance().getMachine();
            name=current==null?null:current.getName();
        }
        Intent intent=new Intent(this,LimboEmuActivity.class);
        if(name!=null) intent.putExtra("winsimu.machine",name);
        startActivity(intent);
    }
    private void runDiagnostic(Button button) {
        final String name="WinSIMU-Diagnostic";
        if(MachineController.getInstance().isRunning()) { openMachine(null); return; }
        button.setEnabled(false);
        new Thread(() -> {
            try {
                if(MachineOpenHelper.getInstance().getMachine(name)==null) {
                    java.io.File image=new java.io.File(getFilesDir(),"winsimu-diagnostic.img");
                    try(java.io.InputStream source=getAssets().open("winsimu-diagnostic.img");
                        java.io.OutputStream output=new java.io.FileOutputStream(image)) {
                        byte[] buffer=new byte[8192]; int count;
                        while((count=source.read(buffer))!=-1) output.write(buffer,0,count);
                    }
                    WinSimuMachines.create(getFilesDir(),WindowsProfile.find("w10"),name,16,null,64,image.getAbsolutePath(),true);
                }
                runOnUiThread(() -> { if(!isDestroyed()) { button.setEnabled(true); openMachine(name); } });
            } catch(Exception ex) {
                runOnUiThread(() -> { if(!isDestroyed()) { button.setEnabled(true);
                    new AlertDialog.Builder(this).setMessage(getString(R.string.ws_create_error)+"\n"+ex.getMessage()).setPositiveButton(android.R.string.ok,null).show(); } });
            }
        },"WinSIMU-diagnostic").start();
    }
    private void removeMachine(Machine machine) {
        if(MachineController.getInstance().isRunning()) {
            new AlertDialog.Builder(this).setMessage(R.string.ws_stop_first).setPositiveButton(android.R.string.ok,null).show(); return;
        }
        new AlertDialog.Builder(this).setTitle(machine.getName()).setMessage(R.string.ws_remove_question)
                .setNegativeButton(android.R.string.cancel,null)
                .setPositiveButton(R.string.ws_remove,(d,w) -> { MachineOpenHelper.getInstance().deleteMachine(machine); render(); }).show();
    }
}
