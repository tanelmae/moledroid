#!/bin/bash
./gradlew clean build
adb install app/build/outputs/apk/app-debug.apk
adb shell pm grant io.github.tanelmae.moledroid android.permission.SET_ANIMATION_SCALE
