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
 * Created by xiaowei on 2015/12/15.
 */
public class VolFragment extends Fragment {

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

    @InjectView(value = R.id.vol)
    ImageView vol;
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
        setMode(MODE_VOL_SINGLE);
    }

    @InjectMethod(
            @InjectListener(
                    ids = {R.id.left1, R.id.right1, R.id.left2, R.id.right2},
                    listeners = {OnClick.class}
            ))
    public void onClick(View v) {
        if (mode == MODE_VOL_SINGLE) {
            mode = MODE_VOL_DOUBLE;
            right.setVisibility(View.GONE);
            textLeft.setText(R.string.vol_double);
            vol.setSelected(false);
            channel.setImageResource(R.drawable.btn_vol_dualchannel);
        } else {
            mode = MODE_VOL_SINGLE;
            channel.setImageResource(R.drawable.btn_vol_singlelchannel);
            right.setVisibility(View.VISIBLE);
            textLeft.setText(R.string.vol_left);
            vol.setSelected(true);
        }
    }

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
    }

    @InjectMethod(
            @InjectListener(
                    ids = {R.id.chanl},
                    listeners = {OnClick.class}
            ))
    public void onChannel(View v) {
        if (vol.isSelected()) {
            setMode(MODE_VOL_DOUBLE);
        } else {
            setMode(MODE_VOL_SINGLE);
        }
    }

    void setMode(int type) {
        if (type == MODE_VOL_SINGLE) {
            mode = MODE_VOL_SINGLE;
            right.setVisibility(View.VISIBLE);
            textLeft.setText(R.string.vol_left);
            channel.setImageResource(R.drawable.btn_vol_singlelchannel);
            vol.setSelected(true);
        } else {
            vol.setSelected(false);
            mode = MODE_VOL_DOUBLE;
            channel.setImageResource(R.drawable.btn_vol_dualchannel);
            right.setVisibility(View.GONE);
            textLeft.setText(R.string.vol_double);
        }
    }
}
