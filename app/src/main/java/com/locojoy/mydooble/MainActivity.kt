package com.locojoy.mydooble

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 涂鸦参数
        // 涂鸦参数
        val params = DoodleParams()
        params.mIsFullScreen = true
        // 图片路径
        // 图片路径
        params.mImagePath = "/storage/emulated/0/DCIM/Doodle/1598679883199.jpg"
        // 初始画笔大小
        // 初始画笔大小
        params.mPaintUnitSize = DoodleView.DEFAULT_SIZE.toFloat()
        // 画笔颜色
        // 画笔颜色
        params.mPaintColor = Color.RED
        // 是否支持缩放item
        // 是否支持缩放item
        params.mSupportScaleItem = true

        val intent = Intent(this, DrawActivity::class.java)
        intent.putExtra("key_doodle_params", params)
        startActivity(intent)

    }

}