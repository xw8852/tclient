package com.msx7.android.tclient.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.Service;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.pc.util.Handler_Inject;
import com.msx7.android.tclient.R;
import com.msx7.android.tclient.common.TApplication;
import com.msx7.android.tclient.fragments.input.SoftKeyboard;

import cn.edu.fudan.libremoteinputmethod.RemoteInputMethod;
import cn.edu.fudan.libvirtualinputevent.VirtualInputEvent;

/**
 * 文件名: TouchFragment.java
 * 描  述:
 * 作  者：Josn@憬承
 * 时  间：2016/1/25
 */
public class TouchFragment extends Fragment implements View.OnTouchListener {
    private GestureDetector mGestureDetector;

    EditText mContent;
    View mClearBtn;
    RemoteInputMethod mRemoteInputMethod;
    SoftKeyboard softKeyboard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_touch, null);
        mContent = (EditText) rootView.findViewById(R.id.content);
        mClearBtn = rootView.findViewById(R.id.clear);
        mClearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRemoteInputMethod().InputText("");
            }
        });
        mContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_GO ||
                        actionId == EditorInfo.IME_ACTION_SEND ||
                        actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    getRemoteInputMethod().Enter();
                }

                return false;
            }
        });
        mContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getRemoteInputMethod().InputText(s.toString());
            }
        });
        InputMethodManager im = (InputMethodManager) rootView.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);

        softKeyboard = new SoftKeyboard((ViewGroup) rootView, im);
        softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged() {

            @Override
            public void onSoftKeyboardHide() {
                TApplication.getInstance().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        getRemoteInputMethod().HideRemoteSoftKeyboard();
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                TApplication.getInstance().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        getRemoteInputMethod().ShowRemoteSoftKeyboard();
                    }
                });

            }
        });
        return rootView;
    }

    /* Prevent memory leaks:
    */
    @Override
    public void onDestroy() {
        super.onDestroy();
        softKeyboard.unRegisterSoftKeyboardCallback();
    }

    RemoteInputMethod getRemoteInputMethod() {
        if (mRemoteInputMethod == null) {
            mRemoteInputMethod = new RemoteInputMethod(TApplication.getInstance().getCurrentInfo().ip);
        }
        return mRemoteInputMethod;
    }


    View touch;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        touch = view.findViewById(R.id.touch);
        mGestureDetector = new GestureDetector(view.getContext(), onGestureListener);
        touch.setOnTouchListener(this);
        touch.setFocusable(true);
        touch.setClickable(true);
        touch.setLongClickable(true);
    }

    GestureDetector.OnGestureListener onGestureListener = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            TApplication.getInstance().getVirtualInputEvent().MouseClick(0);
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            TApplication.getInstance().getVirtualInputEvent().MouseClick(1);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            TApplication.getInstance().getVirtualInputEvent().MouseMove(-(int) distanceX, -(int) distanceY);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }


}
