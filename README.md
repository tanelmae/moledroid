#Moledroid
Convenience tool for contorlling Android test devices

[![Build Status](https://travis-ci.org/tanelmae/moledroid.svg?branch=master)](https://travis-ci.org/tanelmae/moledroid)

###To be able to switch animations on/off:
adb shell pm grant io.github.tanelmae.moledroid android.permission.SET_ANIMATION_SCALE

###Switch animations off:
adb shell am start -n io.github.tanelmae.moledroid/io.github.tanelmae.moledroid.Switcher -e ANIM false

###Switch animations on:
adb shell am start -n io.github.tanelmae.moledroid/io.github.tanelmae.moledroid.Switcher -e ANIM true

###Switch WiFi off:
adb shell am start -n io.github.tanelmae.moledroid/io.github.tanelmae.moledroid.Switcher -e WIFI false

###Switch WiFi on:
adb shell am start -n io.github.tanelmae.moledroid/io.github.tanelmae.moledroid.Switcher -e WIFI true
