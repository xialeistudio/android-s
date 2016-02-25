package com.ddhigh.testview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * @project Study
 * @package com.ddhigh.testview.widget
 * @user xialeistudio
 * @date 2016/2/25 0025
 */
public class CircleProgressView extends View {
    private int max = 100;
    private int progress = 0;

    RectF oval;
    Paint paint;


    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);

        oval = new RectF();
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        paint.setAntiAlias(true);//画笔抗锯齿
        paint.setColor(Color.WHITE);//画笔颜色
        canvas.drawColor(Color.TRANSPARENT);//透明背景
        int progressStrokeWidth = 4;
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);

        oval.left = progressStrokeWidth / 2;//左上角x
        oval.top = progressStrokeWidth / 2;//左上角y
        oval.right = width - progressStrokeWidth / 2;//右下角x
        oval.bottom = height - progressStrokeWidth / 2;//右下角y

        //白色圆圈北京

        canvas.drawArc(oval, -90, 360, false, paint);
        paint.setColor(Color.rgb(0x57, 0x87, 0xb6));
        canvas.drawArc(oval, -90, 360 * ((float) progress / max), false, paint);

        paint.setStrokeWidth(1);
        String text = progress + "%";
        int textHeight = height / 4;
        paint.setTextSize(textHeight);
        int textWidth = (int) paint.measureText(text, 0, text.length());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, width / 2 - textWidth / 2, height / 2 + textHeight / 2, paint);
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public void setProgressNotInUiThread(int progress) {
        this.progress = progress;
        this.postInvalidate();
    }
}
