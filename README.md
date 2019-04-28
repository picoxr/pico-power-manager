# PowerManager 
JAR, Demo APK and PicoUnityActivity.cs are in /resource.     
**Note**: If you want to create your own JAR file, please refer to [the Guideline](https://github.com/picoxr/support/blob/master/How%20to%20Use%20JAR%20file%20in%20Unity%20project%20on%20Pico%20device.docx)      

## Supported Devices
Pico Goblin, Pico Neo, Pico G2, Pico G2 4K 

## Introduction
This demo is used to modify power settings.(Check )

## API defined in JAR file

| Interface           | Instructions                    | Remark                                                              |
| ------------------- | ------------------------------- | --------------------------------------------------------------------|
| androidShutDown     | Shutdown                        | System signature required                                           |
| androidReBoot       | Root                            | System signature required                                           |
| androidLockScreen   | LockScreen                      |                                                                     |
| androidUnlockScreen | UnLockScreen                    |                                                                     |
| acquireWakeLock     | AcquireWakeLock                 | Must be paired with releaseWakeLock                                 |
| releaseWakeLock     | ReleaseWakeLock                 | Must be paired with acquireWakeLock                                 |
| goToApp             | Jump application                | Need to add the package name parameter                              |
| setpropSleep        | Sets the system sleep timeout   | Need to add the time parameter ("-1" means never sleep)             |
| setPropLockScreen   | Sets the screen closing timeout | Need to add the time parameter ("65535" means never screen off      |
| silentInstall       | SilentInstall                   | System signature required                                           |
| silentUninstall     | SilentUninstall                 | System signature required                                           |

The second parameter in the silent installation must be the package name of the current application, not the package name of the installed application.

## Note
This demo requires system signature. About how to sign a apk, refer to this [Customize Launcher on Pico Device](https://github.com/picoxr/support/blob/master/Customize%20Launcher%20on%20Pico%20Device.docx?raw=true).


