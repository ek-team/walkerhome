package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;

import com.github.anastr.speedviewlib.components.Indicators.NormalIndicator;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class SpeedView extends Speedometer {

    private Path markPath = new Path();
    private Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedometerPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF();
    private Matrix mMatrix;

    public SpeedView(Context context) {
        this(context, null);
    }

    public SpeedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultGaugeValues() {

    }

    @Override
    protected void defaultSpeedometerValues() {
        super.setIndicator(new NormalIndicator(getContext()));
        super.setBackgroundCircleColor(0);
    }

    private void init() {
        speedometerPaint.setStyle(Paint.Style.STROKE);
        markPaint.setStyle(Paint.Style.STROKE);
        circlePaint.setColor(0xFF444444);
        mMatrix = new Matrix();
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SpeedView, 0, 0);

        circlePaint.setColor(a.getColor(R.styleable.SpeedView_sv_centerCircleColor, circlePaint.getColor()));
        a.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        updateBackgroundBitmap();
    }

    private void initDraw() {
        speedometerPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawSpeedUnitText(canvas);

        drawIndicator(canvas);
        canvas.drawCircle(getSize() * .5f, getSize() * .5f, getWidthPa() / 12f, circlePaint);

        drawNotes(canvas);
    }

    @Override
    protected void updateBackgroundBitmap() {
        Canvas c = createBackgroundBitmapCanvas();
        initDraw();

        float markH = getViewSizePa() / 28f;
        markPath.reset();
        markPath.moveTo(getSize() * .5f, getPadding());
        markPath.lineTo(getSize() * .5f, markH + getPadding());
        markPaint.setStrokeWidth(markH / 3f);
        float risk = getSpeedometerWidth() * .5f + getPadding();
        speedometerRect.set(risk, risk, getSize() - risk, getSize() - risk);

        speedometerPaint.setColor(getHighSpeedColor());
        c.drawArc(speedometerRect, getStartDegree(), getEndDegree()- getStartDegree(), false, speedometerPaint);
        speedometerPaint.setColor(getMediumSpeedColor());
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree()- getStartDegree())*getMediumSpeedOffset(), false, speedometerPaint);
        speedometerPaint.setColor(getLowSpeedColor());
        c.drawArc(speedometerRect, getStartDegree()
                , (getEndDegree()- getStartDegree())*getLowSpeedOffset(), false, speedometerPaint);

//        int x = getWidth() / 2;
//        int y = getHeight() / 2;
//        mMatrix.setRotate(140, x, y);
//        int[] colors = {Color.parseColor("#ffa800"), Color.GREEN, Color.RED};
//        float[] positions = {0, 0.4f, 0.6f};
//        speedometerPaint.setStrokeCap(Paint.Cap.ROUND);//让弧线两边是圆滑的
//        SweepGradient gradient = new SweepGradient(300, 300, colors , positions);
//        gradient.setLocalMatrix(mMatrix);
//        speedometerPaint.setShader(gradient);
//        c.drawArc(speedometerRect, getStartDegree()
//                , getEndDegree()- getStartDegree(), false, speedometerPaint);

        c.save();
        c.rotate(90f + getStartDegree(), getSize() * .5f, getSize() * .5f);
        float everyDegree = (getEndDegree() - getStartDegree()) * .1f;
        for (float i = getStartDegree(); i < getEndDegree() - (2f * everyDegree) + 1; i += everyDegree) {
            c.rotate(everyDegree, getSize() * .5f, getSize() * .5f);
            c.drawPath(markPath, markPaint);
        }
        c.restore();

        if (getTickNumber() > 0)
            drawTicks(c);
        else
            drawDefMinMaxSpeedPosition(c);
    }

    public int getCenterCircleColor() {
        return circlePaint.getColor();
    }

    /**
     * change the color of the center circle (if exist),
     * <b>this option is not available for all Speedometers</b>.
     *
     * @param centerCircleColor new color.
     */
    public void setCenterCircleColor(int centerCircleColor) {
        circlePaint.setColor(centerCircleColor);
        if (!isAttachedToWindow())
            return;
        invalidate();
    }
}
