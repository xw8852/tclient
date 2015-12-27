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
import com.msx7.android.tclient.common.DefaultMessageHandler;
import com.msx7.android.tclient.common.TApplication;
import com.msx7.android.tclient.ui.CameraView;

import org.apache.mina.core.session.IoSession;

import com.msx7.josn.tvconnection.action.CameraBody;
import com.msx7.josn.tvconnection.action.CameraGetBody;
import com.msx7.josn.tvconnection.action.EmptyBody;
import com.msx7.josn.tvconnection.mima.common.util.MinaUtil;
import com.msx7.josn.tvconnection.pack.Code;
import com.msx7.josn.tvconnection.pack.MessageHandlerLib;
import com.msx7.josn.tvconnection.pack.message.Message;
import com.msx7.josn.tvconnection.pack.message.MessageHandler;
import com.msx7.josn.tvconnection.pack.message.MessageHead;
import com.msx7.josn.tvconnection.pack.message.impl.MessageHeadImpl;
import com.msx7.josn.tvconnection.pack.message.impl.MessageImpl;

/**
 * Created by Josn on 2015/12/19.
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
        MessageHandlerLib.getInstance().addHandler(String.valueOf(Code.ACTION_CAMERA_ANGLE_GET), new DefaultMessageHandler(cameraHandler));
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

    MessageHandler cameraHandler = new MessageHandler() {
        @Override
        public void handleMessage(IoSession var1, Message msg) {
            if (msg.getMessageHead().getActionCode() == Code.ACTION_CAMERA_ANGLE_GET) {
                CameraGetBody body = new CameraGetBody();
                body.decoder(msg.getMessageBody().encode());
                cameraView.setLimited(body.limitLeft,body.limitTop , body.limitRight, body.limitBottom);
                cameraView.setRotate(body.horizontal, body.vertical);
            }
        }
    };

    /**
     * 发送旋转的命令
     *
     * @param hor
     * @param vel
     */
    void sendRotate(int hor, int vel) {
        MessageHead head = new MessageHeadImpl("".getBytes(), MessageHead.HEAD_LENGTH + CameraBody.LENGTH, Code.ACTION_CAMERA_ANGLE_ROTATE, 1);
        Message message = new MessageImpl(head, new CameraBody(hor, vel));
        TApplication.getInstance().sendMessage(MinaUtil.messageClip(message));
    }

    /**
     * 获取云台当前数据指令
     */
    void sendGetDATA() {
        MessageHead head = new MessageHeadImpl("".getBytes(), MessageHead.HEAD_LENGTH + 1, Code.ACTION_CAMERA_ANGLE_GET, 1);
        Message message = new MessageImpl(head, new EmptyBody());
        TApplication.getInstance().sendMessage(MinaUtil.messageClip(message));
    }


}
