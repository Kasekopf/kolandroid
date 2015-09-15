package com.github.kolandroid.kol.android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;

import com.github.kolandroid.kol.android.R;

public class ProgressBar extends TextView {
    private String text = "";
    private float progress = 0f;

    private Paint backPaint;
    private Paint barPaint;
    private Paint outlinePaint;

    public ProgressBar(Context context) {
        super(context);
        init(null, 0);
    }

    public ProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ProgressBar, defStyle, 0);

        int backColor = a.getColor(R.styleable.ProgressBar_emptyColor, Color.WHITE);
        int barColor = a.getColor(R.styleable.ProgressBar_fullColor, Color.LTGRAY);
        int outlineColor = a.getColor(R.styleable.ProgressBar_outlineColor, Color.BLACK);

        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backPaint.setStyle(Paint.Style.FILL);
        backPaint.setColor(backColor);

        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setStyle(Paint.Style.FILL);
        barPaint.setColor(barColor);

        outlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setColor(outlineColor);
        outlinePaint.setStrokeWidth(1);

        a.recycle();

        this.setProgress(0, 1);
    }

    public void setProgress(int val, int max) {
        text = val + " / " + max;
        progress = val / (float) max;

        if (progress < 0) progress = 0;
        if (progress > 1) progress = 1;

        this.setText("   " + text + "   ");
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        //super.onDraw(canvas);

        Rect r = canvas.getClipBounds();

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();


        int contentWidth = (r.right - r.left) - paddingLeft;
        int barPaddingRight = paddingRight + (int) (contentWidth * (1 - progress));

        canvas.drawRect(r.left + paddingLeft, r.top + paddingTop, r.right - paddingRight, r.bottom - paddingBottom, backPaint);
        canvas.drawRect(r.left + paddingLeft, r.top + paddingTop, r.right - barPaddingRight, r.bottom - paddingBottom, barPaint);
        canvas.drawRect(r.left + paddingLeft, r.top + paddingTop, r.right - paddingRight, r.bottom - paddingBottom, outlinePaint);

        super.onDraw(canvas);
    }

}
