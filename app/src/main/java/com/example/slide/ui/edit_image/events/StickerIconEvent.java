package com.example.slide.ui.edit_image.events;

import android.view.MotionEvent;

import com.example.slide.ui.edit_image.framework.StickerView;

public interface StickerIconEvent {
  void onActionDown(StickerView stickerView, MotionEvent event);

  void onActionMove(StickerView stickerView, MotionEvent event);

  void onActionUp(StickerView stickerView, MotionEvent event);
}
