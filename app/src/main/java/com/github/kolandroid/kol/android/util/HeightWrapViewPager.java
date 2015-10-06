package com.github.kolandroid.kol.android.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.github.kolandroid.kol.util.Logger;

public class HeightWrapViewPager extends ViewPager {
    public HeightWrapViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeightWrapViewPager(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount() == 0) {
            Logger.log("HeightWrapViewPager", "No children found to measure...");
            setMeasuredDimension(getMeasuredWidth(), measureHeight(heightMeasureSpec, 0));
        } else {
            View child = getChildAt(0); //just measure the first child for now
            if (child.getVisibility() != GONE) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec),
                        MeasureSpec.UNSPECIFIED);
                child.measure(widthMeasureSpec, heightMeasureSpec);
                setMeasuredDimension(getMeasuredWidth(), measureHeight(heightMeasureSpec, child.getMeasuredHeight()));
            } else {
                setMeasuredDimension(getMeasuredWidth(), measureHeight(heightMeasureSpec, 0));
            }
        }
    }


    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @param childHeight the base view with already measured height
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec, int childHeight) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            // set the height from the base view if available
            result = childHeight;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

}
