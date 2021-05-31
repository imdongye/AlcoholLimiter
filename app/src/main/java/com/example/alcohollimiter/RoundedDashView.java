package com.example.alcohollimiter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class RoundedDashView extends View {
    public enum Orientation {
        VERTICAL,
        HORIZONTAL
    }

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path path = new Path();
    private Orientation orientation = Orientation.VERTICAL;

    public RoundedDashView(Context context) {
        super(context);
        init();
    }

    public RoundedDashView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedDashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RoundedDashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(6);
        paint.setColor(getResources().getColor(R.color.lim_gray_d));
        paint.setPathEffect(new DashPathEffect(new float[]{20, 25}, 20));
    }
    @Override
    public void onWindowFocusChanged(boolean a){
        //Log.i("lim", String.format("rounded dash board w h : %d %d", getWidth(), getHeight()));
        setOrientation((getWidth()>getHeight()) ? Orientation.HORIZONTAL : Orientation.VERTICAL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();

        if (orientation == Orientation.VERTICAL) {
            path.moveTo(getWidth() / 2, 0);
            path.quadTo(getWidth() / 2, getHeight() / 2, getWidth() / 2, getHeight()-10);
        } else {
            path.moveTo(0, getHeight() / 2);
            path.quadTo(getWidth() / 2, getHeight() / 2, getWidth()-20, getHeight() / 2);
        }
        canvas.drawPath(path, paint);
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        invalidate();
    }
}