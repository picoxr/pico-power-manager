# PowerManager 
JAR, Demo APK and PicoUnityActivity.cs are in /resource.
Note: If you want to create your own JAR file, please refer to [the Guideline](https://github.com/picoxr/support/blob/master/How%20to%20Use%20JAR%20file%20in%20Unity%20project%20on%20Pico%20device.docx)

## APK usage
You need to place the test.apk in download directory for testing "silentInstall", "goToApp", "silentUninstall" APIs.

## Introduction
This JAR file is used to modify power settings

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
   android:name="com.example.picovrpowermanager.PicoVRPowerManger"
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
| goToApp             | Jump application                | Need to add the package name parameter |
| goToActivity        | Jump application (Activity)     | Need to add the package name and activity name parameters |
| setpropSleep        | Sets the system sleep timeout   | Need to increase the time parameter    |
| setPropLockScreen   | Sets the screen closing timeout | Need to increase the time parameter    |
| silentInstall       | SilentInstall                   | System signature required              |
| silentUninstall     | SilentUninstall                 | System signature required              |

The second parameter in the silent installation must be the package name of the current application, not the package name of the installed application.

## Note
This demo requires system signature. About how to sign a apk, refer to this [KioskMode[4]](http://static.appstore.picovr.com/docs/KioskMode/chapter_four.html?_blank).


