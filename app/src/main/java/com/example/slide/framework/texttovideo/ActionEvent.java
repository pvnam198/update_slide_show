package com.example.slide.framework.texttovideo;

import android.view.MotionEvent;

public interface ActionEvent {
  void onActionDown(TextListToVideoView stickerView, MotionEvent event);

  void onActionMove(TextListToVideoView stickerView, MotionEvent event);

  void onActionUp(TextListToVideoView stickerView, MotionEvent event);
}
