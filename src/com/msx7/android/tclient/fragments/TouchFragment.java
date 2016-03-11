package com.msx7.android.tclient.fragments;

import android.app.Fragment;
import android.app.Service;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.msx7.android.tclient.MainActivity;
import com.msx7.android.tclient.R;
import com.msx7.android.tclient.common.TApplication;
import com.msx7.android.tclient.fragments.input.SoftKeyboard;
import com.msx7.android.tclient.ui.widget.SecurityEditText;

import cn.edu.fudan.libremoteinputmethod.RemoteInputMethod;

/**
 * 文件名: TouchFragment.java
 * 描  述:
 * 作  者：Josn@憬承
 * 时  间：2016/1/25
 */
public class TouchFragment extends Fragment implements View.OnTouchListener {
    private GestureDetector mGestureDetector;

    State mState = State.MOVE;
    SecurityEditText mContent;
    View mSendBtn;
    RemoteInputMethod mRemoteInputMethod;
    SoftKeyboard softKeyboard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_touch, null);
        mContent = (SecurityEditText) rootView.findViewById(R.id.content);
        mSendBtn = rootView.findViewById(R.id.send);
        mSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRemoteInputMethod().InputText(mContent.getText().toString());
                mContent.setText("");
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
        mContent.setDelKeyEventListener(new SecurityEditText.OnDelKeyEventListener() {
            @Override
            public void onDeleteClick() {
                getRemoteInputMethod().BackSpace();
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
//                        getRemoteInputMethod().HideRemoteSoftKeyboard();
                        mContent.clearFocus();
                    }
                });
            }

            @Override
            public void onSoftKeyboardShow() {
                TApplication.getInstance().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
//                        getRemoteInputMethod().ShowRemoteSoftKeyboard();
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
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mState != State.MOVE) {
                stopDrag();
                return true;
            }
            MouseClick(1);
            try {
                Thread.sleep(200);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            MouseClick(0);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            MouseMove(-(int) distanceX, -(int) distanceY);
            return true;
        }

        /**
         * 长按开启拖动，再次长按/点击 取消
         * @param e
         */
        @Override
        public void onLongPress(MotionEvent e) {
            if (mState == State.MOVE) {
                startDRAG();
            } else {
                stopDrag();
            }
        }

        void stopDrag() {
            mState = State.MOVE;
            MouseClick(0);
//            sendOneClick();
        }


        void sendOneClick() {
            MouseClick(1);
            try {
                Thread.sleep(50);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            MouseMove(1, 1);
            try {
                Thread.sleep(50);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            MouseClick(0);
        }

        /**
         * 双击开启拖动模式
         */
        void startDRAG() {
            mState = State.DRAG;
            MouseClick(1);
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


    void MouseClick(int action) {
        if (TApplication.getInstance().getVirtualInputEvent() != null) {
            TApplication.getInstance().getVirtualInputEvent().MouseClick(action);
        } else {
            MainActivity activity = (MainActivity) getView().getContext();
            activity.showNOConnect();
        }
    }

    void MouseMove(int x, int y) {
        if (TApplication.getInstance().getVirtualInputEvent() != null) {
            TApplication.getInstance().getVirtualInputEvent().MouseMove(x, y);
        }else {
            MainActivity activity = (MainActivity) getView().getContext();
            activity.showNOConnect();
        }
    }

    enum State {MOVE, DRAG}
}
