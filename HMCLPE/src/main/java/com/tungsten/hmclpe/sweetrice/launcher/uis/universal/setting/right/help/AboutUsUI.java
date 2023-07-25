package com.tungsten.hmclpe.sweetrice.launcher.uis.universal.setting.right.help;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.tungsten.hmclpe.sweetrice.R;
import com.tungsten.hmclpe.sweetrice.launcher.HMCLPEApplication;
import com.tungsten.hmclpe.sweetrice.launcher.MainActivity;
import com.tungsten.hmclpe.sweetrice.launcher.dialogs.ContributorListDialog;
import com.tungsten.hmclpe.sweetrice.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.sweetrice.utils.animation.CustomAnimationUtils;

public class AboutUsUI extends BaseUI implements View.OnClickListener {

    public LinearLayout aboutUsUI;

    private ImageButton hmclpe;
    private ImageButton mrxiaom;
    private ImageButton tungs;
    private ImageButton mio;

    private ImageButton community;
    private ImageButton cosine;
    private ImageButton legacy;
    private ImageButton saltfish;
    private ImageButton hmcl;
    private ImageButton bangbang93;
    private ImageButton mcbbs;
    private ImageButton mcmod;

    private ImageButton qq;

    private ImageButton libHmcl;
    private ImageButton libBoat;
    private ImageButton libPojav;
    private ImageButton libGson;
    private ImageButton libTheme;
    private ImageButton libOkhttp;
    private ImageButton libNano;
    private ImageButton libCrash;
    private ImageButton libCommonC;
    private ImageButton libCommonL;
    private ImageButton libOpenNBT;
    private ImageButton libColor;
    private ImageButton libDevice;
    private ImageButton libJsoup;
    private ImageButton libPool;

    private ImageButton copyright;
    private ImageButton eula;
    private ImageButton openSource;

    public AboutUsUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        aboutUsUI = activity.findViewById(R.id.ui_about);

        hmclpe = activity.findViewById(R.id.hmclpe_link);
        mrxiaom = activity.findViewById(R.id.mrxiaom_link);
        tungs = activity.findViewById(R.id.tungs_link);
        mio = activity.findViewById(R.id.mio_link);

        community = activity.findViewById(R.id.community_developer_link);
        cosine = activity.findViewById(R.id.cosine_link);
        legacy = activity.findViewById(R.id.legacy_link);
        saltfish = activity.findViewById(R.id.saltfish_link);
        hmcl = activity.findViewById(R.id.hmcl_link);
        bangbang93 = activity.findViewById(R.id.bangbang93_link);
        mcbbs = activity.findViewById(R.id.mcbbs_link);
        mcmod = activity.findViewById(R.id.mcmod_link);

        qq = activity.findViewById(R.id.qq_link);

        libHmcl = activity.findViewById(R.id.lib_hmcl_link);
        libBoat = activity.findViewById(R.id.lib_boat_link);
        libPojav = activity.findViewById(R.id.lib_pojav_link);
        libGson = activity.findViewById(R.id.lib_gson_link);
        libTheme = activity.findViewById(R.id.lib_theme_link);
        libOkhttp = activity.findViewById(R.id.lib_okhttp_link);
        libNano = activity.findViewById(R.id.lib_nano_link);
        libCrash = activity.findViewById(R.id.lib_crash_link);
        libCommonC = activity.findViewById(R.id.lib_commonc_link);
        libCommonL = activity.findViewById(R.id.lib_commonl_link);
        libOpenNBT = activity.findViewById(R.id.lib_opennbt_link);
        libColor = activity.findViewById(R.id.lib_color_link);
        libDevice = activity.findViewById(R.id.lib_device_link);
        libJsoup = activity.findViewById(R.id.lib_jsoup_link);
        libPool = activity.findViewById(R.id.lib_pool_link);

        copyright = activity.findViewById(R.id.copyright_link);
        eula = activity.findViewById(R.id.eula_link);
        openSource = activity.findViewById(R.id.open_source_link);

        hmclpe.setOnClickListener(this);
        mrxiaom.setOnClickListener(this);
        tungs.setOnClickListener(this);
        mio.setOnClickListener(this);

        community.setOnClickListener(this);
        cosine.setOnClickListener(this);
        legacy.setOnClickListener(this);
        saltfish.setOnClickListener(this);
        hmcl.setOnClickListener(this);
        bangbang93.setOnClickListener(this);
        mcbbs.setOnClickListener(this);
        mcmod.setOnClickListener(this);

        qq.setOnClickListener(this);

