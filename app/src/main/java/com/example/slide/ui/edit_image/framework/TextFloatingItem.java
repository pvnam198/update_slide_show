package com.example.slide.ui.edit_image.framework;

import android.content.Context;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.slide.ui.edit_image.utils.SystemUtil;
import com.example.slide.util.FontProvider;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;


/**
 * Customize your sticker with text and image background.
 * You can place some text into a given region, however,
 * you can also add a plain text sticker. To support text
 * auto resizing , I take most of the code from AutoResizeTextView.
 * See https://adilatwork.blogspot.com/2014/08/android-textview-which-resizes-its-text.html
 * Notice: It's not efficient to add long text due to too much of
 * StaticLayout object allocation.
 * Created by liutao on 30/11/2016.
 */

public class TextFloatingItem extends FloatingItem implements Serializable {

    /**
     * Our ellipsis string.
     */
    private static final String mEllipsis = "\u2026";

    private final Context context;
    //private final Rect realBounds;
    //private final Rect textRect;
    private final TextPaint textPaint;
    private Drawable drawable;
    private StaticLayout staticLayout;
    private int textWidth;
    private int textHeight;
    private int textAlpha;
    private int textColor;
    private int paddingWidth;
    private int paddingHeight;
    private int backgroundColor;
    private int backgroundAlpha;
    private int backgroundBorder;
    private boolean isShowBackground;
    private BitmapDrawable backgroundDrawable;
    private AddTextProperties.TextShadow textShadow;

    public int getTextWidth() {
        return textWidth;
    }

