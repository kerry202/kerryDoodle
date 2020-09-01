package com.locojoy.mydooble

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import com.locojoy.mydooble.dood.*
import com.locojoy.mydooble.dood.core.*
import com.locojoy.mydooble.utils.utils.ImageUtils
import com.locojoy.mydooble.utils.utils.Util
import com.locojoy.mydooble.utils.views.DiyDoodleView
import kotlinx.android.synthetic.main.doodle_layout.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

/**
 * @author: kerry
 * date: On $ {DATE}
 */
class DrawActivity : Activity() {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    var mDoodleParams: DiyDoodleParams? = null

    var mTouchGestureListener: DiyDoodleOnTouchGestureListener? = null
    var mImagePath: String? = null
    var mDoodle: DiyIDoodle? = null
    var mDoodleView: DiyDoodleView? = null

    fun setBallWh(x: Float, y: Float) {
        val i = 8 * 4
//        ball.setPoint(x, y, i.toFloat())
    }

    fun ballIsShow(show: Boolean) {
//        if (show) {
//            ball.visibility = View.VISIBLE
//        } else {
//            ball.visibility = View.GONE
//        }
    }

    fun setScale(scale: Float, left: Float, top: Float) {
//        myimg.setScale(scale, left, top)
//        back.setScale(scale, left, top)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.doodle_layout)

        val params = drawControl.layoutParams

        params.height =1080

//        back.layoutParams=params
        drawControl.layoutParams=params
//        ball.layoutParams=params
//        myimg.layoutParams=params



//        ball.init()
//
//        back.initBitmap(BitmapFactory.decodeResource(resources,R.drawable.tops))


        val decodeResource =
            BitmapFactory.decodeResource(resources, R.drawable.diy_uv_tops_model_icon)
//
//        myimg.initParameters(decodeResource)

        mDoodleParams = intent.getParcelableExtra("key_doodle_params")
        mDoodleParams?.let {
            mImagePath = it.mImagePath

            if (it.mIsFullScreen) {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
        }


        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.diy_uv_tops_model_icon)
        mDoodleView = DoodleViewWrapper(
            this,
            bitmap,
            mDoodleParams!!.mOptimizeDrawing,
            object : DiyIDoodleListener {
                override fun onSaved(
                    doodle: DiyIDoodle,
                    bitmap: Bitmap,
                    callback: Runnable
                ) {
                    // 保存图片为jpg格式
                    var doodleFile: File? = null
                    var file: File? = null

                    val savePath = mDoodleParams?.mSavePath
                    val isDir = mDoodleParams?.mSavePathIsDir
                    if (TextUtils.isEmpty(savePath)) {
                        val dcimFile = File(
                            Environment.getExternalStorageDirectory(),
                            "DCIM"
                        )
                        doodleFile = File(dcimFile, "Doodle")
                        //　保存的路径
                        file = File(
                            doodleFile,
                            System.currentTimeMillis().toString() + ".jpg"
                        )
                    } else {
                        if (isDir!!) {
                            doodleFile = File(savePath)
                            //　保存的路径
                            file = File(
                                doodleFile,
                                System.currentTimeMillis().toString() + ".jpg"
                            )
                        } else {
                            file = File(savePath)
                            doodleFile = file.parentFile
                        }
                    }
                    doodleFile?.mkdirs()
                    var outputStream: FileOutputStream? = null
                    try {
                        outputStream = FileOutputStream(file)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
                        ImageUtils.addImage(contentResolver, file.absolutePath)
                        val intent = Intent()
                        intent.putExtra(
                            KEY_IMAGE_PATH,
                            file.absolutePath
                        )
                        setResult(RESULT_OK, intent)
                        finish()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        onError(DiyDoodleView.ERROR_SAVE, e.message)
                    } finally {
                        Util.closeQuietly(outputStream)
                    }
                }

                fun onError(i: Int, msg: String?) {
                    setResult(RESULT_ERROR)
                    finish()
                }

                override fun onReady(doodle: DiyIDoodle) {

                    mDoodleParams?.let {

                        var size: Float =
                            (if (it.mPaintUnitSize > 0) it?.mPaintUnitSize!! * mDoodle?.unitSize!! else 0) as Float

                        if (size <= 0) {
                            // 设置初始值
                            mDoodle?.size =
                                if (it.mPaintPixelSize!! > 0) it.mPaintPixelSize!! else mDoodle!!.size
                        }
                        mDoodle?.color = DiyDoodleColor(it.mPaintColor)
                        mDoodle?.zoomerScale = it.mZoomerScale
                    }

                    // 选择画笔
                    mDoodle?.pen = DiyDoodlePen.BRUSH
                    mDoodle?.shape = DiyDoodleShape.HAND_WRITE


                }
            },
            null
        )
        mTouchGestureListener =
            object : DiyDoodleOnTouchGestureListener(mDoodleView, object : ISelectionListener {
                // save states before being selected
                var mLastPen: DiyIDoodlePen? = null
                var mLastColor: DiyIDoodleColor? = null
                var mSize: Float? = null
                var mIDoodleItemListener = DiyIDoodleItemListener {
                    if (mTouchGestureListener?.selectedItem == null) {
                        return@DiyIDoodleItemListener
                    }
                    Logs.s("  doodle11 onPropertyChanged ")
                }

                override fun onSelectedItem(
                    doodle: DiyIDoodle,
                    selectableItem: DiyIDoodleSelectableItem,
                    selected: Boolean
                ) {
                    Logs.s("  doodle11 onSelectedItem ")
                    if (selected) {
                        if (mLastPen == null) {
                            mLastPen = mDoodle!!.pen
                        }
                        if (mLastColor == null) {
                            mLastColor = mDoodle!!.color
                        }
                        if (mSize == null) {
                            mSize = mDoodle!!.size
                        }
                        mDoodleView?.isEditMode = true
                        mDoodle?.pen = selectableItem.pen
                        mDoodle?.color = selectableItem.color
                        mDoodle?.size = selectableItem.size
                        selectableItem.addItemListener(mIDoodleItemListener)
                    } else {
                        selectableItem.removeItemListener(mIDoodleItemListener)
                        if (mTouchGestureListener?.selectedItem == null) { // nothing is selected. 当前没有选中任何一个item
                            if (mLastPen != null) {
                                mDoodle?.pen = mLastPen
                                mLastPen = null
                            }
                            if (mLastColor != null) {
                                mDoodle?.color = mLastColor
                                mLastColor = null
                            }
                            if (mSize != null) {
                                mDoodle?.size = mSize!!
                                mSize = null
                            }
                        }
                    }
                }

                override fun onCreateSelectableItem(
                    doodle: DiyIDoodle,
                    x: Float,
                    y: Float
                ) {
                    if (mDoodle?.pen === DiyDoodlePen.TEXT) {
                    } else if (mDoodle?.pen === DiyDoodlePen.BITMAP) {
                    }
                    Logs.s("  doodle11 onCreateSelectableItem ")
                }
            }) {
                override fun setSupportScaleItem(supportScaleItem: Boolean) {
                    super.setSupportScaleItem(supportScaleItem)
                    if (supportScaleItem) {
                    } else {
                    }
                    Logs.s("  doodle11 setSupportScaleItem ")
                }
            }
        mDoodle = mDoodleView
        val detector: DiyIDoodleTouchDetector =
            DiyDoodleTouchDetector(applicationContext, mTouchGestureListener)

        mDoodleView?.defaultTouchDetector = detector

        mDoodleParams?.let {
            mDoodle?.setIsDrawableOutside(it.mIsDrawableOutside)

            mDoodle?.doodleMinScale = it.mMinScale
            mDoodle?.doodleMaxScale = it.mMaxScale
        }

        drawControl?.addView(
            mDoodleView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        redo.setOnClickListener { mDoodleView?.redo() }
        undo.setOnClickListener { mDoodleView?.undo() }

        eraser.setOnClickListener {
            mDoodle?.pen = DiyDoodlePen.ERASER
        }

        brush.setOnClickListener {
            mDoodle?.pen = DiyDoodlePen.BRUSH
        }
    }

    /**
     * 包裹DoodleView，监听相应的设置接口，以改变UI状态
     */
    private inner class DoodleViewWrapper(
        activity: Activity?,
        bitmap: Bitmap?,
        optimizeDrawing: Boolean,
        listener: DiyIDoodleListener?,
        defaultDetector: DiyIDoodleTouchDetector?
    ) : DiyDoodleView(activity, bitmap, optimizeDrawing, listener, defaultDetector) {
        private val mBtnPenIds: Map<DiyIDoodlePen, Int> =
            HashMap()

        override fun setPen(pen: DiyIDoodlePen) {
            val oldPen = getPen()
            super.setPen(pen)
            Logs.s("  doodle11 setPen ")
        }

        private val mBtnShapeIds: Map<DiyIDoodleShape, Int> =
            HashMap()

        override fun setShape(shape: DiyIDoodleShape) {
            super.setShape(shape)
            Logs.s("  doodle11 setShape ")
        }

        override fun setSize(paintSize: Float) {
            super.setSize(paintSize)
            Logs.s("  doodle11 setSize ")
        }

        override fun setColor(color: DiyIDoodleColor) {
            val pen = pen
            super.setColor(color)
            Logs.s("  doodle11 setColor ")
        }

        override fun enableZoomer(enable: Boolean) {
            super.enableZoomer(enable)
            Logs.s("  doodle11 enableZoomer ")
        }

        override fun undo(): Boolean {
            Logs.s("  doodle11 undo ")
            return super.undo()
        }

        override fun clear() {
            Logs.s("  doodle11 clear ")
            super.clear()
        }

        override fun setEditMode(editMode: Boolean) {
            Logs.s("  doodle11 setEditMode ")
            if (editMode == isEditMode) {
                return
            }
            super.setEditMode(editMode)
        }

        private fun setSingleSelected(
            ids: Collection<Int>,
            selectedId: Int
        ) {
            Logs.s("  doodle11 setSingleSelected ")
        }
    }

    companion object {
        const val RESULT_ERROR = -111
        const val KEY_PARAMS = "key_doodle_params"
        const val KEY_IMAGE_PATH = "key_image_path"
    }
}