        libHmcl.setOnClickListener(this);
        libBoat.setOnClickListener(this);
        libPojav.setOnClickListener(this);
        libGson.setOnClickListener(this);
        libTheme.setOnClickListener(this);
        libOkhttp.setOnClickListener(this);
        libNano.setOnClickListener(this);
        libCrash.setOnClickListener(this);
        libCommonC.setOnClickListener(this);
        libCommonL.setOnClickListener(this);
        libOpenNBT.setOnClickListener(this);
        libColor.setOnClickListener(this);
        libDevice.setOnClickListener(this);
        libJsoup.setOnClickListener(this);
        libPool.setOnClickListener(this);

        copyright.setOnClickListener(this);
        eula.setOnClickListener(this);
        openSource.setOnClickListener(this);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(aboutUsUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.settingUI.startAboutUsUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(aboutUsUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.settingUI.startAboutUsUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
    }

    @Override
    public void onClick(View view) {
        Uri uri = null;

        if (view == hmclpe) {
            uri = Uri.parse("https://github.com/MrXiaoM/HMCL-PE-CN");
        }
        if (view == mrxiaom) {
            uri = Uri.parse("https://space.bilibili.com/330771760");
        }
        if (view == tungs) {
            uri = Uri.parse("https://space.bilibili.com/18115101");
        }
        if (view == mio) {
            uri = Uri.parse("https://space.bilibili.com/35801833");
        }

        if (view == community) {
            ContributorListDialog dialog = new ContributorListDialog(context);
            dialog.show();
        }
        if (view == cosine) {
            uri = Uri.parse("https://github.com/CosineMath");
        }
        if (view == legacy) {
            uri = Uri.parse("https://github.com/LegacyGamerHD");
        }
        if (view == saltfish) {
            uri = Uri.parse("https://github.com/TSaltedfishKing");
        }
        if (view == hmcl) {
            uri = Uri.parse("https://hmcl.huangyuhui.net");
        }
        if (view == bangbang93) {
            uri = Uri.parse("https://bmclapidoc.bangbang93.com");
        }
        if (view == mcbbs) {
            uri = Uri.parse("https://www.mcbbs.net");
        }
        if (view == mcmod) {
            uri = Uri.parse("https://www.mcmod.cn");
        }
/*
        if (view == discord) {
            uri = Uri.parse(HMCLPEApplication.properties.getProperty("discord"));
        }
        if (view == qqChannel) {
            uri = Uri.parse(HMCLPEApplication.properties.getProperty("qq-discord"));
        }
 */
        if (view == qq) {
            FeedbackUI.joinQQGroup(context, HMCLPEApplication.properties.getProperty("qq-group-key"));
        }
        if (view == libHmcl) {
            uri = Uri.parse("https://github.com/huanghongxun/HMCL");
        }
        if (view == libBoat) {
            uri = Uri.parse("https://github.com/AOF-Dev/Boat");
        }
        if (view == libPojav) {
            uri = Uri.parse("https://github.com/PojavLauncherTeam/PojavLauncher");
        }
        if (view == libGson) {
            uri = Uri.parse("https://github.com/google/gson");
        }
        if (view == libTheme) {
            uri = Uri.parse("https://github.com/naman14/app-theme-engine");
        }
        if (view == libOkhttp) {
            uri = Uri.parse("https://github.com/square/okhttp");
        }
        if (view == libNano) {
            uri = Uri.parse("https://github.com/NanoHttpd/nanohttpd");
        }
        if (view == libCrash) {
            uri = Uri.parse("https://github.com/Ereza/CustomActivityOnCrash");
        }
        if (view == libCommonC) {
            uri = Uri.parse("https://github.com/apache/commons-compress");
        }
        if (view == libCommonL) {
            uri = Uri.parse("https://github.com/apache/commons-lang");
        }
        if (view == libOpenNBT) {
            uri = Uri.parse("https://github.com/GeyserMC/OpenNBT");
        }
        if (view == libColor) {
            uri = Uri.parse("https://github.com/jaredrummler/ColorPicker");
        }
        if (view == libDevice) {
            uri = Uri.parse("https://github.com/jaredrummler/AndroidDeviceNames");
        }
        if (view == libJsoup) {
            uri = Uri.parse("https://github.com/jhy/jsoup");
        }
        if (view == libPool) {
            uri = Uri.parse("https://github.com/jenkinsci/constant-pool-scanner");
        }

        if (view == copyright) {
            uri = Uri.parse("https://tungstend.github.io/pages/about.html");
        }
        if (view == eula) {
            uri = Uri.parse("https://tungstend.github.io/pages/eula.html");
        }
        if (view == openSource) {
            uri = Uri.parse("https://github.com/MrXiaoM/HMCL-PE-CN");
        }

        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }

}
