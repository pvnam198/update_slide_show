package com.example.slide.ui.edit_image.framework;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;

import com.example.slide.ui.edit_image.ShadowProvider;
import com.example.slide.util.FontProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddTextProperties implements Serializable {

    public static class TextShadow implements Serializable {

        private int radius;

        private int dx;

        private int dy;

        private int colorShadow;

        public TextShadow(int radius, int dx, int dy, int colorShadow) {
            this.radius = radius;
            this.dx = dx;
            this.dy = dy;
            this.colorShadow = colorShadow;
        }

        public int getRadius() {
            return radius;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public int getDx() {
            return dx;
        }

        public int getDy() {
            return dy;
        }

        public int getColorShadow() {
            return colorShadow;
        }

    }

    private TextShadow textShadow;
    private int textShadowIndex;
    private int textSize;
    private int textColor;
    private int textColorIndex;
    private int textAlpha;
    private Shader textShader;
    private int textShaderIndex;
    private String text;
    private int textAlign;
    private String fontName;
    private int fontIndex;
    private int backgroundColor;
    private int backgroundColorIndex;
    private int backgroundAlpha;
    private int backgroundBorder;
    private boolean isFullScreen;
    private boolean isShowBackground;
    private int paddingWidth;
    private int paddingHeight;
    private int textWidth;
    private int textHeight;
    private long id;
    private boolean isColor;

    public static List<TextShadow> getLstTextShadow() {
        List<TextShadow> textShadows = new ArrayList<>();
        textShadows.add(new TextShadow(0, 0, 0, Color.CYAN));
        textShadows.add(new TextShadow(8, 4, 4, Color.parseColor("#FF1493")));
        textShadows.add(new TextShadow(8, 4, 4, Color.MAGENTA));
        textShadows.add(new TextShadow(8, 4, 4, Color.parseColor("#24ffff")));
        textShadows.add(new TextShadow(8, 4, 4, Color.YELLOW));
        textShadows.add(new TextShadow(8, 4, 4, Color.WHITE));
        textShadows.add(new TextShadow(8, 4, 4, Color.GRAY));


        textShadows.add(new TextShadow(8, -4, -4, Color.parseColor("#FF1493")));
        textShadows.add(new TextShadow(8, -4, -4, Color.MAGENTA));
        textShadows.add(new TextShadow(8, -4, -4, Color.parseColor("#24ffff")));
        textShadows.add(new TextShadow(8, -4, -4, Color.YELLOW));
        textShadows.add(new TextShadow(8, -4, -4, Color.WHITE));
        textShadows.add(new TextShadow(8, -4, -4, Color.parseColor("#696969")));


        textShadows.add(new TextShadow(8, -4, 4, Color.parseColor("#FF1493")));
        textShadows.add(new TextShadow(8, -4, 4, Color.MAGENTA));
        textShadows.add(new TextShadow(8, -4, 4, Color.parseColor("#24ffff")));
        textShadows.add(new TextShadow(8, -4, 4, Color.YELLOW));
        textShadows.add(new TextShadow(8, -4, 4, Color.WHITE));
        textShadows.add(new TextShadow(8, -4, 4, Color.parseColor("#696969")));

        textShadows.add(new TextShadow(8, 4, -4, Color.parseColor("#FF1493")));
        textShadows.add(new TextShadow(8, 4, -4, Color.MAGENTA));
        textShadows.add(new TextShadow(8, 4, -4, Color.parseColor("#24ffff")));
        textShadows.add(new TextShadow(8, 4, -4, Color.YELLOW));
        textShadows.add(new TextShadow(8, 4, -4, Color.WHITE));
        textShadows.add(new TextShadow(8, 4, -4, Color.parseColor("#696969")));

        return textShadows;
    }

    public static AddTextProperties getDefaultProperties() {
        AddTextProperties addTextProperties = new AddTextProperties();
        addTextProperties.setTextSize(30);
        addTextProperties.setTextAlign(View.TEXT_ALIGNMENT_CENTER);
        addTextProperties.setFontName(null);
        addTextProperties.setTextColor(Color.WHITE);
        addTextProperties.setTextAlpha(255);
        addTextProperties.setBackgroundAlpha(255);
        addTextProperties.setPaddingWidth(12);
        addTextProperties.setTextShaderIndex(0);
        addTextProperties.setBackgroundColorIndex(21);
        addTextProperties.setTextColorIndex(0);
        addTextProperties.setFontIndex(0);
        addTextProperties.setShowBackground(false);
        addTextProperties.setBackgroundColor(Color.WHITE);
        addTextProperties.setBackgroundBorder(8);
        addTextProperties.setTextAlign(View.TEXT_ALIGNMENT_CENTER);
        addTextProperties.setTextShadow(ShadowProvider.INSTANCE.getSHADOW1());
        return addTextProperties;
    }

    public boolean isColor() {
        return isColor;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public int getTextColorIndex() {
        return textColorIndex;
    }

    public void setTextColorIndex(int textColorIndex) {
        this.textColorIndex = textColorIndex;
    }

    public int getTextShaderIndex() {
        return textShaderIndex;
    }

    public void setTextShaderIndex(int textShaderIndex) {
        this.textShaderIndex = textShaderIndex;
    }

    public int getBackgroundColorIndex() {
        return backgroundColorIndex;
    }

    public void setBackgroundColorIndex(int backgroundColorIndex) {
        this.backgroundColorIndex = backgroundColorIndex;
    }

    int getFontIndex() {
        return fontIndex;
    }

    void setFontIndex(int fontIndex) {
        this.fontIndex = fontIndex;
    }

    public int getTextShadowIndex() {
        return textShadowIndex;
    }

    public void setTextShadowIndex(int textShadowIndex) {
        this.textShadowIndex = textShadowIndex;
    }

    public TextShadow getTextShadow() {
        return textShadow;
    }

    public void setTextShadow(TextShadow textShadow) {
        this.textShadow = textShadow;
    }

    public int getBackgroundBorder() {
        return backgroundBorder;
    }

    void setBackgroundBorder(int backgroundBorder) {
        this.backgroundBorder = backgroundBorder;
    }

    public int getTextHeight() {
        return textHeight;
    }

    public void setTextHeight(int textHeight) {
        this.textHeight = textHeight;
    }

    public int getTextWidth() {
        return textWidth;
    }

    public void setTextWidth(int textWidth) {
        this.textWidth = textWidth;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public int getPaddingWidth() {
        return paddingWidth;
    }

    public void setPaddingWidth(int paddingWidth) {
        this.paddingWidth = paddingWidth;
    }

    public int getPaddingHeight() {
        return paddingHeight;
    }

    public void setPaddingHeight(int paddingHeight) {
        this.paddingHeight = paddingHeight;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        isColor = true;
        this.textColor = textColor;
    }

    public int getTextAlpha() {
        return textAlpha;
    }

    public void setTextAlpha(int textAlpha) {
        this.textAlpha = textAlpha;
    }

    public Shader getTextShader() {
        return textShader;
    }

    public void setTextShader(Shader textShader) {
        isColor = false;
        this.textShader = textShader;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextAlign() {
        return textAlign;
    }

    void setTextAlign(int textAlign) {
        this.textAlign = textAlign;
    }

    public String getFontName() {
        return fontName;
    }

    public Typeface getTypeface(Context context) {
        return TextUtils.isEmpty(fontName) ?
                FontProvider.INSTANCE.getDefaultFont() :
                Typeface.createFromAsset(context.getAssets(), fontName);
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isShowBackground() {
        return isShowBackground;
    }

    public void setShowBackground(boolean showBackground) {
        isShowBackground = showBackground;
    }

    public int getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public void setBackgroundAlpha(int backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
    }
}
