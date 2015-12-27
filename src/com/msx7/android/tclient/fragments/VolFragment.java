package com.msx7.android.tclient.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.inject.InjectMethod;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.msx7.android.tclient.R;
import com.msx7.android.tclient.common.DefaultMessageHandler;
import com.msx7.android.tclient.common.TApplication;
import com.msx7.android.tclient.ui.widget.CircleRectShape;

import org.apache.mina.core.session.IoSession;

import com.msx7.josn.tvconnection.action.EmptyBody;
import com.msx7.josn.tvconnection.action.VolBody;
import com.msx7.josn.tvconnection.mima.common.util.MinaUtil;
import com.msx7.josn.tvconnection.pack.Code;
import com.msx7.josn.tvconnection.pack.MessageHandlerLib;
import com.msx7.josn.tvconnection.pack.message.Message;
import com.msx7.josn.tvconnection.pack.message.MessageHandler;
import com.msx7.josn.tvconnection.pack.message.MessageHead;
import com.msx7.josn.tvconnection.pack.message.impl.MessageHeadImpl;
import com.msx7.josn.tvconnection.pack.message.impl.MessageImpl;

/**
 * Created by xiaowei on 2015/12/15.
 */
public class VolFragment extends Fragment implements CircleRectShape.IAngleListener {

    public static final int MODE_VOL_DOUBLE = 0X001;
    public static final int MODE_VOL_SINGLE = 0X002;
    int mode = MODE_VOL_DOUBLE;

    @InjectView(value = R.id.CircleLeft)
    CircleRectShape circleLeft;
    @InjectView(value = R.id.CircleRight)
    CircleRectShape circleRight;
    @InjectView(value = R.id.left_text)
    TextView textLeft;
    @InjectView(value = R.id.right_text)
    TextView textRight;
    @InjectView(value = R.id.left)
    View left;
    @InjectView(value = R.id.right)
    View right;

    /**
     * 静音按钮
     */
    @InjectView(value = R.id.vol)
    ImageView vol;
    /**
     * 声道按钮
     */
    @InjectView(value = R.id.chanl)
    ImageView channel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vol, container, false);
        Handler_Inject.injectFragment(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        circleLeft.setListener(this);
        circleRight.setListener(this);
        MessageHandlerLib.getInstance().addHandler(String.valueOf(Code.ACTION_VOL_GET), new DefaultMessageHandler(volHandler));
        MessageHandlerLib.getInstance().addHandler(String.valueOf(Code.ACTION_VOL_SET), new DefaultMessageHandler(volHandler));
        getMode();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getMode();
        }
    }

    MessageHandler volHandler = new MessageHandler() {
        @Override
        public void handleMessage(IoSession var1, Message msg) {
            if (msg.getMessageHead().getActionCode() == Code.ACTION_VOL_GET
                    || msg.getMessageHead().getActionCode() == Code.ACTION_VOL_SET
                    ) {
                VolBody body = new VolBody();
                body.decoder(msg.getMessageBody().encode());
                if (body.isSingle) {
                    setMode(MODE_VOL_SINGLE);
                } else {
                    setMode(MODE_VOL_DOUBLE);
                }
                if (body.isMute) {
                    vol.setSelected(true);
                    vol.setImageResource(R.drawable.btn_vol_mute);

                } else {
                    vol.setSelected(false);
                    vol.setImageResource(R.drawable.btn_vol);
                }
                if (body.volL > -1) {
                    circleLeft.setAngle(body.volL);
                }
                if (body.volR > -1) {
                    circleRight.setAngle(body.volR);
                }

            }
        }
    };

    /**
     * 点击四个左右的按钮，切换声道
     * @param v
     */
    @InjectMethod(
            @InjectListener(
                    ids = {R.id.left1, R.id.right1, R.id.left2, R.id.right2},
                    listeners = {OnClick.class}
            ))
    public void onClick(View v) {
        if (mode == MODE_VOL_SINGLE) {
            setMode(MODE_VOL_DOUBLE);
        } else {
            setMode(MODE_VOL_SINGLE);
        }
        sendMode();
    }

    /**
     * 点击下面声道按钮，切换声道
     * @param v
     */
    @InjectMethod(
            @InjectListener(
                    ids = {R.id.chanl},
                    listeners = {OnClick.class}
            ))
    public void onChannel(View v) {
        if (channel.isSelected()) {
            setMode(MODE_VOL_DOUBLE);
        } else {
            setMode(MODE_VOL_SINGLE);
        }
        sendMode();
    }
    void setMode(int type) {
        if (type == MODE_VOL_SINGLE) {
            mode = MODE_VOL_SINGLE;
            right.setVisibility(View.VISIBLE);
            textLeft.setText(R.string.vol_left);
            channel.setImageResource(R.drawable.btn_vol_singlelchannel);
            channel.setSelected(true);
        } else {
            channel.setSelected(false);
            mode = MODE_VOL_DOUBLE;
            channel.setImageResource(R.drawable.btn_vol_dualchannel);
            right.setVisibility(View.GONE);
            textLeft.setText(R.string.vol_double);
        }
    }

    /**
     * 是否静音  isSelected true 表示静音
     * @param v
     */
    @InjectMethod(
            @InjectListener(
                    ids = {R.id.vol},
                    listeners = {OnClick.class}
            ))
    public void onVol(View v) {
        if (vol.isSelected()) {
            vol.setSelected(false);
            vol.setImageResource(R.drawable.btn_vol);
        } else {
            vol.setSelected(true);
            vol.setImageResource(R.drawable.btn_vol_mute);
        }
        sendMode();
    }



    @Override
    public void doAngle(int angle) {
        vol.setSelected(false);
        vol.setImageResource(R.drawable.btn_vol);

        MessageHead head = new MessageHeadImpl("".getBytes(), MessageHead.HEAD_LENGTH + VolBody.LENGTH, Code.ACTION_VOL_SET, 1);
        VolBody body = null;
        if (mode == MODE_VOL_DOUBLE) {
            //TODO:双声道
            body = new VolBody(vol.isSelected(), mode == MODE_VOL_SINGLE, circleLeft.getAngle(), -1);
        } else {
            //TODO: 左右独立音量
            body = new VolBody(vol.isSelected(), mode == MODE_VOL_SINGLE, circleLeft.getAngle(), circleRight.getAngle());
        }
        Message message = new MessageImpl(head, body);
        TApplication.getInstance().sendMessage(MinaUtil.messageClip(message));
    }

    /**
     * 发送当前音量模式
     */
    void sendMode() {
        MessageHead head = new MessageHeadImpl("".getBytes(), MessageHead.HEAD_LENGTH + VolBody.LENGTH, Code.ACTION_VOL_SET, 1);
        VolBody body = new VolBody(vol.isSelected(), mode == MODE_VOL_SINGLE, -1, -1);
        Message message = new MessageImpl(head, body);
        TApplication.getInstance().sendMessage(MinaUtil.messageClip(message));
    }

    /**
     * 发送消息，获取当前音量模式，以及当前音量
     */
    void getMode() {
        MessageHead head = new MessageHeadImpl("".getBytes(), MessageHead.HEAD_LENGTH + 1, Code.ACTION_VOL_GET, 1);
        Message message = new MessageImpl(head, new EmptyBody());
        TApplication.getInstance().sendMessage(MinaUtil.messageClip(message));
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.d("回收", "finalize---VolFragment");
    }
}
