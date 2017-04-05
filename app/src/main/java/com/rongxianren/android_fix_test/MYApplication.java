package com.rongxianren.android_fix_test;

import android.app.Application;
import android.content.Context;

/**
 * Created by wty on 2017/3/31.
 */

public class MYApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        DexFixUtils.loadFixedDex(base);
    }
}
