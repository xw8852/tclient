package com.msx7.android.tclient.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.pc.util.Handler_Inject;
import com.msx7.android.tclient.R;
import com.msx7.android.tclient.common.DefaultMessageHandler;
import com.msx7.android.tclient.common.TApplication;
import com.msx7.josn.tvconnection.action.EmptyBody;
import com.msx7.josn.tvconnection.action.TouchBody;
import com.msx7.josn.tvconnection.action.TouchGetBody;
import com.msx7.josn.tvconnection.mima.common.util.MinaUtil;
import com.msx7.josn.tvconnection.pack.Code;
import com.msx7.josn.tvconnection.pack.MessageHandlerLib;
import com.msx7.josn.tvconnection.pack.message.Message;
import com.msx7.josn.tvconnection.pack.message.MessageHandler;
import com.msx7.josn.tvconnection.pack.message.MessageHead;
import com.msx7.josn.tvconnection.pack.message.impl.MessageHeadImpl;
import com.msx7.josn.tvconnection.pack.message.impl.MessageImpl;

import org.apache.mina.core.session.IoSession;

/**
 * Created by xiaowei on 2015/12/15.
 */
public class TouchFragment extends Fragment implements View.OnTouchListener, MessageHandler {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_touch, null);
        Handler_Inject.injectFragment(this, rootView);
        return rootView;
    }

    View touch;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        touch = view.findViewById(R.id.touch);
        touch.setOnTouchListener(this);
        sendGetData();
        MessageHandlerLib.getInstance().addHandler(String.valueOf(Code.ACTION_MOTION_GET), new DefaultMessageHandler(this));
        MessageHandlerLib.getInstance().addHandler(String.valueOf(Code.ACTION_MOTION_SET), new DefaultMessageHandler(this));
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            sendGetData();
        }
    }

    int width;
    int height;

    @Override
    public void handleMessage(IoSession ioSession, Message msg) {
        if (msg.getMessageHead().getActionCode() == Code.ACTION_MOTION_GET) {
            TouchGetBody body = new TouchGetBody();
            body.decoder(msg.getMessageBody().encode());
            width = body.width;
            height = body.height;

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                byte action = 0;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    action = 1;
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    action = 2;
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    action = 3;
                }
                TouchBody.TouchInfo[] infos = new TouchBody.TouchInfo[event.getPointerCount()];
                for (int i = 0; i < event.getPointerCount(); i++) {
                    float _x = event.getX(i) * width / touch.getMeasuredWidth();
                    float _y = event.getY(i) * height / touch.getMeasuredHeight();
                    Log.d("MSG", "x " + event.getX(i) + "," + (int) _x);
                    Log.d("MSG", "y " + event.getY(i) + "," + (int) _y);
                    infos[i] = new TouchBody.TouchInfo(action, (int) _x, (int) _y);
                }
                sendMotionData(infos);
                break;
        }


        return true;
    }

    void sendGetData() {
        MessageHead head = new MessageHeadImpl("".getBytes(), MessageHead.HEAD_LENGTH + 1, Code.ACTION_MOTION_GET, 1);
        Message message = new MessageImpl(head, new EmptyBody());
        TApplication.getInstance().sendMessage(MinaUtil.messageClip(message));
    }

    void sendMotionData(TouchBody.TouchInfo... infos) {
        TouchBody body = new TouchBody(infos);
        MessageHead head = new MessageHeadImpl("".getBytes(), MessageHead.HEAD_LENGTH + body.getBodyLength(), Code.ACTION_MOTION_SET, 1);
        Message message = new MessageImpl(head, body);
        TApplication.getInstance().sendMessage(MinaUtil.messageClip(message));
    }
}
