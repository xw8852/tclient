package com.msx7.android.tclient.ui.list;

import android.view.LayoutInflater;
import android.view.View;


/**
 * Created by xiaowei on 2015/12/18.
 */
public interface IList<T> {

    /**
     * 绑定数据
     * @param t
     * @param item
     */
    View BinderData(T t, View item, LayoutInflater inflater);
}
