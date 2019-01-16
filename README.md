# PowerManager Instructions

Note: Regarding java package creation and usage, please refer to [the Guideline](https://github.com/PicoSupport/PicoSupport/blob/master/How_to_use_JAR_file_in_Unity_project_on_Pico_device.docx)

1. Create a new Unity project and copy the picovrpowermanager_vxxx. jar package and res folder in the plugins-> Android in the Demo to the directory corresponding to the Unity project.

2. Modify the androidminifests. XML file and Add: android: sharedUserId = "android. Uid. System"

   Add Permission:

   ```
   <uses-permission android:name="android.permission.WAKE_LOCK" />
   <uses-permission android:name="android.permission.DEVICE_POWER" />
   <uses-permission android:name="android.permission.SHUTDOWN" />
   <uses-permission android:name="android.permission.REBOOT" />
   <uses-permission android:name="android.permission.DISABLE_KEYGUARD" />
   ```

   Modify the mainactivity:

   ```
   android:name="com.example.picovrpowermanager.PicoVRPowerManger"
   ```
  ![](https://github.com/PicoSupport/PicoVRPowerManager/blob/master/01.png)

3.Copy the script of PicoUnityActivity. Cs in the Demo to any directory of Unity project.

4.Call the Android interface For example:

```
PicoUnityActivity.CallObjectMethod("androidShutDown");
```

Call the Android  shutdown interface 

**Interface List**

| Interface           | Instructions                    | Remark                                 |
| ------------------- | ------------------------------- | -------------------------------------- |
| androidShutDown     | Shutdown                        | System signature required              |
| androidReBoot       | Root                            | System signature required              |
| androidLockScreen   | LockScreen                      |                                        |
| androidUnlockScreen | UnLockScreen                    |                                        |
| acquireWakeLock     | AcquireWakeLock                 | Must be paired with releaseWakeLock    |
| releaseWakeLock     | ReleaseWakeLock                 | Must be paired with acquireWakeLock    |
| goToApp             | Jump application                | Need to add the package name parameter |
| setpropSleep        | Sets the system sleep timeout   | Need to increase the time parameter    |
| setPropLockScreen   | Sets the screen closing timeout | Need to increase the time parameter    |
| silentInstall       | SilentInstall                   | System signature required              |
| silentUninstall     | SilentUninstall                 | System signature required              |

Note:The second parameter in the silent installation must be the package name of the current application, not the package name of the installed application.

5.**the Bundle Identifier of PlayerSetting in Unity should be consistent with the Android project.**
