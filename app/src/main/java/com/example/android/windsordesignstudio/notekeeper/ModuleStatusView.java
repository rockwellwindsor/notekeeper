package com.example.android.windsordesignstudio.notekeeper;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import java.util.List;

/**
 * TODO: document your custom view class.
 */
public class ModuleStatusView extends View {
    public static final int INVALID_INDEX = -1;
    public static final float DEFAULT_OUTLINE_WIDTH_DP = 2f;
    private final int EDIT_MODE_MODULE_COUNT = 7;
    private String mExampleString; // TODO: use a default from R.string...
    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
    private Drawable mExampleDrawable;
    private float mOutlineWidth;
    private float mShapeSize;
    private float mSpacing;
    private Rect[] mModuleRectangle;
    private int mOutlineColor;
    private Paint mPaintOutline;
    private Paint mPaintFill;
    private int mFillColor;
    private float mRadius;
    private int mMaxHorizontalModules;
    private int SHAPE_CIRCLE;
    private int mShape;
    private ModuleStatusAccessibilityHelper mAccessibilityHelper;

    public boolean[] getModuleStatus() {
        return mModuleStatus;
    }

    public void setModuleStatus(boolean[] moduleStatus) {
        mModuleStatus = moduleStatus;
    }

    private boolean[] mModuleStatus;

