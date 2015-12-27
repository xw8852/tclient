package com.msx7.android.tclient.common;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;

import com.android.pc.ioc.app.Ioc;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.msx7.android.tclient.ui.ConnectPopupWindow.ConnectInfo;

import org.apache.mina.core.session.IoSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.msx7.josn.tvconnection.mima.client.MainMinaClient;
import com.msx7.josn.tvconnection.mima.client.handler.MinaClientHandler;
import com.msx7.josn.tvconnection.mima.client.handler.MinaClientHandler.IStatusHandler;
import com.msx7.josn.tvconnection.pack.message.Message;

/**
 * Created by xiaowei on 2015/12/11.
 */
public class TApplication extends Application implements IStatusHandler {

    static TApplication instance;

    Handler handler = new Handler();

    @Override
    public void onCreate() {
        Ioc.getIoc().init(this);
        super.onCreate();
        instance = this;
        MinaClientHandler.getInstances().setStatusHandler(this);
    }

    public Handler getHandler() {
        return handler;
    }

    public static final TApplication getInstance() {
        return instance;
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMessage(Message msg) {
        sendMessage(Arrays.asList(msg));
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
     * 发送消息
     *
     * @param msgs
     */
    public void sendMessage(List<Message> msgs) {
        if (client != null && client.session.isConnected()) {
            if (msgs == null || msgs.isEmpty()) return;
            for (Message message : msgs) {
                client.session.write(message);
            }
        }
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

    public MainMinaClient getClient() {
        return client;
    }

    MainMinaClient client;

    ConnectInfo currentInfo;

    public ConnectInfo getCurrentInfo() {
        if (client == null || !client.session.isConnected())
            return null;
        return currentInfo;
    }

    /**
     * @param info  建立连接的相关信息
     * @param error 设置监听，连接失败，就会触发此监听
     */
    public void connect(final ConnectInfo info, final IConnectError error) {
        currentInfo = info;
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (client != null) client.session.close(true);
                try {
                    client = new MainMinaClient(info.ip);
                } catch (Exception e) {
                    e.printStackTrace();
                    currentInfo = null;
                    client = null;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (error != null) {
                                error.connectError(info);
                            }
                        }
                    });
                    //TODO:连接失败
                } finally {
                }
            }
        }.start();
    }

    @Override
    public void handStatus(final int status, final IoSession ioSession) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (IStatusHandler handler : handlers) {
                    handler.handStatus(status, ioSession);
                }
            }
        });

    }

    List<IStatusHandler> handlers = new ArrayList<IStatusHandler>();

    /**
     * @param handler 监听当前连接的状态
     */
    public void addStatusHandler(IStatusHandler handler) {
        handlers.add(handler);
    }

    public void removeStatusHandler(IStatusHandler handler) {
        handlers.remove(handler);

    }

    public interface IConnectError {
        void connectError(ConnectInfo info);
    }
}
