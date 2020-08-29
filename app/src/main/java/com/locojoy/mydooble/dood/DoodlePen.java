package com.locojoy.mydooble.dood;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.locojoy.mydooble.DoodleView;
import com.locojoy.mydooble.core.IDoodle;
import com.locojoy.mydooble.core.IDoodleItem;
import com.locojoy.mydooble.core.IDoodlePen;


/**
 * 常用画笔
 */
public enum DoodlePen implements IDoodlePen {

    BRUSH, // 画笔
    COPY, // 仿制
    ERASER, // 橡皮擦
    TEXT, // 文本
    BITMAP, // 贴图
    MOSAIC; // 马赛克

    private CopyLocation mCopyLocation;

    @Override
    public void config(IDoodleItem item, Paint paint) {
        if (this == DoodlePen.COPY || this == DoodlePen.ERASER) {
            IDoodle doodle = item.getDoodle();
            if ((item.getColor() instanceof DoodleColor)
                    && ((DoodleColor) item.getColor()).getBitmap() == doodle.getBitmap()) {
                // nothing
            } else {
                item.setColor(new DoodleColor(doodle.getBitmap()));
            }
        }
    }

    public CopyLocation getCopyLocation() {
        if (this != COPY) {
            return null;
        }
        if (mCopyLocation == null) {
            synchronized (this) {
                if (mCopyLocation == null) {
                    mCopyLocation = new CopyLocation();
                }
            }
        }
        return mCopyLocation;
    }

    @Override
    public IDoodlePen copy() {
        return this;
    }

    @Override
    public void drawHelpers(Canvas canvas, IDoodle doodle) {
        if (this == COPY) {
            if (doodle instanceof DoodleView && !((DoodleView) doodle).isEditMode()) {
                mCopyLocation.drawItSelf(canvas, doodle.getSize());
            }
        }
    }
}
