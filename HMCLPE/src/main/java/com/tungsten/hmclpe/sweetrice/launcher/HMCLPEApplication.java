package com.tungsten.hmclpe.sweetrice.launcher;

import android.app.Application;
import android.content.*;
import com.tungsten.hmclpe.sweetrice.utils.PropertiesFileParse;
import java.util.Properties;

public class HMCLPEApplication extends Application {
    private static Context context;
    public static Properties properties;
    public static SharedPreferences appOtherConfig;
    public static int thisDirectionValue = -1;
    public static int thisDirectionValueTip = -1;
    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * properties文件解析必须放到全局Application
         * 因为Application的onCreate方法只会在程序启动时有且运行一次，适用于全局共享变量数据
         * 向上和向下传递值时候如果传递的是频繁访问数据可不在经过意图传递数据值
         * 解决那些频繁分配内存对象导致程序崩溃问题比如Handler...
        **/
        properties = new PropertiesFileParse("config.properties", getApplicationContext()).getProperties();
        appOtherConfig = getSharedPreferences("config", Context.MODE_PRIVATE);
        context = this.getApplicationContext();
    }
    public static Context getContext(){
        return context;
    }
    public static void releaseContext(){
        context = null;
    }
}