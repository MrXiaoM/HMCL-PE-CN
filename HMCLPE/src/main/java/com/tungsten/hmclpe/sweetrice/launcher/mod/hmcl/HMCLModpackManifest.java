package com.tungsten.hmclpe.sweetrice.launcher.mod.hmcl;

import com.tungsten.hmclpe.sweetrice.launcher.mod.ModpackManifest;
import com.tungsten.hmclpe.sweetrice.launcher.mod.ModpackProvider;

public final class HMCLModpackManifest implements ModpackManifest {
    public static final HMCLModpackManifest INSTANCE = new HMCLModpackManifest();

    private HMCLModpackManifest() {}

    @Override
    public ModpackProvider getProvider() {
        return HMCLModpackProvider.INSTANCE;
    }
}