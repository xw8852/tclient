package com.msx7.android.tclient.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.msx7.android.tclient.R;

/**
 * Created by Josn on 2015/12/19.
 */
public class CameraView extends ImageView {
    public CameraView(Context context) {
        super(context);
        init();
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    Bitmap logo;
    Rect src = null;
    RectF dst = new RectF();

    void init() {
        logo = BitmapFactory.decodeResource(getResources(), R.drawable.btn_camera_mobile);
        src = new Rect(0, 0, logo.getWidth(), logo.getHeight());
    }

    int width;
    int height;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        post(new Runnable() {
            @Override
            public void run() {
                x = width / 2.0f;
                y = height / 2.0f;
                dst.left = x - src.width() / 2;
                dst.top = y - src.height() / 2;
                dst.right = x + src.width() / 2;
                dst.bottom = y + src.height() / 2;
                invalidate();
            }
        });
    }

    float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                x = Math.min(width - src.width() / 2, Math.max(src.width() / 2, event.getX()));
                y = Math.min(height - src.height() / 2, Math.max(src.height() / 2, event.getY()));
                dst.left = x - src.width() / 2;
                dst.top = y - src.height() / 2;
                dst.right = x + src.width() / 2;
                dst.bottom = y + src.height() / 2;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (x == 0 && y == 0) {
            return;
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFF00C7EB);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        DashPathEffect effects = new DashPathEffect(new float[]{40, 40}, 1);
        paint.setPathEffect(effects);
        Path path = new Path();
        path.moveTo(0, y);
        path.lineTo(width, y);
        canvas.drawPath(path, paint);

        path.reset();
        path.moveTo(x, 0);
        path.lineTo(x, height);
        canvas.drawPath(path, paint);

        canvas.drawBitmap(logo, src, dst, paint);
    }
}

