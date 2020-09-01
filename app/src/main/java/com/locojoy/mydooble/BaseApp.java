package com.locojoy.mydooble;

import android.app.Application;
import android.content.Context;

/**
 * @author: kerry
 * date: On $ {DATE}
 */
public class BaseApp extends Application {
    public static Context mContent;

    @Override
    public void onCreate() {
        super.onCreate();
        mContent = this;
    }
}
