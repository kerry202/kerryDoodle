package com.locojoy.mydooble;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author: kerry
 * date: On $ {DATE}
 */
public class DiyHelpView extends View {
    private Bitmap bitmap;

    public DiyHelpView(Context context) {
        super(context);
    }

    public DiyHelpView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DiyHelpView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float scale;
    private float left;
    private float top;

    public void setScale(float scale, float left, float top) {
        this.scale = scale;
        this.left = left;
        this.top = top;
        postInvalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.translate(left, top);
        canvas.scale(scale, scale);

        canvas.drawBitmap(bitmap, 0, 0, null);

    }


    public void initParameters(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


}
