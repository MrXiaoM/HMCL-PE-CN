package com.tungsten.hmclpe.sweetrice.utils.activity;

import android.content.Context;
import android.webkit.CookieManager;

import com.tungsten.hmclpe.sweetrice.utils.file.FileUtils;

public class ActivityUtils {

    public static void clearCacheFiles(Context context){

    }

    public static void clearWebViewCache(Context context) {
        String WEB_VIEW_CACHE_DIR = context.getDir("webview",0).getAbsolutePath();
        FileUtils.deleteDirectory(WEB_VIEW_CACHE_DIR);
        CookieManager.getInstance().removeAllCookies(null);
    }

}
