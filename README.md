# PowerManager 
JAR, Demo APK and PicoUnityActivity.cs are in /resource.    
Note:
If you want to create your own JAR file, please refer to [the Guideline](http://static.appstore.picovr.com/docs/JarUnity/index.html)   
You can use "com.picovr.picovrpowermanager.PicoVRPowerManager" class and refer to section 2 in the guideline above for invoking all interfaces except "androidLockScreen", refer to [here](http://static.appstore.picovr.com/docs/PowerManager/chapter_three.html) to use this interface.

## APK usage
You need to place the test.apk in download directory for testing "silentInstall".   
After installing it, you can test "goToActivity", "goToApp", "silentUninstall" APIs.

## Introduction
This JAR file is used to modify system-level settings

## Modify AndroidManifest

 Modify the androidminifests. XML file and Add: 
 
   
   ```
   android:sharedUserId = "android.uid.system"
   ```

   Add Permission:

   ```
   <uses-permission android:name="android.permission.WAKE_LOCK" />
   <uses-permission android:name="android.permission.DEVICE_POWER" />
   <uses-permission android:name="android.permission.SHUTDOWN" />
   <uses-permission android:name="android.permission.REBOOT" />
   <uses-permission android:name="android.permission.DISABLE_KEYGUARD" />
   ```

## Class Name

   ```
   android:name="com.picovr.picovrpowermanager.PicoVRPowerManagerActivity"
   ```

## Interface List

| Interface           | Instructions                    | Remark                                 |
| ------------------- | ------------------------------- | -------------------------------------- |
| androidShutDown     | Shutdown                        | System signature required              |
| androidReBoot       | Root                            | System signature required              |
| androidLockScreen   | LockScreen                      |                                        |
| androidUnlockScreen | UnLockScreen                    |                                        |
| acquireWakeLock     | AcquireWakeLock                 | Must be paired with releaseWakeLock    |
| releaseWakeLock     | ReleaseWakeLock                 | Must be paired with acquireWakeLock    |
| goToApp             | Start an application            | Need to add the package name parameter |
| goToActivity        | Start an application (specify Activity)     | Need to add the package name and activity name parameters |
| setpropSleep        | Sets the system sleep timeout   | Need to increase the time parameter    |
| setPropLockScreen   | Sets the screen closing timeout | Need to increase the time parameter    |
| silentInstall       | SilentInstall                   | System signature required              |
| silentUninstall     | SilentUninstall                 | System signature required              |

The second parameter in the silent installation must be the package name of the current application, not the package name of the installed application.

## Note
This demo requires system signature. About how to sign a apk, refer to this [KioskMode[4]](http://static.appstore.picovr.com/docs/KioskMode/chapter_four.html?_blank).


