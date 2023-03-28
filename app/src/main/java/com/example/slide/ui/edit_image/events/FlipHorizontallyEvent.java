package com.example.slide.ui.edit_image.events;

import com.example.slide.ui.edit_image.framework.StickerView;

public class FlipHorizontallyEvent extends AbstractFlipEvent {

  @Override
  @StickerView.Flip protected int getFlipDirection() {
    return StickerView.FLIP_HORIZONTALLY;
  }
}
