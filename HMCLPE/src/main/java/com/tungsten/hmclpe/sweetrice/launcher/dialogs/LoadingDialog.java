package com.tungsten.hmclpe.sweetrice.launcher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.sweetrice.R;

public class LoadingDialog extends Dialog {

    private final TextView loadingText;

    public LoadingDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_loading);
        setCancelable(false);
        loadingText = findViewById(R.id.loading_text);
    }

    public void setLoadingText(String string) {
        loadingText.setText(string);
    }

}
