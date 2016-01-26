package com.msx7.android.tclient.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.msx7.android.tclient.R;

/**
 * 文件名: SendWifiDialog.java
 * 描  述: 输入IP和名称的对话框
 * 作  者：Josn@憬承
 * 时  间：2016/1/25
 */
public class SendWifiDialog extends Dialog {
    TextView mTitleView;
    TextView mMessageView;
    Button mPositiveButton;
    Button mNegativeButton;
    RadioGroup mGroup;
//    EditText editText;

    public SendWifiDialog(Context context) {
        super(context, R.style.transparent_dialog);
        setContentView(R.layout.dialog_send_wifi_layout);
        View view = findViewById(R.id.ll_root);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        int with = getContext().getResources().getDisplayMetrics().widthPixels * 2 / 3;
        params.width = with;
        view.setLayoutParams(params);
        mTitleView = (TextView) findViewById(R.id.title);
        mMessageView = (TextView) findViewById(R.id.message);
        mPositiveButton = (Button) findViewById(R.id.button1);
        mNegativeButton = (Button) findViewById(R.id.button2);
        mPositiveButton.setVisibility(View.GONE);
        mNegativeButton.setVisibility(View.GONE);
    }







    public void setNegativeButton(int textId, View.OnClickListener listener) {
        mNegativeButton.setOnClickListener(new CancelListener(listener));
        mNegativeButton.setText(textId);
        mNegativeButton.setVisibility(View.VISIBLE);
    }

    public void setNegativeButton(CharSequence text,
                                  View.OnClickListener listener) {
        mNegativeButton.setOnClickListener(new CancelListener(listener));
        mNegativeButton.setText(text);
        mNegativeButton.setVisibility(View.VISIBLE);
    }


    public void setPositiveButton(int textId, View.OnClickListener listener) {
        mPositiveButton.setOnClickListener(new CancelListener(listener));
        mPositiveButton.setText(textId);
        mPositiveButton.setVisibility(View.VISIBLE);
    }

    public void setPositiveButton(CharSequence text,
                                  View.OnClickListener listener) {
        mPositiveButton.setOnClickListener(new CancelListener(listener));
        mPositiveButton.setText(text);
        mPositiveButton.setVisibility(View.VISIBLE);
    }


    public TextView getTitleView() {
        return mTitleView;
    }
    public TextView getMessageView() {
        return mMessageView;
    }



    public class CancelListener implements View.OnClickListener {
        View.OnClickListener listener;

        public CancelListener() {
            super();
        }

        public CancelListener(View.OnClickListener listener) {
            super();
            this.listener = listener;
        }

        @Override
        public void onClick(View v) {
            if (listener != null)
                listener.onClick(v);
            dismiss();
        }
    }


}
