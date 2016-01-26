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
 * 文件名: CameraView.java
 * 描  述:
 * 作  者：Josn@憬承
 * 时  间：2016/1/25
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
        if (terminal == null) {
            terminal = new RectF(src.width() / 2, src.height() / 2, width - src.width() / 2, height - src.height() / 2);
        }
    }


    /**
     * 记录可以滑动的区域
     */
    RectF terminal;

    float x, y;

    int limited_left;
    int limited_top;
    int limited_right;
    int limited_bottom;
    IRotateLisenter lisenter;

    public IRotateLisenter getLisenter() {
        return lisenter;
    }

    public void setLisenter(IRotateLisenter lisenter) {
        this.lisenter = lisenter;
    }

    /**
     * left ~ right 应该是从负值到值  -90° ~ 90° 表示左右各旋转90°总计水平180°<br/>
     * bottom ~ top 应该是从负值到值  -80° ~ 90° 表示向下最大旋转80°，向上旋转90°总计垂直170°<br/>
     * -180°  ~ 180° 表示可以360°旋转<br/>
     *
     * @param left   左边极限值
     * @param top    上面极限角度
     * @param right  右边极限角度
     * @param bottom 下面极限角度
     */
    public void setLimited(int left, int top, int right, int bottom) {
        this.limited_left = left;
        this.limited_top = top;
        this.limited_right = right;
        this.limited_bottom = bottom;
    }

    float lastx = Integer.MAX_VALUE;
    float lasty = Integer.MAX_VALUE;
    float lastHor = Integer.MAX_VALUE;
    float lastVel = Integer.MAX_VALUE;

    long lastTime=-1;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTime=-1;break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                x = Math.min(terminal.right, Math.max(terminal.left, event.getX()));
                y = Math.min(terminal.bottom, Math.max(terminal.top, event.getY()));
                if (x == lastx && y == lasty)
                    break;
                dst.left = x - src.width() / 2;
                dst.top = y - src.height() / 2;
                dst.right = x + src.width() / 2;
                dst.bottom = y + src.height() / 2;

                if (lisenter != null ) {
                    //修正x,y 始其起始值为0
                    float _x = x - terminal.left;
                    float _y = y - terminal.top;
                    int hor = (int) ((limited_right - limited_left) * _x / terminal.width() + limited_left);
                    int vel = (int) ((limited_top - limited_bottom) * (terminal.height()-_y) / terminal.height() + limited_bottom);
                    if ((hor != lastHor || vel != lastVel) && (
                            event.getAction()==MotionEvent.ACTION_MOVE &&System.currentTimeMillis()-lastTime>100
                            )) {
                        lastTime=System.currentTimeMillis();
                        lastHor = hor;
                        lastVel = vel;
                        lisenter.rotate(hor, vel);
                    }
                }
                lastx = x;
                lasty = y;
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 获取当前水平角度
     *
     * @return
     */
    public int getHor() {
        float _x = x - terminal.left;
        return (int) ((limited_right - limited_left) * _x / terminal.width() + limited_left);
    }

    /**
     * 获取当前垂直角度
     *
     * @return
     */
    public int getVel() {
        float _y = y - terminal.top;
        return (int) ((limited_top - limited_bottom) * _y / terminal.height() + limited_bottom);
    }

    /**
     * 设置当前角度
     * @param hor 水平角度
     * @param vel 垂直角度
     */
    public void setRotate(int hor, int vel) {
        hor = Math.max(limited_left, Math.min(limited_right, hor));
        vel = Math.max(limited_bottom, Math.min(limited_top, vel));
         x = (hor - limited_left) * terminal.width() / (limited_right - limited_left);
        //  int vel = (int) ((limited_top - limited_bottom) * (terminal.height()-_y) / terminal.height() + limited_bottom);

         y = terminal.height()-(vel - limited_bottom) * terminal.height() / (limited_top - limited_bottom);
        x = x + terminal.left;
        y = y + terminal.top;
        lastHor = hor;
        lastVel = vel;
        dst.left = x - src.width() / 2;
        dst.top = y - src.height() / 2;
        dst.right = x + src.width() / 2;
        dst.bottom = y + src.height() / 2;
        invalidate();
    }

    public void reset() {
        x = width / 2.0f;
        y = height / 2.0f;
        dst.left = x - src.width() / 2;
        dst.top = y - src.height() / 2;
        dst.right = x + src.width() / 2;
        dst.bottom = y + src.height() / 2;
        //修正x,y 始其起始值为0
        float _x = x - terminal.left;
        float _y = y - terminal.top;
        int hor = (int) ((limited_right - limited_left) * _x / terminal.width() + limited_left);
        int vel = (int) ((limited_top - limited_bottom) * _y / terminal.height() + limited_bottom);
        lastHor = hor;
        lastVel = vel;
        if (lisenter != null) lisenter.rotate(hor, vel);
        invalidate();
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


    public interface IRotateLisenter {
        /**
         * @param x 水平角度
         * @param y 横向角度
         */
        void rotate(int x, int y);
    }
}

