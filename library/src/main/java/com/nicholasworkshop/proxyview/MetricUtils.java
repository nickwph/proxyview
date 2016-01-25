package com.nicholasworkshop.proxyview;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by nickwph on 4/17/15.
 * <p/>
 * Tools needed for metrics.
 */
public class MetricUtils {

    public static int getPxFromDp(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
