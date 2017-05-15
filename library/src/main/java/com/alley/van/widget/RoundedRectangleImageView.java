package com.alley.van.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class RoundedRectangleImageView extends AppCompatImageView {

    private static float radius; // dp

    public RoundedRectangleImageView(Context context) {
        super(context);
        init(context);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RoundedRectangleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        radius = 2.0f * density;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path roundedRectPath = new Path();
        RectF rect = new RectF(0.0f, 0.0f, getWidth(), getHeight());
        roundedRectPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(roundedRectPath);
        super.onDraw(canvas);
    }
}
