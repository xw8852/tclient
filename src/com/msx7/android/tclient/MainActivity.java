package com.msx7.android.tclient;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.inject.InjectMethod;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.listener.OnClick;
import com.msx7.android.tclient.fragments.CameraFragment;
import com.msx7.android.tclient.fragments.TouchFragment;
import com.msx7.android.tclient.fragments.VolFragment;
import com.msx7.android.tclient.ui.widget.TitleView;

@InjectLayer(R.layout.activity_main)
public class MainActivity extends Activity {

    @InjectView(value = R.id.bottomBar)
    LinearLayout btnGroup;
    @InjectView(value = R.id.btn1)
    ImageView btn1;
    @InjectView(value = R.id.btn2)
    ImageView btn2;
    @InjectView(value = R.id.btn3)
    ImageView btn3;
    @InjectView(value = R.id.btn4)
    ImageView btn4;
    @InjectView(value = R.id.btn5)
    ImageView btn5;
    @InjectView(value = R.id.titleBar)
    TitleView mTitleBar;
    View cur;
    Fragment[] fragments = new Fragment[5];
    Fragment curFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @InjectInit
    void init() {
        mTitleBar.setLeftImg(R.drawable.selected_btn_setting, null);
    }


    @InjectMethod(
            @InjectListener(
                    ids = {R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5},
                    listeners = {OnClick.class}
            ))
    public void onClick(View v) {
        v.setSelected(true);
        if (cur == v) return;
        if (cur != null) cur.setSelected(false);
        if (curFragment != null) getFragmentManager().beginTransaction().hide(curFragment).commit();
        cur = v;
        switch (v.getId()) {
            case R.id.btn1:
                curFragment = null;
                break;
            case R.id.btn2:
                if (fragments[1] == null) {
                    fragments[1] = new VolFragment();
                    getFragmentManager().beginTransaction().add(R.id.content, fragments[1]).commit();
                } else {
                    getFragmentManager().beginTransaction().show(fragments[1]).commit();
                }
                curFragment = fragments[1];
                break;
            case R.id.btn3:
                if (fragments[2] == null) {
                    fragments[2] = new TouchFragment();
                    getFragmentManager().beginTransaction().add(R.id.content, fragments[2]).commit();
                } else {
                    getFragmentManager().beginTransaction().show(fragments[2]).commit();
                }
                curFragment = fragments[2];
                break;
            case R.id.btn4:
                if (fragments[3] == null) {
                    fragments[3] = new CameraFragment();
                    getFragmentManager().beginTransaction().add(R.id.content, fragments[3]).commit();
                } else {
                    getFragmentManager().beginTransaction().show(fragments[3]).commit();
                }
                curFragment = fragments[3];
                break;
            case R.id.btn5:
                curFragment = null;
                break;
        }
    }

}
