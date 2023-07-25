package com.tungsten.hmclpe.sweetrice.utils.platform;

import android.app.ActivityManager;
import android.content.Context;

public class MemoryUtils {

    public static int getTotalDeviceMemory(Context ctx){
        ActivityManager actManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        return (int) (memInfo.totalMem / 1048576L);
    }

    public static int getFreeDeviceMemory(Context ctx){
        ActivityManager actManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        return (int) (memInfo.availMem / 1048576L);
    }

    public static int findBestRAMAllocation(Context context) {
        int totalDeviceMemory = getTotalDeviceMemory(context);
        if (totalDeviceMemory <= 3072) {
            return 1300;
        } else if (totalDeviceMemory <= 4096) {
            return 1700;
        } else if (totalDeviceMemory <= 6144) {
            return 2048;
        }  else if (totalDeviceMemory <= 8192) {
            return 2600;
        } else {
            return 3200;
        }
    }

}
