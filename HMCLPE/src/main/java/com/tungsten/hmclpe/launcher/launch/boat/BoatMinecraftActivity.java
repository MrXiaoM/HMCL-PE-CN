package com.tungsten.hmclpe.launcher.launch.boat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.hardware.SensorManager;
import android.os.*;
import android.util.Log;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.InputBridge;
import com.tungsten.hmclpe.control.MenuHelper;
import com.tungsten.hmclpe.control.view.LayoutPanel;
import com.tungsten.hmclpe.launcher.HMCLPEApplication;
import com.tungsten.hmclpe.launcher.setting.game.GameLaunchSetting;
import com.tungsten.hmclpe.launcher.launch.MCOptionUtils;
import com.tungsten.hmclpe.utils.*;
import java.io.*;
import java.util.Vector;
import cosine.boat.BoatActivity;
import cosine.boat.BoatInput;
import cosine.boat.function.BoatCallback;
import cosine.boat.keyboard.BoatKeycodes;

public class BoatMinecraftActivity extends BoatActivity {
    public static int thisDirectionValue = -1;

    private GameLaunchSetting gameLaunchSetting;

    private DrawerLayout drawerLayout;
    private LayoutPanel baseLayout;

    public MenuHelper menuHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameLaunchSetting = GameLaunchSetting.getGameLaunchSetting(getIntent().getExtras().getString("setting_path"), getIntent().getExtras().getString("version"));
        //if (getIntent().getExtras().getBoolean("test") || gameLaunchSetting.log) {}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (gameLaunchSetting.fullscreen) {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            } else {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            }
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

        setContentView(cosine.boat.R.layout.activity_boat);

        DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(DrawerLayout.LayoutParams.MATCH_PARENT, DrawerLayout.LayoutParams.MATCH_PARENT);

        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_control_pattern, null);
        addContentView(drawerLayout, params);

        baseLayout = drawerLayout.findViewById(R.id.base_layout);

        scaleFactor = gameLaunchSetting.scaleFactor;

        handleCallback();

        init();

        HMCLPEApplication.thisDirectionValue = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        OrientationEventListener mOrientationListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if(orientation == OrientationEventListener.ORIENTATION_UNKNOWN){
                    return;  //手机平放时，检测不到有效的角度
                }else if((orientation > 80 && orientation < 100) || (orientation > 260 && orientation < 280)){ //横屏条件都允许
                    HMCLPEApplication.thisDirectionValue = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
                }
                if(HMCLPEApplication.thisDirectionValue != 3 && HMCLPEApplication.thisDirectionValueTip == -1){
                    HMCLPEApplication.thisDirectionValueTip = 0;
                    ToastUtils.toast("当前手机方向无法使用视角跟随功能,请将手机旋转到另外一个方向", BoatMinecraftActivity.this);
                }else if(HMCLPEApplication.thisDirectionValue == 3){
                    HMCLPEApplication.thisDirectionValueTip = -1;
                }
            }
        };
        if (mOrientationListener.canDetectOrientation()) {
            Log.v("按键事件", "方向检测已开启");
            mOrientationListener.enable();
        } else {
            Log.v("按键事件", "无法检测方向");
            mOrientationListener.disable();
        }

        menuHelper = new MenuHelper(this, this, gameLaunchSetting.fullscreen, gameLaunchSetting.game_directory, drawerLayout, baseLayout, false, gameLaunchSetting.controlLayout, 1, scaleFactor);
    }

    private void handleCallback() {
        setBoatCallback(new BoatCallback() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                surface.setDefaultBufferSize((int) (width * scaleFactor), (int) (height * scaleFactor));
                BoatInput.pushEventWindow(width,height);

                File options=new File(gameLaunchSetting.game_directory,"options.txt");
                if (!options.exists()){
                    try(FileOutputStream out=new FileOutputStream(options);InputStream in = BoatMinecraftActivity.this.getAssets().open("options.txt")){
                        byte[] b=new byte[in.available()];
                        in.read(b);
                        out.write(b);
                    }catch (Exception ignored){

                    }
                }
                MCOptionUtils.load(gameLaunchSetting.game_directory);
                MCOptionUtils.set("overrideWidth", String.valueOf((int) (width * scaleFactor)));
                MCOptionUtils.set("overrideHeight", String.valueOf((int) (height * scaleFactor)));
                MCOptionUtils.set("fullscreen", "false");
                MCOptionUtils.save(gameLaunchSetting.game_directory);

                new Thread(() -> {
                    Vector<String> args = BoatLauncher.getMcArgs(gameLaunchSetting, BoatMinecraftActivity.this, (int) (width * scaleFactor), (int) (height * scaleFactor), gameLaunchSetting.server);
                    runOnUiThread(() -> {
                        BoatActivity.setBoatNativeWindow(new Surface(surface));
                        BoatInput.setEventPipe();

                        startGame(gameLaunchSetting.javaPath,
                                gameLaunchSetting.home,
                                GameLaunchSetting.isHighVersion(gameLaunchSetting),
                                args,
                                gameLaunchSetting.boatRenderer,
                                gameLaunchSetting.game_directory);
                    });
                }).start();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                surface.setDefaultBufferSize((int) (width * scaleFactor), (int) (height * scaleFactor));
                BoatInput.pushEventWindow(width,height);
            }

            @Override
            public void onCursorModeChange(int mode) {
                cursorModeHandler.sendEmptyMessage(mode);
            }

            @Override
            public void onStart() {
                baseLayout.showBackground();
            }

            @Override
            public void onPicOutput() {
                baseLayout.hideBackground();
            }

            @Override
            public void onError(Exception e) {

            }

            @Override
            public void onExit(int code) {
                Intent virGLService = new Intent(BoatMinecraftActivity.this, VirGLService.class);
                stopService(virGLService);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private final Handler cursorModeHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == BoatInput.CursorDisabled) {
                menuHelper.disableCursor();
            }else if (msg.what == BoatInput.CursorEnabled) {
                menuHelper.enableCursor();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (menuHelper.gameMenuSetting.mousePatch) {
            if (event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                InputBridge.sendMouseEvent(1, InputBridge.MOUSE_RIGHT, true);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (menuHelper.gameMenuSetting.mousePatch) {
            if (event.getKeyCode()==KeyEvent.KEYCODE_BACK){
                InputBridge.sendMouseEvent(1, InputBridge.MOUSE_RIGHT, false);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        boolean mouse = false;
        final int[] devices = InputDevice.getDeviceIds();
        for (int j : devices) {
            InputDevice device = InputDevice.getDevice(j);
            if (device != null && !device.isVirtual()) {
                if (device.getName().contains("Mouse") || (menuHelper != null && menuHelper.touchCharInput != null && !menuHelper.touchCharInput.isEnabled())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && device.isExternal()) {
                        mouse = true;
                        break;
                    }
                    else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        mouse = true;
                        break;
                    }
                }
            }
        }
        if (!mouse) {
            BoatInput.setKey(BoatKeycodes.KEY_ESC, 0, true);
            BoatInput.setKey(BoatKeycodes.KEY_ESC, 0, false);
        }
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

    @Override
    protected void onPause() {
        if (menuHelper.viewManager != null && menuHelper.gameCursorMode == 1) {
            BoatInput.setKey(BoatKeycodes.KEY_ESC, 0, true);
            BoatInput.setKey(BoatKeycodes.KEY_ESC, 0, false);
        }
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (gameLaunchSetting.fullscreen) {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            } else {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            }
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
    }

    @Override
    protected void onDestroy() {
        Intent virGLService = new Intent(this, VirGLService.class);
        stopService(virGLService);
        super.onDestroy();
    }
}