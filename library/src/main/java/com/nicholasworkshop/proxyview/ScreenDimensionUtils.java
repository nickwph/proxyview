package com.nicholasworkshop.proxyview;

import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.VisibleForTesting;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by levischmidt on 3/9/15.
 */
public class ScreenDimensionUtils {

    private static final int DEFAULT_MAX_SCREEN_WIDTH = 1920;

    public static Point getScreenSize(WindowManager manager) {
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= 17) {
            manager.getDefaultDisplay().getRealSize(size);
        } else {
            // correct for devices with hardware navigation buttons
            manager.getDefaultDisplay().getSize(size);
        }
        return size;
    }

    public static int getMaxScreenWidth() {
        return getMaxScreenWidth(Resources.getSystem());
    }

    @VisibleForTesting
    static int getMaxScreenWidth(Resources resources) {
        DisplayMetrics metrics = resources != null ? resources.getDisplayMetrics() : null;
        if (metrics != null) {
            int width = metrics.widthPixels;
            int height = metrics.heightPixels;
            return Math.max(width, height);
        } else {
            return DEFAULT_MAX_SCREEN_WIDTH;
        }
    }
}