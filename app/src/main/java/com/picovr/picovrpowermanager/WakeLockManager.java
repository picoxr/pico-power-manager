package com.picovr.picovrpowermanager;

import com.unity3d.player.UnityPlayerNativeActivityPico;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;



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

	
	public void acquireWakeLock() {

		
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
