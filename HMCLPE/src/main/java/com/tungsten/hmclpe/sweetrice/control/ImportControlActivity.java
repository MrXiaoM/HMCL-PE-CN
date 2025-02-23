package com.tungsten.hmclpe.sweetrice.control;

import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity;

import com.tungsten.hmclpe.sweetrice.utils.LocaleUtils;

public class ImportControlActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.setLanguage(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.setLanguage(this);
    }

}
