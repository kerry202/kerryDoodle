package com.locojoy.mydooble;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.locojoy.mydooble.core.IDoodle;
import com.locojoy.mydooble.core.IDoodleColor;
import com.locojoy.mydooble.core.IDoodleItemListener;
import com.locojoy.mydooble.core.IDoodleListener;
import com.locojoy.mydooble.core.IDoodlePen;
import com.locojoy.mydooble.core.IDoodleSelectableItem;
import com.locojoy.mydooble.core.IDoodleShape;
import com.locojoy.mydooble.core.IDoodleTouchDetector;
import com.locojoy.mydooble.dood.DoodleColor;
import com.locojoy.mydooble.dood.DoodlePen;
import com.locojoy.mydooble.dood.DoodleShape;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import cn.forward.androids.utils.ImageUtils;
import cn.forward.androids.utils.LogUtil;
import cn.forward.androids.utils.StatusBarUtil;
import cn.forward.androids.utils.Util;

/**
 * @author: kerry
 * date: On $ {DATE}
 */
public class Draw2Activity extends Activity {



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doodle_layout);

    }


}
