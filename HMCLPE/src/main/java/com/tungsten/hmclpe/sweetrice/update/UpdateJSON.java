package com.tungsten.hmclpe.sweetrice.update;

public class UpdateJSON {

    public LauncherVersion latestRelease;
    public LauncherVersion latestPrerelease;

    public UpdateJSON (LauncherVersion latestRelease,LauncherVersion latestPrerelease) {
        this.latestRelease = latestRelease;
        this.latestPrerelease = latestPrerelease;
    }

}
