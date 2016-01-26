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
 * 文件名: ProgressDialog.java
 * 描  述: 输入IP和名称的对话框
 * 作  者：Josn@憬承
 * 时  间：2016/1/25
 */
public class ProgressDialog extends Dialog {


    public ProgressDialog(Context context) {
        super(context, R.style.transparent_dialog);
        setContentView(R.layout.layout_progress);
        View view = findViewById(R.id.ll_root);

    }

}
