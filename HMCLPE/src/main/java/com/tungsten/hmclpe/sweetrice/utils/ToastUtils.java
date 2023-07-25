package com.tungsten.hmclpe.sweetrice.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void toast(String s, Context context){
        try {
            //Toast.makeText(context,s, Toast.LENGTH_LONG).show();
            Toast.makeText(context,s, Toast.LENGTH_LONG).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
