package com.ddhigh.mylibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * @project android-s
 * @package com.ddhigh.mylibrary.widget
 * @user xialeistudio
 * @date 2016/3/8 0008
 */
public class SqaureLayout extends RelativeLayout {
    public SqaureLayout(Context context) {
        super(context);
    }

    public SqaureLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SqaureLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // want the layout to change as this happens.
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

        // Children are just made to fill our space.
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        //高度和宽度一样
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
