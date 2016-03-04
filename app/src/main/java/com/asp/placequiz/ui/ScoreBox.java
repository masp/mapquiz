/*

Copyright 2015 Akexorcist

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.asp.placequiz.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.common.BaseRoundCornerProgressBar;
import com.asp.placequiz.R;
import com.asp.placequiz.game.ScorePair;

public class ScoreBox extends BaseRoundCornerProgressBar implements View.OnClickListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    protected final static int DEFAULT_ICON_SIZE = 20;
    protected final static int DEFAULT_ICON_PADDING_LEFT = 0;
    protected final static int DEFAULT_ICON_PADDING_RIGHT = 0;
    protected final static int DEFAULT_ICON_PADDING_TOP = 0;
    protected final static int DEFAULT_ICON_PADDING_BOTTOM = 0;

    protected final static int DEFAULT_TEXT_SIZE = 16;
    protected final static int DEFAULT_TEXT_MARGIN = 10;

    private TextView tvProgress;
    private int colorTextProgress;
    private int textProgressSize;
    private int textProgressMargin;
    private String textProgress;

    private ImageView ivProgressIcon;
    private int iconResource;
    private int iconSize;
    private int iconWidth;
    private int iconHeight;
    private int iconPadding;
    private int iconPaddingLeft;
    private int iconPaddingRight;
    private int iconPaddingTop;
    private int iconPaddingBottom;
    private int colorIconBackground;

    private OnIconClickListener iconClickListener;

    public ScoreBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScoreBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int initLayout() {
        return R.layout.text_icon_bar;
    }

    @Override
    protected void initStyleable(Context context, AttributeSet attrs) {
        TypedArray iconTypedArray = context.obtainStyledAttributes(attrs, R.styleable.IconRoundCornerProgress);
        TypedArray textTypedArray = context.obtainStyledAttributes(attrs, R.styleable.TextRoundCornerProgress);

        colorTextProgress = textTypedArray.getColor(R.styleable.TextRoundCornerProgress_rcTextProgressColor, Color.WHITE);

        textProgressSize = (int) textTypedArray.getDimension(R.styleable.TextRoundCornerProgress_rcTextProgressSize, dp2px(DEFAULT_TEXT_SIZE));
        textProgressMargin = (int) textTypedArray.getDimension(R.styleable.TextRoundCornerProgress_rcTextProgressMargin, dp2px(DEFAULT_TEXT_MARGIN));

        textProgress = textTypedArray.getString(R.styleable.TextRoundCornerProgress_rcTextProgress);

        iconResource = iconTypedArray.getResourceId(R.styleable.IconRoundCornerProgress_rcIconSrc, R.mipmap.round_corner_progress_icon);

        iconSize = (int) iconTypedArray.getDimension(R.styleable.IconRoundCornerProgress_rcIconSize, -1);
        iconWidth = (int) iconTypedArray.getDimension(R.styleable.IconRoundCornerProgress_rcIconWidth, dp2px(DEFAULT_ICON_SIZE));
        iconHeight = (int) iconTypedArray.getDimension(R.styleable.IconRoundCornerProgress_rcIconHeight, dp2px(DEFAULT_ICON_SIZE));
        iconPadding = (int) iconTypedArray.getDimension(R.styleable.IconRoundCornerProgress_rcIconPadding, -1);
        iconPaddingLeft = (int) iconTypedArray.getDimension(R.styleable.IconRoundCornerProgress_rcIconPaddingLeft, dp2px(DEFAULT_ICON_PADDING_LEFT));
        iconPaddingRight = (int) iconTypedArray.getDimension(R.styleable.IconRoundCornerProgress_rcIconPaddingRight, dp2px(DEFAULT_ICON_PADDING_RIGHT));
        iconPaddingTop = (int) iconTypedArray.getDimension(R.styleable.IconRoundCornerProgress_rcIconPaddingTop, dp2px(DEFAULT_ICON_PADDING_TOP));
        iconPaddingBottom = (int) iconTypedArray.getDimension(R.styleable.IconRoundCornerProgress_rcIconPaddingBottom, dp2px(DEFAULT_ICON_PADDING_BOTTOM));

        int colorIconBackgroundDefault = context.getResources().getColor(R.color.round_corner_progress_bar_background_default);
        colorIconBackground = iconTypedArray.getColor(R.styleable.IconRoundCornerProgress_rcIconBackgroundColor, colorIconBackgroundDefault);

        iconTypedArray.recycle();
        textTypedArray.recycle();
    }

    @Override
    protected void initView() {
        ivProgressIcon = (ImageView) findViewById(R.id.iv_progress_icon);
        ivProgressIcon.setOnClickListener(this);
        tvProgress = (TextView) findViewById(R.id.tv_progress);
        tvProgress.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_progress_icon && iconClickListener != null) {
            iconClickListener.onIconClick();
        }
    }

    public void setOnIconClickListener(OnIconClickListener listener) {
        iconClickListener = listener;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void drawProgress(LinearLayout layoutProgress, float max, float progress, float totalWidth,
                                int radius, int padding, int colorProgress, boolean isReverse) {
        GradientDrawable backgroundDrawable = createGradientDrawable(colorProgress);
        int newRadius = radius - (padding / 2);
        if (isReverse && progress != max)
            backgroundDrawable.setCornerRadii(new float[]{newRadius, newRadius, newRadius, newRadius, newRadius, newRadius, newRadius, newRadius});
        else
            backgroundDrawable.setCornerRadii(new float[]{0, 0, newRadius, newRadius, newRadius, newRadius, 0, 0});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            layoutProgress.setBackground(backgroundDrawable);
        } else {
            layoutProgress.setBackgroundDrawable(backgroundDrawable);
        }

        float ratio = max / progress;
        int progressWidth = (int) ((totalWidth - ((padding * 2) + ivProgressIcon.getWidth())) / ratio);
        ViewGroup.LayoutParams progressParams = layoutProgress.getLayoutParams();
        progressParams.width = progressWidth;
        layoutProgress.setLayoutParams(progressParams);
    }

    @Override
    protected void onViewDraw() {
        drawImageIcon();
        drawImageIconSize();
        drawImageIconPadding();
        drawIconBackgroundColor();

        drawTextProgress();
        drawTextProgressSize();
        drawTextProgressMargin();
        drawTextProgressPosition();
        drawTextProgressColor();
    }

    public void updateWithScore(ScorePair score) {
        setProgressText(score.score() + "/" + score.maxScore());
        setProgress(score.score());
        setMax(score.maxScore());
    }

    private void drawTextProgress() {
        tvProgress.setText(textProgress);
    }

    private void drawTextProgressColor() {
        tvProgress.setTextColor(colorTextProgress);
    }

    private void drawTextProgressSize() {
        tvProgress.setTextSize(TypedValue.COMPLEX_UNIT_PX, textProgressSize);
    }

    private void drawTextProgressMargin() {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tvProgress.getLayoutParams();
        params.setMargins(textProgressMargin, 0, textProgressMargin, 0);
        tvProgress.setLayoutParams(params);
    }

    private void drawTextProgressPosition() {
        clearTextProgressAlign();
        float ratio = getProgress() / getMax();
        if (ratio >= 0.5) {
            alignTextProgressInsideProgress();
        } else {
            alignTextProgressOutsideProgress();
        }
    }

    private void clearTextProgressAlign() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvProgress.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_LEFT, 0);
        params.addRule(RelativeLayout.ALIGN_RIGHT, 0);
        params.addRule(RelativeLayout.LEFT_OF, 0);
        params.addRule(RelativeLayout.RIGHT_OF, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.removeRule(RelativeLayout.START_OF);
            params.removeRule(RelativeLayout.END_OF);
            params.removeRule(RelativeLayout.ALIGN_START);
            params.removeRule(RelativeLayout.ALIGN_END);
        }
        tvProgress.setLayoutParams(params);
    }

    private void alignTextProgressInsideProgress() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvProgress.getLayoutParams();
        if (isReverse()) {
            params.addRule(RelativeLayout.ALIGN_LEFT, R.id.layout_progress);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                params.addRule(RelativeLayout.ALIGN_START, R.id.layout_progress);
        } else {
            params.addRule(RelativeLayout.ALIGN_RIGHT, R.id.layout_progress);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                params.addRule(RelativeLayout.ALIGN_END, R.id.layout_progress);
        }
        tvProgress.setLayoutParams(params);
    }

    private void alignTextProgressOutsideProgress() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tvProgress.getLayoutParams();
        if (isReverse()) {
            params.addRule(RelativeLayout.LEFT_OF, R.id.layout_progress);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                params.addRule(RelativeLayout.START_OF, R.id.layout_progress);
        } else {
            params.addRule(RelativeLayout.RIGHT_OF, R.id.layout_progress);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                params.addRule(RelativeLayout.END_OF, R.id.layout_progress);
        }
        tvProgress.setLayoutParams(params);
    }

    @Override
    public void setProgress(float progress) {
        super.setProgress(progress);
        drawTextProgressPosition();
    }

    public String getProgressText() {
        return textProgress;
    }

    public void setProgressText(String text) {
        textProgress = text;
        drawTextProgress();
        drawTextProgressPosition();
    }

    public int getTextProgressColor() {
        return colorTextProgress;
    }

    public void setTextProgressColor(int color) {
        this.colorTextProgress = color;
        drawTextProgressColor();
    }

    public int getTextProgressSize() {
        return textProgressSize;
    }

    public void setTextProgressSize(int size) {
        this.textProgressSize = size;
        drawTextProgressSize();
        drawTextProgressPosition();
    }

    public int getTextProgressMargin() {
        return textProgressMargin;
    }

    public void setTextProgressMargin(int margin) {
        this.textProgressMargin = margin;
        drawTextProgressMargin();
        drawTextProgressPosition();
    }

    @Override
    public void onGlobalLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            tvProgress.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        else
            tvProgress.getViewTreeObserver().removeGlobalOnLayoutListener(this);
        drawTextProgressPosition();
    }

    private void drawImageIcon() {
        ivProgressIcon.setImageResource(iconResource);
    }

    private void drawImageIconSize() {
        if (iconSize == -1)
            ivProgressIcon.setLayoutParams(new LayoutParams(iconWidth, iconHeight));
        else
            ivProgressIcon.setLayoutParams(new LayoutParams(iconSize, iconSize));
    }

    private void drawImageIconPadding() {
        if (iconPadding == -1 || iconPadding == 0) {
            ivProgressIcon.setPadding(iconPaddingLeft, iconPaddingTop, iconPaddingRight, iconPaddingBottom);
        } else {
            ivProgressIcon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        }
        ivProgressIcon.invalidate();
    }

    @SuppressWarnings("deprecation")
    private void drawIconBackgroundColor() {
        GradientDrawable iconBackgroundDrawable = createGradientDrawable(colorIconBackground);
        int radius = getRadius() - (getPadding() / 2);
        iconBackgroundDrawable.setCornerRadii(new float[]{radius, radius, 0, 0, 0, 0, radius, radius});
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ivProgressIcon.setBackground(iconBackgroundDrawable);
        } else {
            ivProgressIcon.setBackgroundDrawable(iconBackgroundDrawable);
        }
    }

    public int getIconImageResource() {
        return iconResource;
    }

    public void setIconImageResource(int resId) {
        this.iconResource = resId;
        drawImageIcon();
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setIconSize(int size) {
        if (size >= 0)
            this.iconSize = size;
        drawImageIconSize();
    }

    public int getIconPadding() {
        return iconPadding;
    }

    public void setIconPadding(int padding) {
        if (padding >= 0)
            this.iconPadding = padding;
        drawImageIconPadding();
    }

    public int getIconPaddingLeft() {
        return iconPaddingLeft;
    }

    public void setIconPaddingLeft(int padding) {
        if (padding > 0)
            this.iconPaddingLeft = padding;
        drawImageIconPadding();
    }

    public int getIconPaddingRight() {
        return iconPaddingRight;
    }

    public void setIconPaddingRight(int padding) {
        if (padding > 0)
            this.iconPaddingRight = padding;
        drawImageIconPadding();
    }

    public int getIconPaddingTop() {
        return iconPaddingTop;
    }

    public void setIconPaddingTop(int padding) {
        if (padding > 0)
            this.iconPaddingTop = padding;
        drawImageIconPadding();
    }

    public int getIconPaddingBottom() {
        return iconPaddingBottom;
    }

    public void setIconPaddingBottom(int padding) {
        if (padding > 0)
            this.iconPaddingBottom = padding;
        drawImageIconPadding();
    }

    public int getColorIconBackground() {
        return colorIconBackground;
    }

    public void setIconBackgroundColor(int color) {
        this.colorIconBackground = color;
        drawIconBackgroundColor();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.iconResource = this.iconResource;
        ss.iconSize = this.iconSize;
        ss.iconWidth = this.iconWidth;
        ss.iconHeight = this.iconHeight;

        ss.iconPadding = this.iconPadding;
        ss.iconPaddingLeft = this.iconPaddingLeft;
        ss.iconPaddingRight = this.iconPaddingRight;

        ss.iconPaddingTop = this.iconPaddingTop;
        ss.iconPaddingBottom = this.iconPaddingBottom;
        ss.colorIconBackground = this.colorIconBackground;

        ss.colorTextProgress = this.colorTextProgress;
        ss.textProgressSize = this.textProgressSize;
        ss.textProgressMargin = this.textProgressMargin;

        ss.textProgress = this.textProgress;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.iconResource = ss.iconResource;
        this.iconSize = ss.iconSize;
        this.iconWidth = ss.iconWidth;
        this.iconHeight = ss.iconHeight;
        this.iconPadding = ss.iconPadding;
        this.iconPaddingLeft = ss.iconPaddingLeft;
        this.iconPaddingRight = ss.iconPaddingRight;
        this.iconPaddingTop = ss.iconPaddingTop;
        this.iconPaddingBottom = ss.iconPaddingBottom;
        this.colorIconBackground = ss.colorIconBackground;
        this.colorTextProgress = ss.colorTextProgress;
        this.textProgressSize = ss.textProgressSize;
        this.textProgressMargin = ss.textProgressMargin;

        this.textProgress = ss.textProgress;
    }

    private static class SavedState extends BaseSavedState {
        int iconResource;
        int iconSize;
        int iconWidth;
        int iconHeight;
        int iconPadding;
        int iconPaddingLeft;
        int iconPaddingRight;
        int iconPaddingTop;
        int iconPaddingBottom;
        int colorIconBackground;
        int colorTextProgress;
        int textProgressSize;
        int textProgressMargin;

        String textProgress;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);

            this.iconResource = in.readInt();
            this.iconSize = in.readInt();
            this.iconWidth = in.readInt();
            this.iconHeight = in.readInt();
            this.iconPadding = in.readInt();
            this.iconPaddingLeft = in.readInt();
            this.iconPaddingRight = in.readInt();
            this.iconPaddingTop = in.readInt();
            this.iconPaddingBottom = in.readInt();
            this.colorIconBackground = in.readInt();
            this.colorTextProgress = in.readInt();
            this.textProgressSize = in.readInt();
            this.textProgressMargin = in.readInt();

            this.textProgress = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);

            out.writeInt(this.iconResource);
            out.writeInt(this.iconSize);
            out.writeInt(this.iconWidth);
            out.writeInt(this.iconHeight);
            out.writeInt(this.iconPadding);
            out.writeInt(this.iconPaddingLeft);
            out.writeInt(this.iconPaddingRight);
            out.writeInt(this.iconPaddingTop);
            out.writeInt(this.iconPaddingBottom);
            out.writeInt(this.colorIconBackground);
            out.writeInt(this.colorTextProgress);
            out.writeInt(this.textProgressSize);
            out.writeInt(this.textProgressMargin);

            out.writeString(this.textProgress);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    public interface OnIconClickListener {
        public void onIconClick();
    }
}