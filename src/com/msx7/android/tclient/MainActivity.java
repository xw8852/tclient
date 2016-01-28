package com.msx7.android.tclient;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.android.pc.ioc.inject.InjectInit;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.inject.InjectListener;
import com.android.pc.ioc.inject.InjectMethod;
import com.android.pc.ioc.inject.InjectView;
import com.android.pc.ioc.view.listener.OnClick;
import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.msx7.android.tclient.common.TApplication;
import com.msx7.android.tclient.fragments.CameraFragment;
import com.msx7.android.tclient.fragments.TouchFragment;
import com.msx7.android.tclient.fragments.VolFragment;
import com.msx7.android.tclient.ui.ConnectPopupWindow;
import com.msx7.android.tclient.ui.widget.TitleView;
import com.msx7.android.tclient.utils.ToastUtil;

/**
 * 文件名: MainActivity.java
 * 描  述:
 * 作  者：Josn@憬承
 * 时  间：2016/1/25
 */
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
          /*set it to be no title*/
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       /*set it to be full screen*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void setTitle(String title) {
        mTitleBar.setTitle(title);
    }

    @InjectInit
    void init() {
        mTitleBar.setLeftImg(R.drawable.selected_btn_setting, menuListener);

        final View root = ((View) mTitleBar.getParent());
        root.setOnTouchListener(this);
        if (TApplication.getInstance().getCurrentInfo() == null) {
            connectPopupWindow = new ConnectPopupWindow(mTitleBar);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    connectPopupWindow.showPopupWindow();
                    root.getHitRect(outSide);
                }
            }, 200);
            //默认跳转到 触屏页
            onClick(btn3);
        }

        outSide_Dimen = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics());


            /*
         * 设置渠道的推荐方法。该方法同setAppChannel（String）， 如果第三个参数设置为true（防止渠道代码设置会丢失的情况），将会保存该渠道，每次设置都会更新保存的渠道，
         * 如果之前的版本使用了该函数设置渠道，而后来的版本需要AndroidManifest.xml设置渠道，那么需要将第二个参数设置为空字符串,并且第三个参数设置为false即可。
         * appChannel是应用的发布渠道，不需要在mtj网站上注册，直接填写就可以 该参数也可以设置在AndroidManifest.xml中
         */
        // StatService.setAppChannel(this, "RepleceWithYourChannel", true);
        // 测试时，可以使用1秒钟session过期，这样不断的间隔1S启动退出会产生大量日志。
        StatService.setSessionTimeOut(30);
        // setOn也可以在AndroidManifest.xml文件中填写，BaiduMobAd_EXCEPTION_LOG，打开崩溃错误收集，默认是关闭的
        StatService.setOn(this, StatService.EXCEPTION_LOG);
        /*
         * 设置启动时日志发送延时的秒数<br/> 单位为秒，大小为0s到30s之间<br/> 注：请在StatService.setSendLogStrategy之前调用，否则设置不起作用
         *
         * 如果设置的是发送策略是启动时发送，那么这个参数就会在发送前检查您设置的这个参数，表示延迟多少S发送。<br/> 这个参数的设置暂时只支持代码加入，
         * 在您的首个启动的Activity中的onCreate函数中使用就可以。<br/>
         */
        StatService.setLogSenderDelayed(0);
        /*
         * 用于设置日志发送策略<br /> 嵌入位置：Activity的onCreate()函数中 <br />
         *
         * 调用方式：StatService.setSendLogStrategy(this,SendStrategyEnum. SET_TIME_INTERVAL, 1, false); 第二个参数可选：
         * SendStrategyEnum.APP_START SendStrategyEnum.ONCE_A_DAY SendStrategyEnum.SET_TIME_INTERVAL 第三个参数：
         * 这个参数在第二个参数选择SendStrategyEnum.SET_TIME_INTERVAL时生效、 取值。为1-24之间的整数,即1<=rtime_interval<=24，以小时为单位 第四个参数：
         * 表示是否仅支持wifi下日志发送，若为true，表示仅在wifi环境下发送日志；若为false，表示可以在任何联网环境下发送日志
         */
        StatService.setSendLogStrategy(this, SendStrategyEnum.SET_TIME_INTERVAL, 1, false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        final View root = ((View) mTitleBar.getParent());
        root.setOnTouchListener(this);
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    ConnectPopupWindow connectPopupWindow;


    /**
     * 外滑菜单的触摸监听
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (outSide == null || outSide.width() == 0 || outSide.height() == 0) {
            v.getHitRect(outSide);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float minX = Math.min(Math.abs(outSide.left - event.getX()), Math.abs(outSide.right - event.getX()));
                float minY = Math.min(Math.abs(outSide.top - event.getY()), Math.abs(outSide.bottom - event.getY()));
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

        }
    };

    View.OnClickListener menuItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (menuPopupWindow != null) menuPopupWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn1:
                    /**
                     * 连接状态
                     */
                    ToastUtil.showToastShort("点击了  连接状态");
                    break;
                case R.id.btn2:
                    /**
                     * 切换连接设备
                     */
                    if (connectPopupWindow == null)
                        connectPopupWindow = new ConnectPopupWindow(mTitleBar);
                    connectPopupWindow.showPopupWindow();
                    break;
                case R.id.btn3:
                    /**
                     * 盒子配置信息
                     */
                    ToastUtil.showToastShort("点击了  盒子配置信息");
                    break;
                case R.id.btn4:
                    /**
                     * 用户信息
                     */
                    ToastUtil.showToastShort("点击了  用户信息");
                    break;
                case R.id.btn5:
                    /**
                     *  退出遥控器
                     */
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outSideMenu.dismiss();
            }
        });
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
        if (v.getId() == R.id.btn1) {
            sendHomeAction();
            return;
        } else if (v.getId() == R.id.btn5) {
            sendBackAction();
            return;
        }
        v.setSelected(true);
        if (cur == v) return;
        if (cur != null) cur.setSelected(false);
        if (cur == v) return;
        if (cur != null) cur.setSelected(false);
        if (curFragment != null) getFragmentManager().beginTransaction().hide(curFragment).commit();
        cur = v;
        switch (v.getId()) {
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
        }
    }

    /**
     * 发送点击Home键
     */
    public void sendHomeAction() {

        TApplication.getInstance().getVirtualInputEvent().VirtualKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HOME);
        try {
            Thread.sleep(200);
        }catch (Exception e){
            e.printStackTrace();
        }
        TApplication.getInstance().getVirtualInputEvent().VirtualKey(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HOME);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TApplication.getInstance().destoryVirtualInputEvent();
    }

    /**
     * 发送点击返回键
     */
    public void sendBackAction() {
        TApplication.getInstance().getVirtualInputEvent().VirtualKey(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK);
        try {
            Thread.sleep(200);
        }catch (Exception e){
            e.printStackTrace();
        }
        TApplication.getInstance().getVirtualInputEvent().VirtualKey(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK);
    }
}
