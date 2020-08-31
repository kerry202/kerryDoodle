package com.locojoy.mydooble;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.locojoy.mydooble.dood.core.DiyIDoodle;
import com.locojoy.mydooble.dood.core.DiyIDoodleColor;
import com.locojoy.mydooble.dood.core.DiyIDoodleItemListener;
import com.locojoy.mydooble.dood.core.DiyIDoodleListener;
import com.locojoy.mydooble.dood.core.DiyIDoodlePen;
import com.locojoy.mydooble.dood.core.DiyIDoodleSelectableItem;
import com.locojoy.mydooble.dood.core.DiyIDoodleShape;
import com.locojoy.mydooble.dood.core.DiyIDoodleTouchDetector;
import com.locojoy.mydooble.dood.DiyDoodleColor;
import com.locojoy.mydooble.dood.DiyDoodleOnTouchGestureListener;
import com.locojoy.mydooble.dood.DiyDoodleParams;
import com.locojoy.mydooble.dood.DiyDoodlePen;
import com.locojoy.mydooble.dood.DiyDoodleShape;
import com.locojoy.mydooble.dood.DiyDoodleTouchDetector;
import com.locojoy.mydooble.dood.DiyDoodleView;
import com.locojoy.mydooble.utils.utils.ImageUtils;
import com.locojoy.mydooble.utils.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * @author: kerry
 * date: On $ {DATE}
 */
public class DrawActivity extends Activity {


    public static final int RESULT_ERROR = -111;

