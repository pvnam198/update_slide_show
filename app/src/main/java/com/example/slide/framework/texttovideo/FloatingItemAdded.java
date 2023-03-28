package com.example.slide.framework.texttovideo;

import android.content.Context;

import com.example.slide.ui.edit_image.framework.FloatingItem;

public abstract class FloatingItemAdded extends FloatingItem {

    int startTime;
    int endTime;
    boolean isFullTime = true;

    abstract VideoTextExport convertToExport(Context context, int quality, int width, int height);
}
