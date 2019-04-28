package com.picovr.picovrpowermanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

/*
 * 屏幕点亮服务，该服务起到延迟点亮服务的功能
 */
public class ScreenService extends Service {

	// 声明键盘管理器
//	KeyguardManager mKeyguardManager = null;
	// 声明键盘锁
//	private KeyguardLock mKeyguardLock = null;
	// 声明电源管理器
	private PowerManager pm;
	private PowerManager.WakeLock wakeLock;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {

		// 获取电源的服务
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		// 获取系统服务
//		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		
		new Handler().postDelayed(new Runnable(){    
		    public void run() {
		    	
		    	// 点亮亮屏
				wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag:");
				wakeLock.acquire();
				wakeLock.release();
				Log.i("Log : ", "------>mKeyguardLock");
				// 初始化键盘锁，可以锁定或解开键盘锁
//				mKeyguardLock = mKeyguardManager.newKeyguardLock("");
				// 禁用显示键盘锁定
//				mKeyguardLock.disableKeyguard();
		    }    
		 }, 10000);
		
	}

	@Override
	public void onDestroy() {
//		wakeLock.release();
		super.onDestroy();
	}
}
