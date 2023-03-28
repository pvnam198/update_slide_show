package com.example.slide.ui.edit_image.events;

import android.view.MotionEvent;

import com.example.slide.ui.edit_image.framework.StickerView;

public class EditTextIconEvent implements StickerIconEvent {
    @Override
    public void onActionDown(StickerView stickerView, MotionEvent event) {

    }

    @Override
    public void onActionMove(StickerView stickerView, MotionEvent event) {

    }

    @Override
    public void onActionUp(StickerView stickerView, MotionEvent event) {
        stickerView.editTextSticker();
    }
}
