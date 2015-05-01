package io.github.tanelmae.moledroid;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;


public class Switcher extends ActionBarActivity {
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
            } else if (params.contains("WIFI")){
                switchWifi(Boolean.valueOf(extras.getString("WIFI")));
            }
        }
        finish();
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

    // To be removed:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switcher);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_switcher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
