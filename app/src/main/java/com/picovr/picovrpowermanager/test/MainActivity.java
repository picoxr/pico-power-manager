package com.picovr.picovrpowermanager.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
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

import com.picovr.picovrpowermanager.AdminReceiver;
import com.picovr.picovrpowermanager.R;
import com.picovr.picovrpowermanager.ShellCmd;
import com.picovr.picovrpowermanager.SilentInstaller;

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

    private static final String PACKAGE_NAME = "com.example.picovr.test";
    private static final String SLEEP_TIME = "setprop persist.psensor.sleep.delay ";
    private static final String LOCK_SCREEN = "setprop persist.psensor.screenoff.delay ";
    private static final String APK_PATH = "/storage/emulated/0/Download/PicoVRtest.apk";
    private static final String ONESELF_NAME = "com.example.picovrpowermanager";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, AdminReceiver.class);
    }

    // 关机，必须签名
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

    // 重启，必须签名
    public void reBootClick(View v) {

        Log.i(TAG, "reBootClick");
        pm.reboot("");
    }

    // 锁屏
    public void lockScreenClick(View v) {

        Log.i(TAG, "androidLockScreen");
        if (policyManager.isAdminActive(componentName)) {
            Log.i(TAG, "lockNow");
            policyManager.lockNow();// 锁屏

        } else {

            Log.i(TAG, "activeManage");
            activeManage(); // 获取权限
        }
    }

    // 获取锁屏权限
    private void activeManage() {

        Log.i(TAG, "activeManage()");
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活后就能一键锁屏了");
        startActivityForResult(intent, MY_REQUEST_CODE);
    }

    // 锁屏后，自动开屏实例
    public void unlockScreenClick(View v) {

        Log.i(TAG, "unlockScreenClick");
        if (policyManager.isAdminActive(componentName)) {
            Log.i(TAG, "lockNow");
            policyManager.lockNow();// 锁屏

            Intent intent = new Intent(this, ScreenService.class);
            startService(intent);

        } else {

            Log.i(TAG, "activeManage");
            unlockActiveManage(); // 获取权限
        }
    }

    private void unlockActiveManage() {

        Log.i(TAG, "unlockActiveManage()");
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "激活后就能一键锁屏了");
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

    // 请求系统锁，应用于子线程或者服务，必须与releaseWakeLock成对存在
    public void acquireWakeLockClick(View v) {

        if (wakeLock == null) {
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.setReferenceCounted(false);
            wakeLock.acquire();
            Log.i(TAG, "acquireWakeLockClick");
        }

    }

    // 释放系统锁，必须与acquireWakeLock成对存在
    public void releaseWakeLockClick(View v) {

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
            Log.i(TAG, "releaseWakeLock");
        }
    }

    // shell命令实现设置休眠时间：setprop persist.psensor.sleep.delay -1 表示系统永不休眠
    public void setpropUnsleepClick(View v) {

        Log.e(TAG, "setpropUnsleepClick");
        try {
            execCommand(SLEEP_TIME + "-1");
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // shell命令实现设置休眠时间：setprop persist.psensor.sleep.delay 15 表示系统15s后休眠
    public void setpropSleepClick(View v) {

        Log.e(TAG, "setpropSleepClick");
        try {
            execCommand(SLEEP_TIME + 15);
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // shell命令实现设置锁屏时间：setprop persist.psensor.screenoff.delay 65535 表示系统屏幕常亮
    public void setpropUnlockScreenClick(View v) {

        Log.e(TAG, "setpropUnlockScreenClick");
        try {
            execCommand(LOCK_SCREEN + 65535);
            Toast.makeText(this, "设置成功！", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "设置失败！", Toast.LENGTH_SHORT).show();
        }
    }

    // shell命令实现设置锁屏时间：setprop persist.psensor.screenoff.delay 10 表示系统屏幕10s后息屏
    public void setpropLockScreenClick(View v) {

        Log.e(TAG, "setpropLockScreenClick");
        try {
            execCommand(LOCK_SCREEN + 10);
            Toast.makeText(this, "设置成功！", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "设置失败！", Toast.LENGTH_SHORT).show();
        }
    }

    // 发送shell命令的实现方法
    public void execCommand(String command) throws IOException {

        Log.i(TAG, "command = " + command);
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec(command); // 这句话就是shell与高级语言间的调用
        // 如果有参数的话可以用另外一个被重载的exec方法
        // 实际上这样执行时启动了一个子进程,它没有父进程的控制台
        // 也就看不到输出,所以我们需要用输出流来得到shell执行后的输出
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
        // 使用exec执行不会等执行成功以后才返回,它会立即返回
        // 所以在某些情况下是很要命的(比如复制文件的时候)
        // 使用wairFor()可以等待命令执行完成以后才返回
        try {
            if (proc.waitFor() != 0) {
                System.err.println("exit value = " + proc.exitValue());
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    // 静默安装
    public void silentInstallClick(View v) {

        silentInstallapp(APK_PATH, ONESELF_NAME);
        Log.e(TAG, "开启静默安装，APK_PATH = " + APK_PATH);

    }

    private void silentInstallapp(String apkPath, String installerPkgName) {
        SilentInstaller.install(apkPath, installerPkgName, new ShellCmd.ICmdResultCallback() {

            @Override
            public void onException(Exception arg0) {
                // TODO Auto-generated method stub
                Log.e(TAG, "开启静默安装，onException");
            }

            @Override
            public void onError(String arg0) {
                // TODO Auto-generated method stub
                Log.e(TAG, "开启静默安装， onError");
            }

            @Override
            public void onComplete(String arg0) {
                // TODO Auto-generated method stub
                Log.e(TAG, "开启静默安装，onComplete");
            }
        });
    }

    // 跳转到其他应用
    public void openAPPClick(View v) {
        goToApp(PACKAGE_NAME);
    }

    // 跳转到其他应用
    private void goToApp(String packagename) {

        Intent intent = new Intent();
        PackageManager packageManager = mContext.getPackageManager();
        intent = packageManager.getLaunchIntentForPackage(packagename);
        startActivity(intent);
    }

    // 静默卸载
    public void silentUninstallClick(View v) {

        silentUninstall(PACKAGE_NAME);

    }

    // 静默卸载实现方式
    private void silentUninstall(final String packageName) {

        new Thread() {
            public void run() {
                PackageManager pm = mContext.getPackageManager();
                IPackageDeleteObserver observer = new MyPackageDeleteObserver();
//				pm.deletePackage(packageName, observer, 0);
                Class c = null;
                try {
                    c = Class.forName("android.content.pm.PackageManager");
                    Method m = c.getMethod("deletePackage", new Class[]{String.class, Object.class});
                    Object o = m.invoke(null, new Object[]{packageName, observer});
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    class MyPackageDeleteObserver extends IPackageDeleteObserver.Stub {

        @Override
        public void packageDeleted(String packageName, int returnCode) throws RemoteException {

            if (returnCode == 1) {
                Log.i(TAG, "删除成功！ " + returnCode);
            } else {
                Log.i(TAG, "删除失败！ " + returnCode);
            }
        }
    }

}
