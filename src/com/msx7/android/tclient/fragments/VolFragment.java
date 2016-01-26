package com.msx7.android.tclient.fragments;

import android.app.Fragment;
import android.os.Bundle;
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
import com.msx7.android.tclient.ui.widget.CircleRectShape;

/**
 * 文件名: VolFragment.java
 * 描  述:
 * 作  者：Josn@憬承
 * 时  间：2016/1/25
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

        getMode();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getMode();
        }
    }



    /**
     * 点击四个左右的按钮，切换声道
     *
     * @param v
     */
    @InjectMethod(
            @InjectListener(
                    ids = {R.id.left1, R.id.right1, R.id.left2, R.id.right2},
                    listeners = {OnClick.class}
            ))
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left1:
                circleLeft.setAngle(circleLeft.getAngle() - 1);
                break;
            case R.id.left2:
                circleRight.setAngle(circleRight.getAngle() - 1);
                break;
            case R.id.right1:
                circleLeft.setAngle(circleLeft.getAngle() + 1);
                break;
            case R.id.right2:
                circleRight.setAngle(circleRight.getAngle() + 1);
                break;
        }
        doAngle(-1);
    }

    /**
     * 点击下面声道按钮，切换声道
     *
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
     *
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


      //TODO:发送当前音量
    }

    /**
     * 发送当前音量模式
     */
    void sendMode() {
       //TODO:发送当前音量模式
    }

    /**
     * 发送消息，获取当前音量模式，以及当前音量
     */
    void getMode() {
       //TODO:发送消息，获取当前音量模式，以及当前音量
    }


}
