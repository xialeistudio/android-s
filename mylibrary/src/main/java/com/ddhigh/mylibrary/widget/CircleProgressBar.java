package com.ddhigh.mylibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressBar extends View {
    private int maxProgress = 100;
    private int progress = 0;
    private String text;
    //画圆所在的距形区域
    RectF oval;
    Paint paint;

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        oval = new RectF();
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        paint.setAntiAlias(true); // 设置画笔为抗锯齿
        paint.setColor(Color.WHITE); // 设置画笔颜色
        canvas.drawColor(Color.TRANSPARENT); // 白色背景
        int progressStrokeWidth = 4;
        paint.setStrokeWidth(progressStrokeWidth); //线宽
        paint.setStyle(Paint.Style.STROKE);

        oval.left = progressStrokeWidth / 2; // 左上角x
        oval.top = progressStrokeWidth / 2; // 左上角y
        oval.right = width - progressStrokeWidth / 2; // 左下角x
        oval.bottom = height - progressStrokeWidth / 2; // 右下角y

        canvas.drawArc(oval, -90, 360, false, paint); // 绘制白色圆圈，即进度条背景
        paint.setColor(Color.rgb(0x57, 0x87, 0xb6));
        canvas.drawArc(oval, -90, ((float) progress / maxProgress) * 360, false, paint); // 绘制进度圆弧，这里是蓝色

        paint.setStrokeWidth(1);
        if (TextUtils.isEmpty(text)) {
            text = progress + "%";
        }
        Rect bounds = new Rect();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(40);
        paint.setStyle(Paint.Style.FILL);
        paint.getTextBounds(text,0,text.length(),bounds);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(text, getMeasuredWidth() / 2 - bounds.width() / 2, baseline, paint);
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public void setTextNotUIThread(String text) {
        this.text = text;
        postInvalidate();
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.invalidate();
    }

    /**
     * 非ＵＩ线程调用
     */
    public void setProgressNotInUIThread(int progress) {
        this.progress = progress;
        this.postInvalidate();
    }
}