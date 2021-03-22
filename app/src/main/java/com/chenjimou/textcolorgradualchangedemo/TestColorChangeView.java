package com.chenjimou.textcolorgradualchangedemo;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TestColorChangeView extends View {

    private Paint mPaint;
    // View属性的默认值
    private String mText = "Tab";
    private int mTextSize = sp2px(20);
    private int mTextColor = Color.BLACK;
    private int mTextColorChange = Color.RED;
    private int mPoint = POINT_ON;
    private float mProgress;

    private final Rect mTextBound = new Rect();
    // 文字的总宽度
    private int mTextWidth;
    // 文字开始绘制点的X坐标
    private int mTextStartX;

    // 使用注解替代枚举
    @IntDef(flag = true, value = {POINT_ON, POINT_OUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Point {
    }

    public static final int POINT_ON = 0;
    public static final int POINT_OUT = 1;

    public TestColorChangeView(Context context) {
        this(context, null);
    }

    public TestColorChangeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestColorChangeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable final AttributeSet attrs) {
        mPaint = new Paint();
        // 通过TypedArray获取布局文件中设置的属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TestColorChangeView);
        mText = typedArray.getString(R.styleable.TestColorChangeView_text);
        mTextSize = typedArray.getDimensionPixelSize(
                R.styleable.TestColorChangeView_text_size, mTextSize);
        mTextColor = typedArray.getColor(
                R.styleable.TestColorChangeView_text_color, mTextColor);
        mTextColorChange = typedArray.getColor(
                R.styleable.TestColorChangeView_text_color_change, mTextColorChange);
        mProgress = typedArray.getFloat(R.styleable.TestColorChangeView_progress, 0);
        typedArray.recycle();
        // 设置文字大小
        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 测量文字的总宽度
        mPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
        mTextWidth = (int) (mPaint.measureText(mText) + .5f);
        // 测量View的宽高
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
        // 记录开始绘制点的X坐标
        mTextStartX = getMeasuredWidth() / 2 - mTextWidth / 2;
    }

    private int measureWidth(final int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = (int) (mTextWidth + .5f) + getPaddingLeft() + getPaddingRight();
                break;
        }
        // 如果是AT_MOST,不能超过父布局的尺寸
        result = (mode == MeasureSpec.AT_MOST) ? Math.min(result, size) : result;
        return result;
    }

    private int measureHeight(final int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                result = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = (int) (mTextBound.height() + .5f) + getPaddingTop() + getPaddingBottom();
                break;
        }
        // 如果是AT_MOST,不能超过父布局的尺寸
        result = (mode == MeasureSpec.AT_MOST) ? Math.min(result, size) : result;
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (mPoint){
            // 当要进入item，视为将字体覆盖为红色
            case POINT_ON:
                // 设置画笔颜色为红色
                mPaint.setColor(mTextColorChange);
                // 红色画笔的绘图范围随 mProgress 逐渐增大
                canvas.save();
                canvas.clipRect(mTextStartX,
                        0,
                        (int) (mTextStartX + mProgress * mTextWidth),
                        getMeasuredHeight());
                canvas.drawText(mText,
                        mTextStartX,
                        getMeasuredHeight()/2 - (mPaint.descent()/2 + mPaint.ascent()/2),
                        mPaint);
                canvas.restore();
                // 设置画笔颜色为黑色
                mPaint.setColor(mTextColor);
                // 黑色画笔的绘图范围随 mProgress 逐渐减小
                canvas.save();
                canvas.clipRect((int) (mTextStartX + mProgress * mTextWidth),
                        0,
                        mTextStartX + mTextWidth,
                        getMeasuredHeight());
                canvas.drawText(mText,
                        mTextStartX,
                        getMeasuredHeight()/2 - (mPaint.descent()/2 + mPaint.ascent()/2),
                        mPaint);
                canvas.restore();
                break;
            // 当要离开item，将字体覆盖为黑色
            case POINT_OUT:
                // 设置画笔颜色为黑色
                mPaint.setColor(mTextColor);
                // 黑色画笔的绘图范围随 mProgress 逐渐增大
                canvas.save();
                canvas.clipRect(mTextStartX,
                        0,
                        // 因为传进来的 mProgress 是递减的，所以乘以 1 - mProgress
                        (int) (mTextStartX + (1 - mProgress) * mTextWidth),
                        getMeasuredHeight());
                canvas.drawText(mText,
                        mTextStartX,
                        getMeasuredHeight()/2 - (mPaint.descent()/2 + mPaint.ascent()/2),
                        mPaint);
                canvas.restore();
                // 设置画笔颜色为红色
                mPaint.setColor(mTextColorChange);
                // 红色画笔的绘图范围随 mProgress 逐渐减小
                canvas.save();
                canvas.clipRect((int) (mTextStartX + (1 - mProgress) * mTextWidth),
                        0,
                        mTextStartX + mTextWidth,
                        getMeasuredHeight());
                canvas.drawText(mText,
                        mTextStartX,
                        getMeasuredHeight()/2 - (mPaint.descent()/2 + mPaint.ascent()/2),
                        mPaint);
                canvas.restore();
                break;
        }
    }

    public void setText(String mText) {
        this.mText = mText;
        requestLayout();
        invalidate();
    }

    public void setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        mPaint.setTextSize(mTextSize);
        requestLayout();
        invalidate();
    }

    public void setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        invalidate();
    }

    public void setTextColorChange(int mTextColorChange) {
        this.mTextColorChange = mTextColorChange;
        invalidate();
    }

    public void setPoint(@Point int mPoint) {
        this.mPoint = mPoint;
    }

    public void setProgress(float mProgress) {
        this.mProgress = mProgress;
        invalidate();
    }

    static int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    static int sp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
