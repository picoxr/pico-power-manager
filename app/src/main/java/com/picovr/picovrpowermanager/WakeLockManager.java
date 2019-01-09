package com.picovr.picovrpowermanager;

import com.unity3d.player.UnityPlayerNativeActivityPico;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 * Wake Lock是一种锁的机制, 只要有人拿着这个锁,系统就无法进入休眠， 可以被用户态程序和内核获得. 这个锁可以是有超时的或者是没有超时的,
 * 超时的锁会在时间过去以后自动解锁. 如果没有锁了或者超时了, 内核就 会启动休眠的那套机制来进入休眠.
 * 
 * 一、关于int flags 各种锁的类型对CPU 、屏幕、键盘的影响：
 * 1.PARTIAL_WAKE_LOCK:保持CPU运转，屏幕和键盘灯有可能是关闭的。 2.SCREEN_DIM_WAKE_LOCK：保持CPU
 * 运转，允许保持屏幕显示但有可能是灰的，允许关闭键盘灯 3.SCREEN_BRIGHT_WAKE_LOCK：保持CPU
 * 运转，允许保持屏幕高亮显示，允许关闭键盘灯 4.FULL_WAKE_LOCK：保持CPU运转，保持屏幕高亮显示，键盘灯也保持亮度
 * 5.ACQUIRE_CAUSES_WAKEUP：Normal wake locks don't actually turn on the
 * illumination. Instead, they cause the illumination to remain on once it turns
 * on (e.g. from user activity). This flag will force the screen and/or keyboard
 * to turn on immediately, when the WakeLock is acquired. A typical use would be
 * for notifications which are important for the user to see immediately.
 * 6.ON_AFTER_RELEASE：f this flag is set, the user activity timer will be reset
 * when the WakeLock is released, causing the illumination to remain on a bit
 * longer. This can be used to reduce flicker if you are cycling between wake
 * lock conditions.
 * 
 * 你能加两个以上的标志，这些仅能影响屏幕的行为。这些标志当组合中有一个PARTIAL_WAKE_LOCK时将没有效果。
 * 
 * 二、权限获取 要进行电源的操作需要在AndroidManifest.xml中声明该应用有设置电源管理的权限。
 * <uses-permission android:name="android.permission.WAKE_LOCK" /> 你可能还需要
 * <uses-permission android:name="android.permission.DEVICE_POWER" />
 * 
 * 三、WakeLock的设置是 Activiy 级别的，不是针对整个Application应用的。
 * 
 **/

public class WakeLockManager extends UnityPlayerNativeActivityPico {

	private static final String TAG = "WakeLockManager";

	public static Activity unityActivity = null;

	private WakeLock wakeLock;
	private PowerManager pm;
//	private static String idData = "13613173b6";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		unityActivity = this;
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		Bmob.initialize(this, "875991c2ced4de85fd4e2b3bfa4e3623");
	}

	/**
	 * 获取锁，保持屏幕亮度。 Android中通过各种Lock锁对电源进行控制，需要注意的是加锁和解锁必须成对出现。
	 * 一般使用:这个函数在Activity的
	 * onResume中被调用。releaseWakeLock()方法则是释放该锁,在Activity的onPause中被调用。
	 */
	public void acquireWakeLock() {

		// 通过PowerManager的newWakeLock((int flags, String tag)方法来生成WakeLock实例。
		// int Flags指示要获取哪种WakeLock，不同的Lock对cpu、屏幕、键盘灯有不同影响。
		if (wakeLock == null) {
			wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, this.getClass().getCanonicalName());
			wakeLock.setReferenceCounted(false);
			wakeLock.acquire();
//			Intent startIntent = new Intent(this, MyService.class);
//			startService(startIntent);
			Log.e(TAG, "acquireWakeLock");
		}
	}

	public void acquireWakeLock(long timeout) {

		// 通过PowerManager的newWakeLock((int flags, String tag)方法来生成WakeLock实例。
		// int Flags指示要获取哪种WakeLock，不同的Lock对cpu、屏幕、键盘灯有不同影响。
		if (wakeLock == null) {
			wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
			wakeLock.setReferenceCounted(false);
			wakeLock.acquire(timeout);
			Log.e(TAG, "acquireWakeLock(long timeout)");
		}
	}

	public void releaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
//			Intent stopIntent = new Intent(this, MyService.class);
//			stopService(stopIntent);
			Log.e(TAG, "releaseWakeLock");
		}
	}

}
