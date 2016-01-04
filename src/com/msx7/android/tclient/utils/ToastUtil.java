package com.msx7.android.tclient.utils;

import android.widget.Toast;

import com.msx7.android.tclient.common.TApplication;

/**
 * Created by Josn on 2016/1/3.
 */
public class ToastUtil {
    public static final void  showToastShort(String msg){
        Toast.makeText(TApplication.getInstance(),msg,Toast.LENGTH_SHORT).show();
    }

    public static final void  showToastLong(String msg){
        Toast.makeText(TApplication.getInstance(),msg,Toast.LENGTH_LONG).show();
    }

    public static final void  showToastShort(int resId){
        Toast.makeText(TApplication.getInstance(),resId,Toast.LENGTH_SHORT).show();
    }

    public static final void  showToastLong(int resId){
        Toast.makeText(TApplication.getInstance(),resId,Toast.LENGTH_LONG).show();
    }
}
