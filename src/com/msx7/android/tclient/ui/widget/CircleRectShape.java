package com.msx7.android.tclient.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.msx7.android.tclient.R;

import java.util.Arrays;


/**
 * Created by Josn on 2015/10/31.
 */
public class CircleRectShape extends View {

    public CircleRectShape(Context context) {
        super(context);
        init();
    }

    private float startAngle = 120;
    private float AngleLength = 120;
    private float _angle = 0;


    //    CircleAngleAnimation animation;
    Drawable drawable;
    int drawableHeight;

    public CircleRectShape(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleRectShape);
        int duration = typedArray.getInt(R.styleable.CircleRectShape_CircleAnimDuration, 0);
        startAngle = typedArray.getInt(R.styleable.CircleRectShape_CircleStartAngle, 120);
        AngleLength = typedArray.getInt(R.styleable.CircleRectShape_AngleLength, 0);

        drawable = typedArray.getDrawable(R.styleable.CircleRectShape_CircleDrawable);
        drawableHeight = typedArray.getDimensionPixelSize(R.styleable.CircleRectShape_DrawableHeight, 40);
        ArcWidth = typedArray.getDimensionPixelSize(R.styleable.CircleRectShape_ArcWidth, ArcWidth);
        init();


    }

    public CircleRectShape(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleRectShape);
        int duration = typedArray.getInt(R.styleable.CircleRectShape_CircleAnimDuration, 0);
        startAngle = typedArray.getInt(R.styleable.CircleRectShape_CircleStartAngle, 150);
        AngleLength = typedArray.getInt(R.styleable.CircleRectShape_AngleLength, 240);
        drawable = typedArray.getDrawable(R.styleable.CircleRectShape_CircleDrawable);
        drawableHeight = typedArray.getDimensionPixelSize(R.styleable.CircleRectShape_DrawableHeight, 40);
        init();

    }

    /**
     * 圆弧宽度
     */
    int ArcWidth = 80;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(drawableHeight + ArcWidth + getPaddingLeft() + getPaddingRight(),
                drawableHeight + ArcWidth + getPaddingTop() + getPaddingBottom());

    }

    float lastY;
    float lastX;
    PointF dst;
    PointF center;
    PointF dividerPoint;
    float dividerAngle;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (dst == null) {
                    /**
                     * 分割点,求最上方中点位置,
                     * 默认起始点:最右边中点位置
                     * 利用matrix旋转,求出对应旋转startAngle之后的起始点
                     */
                    PointF _point = new PointF(getRight() - getLeft(), (getBottom() - getTop()) / 2);
                    Matrix matrix = new Matrix();
                    center = new PointF((getRight() - getLeft()) / 2, (getBottom() - getTop()) / 2);
                    matrix.postRotate(startAngle, center.x, center.y);
                    float[] dsts = new float[2];
                    matrix.mapPoints(dsts, new float[]{_point.x, _point.y});
                    dst = new PointF(dsts[0], dsts[1]);
                    dividerPoint = new PointF((getRight() - getLeft()) / 2, 0);
                    dividerAngle = (float) (Math.acos(getCosA(dst, center, dividerPoint)) * 180 / Math.PI);
                }
                lastX = event.getX();
                lastY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                /**
                 * 在分割点，左边的，直接求值，
                 * 在分割点右边的分2部分求值
                 */
                PointF point = new PointF(event.getX(), event.getY());
                if (point.y > dst.y) {
                    if (point.y - dst.y < getResources().getDisplayMetrics().density * 4) {
                        if (point.x < dividerPoint.x)
                            _angle = 0;
                        else if (point.x > dividerPoint.x) _angle = AngleLength;
                        invalidate();
                    }

                } else if (point.x > dividerPoint.x) {
                    float cosa = getCosA(dividerPoint, center, point);
                    _angle = dividerAngle + (float) (Math.acos(cosa) * 180 / Math.PI);
                } else {
                    float cosa = getCosA(dst, center, point);
                    _angle = (float) (Math.acos(cosa) * 180 / Math.PI);
                }
                if (event.getAction() == MotionEvent.ACTION_UP)
                    dispatchAngleListener();
                invalidate();

                break;
        }

        return true;
    }

    int lastAngle = -1;

    private void dispatchAngleListener() {
        int angle = (int) (_angle * 100 / AngleLength);
        Log.d("Angle", "------ _angle  " + _angle);
        /**
         * 某些不知原因的特殊状况会导致angle超过上限100，特此在做修正
         */
        angle = Math.max(0, Math.min(100, angle));
        if (angle == lastAngle) return;
        lastAngle = angle;
        Log.d("Angle", "angle  " + angle);
        if (listener != null) listener.doAngle(angle);
    }

    AngleAnimation angleAnimation;

    /**
     * 设置当前的角度 0~100
     *
     * @param angle angle 小于 0 则以0计算，angle大于100 则以100计算
     */
    public void setAngle(int angle) {
        // 修正设置的角度，防止设置的角度超出上限
        angle = Math.max(0, Math.min(100, angle));
        lastAngle = angle;
        if (angleAnimation != null) angleAnimation.cancel();
        angleAnimation = new AngleAnimation(_angle, angle * AngleLength / 100);
        startAnimation(angleAnimation);
//        _angle = angle * AngleLength / 100;
        invalidate();
    }

    class AngleAnimation extends Animation {
        float startAngle;
        float endAngle;

        public AngleAnimation(float startAngle, float endAngle) {
            this.startAngle = startAngle;
            this.endAngle = endAngle;
            setDuration(400);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            _angle = interpolatedTime * (endAngle - startAngle) + startAngle;
            invalidate();
        }
    }

    /**
     * @return 当前旋转的角度 0~100
     */
    public int getAngle() {
        if (lastAngle == 0) {
            lastAngle = (int) (_angle * 100 / AngleLength);
        }
        return Math.max(0, lastAngle);
    }

    IAngleListener listener;

    public IAngleListener getListener() {
        return listener;
    }

    public void setListener(IAngleListener listener) {
        this.listener = listener;
    }

    /**
     * p1            p3
     * <p/>
     * a
     * <p/>
     * p2
     * 求p1p2与p2p3之间余弦夹角的值
     */
    public float getCosA(PointF p1, PointF p2, PointF p3) {
        float lena = getLenth(p1, p2);
        float lenb = getLenth(p2, p3);
        float lenc = getLenth(p1, p3);
        return (lena * lena + lenb * lenb - lenc * lenc) / (2 * lena * lenb);
    }

    public float getLenth(PointF p1, PointF p2) {
        return (float) Math.hypot(p1.x - p2.x, p1.y - p2.y);
    }


    private Paint paint;
    private RectF rect;
    float dlen;
    Bitmap light;
    Bitmap drak;
    int cellWidth;
    int cellHeght;
    Rect src = null;

    void init() {
        dlen = getResources().getDisplayMetrics().density;
        final int strokeWidth = 20;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        //Circle color
        paint.setColor(Color.RED);

        Path circle = new Path();
        circle.addCircle(0, 0, strokeWidth / 2, Path.Direction.CW);

        paint.setPathEffect(new PathDashPathEffect(circle, strokeWidth * 2, strokeWidth * 2, PathDashPathEffect.Style.ROTATE));
        //size 200x200 example
        light = BitmapFactory.decodeResource(getResources(), R.drawable.shuang_vol_4);
        drak = BitmapFactory.decodeResource(getResources(), R.drawable.shuang_vol_5);
        cellWidth = light.getWidth() / 2;
        cellHeght = light.getHeight() / 2;
        src = new Rect(0, 0, light.getWidth(), light.getHeight());
    }

    //一个弧度的宽度
    float arcWidth;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rect = new RectF(
                getPaddingLeft() + ArcWidth / 2,
                getPaddingTop() + ArcWidth / 2,
                getMeasuredWidth() - getPaddingRight() - ArcWidth / 2,
                getMeasuredHeight() - getPaddingBottom() - ArcWidth / 2);
        if (arcWidth == 0) {
            PointF tmp1 = new PointF(rect.right, rect.top + rect.height() / 2);
            PointF tmp = getRotatePoint(1, tmp1, new PointF(rect.centerX(), rect.centerY()));
            arcWidth = (float) Math.hypot(tmp1.x - tmp.x, tmp1.y - tmp.y);
        }
        Paint _paint = new Paint();
        _paint.setAntiAlias(true);
        _paint.setStyle(Paint.Style.STROKE);
        _paint.setStrokeWidth(20);
        _paint.setColor(Color.BLUE);
        _paint.setFilterBitmap(true);
