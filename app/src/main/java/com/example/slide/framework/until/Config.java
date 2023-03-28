package com.example.slide.framework.until;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by anh on 6/10/2016.
 */
public class Config {
    public static final String EXTRA_VIDEO_ENTITY = "extra.video.entity";

    public static String linkApp = "Video had been created: https://play.google.com/store/apps/details?id=";
    public static int SCREENWIDTH;
    public static int SCREENHEIGHT;
    public static int height_rec;
    public static String NAME_FILE_LOOP = "in.txt";

    public static void init(Activity mActivity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        SCREENWIDTH = displaymetrics.widthPixels;
        SCREENHEIGHT = displaymetrics.heightPixels;

        height_rec = (SCREENHEIGHT / 2 - SCREENWIDTH / 2) / 2;
    }
}
