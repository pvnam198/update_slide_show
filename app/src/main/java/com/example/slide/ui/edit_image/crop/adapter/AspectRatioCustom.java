package com.example.slide.ui.edit_image.crop.adapter;

import com.steelkiwi.cropiwa.AspectRatio;

class AspectRatioCustom extends AspectRatio {

    private int selectedIem;
    private int unselectItem;

    AspectRatioCustom(int w, int h, int unselectItem, int selectedIem) {
        super(w, h);
        this.selectedIem = selectedIem;
        this.unselectItem = unselectItem;
    }

    int getSelectedIem() {
        return selectedIem;
    }


    int getUnselectItem() {
        return unselectItem;
    }

}