//        if (dividerPoint != null)
//            canvas.drawPoint(dividerPoint.x + getLeft(), dividerPoint.y + getTop(), _paint);
//        if (center != null)
//            canvas.drawPoint(center.x + getLeft(), center.y + getTop(), _paint);

        drawGreyArc(canvas);
        if (_angle <= 0) _angle = 0;
        _angle = Math.min(_angle, AngleLength);
        drawLightArc(canvas, _angle);
        drawDrawable(canvas, _angle);
    }

    void drawDrawable(Canvas canvas, float angle) {
        if (drawable != null) {
            drawable.setBounds(0, 0, drawableHeight, drawableHeight);
            canvas.rotate(startAngle + 90 + angle, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
            canvas.translate(ArcWidth / 2 + getPaddingLeft(), ArcWidth / 2 + getPaddingTop());
            drawable.draw(canvas);
        }
    }

    /**
     * 画默认灰色刻度
     *
     * @param canvas
     */
    void drawGreyArc(Canvas canvas) {
//        paint.setColor(Color.GRAY);
//        canvas.drawArc(rect, startAngle, AngleLength, false, paint);


        float _angle = (1.5f * cellWidth / arcWidth);
        for (float i = startAngle; i < startAngle + AngleLength; i += _angle) {
            PointF _start = getRotatePoint(i, new PointF(rect.right, rect.top + rect.height() / 2),
                    new PointF(rect.centerX(), rect.centerY()));
            Rect dst = new Rect((int) _start.x - cellWidth,
                    (int) _start.y - cellHeght,
                    (int) _start.x + cellWidth,
                    (int) _start.y + cellHeght);
            canvas.drawBitmap(drak, src, dst, paint);
        }


    }

    int[] colors = new int[]{
            0xFF00C7EB,
            0x1000C7EB,
            0xFF00C7EB
    };
    float[] dirs = new float[]{
            0.1f, 0.2f, 1.0f
    };


    /**
     * 画旋转刻度
     *
     * @return
     */
    void drawLightArc(Canvas canvas, float angle) {
        if (_angle <= 0) return;
        paint.setColor(Color.RED);
        RectF _rect = new RectF(rect.left - ArcWidth * 0.3f,
                rect.top - ArcWidth * 0.3f,
                rect.right + ArcWidth * 0.3f,
                rect.bottom + ArcWidth * 0.3f);

        Paint _paint = new Paint();
        _paint.setAntiAlias(true);
        _paint.setStyle(Paint.Style.STROKE);
        _paint.setColor(Color.BLUE);
        _paint.setFilterBitmap(true);
        _paint.setStrokeWidth(ArcWidth * 0.2f);
        Path circle = new Path();
        circle.addCircle(0, 0, ArcWidth * 0.1f, Path.Direction.CCW);
        _paint.setPathEffect(new PathDashPathEffect(circle, 2, 1, PathDashPathEffect.Style.ROTATE));
        if (startAngle + angle < 360) {
            dirs[dirs.length - 1] = (startAngle + angle) / 360.0f;
        } else {
            dirs[dirs.length - 1] = 1.0f;
        }
        SweepGradient gradient = new SweepGradient(rect.centerX(), rect.centerY(), colors, dirs);
        _paint.setShader(gradient);

        canvas.drawArc(_rect, startAngle, angle, false, _paint);

        float _angle = (1.5f * cellWidth / arcWidth);
        for (float i = startAngle; i < startAngle + angle; i += _angle) {
            PointF _start = getRotatePoint(i, new PointF(rect.right, rect.top + rect.height() / 2),
                    new PointF(rect.centerX(), rect.centerY()));
            Rect dst = new Rect((int) _start.x - cellWidth,
                    (int) _start.y - cellHeght,
                    (int) _start.x + cellWidth,
                    (int) _start.y + cellHeght);
            canvas.drawBitmap(light, src, dst, paint);
        }

    }

    /**
     * @param angle  旋转的度数
     * @param pointF 原始的点
     * @param center 圆心点
     * @return 原始点绕圆心点，旋转angle度之后的点
     */
    public PointF getRotatePoint(float angle, PointF pointF, PointF center) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle, center.x, center.y);
        float[] dsts = new float[2];
        matrix.mapPoints(dsts, new float[]{pointF.x, pointF.y});
        return new PointF(dsts[0], dsts[1]);
    }


    public interface IAngleListener {
        void doAngle(int angle);
    }
}
