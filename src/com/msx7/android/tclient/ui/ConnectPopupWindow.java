package com.msx7.android.tclient.ui;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.msx7.android.tclient.MainActivity;
import com.msx7.android.tclient.R;
import com.msx7.android.tclient.common.TApplication;
import com.msx7.android.tclient.ui.dialog.CustomDialog;
import com.msx7.android.tclient.ui.list.BinderListView;
import com.msx7.android.tclient.ui.list.IList;

import org.apache.mina.core.session.IoSession;

import com.msx7.josn.tvconnection.mima.client.handler.MinaClientHandler;

/**
 * Created by Josn on 2015/12/26.
 */
public class ConnectPopupWindow implements IList<ConnectPopupWindow.ConnectInfo>, MinaClientHandler.IStatusHandler {
    PopupWindow popupWindow;

    View view;
    View root;
    View add;
    View selected;
    Holder holder;
    ListView listView;
    BinderListView binderListView;

    public ConnectPopupWindow(View view) {
        this.view = view;
        root = LayoutInflater.from(view.getContext()).inflate(R.layout.layout_home, null);
        add = root.findViewById(R.id.add);

        selected = root.findViewById(R.id.selected);
        holder = new Holder();
        holder.title = (TextView) selected.findViewById(R.id.title);
        holder.progressBar = selected.findViewById(R.id.progressBar);
        holder.status = (ImageView) selected.findViewById(R.id.status);
        selected.setVisibility(View.GONE);

        add.setOnClickListener(addListener);
        listView = (ListView) root.findViewById(R.id.list);
        binderListView = new BinderListView<ConnectInfo>(listView).setIList(this);
        TApplication.getInstance().addStatusHandler(this);
        setData();


    }

    void setData() {

        /**
         * 如果存在上次的连接信息，或者当前的连接信息，在上面一行，显示
         */
        final ConnectInfo info = TApplication.getInstance().getCurrentInfo();
        if (info != null) {
            selected.setVisibility(View.VISIBLE);
            selected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    connect(info);
                }
            });
            holder.title.setText(info.name);
            holder.progressBar.setVisibility(View.INVISIBLE);
            if (TApplication.getInstance().getClient() != null && TApplication.getInstance().getClient().session != null
                    && TApplication.getInstance().getClient().session.isConnected()) {
                holder.status.setImageResource(R.drawable.connected);
            } else {
                holder.status.setImageResource(R.drawable.notconnected);
            }

        }
        binderListView.setData(TApplication.getInstance().getAllConnects());
        /**
         * 稍作延迟，以确保能够得到正确的高度
         */
        TApplication.getInstance().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int limitedHeight = view.getResources().getDisplayMetrics().heightPixels * 2 / 3;
                if (root.getMeasuredHeight() > limitedHeight) {
                    popupWindow.update(popupWindow.getWidth(), limitedHeight);
                }
            }
        }, 10);
    }

    public void showPopupWindow() {
        int with = view.getResources().getDisplayMetrics().widthPixels * 2 / 3;
        popupWindow = new PopupWindow(root,
                with, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置好参数之后再show
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        root.setFocusable(true);
        root.setFocusableInTouchMode(true);
        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    TApplication.getInstance().removeStatusHandler(ConnectPopupWindow.this);
                    popupWindow.dismiss();
                    ((Activity) v.getContext()).finish();
                }
                return false;
            }
        });
    }

    View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final CustomDialog dialog = new CustomDialog(v.getContext());
            dialog.setPositiveButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager mInputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
            });
            dialog.setNegativeButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager mInputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    String name = dialog.getTitleView().getText().toString().trim();
                    String ip = dialog.getMessageView().getText().toString().trim();
                    if (TextUtils.isEmpty(name)) {
                        name = "VBOX";
                    }
                    if (!ip.matches("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))")) {
                        Toast.makeText(v.getContext(), "输入的ip地址不合法", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ConnectInfo info = new ConnectInfo(ip, name);
                    if (TApplication.getInstance().getAllConnects().contains(info)) {
                        Toast.makeText(v.getContext(), "重复的IP地址", Toast.LENGTH_SHORT).show();
                    }
                    connect(info);
                }
            });
            dialog.show();
        }
    };

    private void connect(ConnectInfo info) {
        TApplication.getInstance().save(info);
        setData();
        selected.setVisibility(View.VISIBLE);
        selected.setOnClickListener(null);
        holder.title.setText(info.name);
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.status.setImageResource(R.drawable.connected);
        TApplication.getInstance().connect(info, new TApplication.IConnectError() {
            @Override
            public void connectError(final ConnectInfo info) {
                //TODO:连接失败
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.status.setImageResource(R.drawable.notconnected);
                selected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connect(info);
                    }
                });
            }
        });
    }

    @Override
    public void handStatus(int status, IoSession ioSession) {
        if (status == STATUC_CONNECT) {
            popupWindow.dismiss();
            ((MainActivity) view.getContext()).setTitle(TApplication.getInstance().getCurrentInfo().name);
            Toast.makeText(TApplication.getInstance(), "连接成功", Toast.LENGTH_SHORT).show();
            TApplication.getInstance().removeStatusHandler(ConnectPopupWindow.this);
        }
    }

    @Override
    public View BinderData(final ConnectInfo connectInfo, View item, LayoutInflater inflater) {
        Holder holder;
        if (item == null) {
            item = inflater.inflate(R.layout.item_connect, null);
            holder = new Holder();
            holder.title = (TextView) item.findViewById(R.id.title);
            holder.progressBar = item.findViewById(R.id.progressBar);
            holder.status = (ImageView) item.findViewById(R.id.status);
            item.setTag(holder);
        } else {
            holder = (Holder) item.getTag();
        }
        holder.progressBar.setVisibility(View.INVISIBLE);
        holder.title.setText(connectInfo.ip);
        holder.title.setText(connectInfo.name);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect(connectInfo);
            }
        });
        item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                TApplication.getInstance().delete(connectInfo);
                setData();
                return true;
            }
        });
        return item;
    }

    public static class Holder {
        TextView title;
        View progressBar;
        ImageView status;
    }

    public static class ConnectInfo {
        public String ip;
        public String name;

        public ConnectInfo() {
        }

        public ConnectInfo(String ip, String name) {
            this.ip = ip;
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj instanceof ConnectInfo) {
                ConnectInfo info = (ConnectInfo) obj;
                return ip.equals(info.ip);
            } else
                return false;
        }
    }
}
