#!/bin/bash
./gradlew clean build
adb install -r app/build/outputs/apk/moledroid-debug.apk
adb shell pm grant io.github.tanelmae.moledroid android.permission.SET_ANIMATION_SCALE
