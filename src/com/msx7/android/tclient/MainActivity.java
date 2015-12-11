package com.msx7.android.tclient;

import android.app.Activity;
import android.os.Bundle;

import com.android.pc.ioc.inject.InjectLayer;

@InjectLayer(R.layout.activity_main)
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
