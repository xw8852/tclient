package com.msx7.android.tclient.common;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;

import com.android.pc.ioc.app.Ioc;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.msx7.android.tclient.ui.ConnectPopupWindow.ConnectInfo;

import java.util.ArrayList;
import java.util.List;

import cn.edu.fudan.libvirtualinputevent.VirtualInputEvent;


/**
 * 文件名: TApplication.java
 * 描  述:
 * 作  者：Josn@憬承
 * 时  间：2016/1/25
 */
public class TApplication extends Application {

    static TApplication instance;

    Handler handler = new Handler();

    @Override
    public void onCreate() {
        Ioc.getIoc().init(this);
        super.onCreate();
        instance = this;
    }

    public Handler getHandler() {
        return handler;
    }

    public static final TApplication getInstance() {
        return instance;
    }


    /**
     * 保存新增的连接信息
     *
     * @param info
     */
    public void save(ConnectInfo info) {
        List<ConnectInfo> infos = getAllConnects();
        if (infos.contains(info)) infos.remove(info);
        infos.add(0, info);
        getSharedPreferences().edit().putString("ConnectInfo", new Gson().toJson(infos)).commit();
    }

    /**
     * 删除连接信息
     *
     * @param info
     */
    public void delete(ConnectInfo info) {
        List<ConnectInfo> infos = getAllConnects();
        infos.remove(info);
        getSharedPreferences().edit().putString("ConnectInfo", new Gson().toJson(infos)).commit();
    }


    /**
     * 获取已经记录的所有连接信息
     *
     * @return
     */
    public List<ConnectInfo> getAllConnects() {
        String data = getSharedPreferences().getString("ConnectInfo", "");
        List<ConnectInfo> infos = new Gson().fromJson(data, new TypeToken<ArrayList<ConnectInfo>>() {
        }.getType());
        if (infos == null) return new ArrayList<ConnectInfo>();
        return infos;
    }

    public SharedPreferences getSharedPreferences() {
        SharedPreferences preferences = getSharedPreferences("tv", Activity.MODE_PRIVATE);
        return preferences;
    }


    /**
     * 保存当前的连接信息
     *
     * @param info
     */
    public void saveCurrentInfo(ConnectInfo info) {
        getSharedPreferences().edit().putString("CurrentInfo", new Gson().toJson(info)).commit();
    }

    /**
     * 获取当前连接信息
     *
     * @return
     */
    public ConnectInfo getCurrentInfo() {
        String data = getSharedPreferences().getString("CurrentInfo", "");
        return new Gson().fromJson(data, ConnectInfo.class);
    }

    VirtualInputEvent mCurInputEvent;

    /**
     * 获取当前 虚拟硬件输入 连接
     */
    public VirtualInputEvent getVirtualInputEvent() {
        if (mCurInputEvent == null) {
            if (getCurrentInfo() == null) return null;
            mCurInputEvent = new VirtualInputEvent(getCurrentInfo().ip);
        }
        return mCurInputEvent;
    }
    /**
     * 销毁 当前 虚拟硬件输入 连接
     */
    public void destoryVirtualInputEvent() {
        if (mCurInputEvent == null) return;
        mCurInputEvent.Destroy();
        mCurInputEvent = null;
    }

}
