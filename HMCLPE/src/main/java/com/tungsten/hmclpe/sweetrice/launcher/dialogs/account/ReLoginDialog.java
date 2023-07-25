package com.tungsten.hmclpe.sweetrice.launcher.dialogs.account;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.*;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.tungsten.hmclpe.sweetrice.R;
import com.tungsten.hmclpe.sweetrice.auth.*;
import com.tungsten.hmclpe.sweetrice.auth.yggdrasil.*;
import com.tungsten.hmclpe.sweetrice.skin.utils.Avatar;
import com.tungsten.hmclpe.sweetrice.utils.gson.UUIDTypeAdapter;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReLoginDialog extends Dialog implements View.OnClickListener {

    private String email;
    private YggdrasilService yggdrasilService;
    private Account account;
    private ReloginCallback callback;

    private TextView emailText;
    private EditText editPassword;
    private Button positive;
    private Button negative;
    private ProgressBar progressBar;

    public ReLoginDialog(@NonNull Context context,String email,YggdrasilService yggdrasilService,Account account,ReloginCallback callback) {
        super(context);
        this.email = email;
        this.yggdrasilService = yggdrasilService;
        this.account = account;
        this.callback = callback;
        setContentView(R.layout.dialog_relogin);
        setCancelable(false);
        init();
    }

    private void init(){
        emailText = findViewById(R.id.relogin_email);
        editPassword = findViewById(R.id.edit_password);
        positive = findViewById(R.id.relogin);
        negative = findViewById(R.id.cancel_relogin);
        progressBar = findViewById(R.id.login_progress);

        positive.setOnClickListener(this);
        negative.setOnClickListener(this);

        emailText.setText(email);
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            progressBar.setVisibility(View.VISIBLE);
            positive.setVisibility(View.GONE);
            negative.setEnabled(false);
            String password = editPassword.getText().toString();
            AtomicBoolean haveManyAccount = new AtomicBoolean(false);
            Thread thread = new Thread(() -> {
                try {
                    YggdrasilSession yggdrasilSession = yggdrasilService.authenticate(email, password, UUIDTypeAdapter.fromUUID(UUID.randomUUID()));
                    if(yggdrasilSession.getAvailableProfiles().size() > 1){
                        haveManyAccount.set(true);
                    }else{
                        haveManyAccount.set(false);
                        AuthInfo authInfo = yggdrasilSession.toAuthInfo();
                        Map<TextureType, Texture> map = YggdrasilService.getTextures(yggdrasilService.getCompleteGameProfile(authInfo.getUUID()).get()).get();
                        Texture texture = map.get(TextureType.SKIN);
                        Bitmap skin;
                        if (texture == null){
                            AssetManager manager = getContext().getAssets();
                            InputStream inputStream;
                            inputStream = manager.open("img/alex.png");
                            skin = BitmapFactory.decodeStream(inputStream);
                        }else{
                            String u = texture.getUrl();
                            URL url = new URL(u);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setDoInput(true);
                            httpURLConnection.connect();
                            InputStream inputStream = httpURLConnection.getInputStream();
                            skin = BitmapFactory.decodeStream(inputStream);
                        }
                        loginHandler.post(() -> {
                            String skinTexture = Avatar.bitmapToString(skin);
                            Account newAccount = new Account(4,
                                    email,
                                    password,
                                    account.user_type,
                                    account.auth_session,
                                    yggdrasilSession.getSelectedProfile().getName(),
                                    authInfo.getUUID().toString(),
                                    authInfo.getAccessToken(),
                                    yggdrasilSession.getClientToken(),
                                    account.refresh_token,
                                    account.loginServer,
                                    skinTexture);
                            callback.onRelogin(newAccount);
                            dismiss();
                        });
                    }
                } catch (AuthenticationException | IOException e) {
                    e.printStackTrace();
                    loginHandler.sendEmptyMessage(1);
                }
                loginHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    positive.setVisibility(View.VISIBLE);
                    negative.setEnabled(true);
                    if(haveManyAccount.get()){
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                        alertDialog.setCancelable(false);
                        alertDialog.setTitle("错误");
                        alertDialog.setMessage("检测到当前账户登录已失效，请去启动器账户页面删除对应账户后重新添加！");
                        alertDialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                callback.onCancel();
                                dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                });
            });
            thread.start();
        }
        if (view == negative) {
            callback.onCancel();
            dismiss();
        }
    }

    @SuppressLint("HandlerLeak")
    public final Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(getContext(), getContext().getString(R.string.dialog_add_authlib_injector_account_failed), Toast.LENGTH_LONG).show();
            }
        }
    };

    public interface ReloginCallback{
        void onRelogin(Account account);
        void onCancel();
    }
}
