package com.example.musichub.helper.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

public class Helper {

    public static void changeStatusBarColor(Activity activity, int colorResId) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, colorResId));
    }

    public static void changeNavigationColor(Activity activity, int colorResId, boolean lightIcons) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // Change the navigation bar color
        window.setNavigationBarColor(ContextCompat.getColor(activity, colorResId));

        // If light icons are desired
        int flags = window.getDecorView().getSystemUiVisibility();
        if (lightIcons) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        } else {
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        window.getDecorView().setSystemUiVisibility(flags);
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static String convertToIntString(int number) {
        String numberString = String.valueOf(number);
        int length = numberString.length();

        if (length == 4) {
            return numberString.charAt(0) + "." + numberString.charAt(1) + "K";
        } else if (length == 5) {
            return numberString.charAt(0) + numberString.charAt(1) + "K";
        } else if (length == 6) {
            return numberString.charAt(0) + numberString.charAt(1) + numberString.charAt(2) + "K";
        } else if (length == 7) {
            return numberString.charAt(0) + "." + numberString.charAt(1) + "M";
        } else if (length == 8) {
            return numberString.charAt(0) + numberString.charAt(1) + "M";
        } else if (length == 9) {
            return numberString.charAt(0) + numberString.charAt(1) + numberString.charAt(2) + "M";
        } else {
            return numberString;
        }
    }
}