    public int getTextHeight() {
        return textHeight;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getBackgroundBorder() {
        return backgroundBorder;
    }

    public boolean isShowBackground() {
        return isShowBackground;
    }

    public AddTextProperties.TextShadow getTextShadow() {
        return textShadow;
    }

    public Layout.Alignment getTextAlign() {
        return textAlign;
    }

    public long getId() {
        return id;
    }

    private String text;
    private Layout.Alignment textAlign;
    private AddTextProperties addTextProperties;
    private Typeface typeface;
    public long id;

    /**
     * Upper bounds for text size.
     * This acts as a starting point for resizing.
     */
    private float maxTextSizePixels;

    /**
     * Lower bounds for text size.
     */
    private float minTextSizePixels;

    /**
     * Line spacing multiplier.
     */
    private float lineSpacingMultiplier = 1.0f;

    /**
     * Additional line spacing.
     */
    private float lineSpacingExtra = 0.0f;

    public TextFloatingItem(@NonNull Context context) {
        this.context = context;
        textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(25);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public TextFloatingItem(@NonNull Context context, AddTextProperties addTextProperties) {
        id = System.currentTimeMillis();
        this.context = context;
        this.addTextProperties = addTextProperties;
        textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);

        try {
            if (TextUtils.isEmpty(addTextProperties.getFontName())) {
                addTextProperties.setFontName(null);
            }
            typeface = addTextProperties.getFontName() == null ?
                    FontProvider.INSTANCE.getDefaultFont() :
                    Typeface.createFromAsset(context.getAssets(), addTextProperties.getFontName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTextSize(addTextProperties.getTextSize())
                .setTextWidth(addTextProperties.getTextWidth())
                .setTextHeight(addTextProperties.getTextHeight())
                .setText(addTextProperties.getText())
                .setPaddingWidth(SystemUtil.dpToPx(context, addTextProperties.getPaddingWidth()))
                .setBackgroundBorder(SystemUtil.dpToPx(context, addTextProperties.getBackgroundBorder()))
                .setTextShadow(addTextProperties.getTextShadow())
                .setTextColor(addTextProperties.getTextColor())
                .setTextAlpha(addTextProperties.getTextAlpha())
                .setBackgroundColor(addTextProperties.getBackgroundColor())
                .setBackgroundAlpha(addTextProperties.getBackgroundAlpha())
                .setShowBackground(addTextProperties.isShowBackground())
                .setTextColor(addTextProperties.getTextColor())
                .setTypeface(typeface)
                .setTextAlign(addTextProperties.getTextAlign())
                .setTextShare(addTextProperties.getTextShader())
                .resizeText();
    }

    public void updateConfig(@NotNull AddTextProperties addTextProperties) {
        this.addTextProperties = addTextProperties;
        setTextSize(this.addTextProperties.getTextSize())
                .setTextWidth(this.addTextProperties.getTextWidth())
                .setTextHeight(this.addTextProperties.getTextHeight())
                .setText(this.addTextProperties.getText())
                .setPaddingWidth(SystemUtil.dpToPx(context, this.addTextProperties.getPaddingWidth()))
                .setBackgroundBorder(SystemUtil.dpToPx(context, this.addTextProperties.getBackgroundBorder()))
                .setTextShadow(this.addTextProperties.getTextShadow())
                .setTextColor(this.addTextProperties.getTextColor())
                .setTextAlpha(this.addTextProperties.getTextAlpha())
                .setBackgroundColor(this.addTextProperties.getBackgroundColor())
                .setBackgroundAlpha(this.addTextProperties.getBackgroundAlpha())
                .setShowBackground(this.addTextProperties.isShowBackground())
                .setTextColor(this.addTextProperties.getTextColor())
                .setTypeface(this.addTextProperties.getTypeface(context))
                .setTextAlign(this.addTextProperties.getTextAlign())
                .setTextShare(this.addTextProperties.getTextShader())
                .resizeText();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Matrix matrix = getMatrix();
        canvas.save();
        canvas.concat(matrix);
        if (isShowBackground) {
            Paint paint = new Paint();
            if (backgroundDrawable != null) {
                Shader shader = new BitmapShader(backgroundDrawable.getBitmap(), Shader.TileMode.MIRROR, Shader.TileMode.MIRROR);
                paint.setShader(shader);
                paint.setAlpha(backgroundAlpha);
            } else {
                paint.setARGB(backgroundAlpha, Color.red(backgroundColor), Color.green(backgroundColor), Color.blue(backgroundColor));
            }
            canvas.drawRoundRect(0, 0, textWidth, textHeight, backgroundBorder, backgroundBorder, paint);
            canvas.restore();
            canvas.save();
            canvas.concat(matrix);
        }
        canvas.restore();
        canvas.save();
        canvas.concat(matrix);
        int dx = paddingWidth;
        int dy = textHeight / 2 - staticLayout.getHeight() / 2;
        // center vertical
        canvas.translate(dx, dy);
        staticLayout.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.concat(matrix);
        canvas.restore();
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public AddTextProperties getAddTextProperties() {
        return addTextProperties;
    }

    public void setAddTextProperties(AddTextProperties addTextProperties) {
        this.addTextProperties = addTextProperties;
    }

    public TextFloatingItem setTextShadow(AddTextProperties.TextShadow textShadow) {
        this.textShadow = textShadow;
        return this;
    }

    public TextFloatingItem setBackgroundBorder(int backgroundBorder) {
        this.backgroundBorder = backgroundBorder;
        return this;
    }

    public TextFloatingItem setShowBackground(boolean showBackground) {
        this.isShowBackground = showBackground;
        return this;
    }

    public BitmapDrawable getBackgroundDrawable() {
        return backgroundDrawable;
    }

    public TextFloatingItem setBackgroundDrawable(BitmapDrawable backgroundDrawable) {
        this.backgroundDrawable = backgroundDrawable;
        return this;
    }

    @Override
    public int getWidth() {
        return textWidth;
    }

    @Override
    public int getHeight() {
        return textHeight;
    }

    @Override
    public void release() {
        super.release();
        if (drawable != null) {
            drawable = null;
        }
    }

    public int getTextAlpha() {
        return textAlpha;
    }

    public TextFloatingItem setTextAlpha(int textAlpha) {
        this.textAlpha = textAlpha;
        return this;
    }

    @NonNull
    @Override
    public TextFloatingItem setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        textPaint.setAlpha(alpha);
        return this;
    }

    @Override
    public int getAlpha() {
        return textPaint.getAlpha();
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    public TextFloatingItem setDrawable(@NonNull Drawable drawable) {
        this.drawable = drawable;
        //realBounds.set(0, 0, getWidth(), getHeight());
        //textRect.set(0, 0, getWidth(), getHeight());
        return this;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public TextFloatingItem setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public int getBackgroundAlpha() {
        return backgroundAlpha;
    }

    public TextFloatingItem setBackgroundAlpha(int backgroundAlpha) {
        this.backgroundAlpha = backgroundAlpha;
        return this;
    }

    public TextFloatingItem setTextWidth(int width) {
        this.textWidth = width;
        return this;
    }

    public TextFloatingItem setTextHeight(int height) {
        this.textHeight = height;
        return this;
    }

    @NonNull
    public TextFloatingItem setDrawable(@NonNull Drawable drawable, @Nullable Rect region) {
        this.drawable = drawable;
        //realBounds.set(0, 0, getWidth(), getHeight());
        if (region == null) {
            //textRect.set(0, 0, getWidth(), getHeight());
        } else {
            //textRect.set(region.left, region.top, region.right, region.bottom);
        }
        return this;
    }

    @NonNull
    public TextFloatingItem setTypeface(@Nullable Typeface typeface) {
        textPaint.setTypeface(typeface);
        return this;
    }

    @NonNull
    public TextFloatingItem setTextSize(int textSize) {
        this.textPaint.setTextSize(convertSpToPx(textSize));
        return this;
    }

    @NonNull
    public TextFloatingItem setTextShare(@Nullable Shader share) {
        textPaint.setShader(share);
        return this;
    }

    @NonNull
    public TextFloatingItem setTextColor(@ColorInt int color) {
        this.textColor = color;
        return this;
    }

    @NonNull
    public TextFloatingItem setTextAlign(@NonNull int alignment) {
        switch (alignment) {
            case View.TEXT_ALIGNMENT_CENTER:
                textAlign = Layout.Alignment.ALIGN_CENTER;
                break;
            case View.TEXT_ALIGNMENT_TEXT_START:
                textAlign = Layout.Alignment.ALIGN_NORMAL;
                break;
            case View.TEXT_ALIGNMENT_TEXT_END:
                textAlign = Layout.Alignment.ALIGN_OPPOSITE;
                break;
        }
        return this;
    }

    public int getPaddingWidth() {
        return paddingWidth;
    }

    public TextFloatingItem setPaddingWidth(int paddingWidth) {
        this.paddingWidth = paddingWidth;
        return this;
    }

    public int getPaddingHeight() {
        return paddingHeight;
    }

    public TextFloatingItem setPaddingHeight(int paddingHeight) {
        this.paddingHeight = paddingHeight;
        return this;
    }

    @NonNull
    public TextFloatingItem setMaxTextSize(@Dimension(unit = Dimension.SP) float size) {
        textPaint.setTextSize(convertSpToPx(size));
        maxTextSizePixels = textPaint.getTextSize();
        return this;
    }

    /**
     * Sets the lower text size limit
     *
     * @param minTextSizeScaledPixels the minimum size to use for text in this view,
     *                                in scaled pixels.
     */
    @NonNull
    public TextFloatingItem setMinTextSize(float minTextSizeScaledPixels) {
        minTextSizePixels = convertSpToPx(minTextSizeScaledPixels);
        return this;
    }

    @NonNull
    public TextFloatingItem setLineSpacing(float add, float multiplier) {
        lineSpacingMultiplier = multiplier;
        lineSpacingExtra = add;
        return this;
    }

    @NonNull
    public TextFloatingItem setText(@Nullable String text) {
        this.text = text;
        return this;
    }

    @Nullable
    public String getText() {
        return text;
    }

    public void setShadow(int radius, int dx, int dy) {
        textPaint.setShadowLayer(radius, dx, dy, textColor);
    }

    /**
     * Resize this view's text size with respect to its width and height
     * (minus padding). You should always call this method after the initialization.
     */
    @NonNull
    public TextFloatingItem resizeText() {

        final CharSequence text = getText();

        if (text == null
                || text.length() <= 0
        ) {
            return this;
        }
        if (textShadow != null)
            textPaint.setShadowLayer(textShadow.getRadius(), textShadow.getDx(), textShadow.getDy(), textShadow.getColorShadow());
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setARGB(textAlpha, Color.red(textColor), Color.green(textColor), Color.blue(textColor));

        int width = textWidth - paddingWidth * 2;
        if (width <= 0)
            width = 100;
        staticLayout =
                new StaticLayout(this.text, textPaint, width, textAlign, lineSpacingMultiplier,
                        lineSpacingExtra, true);
        return this;
    }

    /**
     * @return lower text size limit, in pixels.
     */
    public float getMinTextSizePixels() {
        return minTextSizePixels;
    }

    /**
     * Sets the text size of a clone of the view's {@link TextPaint} object
     * and uses a {@link StaticLayout} instance to measure the height of the text.
     *
     * @return the height of the text when placed in a view
     * with the specified width
     * and when the text has the specified size.
     */
    protected int getTextHeightPixels(@NonNull CharSequence source, int availableWidthPixels,
                                      float textSizePixels) {
        textPaint.setTextSize(textSizePixels);
        // It's not efficient to create a StaticLayout instance
        // every time when measuring, we can use StaticLayout.Builder
        // since api 23.
        StaticLayout staticLayout =
                new StaticLayout(source, textPaint, availableWidthPixels, textAlign,
                        lineSpacingMultiplier, lineSpacingExtra, true);
        return staticLayout.getHeight();
    }

    /**
     * @return the number of pixels which scaledPixels corresponds to on the device.
     */
    private float convertSpToPx(float scaledPixels) {
        return scaledPixels * context.getResources().getDisplayMetrics().scaledDensity;
    }
}
