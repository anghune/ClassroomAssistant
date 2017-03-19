package com.lzp.classroomassistant.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.lzp.classroomassistant.R;

/**
 * TODO: document your custom view class.
 */
public class SeatView extends View {
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;
    private Paint mPaint;

    private static final int SEAT_SELECTED = 1;    // 已选
    private static final int SEAT_SELECT = 2;       //  选中
    private static final int SEAT_AVAILABLE = 3;     //  可选
    private static final int SEAT_NOT_AVAILABLE = 4; //  不可选

    private int row;
    private int column;
    int horspacing = 5;
    int verSpacing = 5;




    public SeatView(Context context) {
        super(context);
        init(null, 0);
    }

    public SeatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SeatView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SeatView, defStyle, 0);

        mExampleString = a.getString(
                R.styleable.SeatView_exampleString);
        mExampleColor = a.getColor(
                R.styleable.SeatView_exampleColor,
                mExampleColor);
        // Use getDimensionPixelSize or getDimensionPixelOffset when dealing with
        // values that should fall on pixel boundaries.
        mExampleDimension = a.getDimension(
                R.styleable.SeatView_exampleDimension,
                mExampleDimension);

        if (a.hasValue(R.styleable.SeatView_exampleDrawable)) {
            mExampleDrawable = a.getDrawable(
                    R.styleable.SeatView_exampleDrawable);
            mExampleDrawable.setCallback(this);
        }

//        a.recycle();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        // Update TextPaint and text measurements from attributes
        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(mExampleString);

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(14);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
//        int paddingLeft = getPaddingLeft();
//        int paddingTop = getPaddingTop();
//        int paddingRight = getPaddingRight();
//        int paddingBottom = getPaddingBottom();
//
//        int contentWidth = getWidth() - paddingLeft - paddingRight;
//        int contentHeight = getHeight() - paddingTop - paddingBottom;
//
//        // Draw the text.
//        canvas.drawText(mExampleString,
//                paddingLeft + (contentWidth - mTextWidth) / 2,
//                paddingTop + (contentHeight + mTextHeight) / 2,
//                mTextPaint);
//
//        // Draw the example drawable on top of the text.
//        if (mExampleDrawable != null) {
//            mExampleDrawable.setBounds(paddingLeft, paddingTop,
//                    paddingLeft + contentWidth, paddingTop + contentHeight);
//            mExampleDrawable.draw(canvas);
//        }

        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 11; j++){
               int top = i * verSpacing;
                int left = j * horspacing;
                if ( i == 0 && j > 0){
                    if (j == 1)
                        canvas.drawText("A",left,top,mTextPaint);
                    if (j == 2)
                        canvas.drawText("B",left,top,mTextPaint);
                    if (j == 3)
                        canvas.drawText("C",left,top,mTextPaint);
                    if (j == 4)
                        canvas.drawText("D",left,top,mTextPaint);
                    if (j == 5)
                        canvas.drawText("E",left,top,mTextPaint);
                    if (j == 6)
                        canvas.drawText("F",left,top,mTextPaint);
                    if (j == 7)
                        canvas.drawText("G",left,top,mTextPaint);
                    if (j == 8)
                        canvas.drawText("H",left,top,mTextPaint);
                    if (j == 9)
                        canvas.drawText("I",left,top,mTextPaint);
                    if (j == 10)
                        canvas.drawText("J",left,top,mTextPaint);
                }
                else if (j == 0 && i > 0){
                        canvas.drawText(i+"",left,top,mTextPaint);
                }
                else if (i > 0 && j > 0){

                    canvas.drawCircle(left,top, 2 ,mPaint);
                    }
                }
            }
    }

    /**
     * Gets the example string attribute value.
     *
     * @return The example string attribute value.
     */
    public String getExampleString() {
        return mExampleString;
    }

    /**
     * Sets the view's example string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param exampleString The example string attribute value to use.
     */
    public void setExampleString(String exampleString) {
        mExampleString = exampleString;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getExampleColor() {
        return mExampleColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param exampleColor The example color attribute value to use.
     */
    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example dimension attribute value.
     *
     * @return The example dimension attribute value.
     */
    public float getExampleDimension() {
        return mExampleDimension;
    }

    /**
     * Sets the view's example dimension attribute value. In the example view, this dimension
     * is the font size.
     *
     * @param exampleDimension The example dimension attribute value to use.
     */
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getExampleDrawable() {
        return mExampleDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setExampleDrawable(Drawable exampleDrawable) {
        mExampleDrawable = exampleDrawable;
    }
}
