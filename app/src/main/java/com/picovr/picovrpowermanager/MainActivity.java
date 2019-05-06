package com.picovr.picovrpowermanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MY_REQUEST_CODE = 9999;
    private static final int MY_REQUEST_UNLOCK_CODE = 1111;

    private static Context mContext;
    private static WakeLock wakeLock;
    private static PowerManager pm;
    private static DevicePolicyManager policyManager;
    private static ComponentName componentName;
    private static ExecutorService mInstaller = Executors.newFixedThreadPool(2);

    private static final String PACKAGE_NAME = "com.picovr.testapk";
    private static final String ACTIVITY_NAME = "com.picovr.testapk.MainActivity";
    private static final String SLEEP_TIME = "setprop persist.psensor.sleep.delay ";
    private static final String LOCK_SCREEN = "setprop persist.psensor.screenoff.delay ";
    private static final String APK_PATH = "/storage/emulated/0/Download/test.apk";
    private static final String ONESELF_NAME = "com.picovr.picovrpowermanager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, AdminReceiver.class);
    }

   
    public void shutDownClick(View v) {

        Log.i(TAG, "shutDownClick");

        if (Build.VERSION.SDK_INT < 24) {
            try {
                Method method = pm.getClass().getDeclaredMethod("shutdown", boolean.class, boolean.class);
                method.invoke(pm, false, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Method method = pm.getClass().getDeclaredMethod("shutdown", boolean.class, String.class, boolean.class);
                method.invoke(pm, false, null, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    
    public void reBootClick(View v) {
        Log.i(TAG, "reBootClick");
        pm.reboot("");
    }

   
    public void lockScreenClick(View v) {

        Log.i(TAG, "androidLockScreen");
        if (policyManager.isAdminActive(componentName)) {
            Log.i(TAG, "lockNow");
            policyManager.lockNow();

        } else {

            Log.i(TAG, "activeManage");
            activeManage(); 
        }
    }

   
    private void activeManage() {

        Log.i(TAG, "activeManage()");
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Lock Screen");
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

   
    public void unlockScreenClick(View v) {

        Log.i(TAG, "unlockScreenClick");
        if (policyManager.isAdminActive(componentName)) {
            Log.i(TAG, "lockNow");
            policyManager.lockNow();
            Intent intent = new Intent(this, ScreenService.class);
            startService(intent);
        } else {
            Log.i(TAG, "activeManage");
            unlockActiveManage(); 
        }
    }

    private void unlockActiveManage() {

        Log.i(TAG, "unlockActiveManage()");
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Lock screen");
        startActivityForResult(intent, MY_REQUEST_UNLOCK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "onActivityResult.lockNow");
            policyManager.lockNow();
        } else if (requestCode == MY_REQUEST_UNLOCK_CODE && resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "unlockActiveManage.lockNow");
            policyManager.lockNow();
            Intent intent = new Intent(this, ScreenService.class);
            startService(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

  
    public void acquireWakeLockClick(View v) {
        if (wakeLock == null) {
            wakeLock = this.pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.setReferenceCounted(false);
            wakeLock.acquire();
            Log.i(TAG, "acquireWakeLockClick");
        }
    }

   
    public void releaseWakeLockClick(View v) {

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
            Log.i(TAG, "releaseWakeLock");
        }
    }

    
    public void setpropUnsleepClick(View v) {

        Log.e(TAG, "setpropUnsleepClick");
        try {
            execCommand(SLEEP_TIME + "-1");
            Toast.makeText(this, "Succeed", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
    public void setpropSleepClick(View v) {

        Log.e(TAG, "setpropSleepClick");
        try {
            execCommand(SLEEP_TIME + 15);
            Toast.makeText(this, "Succeed", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   
    public void setpropUnlockScreenClick(View v) {

        Log.e(TAG, "setpropUnlockScreenClick");
        try {
            execCommand(LOCK_SCREEN + 65535);
            Toast.makeText(this, "Succeed", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed！", Toast.LENGTH_SHORT).show();
        }
    }

  
    public void setpropLockScreenClick(View v) {

        Log.e(TAG, "setpropLockScreenClick");
        try {
            execCommand(LOCK_SCREEN + 10);
            Toast.makeText(this, "Succeed！", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed！", Toast.LENGTH_SHORT).show();
        }
    }

    
    public void execCommand(String command) throws IOException {

        Log.i(TAG, "command = " + command);
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command); 
        InputStream inputstream = proc.getInputStream();
        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
        BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
        String line = "";
        StringBuilder sb = new StringBuilder(line);
        while ((line = bufferedreader.readLine()) != null) {
            sb.append(line);
            sb.append("/n");
        }
        Log.e(TAG, sb.toString());
        
        try {
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }

   
    public void silentInstallClick(View v) {
        File file = new File(APK_PATH);
        if (file.exists()) {
            silentInstallapp(APK_PATH, ONESELF_NAME);
        } else {
            new AlertDialog.Builder(this).setMessage("The apk file doesn't exist!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
        Log.e(TAG, "Enable silent installation，APK_PATH = " + APK_PATH);

    }

    private void silentInstallapp(String apkPath, String installerPkgName) {
        SilentInstaller.install(apkPath, installerPkgName, new ShellCmd.ICmdResultCallback() {

            @Override
            public void onException(Exception arg0) {
                // TODO Auto-generated method stub
                Log.e(TAG, "Enable silent installation，onException");
            }

            @Override
            public void onError(String arg0) {
                // TODO Auto-generated method stub
                Log.e(TAG, "Enable silent installation， onError");
            }

            @Override
            public void onComplete(String arg0) {
                // TODO Auto-generated method stub
                Log.e(TAG, "Enable silent installation，onComplete");
            }
        });
    }

    // Intent other app
    public void openAPPClick(View v) {
        goToApp(PACKAGE_NAME);
    }

    // Intent other app
    private void goToApp(String packagename) {
        if (getPackageManager().getLaunchIntentForPackage(packagename) != null) {
            Intent intent = new Intent();
            PackageManager packageManager = mContext.getPackageManager();
            intent = packageManager.getLaunchIntentForPackage(packagename);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this).setMessage("The specific package doesn't exist!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }

    }

    public void openActivityClick(View view) {
        goToActivity(PACKAGE_NAME, ACTIVITY_NAME);
    }

    public void goToActivity(String packagename, String activityname) {
        if (getPackageManager().getLaunchIntentForPackage(packagename) != null) {
            Intent intent = new Intent();
            ComponentName comp = new ComponentName(packagename, activityname);
            intent.setComponent(comp);
            startActivity(intent);
        } else {
            new AlertDialog.Builder(this).setMessage("The specific package doesn't exist!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }

    }
    
    public void silentUninstallClick(View v) {
        if (getPackageManager().getLaunchIntentForPackage(PACKAGE_NAME) != null) {
            PackageManager pm = mContext.getPackageManager();
            Class<?>[] uninstalltypes = new Class[] {String.class, IPackageDeleteObserver.class, int.class};
            Method uninstallmethod = null;
            try {
                uninstallmethod = pm.getClass().getMethod("deletePackage", uninstalltypes);
                uninstallmethod.invoke(pm, new Object[] {PACKAGE_NAME, new MyPackageDeleteObserver(), 0});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            new AlertDialog.Builder(this).setMessage("The specific package doesn't exist!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }

    }


    class MyPackageDeleteObserver extends IPackageDeleteObserver.Stub {

        @Override
        public void packageDeleted(String packageName, int returnCode) throws RemoteException {

            if (returnCode == 1) {
                Log.i(TAG, "Succeed " + returnCode);
            } else {
                Log.i(TAG, "Failed " + returnCode);
            }
        }
    }

}
