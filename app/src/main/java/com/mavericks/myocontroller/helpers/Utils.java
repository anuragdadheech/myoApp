package com.mavericks.myocontroller.helpers;

import android.content.Context;

/**
 * @author Anurag
 */
public class Utils {
    public static int convertDPTOPixels(Context context, int dp) {
        // Get the screen's density scale
        float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (dp * scale + 0.5f);
    }
}
