#Moledroid
Convenience tool for controlling Android test device

[![Build Status](https://travis-ci.org/tanelmae/moledroid.svg?branch=master)](https://travis-ci.org/tanelmae/moledroid)

###To be able to switch animations on/off:
```
adb shell pm grant io.github.tanelmae.moledroid android.permission.SET_ANIMATION_SCALE
```

###Switch animations off:
```
adb shell am start -n io.github.tanelmae.moledroid/io.github.tanelmae.moledroid.Main -e ANIM false
```

###Switch animations on:
```
adb shell am start -n io.github.tanelmae.moledroid/io.github.tanelmae.moledroid.Main -e ANIM true
```

###Switch WiFi off:
```
adb shell am start -n io.github.tanelmae.moledroid/io.github.tanelmae.moledroid.Main -e WIFI false
```

###Switch WiFi on:
```
adb shell am start -n io.github.tanelmae.moledroid/io.github.tanelmae.moledroid.Main -e WIFI true
```

###Share text, html or media example:

```
adb shell am start -n io.github.tanelmae.moledroid/io.github.tanelmae.moledroid.Main --esn SHARE -e EXTRA_TEXT "Woohooo80!" -e TYPE "text/plain"
```
Supported extras: EXTRAS_TEXT, EXTRAS_HTML_TEXT, EXTRA_STREAM, TYPE