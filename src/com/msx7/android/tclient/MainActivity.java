package com.msx7.android.tclient;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.inject.InjectMethod;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.listener.OnClick;
import com.msx7.android.tclient.common.TApplication;
import com.msx7.android.tclient.fragments.CameraFragment;
import com.msx7.android.tclient.fragments.TouchFragment;
import com.msx7.android.tclient.fragments.VolFragment;
import com.msx7.android.tclient.ui.ConnectPopupWindow;
import com.msx7.android.tclient.ui.widget.TitleView;

import com.msx7.josn.tvconnection.action.EmptyBody;
import com.msx7.josn.tvconnection.mima.common.util.ByteUtil;
import com.msx7.josn.tvconnection.pack.Code;
import com.msx7.josn.tvconnection.pack.message.Message;
import com.msx7.josn.tvconnection.pack.message.MessageHead;
import com.msx7.josn.tvconnection.pack.message.impl.MessageHeadImpl;
import com.msx7.josn.tvconnection.pack.message.impl.MessageImpl;

import java.util.Arrays;

@InjectLayer(R.layout.activity_main)
public class MainActivity extends Activity implements View.OnTouchListener {
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
    View menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setTitle(String title) {
        mTitleBar.setTitle(title);
    }

    @InjectInit
    void init() {
        mTitleBar.setLeftImg(R.drawable.selected_btn_setting, menuListener);
        menu = getLayoutInflater().inflate(R.layout.layout_menu, null);
        ((View) mTitleBar.getParent()).setOnTouchListener(this);
        if (TApplication.getInstance().getClient() != null && TApplication.getInstance().getClient().session.isConnected()) {
            mTitleBar.setTitle(TApplication.getInstance().getCurrentInfo().name);
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new ConnectPopupWindow(mTitleBar).showPopupWindow();
            }
        }, 200);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_OUTSIDE:
                Log.d("MotionEvent", "MotionEvent.ACTION_OUTSIDE");
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_DOWN:
                Log.d("MotionEvent", "MotionEvent.ACTION_DOWN  " + event.getRawX() + "," + event.getRawY());
                break;
            case MotionEvent.ACTION_UP:
                Log.d("MotionEvent", "MotionEvent.ACTION_UP  " + event.getRawX() + "," + event.getRawY());
                break;
        }
        return true;
    }


    View.OnClickListener menuListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            PopupWindow popupWindow = new PopupWindow(menu,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // 设置好参数之后再show
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        }
    };

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
                sendHomeAction();
                v.setSelected(false);
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
                v.setSelected(false);
                sendBackAction();
                break;
        }
    }

    /**
     * 发送点击Home键
     */
    public void sendHomeAction() {
        MessageHead head = new MessageHeadImpl("".getBytes(), MessageHead.HEAD_LENGTH + 1, Code.ACTION_SYSTEM_HOME, 1);
        Message message = new MessageImpl(head, new EmptyBody());
        TApplication.getInstance().sendMessage(message);

    }

    /**
     * 发送点击返回键
     */
    public void sendBackAction() {
        MessageHead head = new MessageHeadImpl("".getBytes(), MessageHead.HEAD_LENGTH + 1, Code.ACTION_SYSTEM_BACK, 1);
        Message message = new MessageImpl(head, new EmptyBody());
        TApplication.getInstance().sendMessage(message);
    }
}
