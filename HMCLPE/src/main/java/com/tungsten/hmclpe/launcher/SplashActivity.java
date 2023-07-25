package com.tungsten.hmclpe.launcher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FileChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.setting.InitializeSetting;
import com.tungsten.hmclpe.launcher.setting.InstallLauncherFile;
import com.tungsten.hmclpe.launcher.setting.launcher.LauncherSetting;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.LocaleUtils;
import com.tungsten.hmclpe.utils.PropertiesFileParse;
import com.tungsten.hmclpe.utils.file.UriUtils;
import com.tungsten.hmclpe.utils.io.FileUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.Properties;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    public ProgressBar loadingProgress;
    public TextView loadingText;
    public TextView loadingProgressText;
    public ImageView titleImage;
    public ImageView background;
    public LauncherSetting launcherSetting;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HMCLPEApplication.properties = new PropertiesFileParse("config.properties", getApplicationContext()).getProperties();
        HMCLPEApplication.appOtherConfig = getSharedPreferences("config", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_splash);
        loadingProgress = findViewById(R.id.loading_progress_bar);
        loadingText = findViewById(R.id.loading_text);
        loadingProgressText = findViewById(R.id.loading_progress_text);
        titleImage = findViewById(R.id.title_image);
        background = findViewById(R.id.background);
        initTheme();
        requestPermission();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.setLanguage(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.setLanguage(this);
    }

    private void initTheme() {
        File themePath = getExternalFilesDir("Theme");
        if (!themePath.exists()) {
            return;
        }
        changeIcon(background, themePath, "splashBackground");
    }

    private void changeIcon(View view, File themePath, String iconName) {
        File path = new File(themePath, iconName + ".png");
        if (path.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path.getAbsolutePath());
            if (view instanceof ImageView) {
                ((ImageView) view).setImageBitmap(bitmap);
            } else {
                view.setBackground(new BitmapDrawable(getResources(), bitmap));
            }
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (Environment.isExternalStorageManager()) {
                init();
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1000);
            }
        } else {
            // 先判断有没有权限
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                init();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
            }
        }
    }

    private void init() {
        new Thread(() -> {
            AppManifest.initializeManifest(SplashActivity.this);
            launcherSetting = InitializeSetting.initializeLauncherSetting();
            runOnUiThread(() -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (launcherSetting.fullscreen) {
                        getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                    } else {
                        getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
                    }
                }
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
            });

            InstallLauncherFile.checkLauncherFiles(SplashActivity.this);
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //通过requestCode来识别是否同一个请求
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户同意，执行操作
                init();
            } else {
                //用户不同意，向用户展示该权限作用
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setCancelable(false).setMessage(R.string.storage_permissions_remind).setPositiveButton("授予权限", (dialog1, which) -> ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000)).setNegativeButton("关闭应用", (dialog2, which) -> System.exit(0));
                    final AlertDialog dialog = alert.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                init();
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setCancelable(false).setMessage(R.string.storage_permissions_remind).setPositiveButton("授予权限", (dialog1, which) -> requestPermission()).setNegativeButton("关闭应用", (dialog2, which) -> System.exit(0));
                final AlertDialog dialog = alert.create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
