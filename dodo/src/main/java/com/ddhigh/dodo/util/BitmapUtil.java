package com.ddhigh.dodo.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * @project Study
 * @package com.ddhigh.dodo.util
 * @user xialeistudio
 * @date 2016/3/1 0001
 */
public class BitmapUtil {
    /**
     * 缩放
     *
     * @param bitmap
     * @param scale
     * @return
     */
    public static Bitmap scale(Bitmap bitmap, float scale) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