    public ModuleStatusView(Context context) {
        super(context);
        init(null, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ModuleStatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredHeight = 0;
        int desiredWidth = 0;
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int availableWidth = specWidth - getPaddingLeft() - getPaddingRight();
        int horizontalModulesThatCanFit = (int) (availableWidth / (mShapeSize + mSpacing));
        mMaxHorizontalModules = Math.min(horizontalModulesThatCanFit, mModuleStatus.length); // If enough width available then it will contain length of module array, otherwise it will contain how many will fit

        desiredWidth = (int) ((mMaxHorizontalModules * (mShapeSize + mSpacing)) - mSpacing);
        desiredWidth += getPaddingLeft() + getPaddingRight(); // Account for padding on width

        int rows = ((mModuleStatus.length -1) / mMaxHorizontalModules) + 1; // Need to determine how many rows are required

        desiredHeight = (int) (rows * (mShapeSize + mSpacing));
        desiredHeight += getPaddingTop() + getPaddingBottom();

        int width = resolveSizeAndState(desiredWidth, widthMeasureSpec, 0);
        int height = resolveSizeAndState(desiredHeight, heightMeasureSpec, 0);

        // Inform system what measurements are
        setMeasuredDimension(width, height);
    }

    private void init(AttributeSet attrs, int defStyle) {

        if(isInEditMode()) {
            setupEditModeValues();
        }

        // Add accessibility support
        setFocusable(true);
        mAccessibilityHelper = new ModuleStatusAccessibilityHelper(this);
        ViewCompat.setAccessibilityDelegate(this, mAccessibilityHelper); // Indicates mAccessibilityHelper is providing the accessibility information for our custom view

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        float displayDensity = dm.density;
        float defaultOutlineWidthPixels = displayDensity * DEFAULT_OUTLINE_WIDTH_DP;

        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ModuleStatusView, defStyle, 0);

        mOutlineColor = a.getColor(R.styleable.ModuleStatusView_outlineColor, Color.BLACK);
        SHAPE_CIRCLE = 0;
        mShape = a.getInt(R.styleable.ModuleStatusView_shape, SHAPE_CIRCLE);
        mOutlineWidth = a.getDimension(R.styleable.ModuleStatusView_outlineWidth, defaultOutlineWidthPixels);

        a.recycle(); // Once this is called we cannot interact with the TypedArray anymore

        mShapeSize = 70f;
        mSpacing = 30f;
        mRadius = (mShapeSize - mOutlineWidth) / 2; // By moving this up to here we only do calculation once

        // Draw outline
        mPaintOutline = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintOutline.setStyle(Paint.Style.STROKE);
        mPaintOutline.setStrokeWidth(mOutlineWidth);
        mPaintOutline.setColor(mOutlineColor);

        // Fill in circle
        mFillColor = getContext().getResources().getColor(R.color.pluralsightOrange);
        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(mFillColor);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        mAccessibilityHelper.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mAccessibilityHelper.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        return mAccessibilityHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event);
    }

    private void setupEditModeValues() {
        boolean[] exampleModuleValues = new boolean[EDIT_MODE_MODULE_COUNT];
        int middle = EDIT_MODE_MODULE_COUNT / 2;

        for(int i = 0; i < middle; i++) {
            exampleModuleValues[i] = true;
        }

        setModuleStatus(exampleModuleValues);
    }

    private void setupModuleRectangles(int width) {

        int availableWidth = width - getPaddingLeft() - getPaddingRight();
        int horizontalModulesThatCanFit = (int) (availableWidth / (mShapeSize + mSpacing));
        int maxHorizontalModules = Math.min(horizontalModulesThatCanFit, mModuleStatus.length);
        mModuleRectangle = new Rect[mModuleStatus.length];

        for(int moduleIndex = 0; moduleIndex < mModuleRectangle.length; moduleIndex++ ) {
            int column = moduleIndex % maxHorizontalModules;
            int row = moduleIndex / maxHorizontalModules;
            int x = getPaddingLeft() + (int) (column * (mShapeSize + mSpacing)); // contains position of left edge of rectangle
            int y = getPaddingTop() + (int) (row * (mShapeSize + mSpacing));     // top. getPaddingTop applies value of the paddingTop value in the views XML file
            mModuleRectangle[moduleIndex] = new Rect(x, y, x + (int) mShapeSize, y + (int) mShapeSize);
        }
    }

    private void invalidateTextPaintAndMeasurements() {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        setupModuleRectangles(w); // Setup rectangles when system says the view size has changed
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int moduleIndex=0; moduleIndex < mModuleRectangle.length; moduleIndex++) {
            if(mShape == SHAPE_CIRCLE) {
                float x = mModuleRectangle[moduleIndex].centerX();
                float y = mModuleRectangle[moduleIndex].centerY();
                if (mModuleStatus[moduleIndex])
                    canvas.drawCircle(x, y, mRadius, mPaintFill);
                canvas.drawCircle(x, y, mRadius, mPaintOutline);
            } else {
                drawSquare(canvas, moduleIndex);
            }
        }
    }

    private void drawSquare(Canvas canvas, int moduleIndex) {
        Rect moduleRectangle = mModuleRectangle[moduleIndex];

        if(mModuleStatus[moduleIndex])
            canvas.drawRect(moduleRectangle, mPaintFill);

        canvas.drawRect(moduleRectangle.left + (mOutlineWidth/2),
                moduleRectangle.top + (mOutlineWidth/2),
                moduleRectangle.right - (mOutlineWidth/2),
                moduleRectangle.bottom - (mOutlineWidth/2),
                mPaintOutline);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                int moduleIndex = findItemAtPoint(event.getX(), event.getY());
                onModuleSelected(moduleIndex);
                return true;

        }
        return super.onTouchEvent(event);
    }

    private void onModuleSelected(int moduleIndex) {
        if(moduleIndex == INVALID_INDEX) {
            return;
        }
        mModuleStatus[moduleIndex] = ! mModuleStatus[moduleIndex];
        invalidate();
        mAccessibilityHelper.invalidateVirtualView(moduleIndex);
        mAccessibilityHelper.sendEventForVirtualView(moduleIndex, AccessibilityEvent.TYPE_VIEW_CLICKED);
    }

    private int findItemAtPoint(float x, float y) {
        int moduleIndex = INVALID_INDEX;
        for(int i = 0; i < mModuleRectangle.length; i++) {
            if(mModuleRectangle[i].contains((int) x, (int) y)) {
                moduleIndex = i;
                break;
            }
        }
        return moduleIndex;
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

    private class ModuleStatusAccessibilityHelper extends ExploreByTouchHelper {
        /**
         * Constructs a new helper that can expose a virtual view hierarchy for the
         * specified host view.
         *
         * @param host view whose virtual view hierarchy is exposed by this helper
         */
        public ModuleStatusAccessibilityHelper(View host) {
            super(host);
        }

        @Override
        protected int getVirtualViewAt(float x, float y) {
            int moduleIndex = findItemAtPoint(x, y);

            return moduleIndex == INVALID_INDEX ? ExploreByTouchHelper.INVALID_ID : moduleIndex;
        }

        @Override
        protected void getVisibleVirtualViews(List<Integer> virtualViewIds) {
            // check first
            if(mModuleRectangle == null) {
                return;
            }

            // Add ids to the elements in the custom view
            for(int moduleIndex = 0; moduleIndex < mModuleRectangle.length; moduleIndex++) {
                virtualViewIds.add(moduleIndex);
            }
        }

        @Override
        protected void onPopulateNodeForVirtualView(int virtualViewId, AccessibilityNodeInfoCompat node) {
            node.setFocusable(true);
            node.setBoundsInParent(mModuleRectangle[virtualViewId]);
            node.setContentDescription("Module " + virtualViewId + 1); // Data for the screen reader
            node.setCheckable(true);
            node.setChecked(mModuleStatus[virtualViewId]);

            // Add d pad click support
            node.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK);

        }

        @Override
        protected boolean onPerformActionForVirtualView(int virtualViewId, int action, Bundle arguments) {
            switch(action) {
                case AccessibilityNodeInfoCompat.ACTION_CLICK:
                    onModuleSelected(virtualViewId);
                    return true;
            }
            return false;
        }
    }
}
