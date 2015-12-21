package com.msx7.android.tclient.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.pc.util.Handler_Inject;
import com.msx7.android.tclient.R;

/**
 * Created by xiaowei on 2015/12/15.
 */
public class TouchFragment extends Fragment implements View.OnTouchListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_touch, null);
        Handler_Inject.injectFragment(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.touch).setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        return true;
    }
}
