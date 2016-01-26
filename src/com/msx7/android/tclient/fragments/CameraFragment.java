package com.msx7.android.tclient.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.inject.InjectMethod;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.msx7.android.tclient.R;
import com.msx7.android.tclient.ui.CameraView;

/**
 * 文件名: CameraFragment.java
 * 描  述:
 * 作  者：Josn@憬承
 * 时  间：2016/1/25
 */
public class CameraFragment extends Fragment {
    @InjectView(value = R.id.CameraView)
    CameraView cameraView;
    @InjectView(value = R.id.reset)
    View btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera, null);
        Handler_Inject.injectFragment(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cameraView.setLimited(-90, -90, 90, 90);
        cameraView.setLisenter(new CameraView.IRotateLisenter() {
            @Override
            public void rotate(int x, int y) {
                sendRotate(x, y);
            }
        });
        sendGetDATA();

    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            sendGetDATA();
        }
    }

    @InjectMethod(
            @InjectListener(
                    ids = {R.id.reset},
                    listeners = {OnClick.class}
            ))
    public void onClick(View v) {
        /**
         * 会自动触发监听回调
         */
        cameraView.reset();
    }



    /**
     * 发送旋转的命令
     *
     * @param hor
     * @param vel
     */
    void sendRotate(int hor, int vel) {
        //TODO:发送旋转的命令
    }

    /**
     * 获取云台当前数据指令
     */
    void sendGetDATA() {
       //TODO:获取云台当前数据指令
    }


}
