package com.example.slide.ui.edit_image.framework;

public class SaveSettings {
    private boolean isTransparencyEnabled;
    private boolean isClearViewsEnabled;

    public boolean isTransparencyEnabled() {
        return isTransparencyEnabled;
    }

    public boolean isClearViewsEnabled() {
        return isClearViewsEnabled;
    }

    private SaveSettings(Builder builder) {
        this.isClearViewsEnabled = builder.isClearViewsEnabled;
        this.isTransparencyEnabled = builder.isTransparencyEnabled;
    }

    public static class Builder {
        private boolean isTransparencyEnabled = true;
        private boolean isClearViewsEnabled = true;

        public Builder setTransparencyEnabled(boolean transparencyEnabled) {
            isTransparencyEnabled = transparencyEnabled;
            return this;
        }

        /**
         * Define a flag to clear the view after saving the image
         *
         * @param clearViewsEnabled true if you want to clear all the views on {@link PhotoEditorView}
         * @return Builder
         */
        public Builder setClearViewsEnabled(boolean clearViewsEnabled) {
            isClearViewsEnabled = clearViewsEnabled;
            return this;
        }

        public SaveSettings build() {
            return new SaveSettings(this);
        }
    }
}