    public static final String KEY_PARAMS = "key_doodle_params";
    public static final String KEY_IMAGE_PATH = "key_image_path";


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    private DiyDoodleParams mDoodleParams;

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        mDoodleParams = savedInstanceState.getParcelable(KEY_PARAMS);
    }

    private DiyDoodleOnTouchGestureListener mTouchGestureListener;
    private String mImagePath;
    private FrameLayout mFrameLayout;
    private DiyIDoodle mDoodle;
    private DiyDoodleView mDoodleView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.doodle_layout);


        mFrameLayout = findViewById(R.id.drawControl);


        mDoodleParams = getIntent().getParcelableExtra("key_doodle_params");


        mImagePath = mDoodleParams.mImagePath;

        if (mDoodleParams.mIsFullScreen) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }


        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tops);
        mDoodleView = new DoodleViewWrapper(this, bitmap, mDoodleParams.mOptimizeDrawing, new DiyIDoodleListener() {
            @Override
            public void onSaved(DiyIDoodle doodle, Bitmap bitmap, Runnable callback) { // 保存图片为jpg格式
                File doodleFile = null;
                File file = null;
                String savePath = mDoodleParams.mSavePath;
                boolean isDir = mDoodleParams.mSavePathIsDir;
                if (TextUtils.isEmpty(savePath)) {
                    File dcimFile = new File(Environment.getExternalStorageDirectory(), "DCIM");
                    doodleFile = new File(dcimFile, "Doodle");
                    //　保存的路径
                    file = new File(doodleFile, System.currentTimeMillis() + ".jpg");
                } else {
                    if (isDir) {
                        doodleFile = new File(savePath);
                        //　保存的路径
                        file = new File(doodleFile, System.currentTimeMillis() + ".jpg");
                    } else {
                        file = new File(savePath);
                        doodleFile = file.getParentFile();
                    }
                }
                doodleFile.mkdirs();

                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
                    ImageUtils.addImage(getContentResolver(), file.getAbsolutePath());
                    Intent intent = new Intent();
                    intent.putExtra(KEY_IMAGE_PATH, file.getAbsolutePath());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    onError(DiyDoodleView.ERROR_SAVE, e.getMessage());
                } finally {
                    Util.closeQuietly(outputStream);
                }
            }

            public void onError(int i, String msg) {
                setResult(RESULT_ERROR);
                finish();
            }

            @Override
            public void onReady(DiyIDoodle doodle) {

                float size = mDoodleParams.mPaintUnitSize > 0 ? mDoodleParams.mPaintUnitSize * mDoodle.getUnitSize() : 0;
                if (size <= 0) {
                    size = mDoodleParams.mPaintPixelSize > 0 ? mDoodleParams.mPaintPixelSize : mDoodle.getSize();
                }

                // 设置初始值
                mDoodle.setSize(size);
                // 选择画笔
                mDoodle.setPen(DiyDoodlePen.BRUSH);
                mDoodle.setShape(DiyDoodleShape.HAND_WRITE);
                mDoodle.setColor(new DiyDoodleColor(mDoodleParams.mPaintColor));

                mDoodle.setZoomerScale(mDoodleParams.mZoomerScale);

            }
        }, null);

        mTouchGestureListener = new DiyDoodleOnTouchGestureListener(mDoodleView, new DiyDoodleOnTouchGestureListener.ISelectionListener() {
            // save states before being selected
            DiyIDoodlePen mLastPen = null;
            DiyIDoodleColor mLastColor = null;
            Float mSize = null;

            DiyIDoodleItemListener mIDoodleItemListener = new DiyIDoodleItemListener() {
                @Override
                public void onPropertyChanged(int property) {
                    if (mTouchGestureListener.getSelectedItem() == null) {
                        return;
                    }
                    Logs.s("  doodle11 onPropertyChanged ");
                }
            };

            @Override
            public void onSelectedItem(DiyIDoodle doodle, DiyIDoodleSelectableItem selectableItem, boolean selected) {
                Logs.s("  doodle11 onSelectedItem ");
                if (selected) {
                    if (mLastPen == null) {
                        mLastPen = mDoodle.getPen();
                    }
                    if (mLastColor == null) {
                        mLastColor = mDoodle.getColor();
                    }
                    if (mSize == null) {
                        mSize = mDoodle.getSize();
                    }
                    mDoodleView.setEditMode(true);
                    mDoodle.setPen(selectableItem.getPen());
                    mDoodle.setColor(selectableItem.getColor());
                    mDoodle.setSize(selectableItem.getSize());

                    selectableItem.addItemListener(mIDoodleItemListener);
                } else {
                    selectableItem.removeItemListener(mIDoodleItemListener);

                    if (mTouchGestureListener.getSelectedItem() == null) { // nothing is selected. 当前没有选中任何一个item
                        if (mLastPen != null) {
                            mDoodle.setPen(mLastPen);
                            mLastPen = null;
                        }
                        if (mLastColor != null) {
                            mDoodle.setColor(mLastColor);
                            mLastColor = null;
                        }
                        if (mSize != null) {
                            mDoodle.setSize(mSize);
                            mSize = null;
                        }
                    }
                }
            }

            @Override
            public void onCreateSelectableItem(DiyIDoodle doodle, float x, float y) {
                if (mDoodle.getPen() == DiyDoodlePen.TEXT) {

                } else if (mDoodle.getPen() == DiyDoodlePen.BITMAP) {

                }
                Logs.s("  doodle11 onCreateSelectableItem ");
            }
        }) {
            @Override
            public void setSupportScaleItem(boolean supportScaleItem) {
                super.setSupportScaleItem(supportScaleItem);
                if (supportScaleItem) {

                } else {

                }
                Logs.s("  doodle11 setSupportScaleItem ");
            }
        };
        mDoodle = mDoodleView;

        DiyIDoodleTouchDetector detector = new DiyDoodleTouchDetector(getApplicationContext(), mTouchGestureListener);
        mDoodleView.setDefaultTouchDetector(detector);

        mDoodle.setIsDrawableOutside(mDoodleParams.mIsDrawableOutside);
        mFrameLayout.addView(mDoodleView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDoodle.setDoodleMinScale(mDoodleParams.mMinScale);
        mDoodle.setDoodleMaxScale(mDoodleParams.mMaxScale);

        findViewById(R.id.redo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoodleView.redo();
            }
        });

        findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDoodleView.undo();
            }
        });

    }


    /**
     * 包裹DoodleView，监听相应的设置接口，以改变UI状态
     */
    private class DoodleViewWrapper extends DiyDoodleView {

        public DoodleViewWrapper(Context context, Bitmap bitmap, boolean optimizeDrawing, DiyIDoodleListener listener, DiyIDoodleTouchDetector defaultDetector) {
            super(context, bitmap, optimizeDrawing, listener, defaultDetector);
        }

        private Map<DiyIDoodlePen, Integer> mBtnPenIds = new HashMap<>();

        @Override
        public void setPen(DiyIDoodlePen pen) {
            DiyIDoodlePen oldPen = getPen();
            super.setPen(pen);
            Logs.s("  doodle11 setPen ");

        }

        private Map<DiyIDoodleShape, Integer> mBtnShapeIds = new HashMap<>();


        @Override
        public void setShape(DiyIDoodleShape shape) {
            super.setShape(shape);
            Logs.s("  doodle11 setShape ");
        }


        @Override
        public void setSize(float paintSize) {
            super.setSize(paintSize);
            Logs.s("  doodle11 setSize ");
        }

        @Override
        public void setColor(DiyIDoodleColor color) {
            DiyIDoodlePen pen = getPen();
            super.setColor(color);

            Logs.s("  doodle11 setColor ");
        }

        @Override
        public void enableZoomer(boolean enable) {
            super.enableZoomer(enable);
            Logs.s("  doodle11 enableZoomer ");
        }

        @Override
        public boolean undo() {
            Logs.s("  doodle11 undo ");
            return super.undo();
        }

        @Override
        public void clear() {
            Logs.s("  doodle11 clear ");
            super.clear();
        }


        @Override
        public void setEditMode(boolean editMode) {
            Logs.s("  doodle11 setEditMode ");
            if (editMode == isEditMode()) {
                return;
            }

            super.setEditMode(editMode);

        }

        private void setSingleSelected(Collection<Integer> ids, int selectedId) {
            Logs.s("  doodle11 setSingleSelected ");
        }
    }
}
