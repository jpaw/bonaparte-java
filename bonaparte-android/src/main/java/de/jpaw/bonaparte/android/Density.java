package de.jpaw.bonaparte.android;

import android.app.Activity;
import android.util.DisplayMetrics;

public enum Density {
    LDPI(DisplayMetrics.DENSITY_LOW),       // 120 (1)
    MDPI(DisplayMetrics.DENSITY_MEDIUM),    // 160 (1)
    TV(DisplayMetrics.DENSITY_TV),          // 213 (weird)
    HDPI(DisplayMetrics.DENSITY_HIGH),      // 240 (1.5)
    XHDPI(DisplayMetrics.DENSITY_XHIGH),    // 320 (2)
    XXHDPI(DisplayMetrics.DENSITY_XXHIGH),  // 480 (3)
    XXXHDPI(640 /*DisplayMetrics.DENSITY_XXXHIGH */);    // 640 (4)

    private final int standardizedDpi;
    private final int halfTheDpi;

    Density(int standardizedDpi) {
        this.standardizedDpi = standardizedDpi;
        this.halfTheDpi = standardizedDpi / 2;  // only do division once
    }

    public int getStandardDpi() {
        return standardizedDpi;
    }

    /** Factory method to determine the current device's density from an Activity. */
    public static Density myDensity(Activity a) {
        DisplayMetrics metrics = new DisplayMetrics();
        a.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        switch (metrics.densityDpi) {
        case DisplayMetrics.DENSITY_LOW: return LDPI;
        case DisplayMetrics.DENSITY_MEDIUM: return MDPI;
        case DisplayMetrics.DENSITY_TV: return TV;
        case DisplayMetrics.DENSITY_HIGH: return HDPI;
        case DisplayMetrics.DENSITY_XHIGH: return XHDPI;
        case DisplayMetrics.DENSITY_XXHIGH: return XXHDPI;
        case 640: return XXXHDPI;
        default: return null;   // unidentified!
        }
    }

    /** Convert native pixels to display points (the device independent, recommended way to provide sizes). */
    public int px2dp(int px) {
        return (px * 160 + halfTheDpi) / standardizedDpi;
    }

    /** Convert a provided standardized size into device dependent pixel size. */
    public int dp2px(int dp) {
        return (dp * standardizedDpi + 80) / 160;  // conversion including rounding
    }
}
