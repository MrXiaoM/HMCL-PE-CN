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
import com.tungsten.hmclpe.sweetrice.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.sweetrice.utils.animation.CustomAnimationUtils;

public class FeedbackUI extends BaseUI implements View.OnClickListener {

    public LinearLayout feedbackUI;
    private ImageButton joinQQ;
    private ImageButton jumpToGit;

    public FeedbackUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        feedbackUI = activity.findViewById(R.id.ui_feedback);

        joinQQ = activity.findViewById(R.id.join_qq_group);
        jumpToGit = activity.findViewById(R.id.jump_to_git_issues);

        joinQQ.setOnClickListener(this);
        jumpToGit.setOnClickListener(this);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(feedbackUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.settingUI.startFeedbackUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(feedbackUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.settingUI.startFeedbackUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
    }

    /****************
     * 发起添加群流程。群号：我的世界&amp;nbsp;甜米服&amp;nbsp;&amp;nbsp;玩家(594212444) 的 key 为： 6ILfS-5KyOyUpysswfioH1WTdrsNI_qp
     * 调用 joinQQGroup(6ILfS-5KyOyUpysswfioH1WTdrsNI_qp) 即可发起手Q客户端申请加群 我的世界&amp;nbsp;甜米服&amp;nbsp;&amp;nbsp;玩家(594212444)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回false表示呼起失败
     ******************/
    public static boolean joinQQGroup(Context context, String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }


    @Override
    public void onClick(View v) {
        /*
        if (v == joinDiscord) {
            Uri uri = Uri.parse(HMCLPEApplication.properties.getProperty("discord"));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
        if (v == joinQQChannel) {
            Uri uri = Uri.parse(HMCLPEApplication.properties.getProperty("qq-discord"));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
        */
        if (v == joinQQ){
            joinQQGroup(context, HMCLPEApplication.properties.getProperty("qq-group-key"));
        }
        if (v == jumpToGit){
            Uri uri = Uri.parse("https://github.com/MrXiaoM/HMCL-PE-CN/issues");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
    }
}
