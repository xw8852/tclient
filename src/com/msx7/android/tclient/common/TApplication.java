package com.msx7.android.tclient.common;

import android.app.Application;

import com.android.pc.ioc.app.Ioc;

/**
 * Created by xiaowei on 2015/12/11.
 */
public class TApplication extends Application {

    @Override
    public void onCreate() {
        Ioc.getIoc().init(this);
        super.onCreate();
    }
}
