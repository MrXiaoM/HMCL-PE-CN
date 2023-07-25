package com.tungsten.hmclpe.sweetrice.manifest;

import android.content.Context;
import android.os.Environment;
import com.tungsten.hmclpe.sweetrice.launcher.HMCLPEApplication;
import com.tungsten.hmclpe.sweetrice.utils.file.FileUtils;

public class AppManifest {
    public static String LAUNCHER_DIR = Environment.getExternalStorageDirectory() + "/" + HMCLPEApplication.properties.getProperty("put-directory");
    public static String DEFAULT_GAME_DIR = LAUNCHER_DIR + "/.minecraft";
    public static String INNER_DIR;
    public static String INNER_FILE_DIR;
    public static String EXTERNAL_DIR;
    public static String ACCOUNT_DIR;
    public static String GAME_FILE_DIRECTORY_DIR;
    public static String SETTING_DIR;
    public static String CONTROLLER_DIR;
    public static String PLUGIN_DIR;
    public static String STYLE_DIR;
    public static String DEBUG_DIR;
    public static String INNER_GAME_DIR;
    public static String DEFAULT_CACHE_DIR;
    public static String INSTALL_DIR;
    public static String SAVES_CACHE_DIR;
    public static String DEFAULT_RUNTIME_DIR;
    public static String JAVA_DIR;
    public static String CACIOCAVALLO_DIR;
    public static String BOAT_LIB_DIR;
    public static String POJAV_LIB_DIR;
    public static void initializeManifest (Context context){
        INNER_DIR = context.getFilesDir().getParent();
        INNER_FILE_DIR = context.getFilesDir().getAbsolutePath();
        EXTERNAL_DIR = context.getExternalCacheDir().getParent();
        ACCOUNT_DIR = INNER_FILE_DIR + "/accounts";
        GAME_FILE_DIRECTORY_DIR = INNER_FILE_DIR + "/paths";
        SETTING_DIR = INNER_FILE_DIR + "/settings";
        CONTROLLER_DIR = INNER_FILE_DIR + "/control";
        PLUGIN_DIR = INNER_FILE_DIR + "/plugin";
        STYLE_DIR = INNER_FILE_DIR + "/style";
        //通过getExternalFilesDir方法获取目录路径若不存在会自动创建一个这个目录
        DEBUG_DIR = context.getExternalFilesDir("debug").getAbsolutePath();
        INNER_GAME_DIR = context.getExternalFilesDir(null).getAbsolutePath() + "/.minecraft";
        if("true".equals(HMCLPEApplication.properties.getProperty("enable-private-directory-mode"))){
            DEFAULT_GAME_DIR = INNER_GAME_DIR;
        }
        DEFAULT_CACHE_DIR = context.getCacheDir().getAbsolutePath();
        INSTALL_DIR = DEFAULT_CACHE_DIR + "/install";
        SAVES_CACHE_DIR = DEFAULT_CACHE_DIR + "/saves";
        DEFAULT_RUNTIME_DIR = context.getDir("runtime",0).getAbsolutePath();
        JAVA_DIR = DEFAULT_RUNTIME_DIR + "/java";
        CACIOCAVALLO_DIR = DEFAULT_RUNTIME_DIR + "/caciocavallo";
        BOAT_LIB_DIR = DEFAULT_RUNTIME_DIR + "/boat";
        POJAV_LIB_DIR = DEFAULT_RUNTIME_DIR + "/pojav";
        /**
        FileUtils.createDirectory(LAUNCHER_DIR);
        FileUtils.createDirectory(DEFAULT_GAME_DIR);
        **/
        FileUtils.createDirectory(INNER_DIR);
        FileUtils.createDirectory(INNER_FILE_DIR);
        FileUtils.createDirectory(EXTERNAL_DIR);
        FileUtils.createDirectory(ACCOUNT_DIR);
        FileUtils.createDirectory(GAME_FILE_DIRECTORY_DIR);
        FileUtils.createDirectory(SETTING_DIR);
        FileUtils.createDirectory(CONTROLLER_DIR);
        FileUtils.createDirectory(INSTALL_DIR);
        FileUtils.createDirectory(PLUGIN_DIR);
        FileUtils.createDirectory(STYLE_DIR);
        FileUtils.createDirectory(DEBUG_DIR);
        //FileUtils.createDirectory(INNER_GAME_DIR);
        FileUtils.createDirectory(DEFAULT_CACHE_DIR);
        FileUtils.createDirectory(INSTALL_DIR);
        FileUtils.createDirectory(SAVES_CACHE_DIR);
        FileUtils.createDirectory(DEFAULT_RUNTIME_DIR);
        FileUtils.createDirectory(JAVA_DIR);
        FileUtils.createDirectory(CACIOCAVALLO_DIR);
        FileUtils.createDirectory(BOAT_LIB_DIR);
        FileUtils.createDirectory(POJAV_LIB_DIR);
    }

}
