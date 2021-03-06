package io.github.tanelmae.moledroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;


public class Main extends Activity {
    final String TAG = "moledroid";

    private static final String ANIMATION_PERMISSION = "android.permission.SET_ANIMATION_SCALE";

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Started");
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Set params = extras.keySet();

            if (params.contains("ANIM")){
                switchAnimations(Boolean.valueOf(extras.getString("ANIM")));
            } else if (params.contains("WIFI")) {
                switchWifi(Boolean.valueOf(extras.getString("WIFI")));
            } else if (params.contains("SHARE")) {
                shareContent(params, extras);
            }
           } else {
            Log.d(TAG, "no parameters given");
        }
        finish();
    }

    private void shareContent(Set params, Bundle extras) {
        Intent shareIntent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (params.contains("TYPE")) {
            shareIntent.setType(extras.getString("TYPE"));
        }
        if (params.contains("EXTRA_TEXT")) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, extras.getString("EXTRA_TEXT"));
        }
        if (params.contains("EXTRA_STREAM")) {
            shareIntent.putExtra(Intent.EXTRA_STREAM,
                    Uri.parse(extras.getString("URI")));
        }
        if (params.contains("EXTRA_HTML_TEXT")) {
            shareIntent.putExtra(Intent.EXTRA_HTML_TEXT, extras.getString("EXTRA_HTML_TEXT"));
        }
        startActivity(shareIntent);
    }

    private void switchWifi(boolean state) {
        Log.i(TAG, "Setting WIFI state: " + String.valueOf(state));
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        Log.i(TAG, "WIFI sate before: " + String.valueOf(wifiManager.getWifiState()));
        wifiManager.setWifiEnabled(state);
        Log.i(TAG, "WIFI sate after: " + String.valueOf(wifiManager.getWifiState()));
    }

    // Based on: https://gist.github.com/xrigau/11284124#file-systemanimations-java
    // And: https://code.google.com/p/android-test-kit/wiki/DisablingAnimations
    private void switchAnimations(boolean status) {
        if (checkCallingOrSelfPermission(ANIMATION_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Cannot disable animations due to lack of permission.");
            return;
        }

        // Requires: adb shell pm grant io.github.tanelmae.moledroid android.permission.SET_ANIMATION_SCALE
        // to be run from CLI after installing the app
        float animationScale;
        if (status) {
            animationScale = 1.0f;
        } else {
            animationScale = 0.0f;
        }

        try {
            Class<?> windowManagerStubClazz = Class.forName("android.view.IWindowManager$Stub");
            Method asInterface = windowManagerStubClazz.getDeclaredMethod("asInterface", IBinder.class);
            Class<?> serviceManagerClazz = Class.forName("android.os.ServiceManager");
            Method getService = serviceManagerClazz.getDeclaredMethod("getService", String.class);
            Class<?> windowManagerClazz = Class.forName("android.view.IWindowManager");
            Method setAnimationScales = windowManagerClazz.getDeclaredMethod("setAnimationScales", float[].class);
            Method getAnimationScales = windowManagerClazz.getDeclaredMethod("getAnimationScales");

            IBinder windowManagerBinder = (IBinder) getService.invoke(null, "window");
            Object windowManagerObj = asInterface.invoke(null, windowManagerBinder);
            float[] currentScales = (float[]) getAnimationScales.invoke(windowManagerObj);
            for (int i = 0; i < currentScales.length; i++) {
                currentScales[i] = animationScale;
            }
            setAnimationScales.invoke(windowManagerObj, new Object[]{currentScales});
            Log.i(TAG, "Animations set to:" + String.valueOf(status));
        } catch (ClassNotFoundException cnfe) {
            Log.w(TAG, "Cannot disable animations reflectively.", cnfe);
        } catch (NoSuchMethodException mnfe) {
            Log.w(TAG, "Cannot disable animations reflectively.", mnfe);
        } catch (SecurityException se) {
            Log.w(TAG, "Cannot disable animations reflectively.", se);
        } catch (InvocationTargetException ite) {
            Log.w(TAG, "Cannot disable animations reflectively.", ite);
        } catch (IllegalAccessException iae) {
            Log.w(TAG, "Cannot disable animations reflectively.", iae);
        } catch (RuntimeException re) {
            Log.w(TAG, "Cannot disable animations reflectively.", re);
        }
    }
}
