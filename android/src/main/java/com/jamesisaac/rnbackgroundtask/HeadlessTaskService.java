package com.jamesisaac.rnbackgroundtask;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;
import com.facebook.react.HeadlessJsTaskService;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class HeadlessTaskService extends HeadlessJsTaskService {
    private static final String TAG = "BackgroundTask";

    @Override
    protected @Nullable HeadlessJsTaskConfig getTaskConfig(Intent intent) {
        Bundle extras = intent.getExtras();
        // If extras have been passed to the intent, pass them on into the JS as taskData
        // which can be accessed as the first param.
        WritableMap data = /* extras != null ? Arguments.fromBundle(extras) : */ Arguments.createMap();

        int timeout = extras.getInt("timeout");

        Log.d(TAG, String.format("Returning HeadlessJsTaskConfig, timeout=%s ms", timeout));
        if(!isAppOnForeground(this)) {
            return new HeadlessJsTaskConfig(
                    // The the task was registered with in JS - must match
                    "BackgroundTask",
                    data,
                    TimeUnit.SECONDS.toMillis(timeout)
            );
        } else {
            return null;   
        }
    }
    
    // From https://facebook.github.io/react-native/docs/headless-js-android.html
    public static boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert activityManager != null;
        List<ActivityManager.RunningAppProcessInfo> appProcesses =
                activityManager.getRunningAppProcesses();

        if (appProcesses == null) {
            return false;
        }

        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance ==
                    ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    appProcess.processName.equals(packageName)) {
                return true;
            }
        }

        return false;
    }
}
