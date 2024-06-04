package com.example.musichub.helper.ui;

import android.annotation.SuppressLint;
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


    @SuppressLint("DefaultLocale")
    public static String convertToIntString(int number) {
        String numberStr = String.valueOf(number);
        int length = numberStr.length();

        if (length <= 3) {
            return numberStr;
        } else if (length <= 6) {
            int thousands = number / 1000;
            double decimal = (double) number % 1000 / 100;
            return String.format("%,d.%dK", thousands, Math.round(decimal));
        } else if (length <= 9) {
            int millions = number / 1000000;
            return String.format("%,dM", millions);
        } else {
            return String.format("%,d.##B", number);

        }
    }

}
