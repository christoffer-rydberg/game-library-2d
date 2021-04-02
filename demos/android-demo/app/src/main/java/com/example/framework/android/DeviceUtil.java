package com.example.framework.android;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.Surface;
import android.view.WindowManager;

public class DeviceUtil {

    private static int getRotation(Activity activity) {
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getRotation();
    }

    public static NaturalOrientation getNaturalOrientation(Activity activity) {
        int rotation = getRotation(activity);
        int orientation = activity.getResources().getConfiguration().orientation;

        boolean landscape = orientation == Configuration.ORIENTATION_LANDSCAPE;
        boolean portrait = orientation == Configuration.ORIENTATION_PORTRAIT;
        boolean isTilted = rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270;

        return landscape && !isTilted || portrait && isTilted
                ? NaturalOrientation.LANDSCAPE
                : NaturalOrientation.PORTRAIT;
    }

    public static void lockOrientation(Activity activity) {
        switch (getNaturalOrientation(activity)) {
            case LANDSCAPE:
                switch (getRotation(activity)) {
                    case Surface.ROTATION_0:
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case Surface.ROTATION_90:
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        break;
                    case Surface.ROTATION_180:
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        break;
                    case Surface.ROTATION_270:
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                }
            case PORTRAIT:
                switch (getRotation(activity)) {
                    case Surface.ROTATION_0:
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        break;
                    case Surface.ROTATION_90:
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        break;
                    case Surface.ROTATION_180:
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                        break;
                    case Surface.ROTATION_270:
                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                        break;
                }
        }
    }

    public static void unlockOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public enum NaturalOrientation {
        LANDSCAPE,
        PORTRAIT
    }
}
