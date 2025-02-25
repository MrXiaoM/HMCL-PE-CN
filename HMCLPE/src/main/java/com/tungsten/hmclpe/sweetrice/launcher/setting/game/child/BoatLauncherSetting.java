package com.tungsten.hmclpe.sweetrice.launcher.setting.game.child;

import androidx.annotation.NonNull;

public class BoatLauncherSetting implements Cloneable {

    public boolean enable;
    public String renderer;
    public String java;

    public BoatLauncherSetting(boolean enable,String renderer,String java){
        this.enable = enable;
        this.renderer = renderer;
        this.java = java;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setRenderer(String renderer) {
        this.renderer = renderer;
    }

    public String getRenderer() {
        return renderer;
    }

    public void setJava(String java) {
        this.java = java;
    }

    public String getJava() {
        return java;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
