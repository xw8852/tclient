package com.msx7.android.tclient;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
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

import com.msx7.android.tclient.utils.ToastUtil;
import com.msx7.josn.tvconnection.action.EmptyBody;
import com.msx7.josn.tvconnection.mima.client.handler.MinaClientHandler;
import com.msx7.josn.tvconnection.pack.Code;
import com.msx7.josn.tvconnection.pack.message.Message;
import com.msx7.josn.tvconnection.pack.message.MessageHead;
import com.msx7.josn.tvconnection.pack.message.impl.MessageHeadImpl;
import com.msx7.josn.tvconnection.pack.message.impl.MessageImpl;

import org.apache.mina.core.session.IoSession;

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

    /**
     * 手指从边缘滑入的，设置离边缘的有效距离
     */
    float outSide_Dimen;
    /**
     * 检测滑动的区域
     */
    Rect outSide = new Rect();
    /**
     * 是否可以弹出 滑入菜单
     */
    boolean isOutside;
    /**
     * 边缘滑入显示的菜单
     */
    PopupWindow outSideMenu;
    /**
     * 设置键 弹出的菜单
     */
    PopupWindow menuPopupWindow;

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

        final View root = ((View) mTitleBar.getParent());
        root.setOnTouchListener(this);
        if (TApplication.getInstance().getClient() != null
                && TApplication.getInstance().getClient().session != null
                && TApplication.getInstance().getClient().session.isConnected()) {
            mTitleBar.setTitle(TApplication.getInstance().getCurrentInfo().name);
            root.getHitRect(outSide);
            return;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new ConnectPopupWindow(mTitleBar).showPopupWindow();
                root.getHitRect(outSide);
            }
        }, 200);
        outSide_Dimen = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics());
        TApplication.getInstance().addStatusHandler(tcpStatusHandler);
    }

    MinaClientHandler.IStatusHandler tcpStatusHandler = new MinaClientHandler.IStatusHandler() {
        @Override
        public void handStatus(int status, IoSession ioSession) {
            //连接结束，或者连接异常
            if (status == STATUC_ERROR || status == STATUC_FINISH) {
                new ConnectPopupWindow(mTitleBar).showPopupWindow();
            } else if (status == STATUC_CONNECT) {
                //TODO:连接成功
            }

        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float minX = Math.min(Math.abs(outSide.left - event.getX()), Math.abs(outSide.right - event.getX()));
                float minY = Math.min(Math.abs(outSide.top - event.getY()), Math.abs(outSide.bottom - event.getY()));
//                Log.d("MSG","minx:"+minX+",minY:"+minY);
//                Log.d("MSG","x:"+event.getX()+",y:"+event.getY());
//                Log.d("MSG","rect:"+outSide+","+ outSide_Dimen);
                if (minX < outSide_Dimen || minY < outSide_Dimen) {
                    isOutside = true;
                }
                break;
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                if (isOutside)
                    showOutSideMenu();
                isOutside = false;
                break;
        }
        return true;
    }

    /**
     * 监听：外滑入菜单的话筒
     */
    View.OnClickListener microphoneListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (outSideMenu != null) outSideMenu.dismiss();
            ToastUtil.showToastShort("点击了 话筒");
            MessageHead head = new MessageHeadImpl("".getBytes(), MessageHead.HEAD_LENGTH + 1, Code.ACTION_MICROPHONE, 1);
            Message message = new MessageImpl(head, new EmptyBody());
            TApplication.getInstance().sendMessage(message);
        }
    };
    /**
     * 监听：外滑入菜单的 远程监控
     */
    View.OnClickListener monitoringListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (outSideMenu != null) outSideMenu.dismiss();
            ToastUtil.showToastShort("点击了  远程监听");
            MessageHead head = new MessageHeadImpl("".getBytes(), MessageHead.HEAD_LENGTH + 1, Code.ACTION_MONITORING, 1);
            Message message = new MessageImpl(head, new EmptyBody());
            TApplication.getInstance().sendMessage(message);
        }
    };
    View.OnClickListener menuItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (menuPopupWindow != null) menuPopupWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn1:
                    //TODO:连接状态
                    ToastUtil.showToastShort("点击了  连接状态");
                    break;
                case R.id.btn2:
                    //TODO:切换连接设备
//                    ToastUtil.showToastShort("点击了  切换连接设备");
                    new ConnectPopupWindow(mTitleBar).showPopupWindow();
                    break;
                case R.id.btn3:
                    //TODO:盒子配置信息
                    ToastUtil.showToastShort("点击了  盒子配置信息");
                    break;
                case R.id.btn4:
                    //TODO:用户信息
                    ToastUtil.showToastShort("点击了  用户信息");
                    break;
                case R.id.btn5:
                    //TODO:退出遥控器
//                    ToastUtil.showToastShort("点击了  退出遥控器");
                    finish();
                    break;
            }
        }
    };


    /**
     * 边缘滑入显示的菜单
     */
    public void showOutSideMenu() {
        if (outSideMenu != null && outSideMenu.isShowing()) return;
        View view = getLayoutInflater().inflate(R.layout.layout_outside_menu, null);
        view.findViewById(R.id.monitoring).setOnClickListener(monitoringListener);
        view.findViewById(R.id.microphone).setOnClickListener(microphoneListener);
        outSideMenu = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        outSideMenu.setHeight(outSide.height() - mTitleBar.getMeasuredHeight());
        outSideMenu.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置好参数之后再show
        outSideMenu.showAtLocation(mTitleBar, Gravity.BOTTOM, 0, 0);

    }


    /**
     * 点击标题栏左上角 设置键 弹出的菜单
     */
    View.OnClickListener menuListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View menu = getLayoutInflater().inflate(R.layout.layout_menu, null);
            menu.findViewById(R.id.btn1).setOnClickListener(menuItemListener);
            menu.findViewById(R.id.btn2).setOnClickListener(menuItemListener);
            menu.findViewById(R.id.btn3).setOnClickListener(menuItemListener);
            menu.findViewById(R.id.btn4).setOnClickListener(menuItemListener);
            menu.findViewById(R.id.btn5).setOnClickListener(menuItemListener);
            menuPopupWindow = new PopupWindow(menu,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
            menuPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            // 设置好参数之后再show
            menuPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        }
    };

    /**
     * 下方bottomBar的监听
     *
     * @param v
     */
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
