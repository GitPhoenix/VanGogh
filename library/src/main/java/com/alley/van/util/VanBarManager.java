package com.alley.van.util;


import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

/**
 * 系统状态栏、导航栏管理类，兼容Android 5.0及以上
 *
 * 主要的几个flag:
 * SYSTEM_UI_FLAG_HIDE_NAVIGATION 隐藏导航栏
 * SYSTEM_UI_FLAG_FULLSCREEN 字面意思是全屏显示，实际是状态栏会被隐藏而导航栏未作处理
 * SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION 导航栏不会被隐藏但布局会扩展到导航栏所在位置
 * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 状态栏不会被隐藏，但布局会扩展到状态栏所在位置
 *
 * @author Phoenix
 * @date 2017/2/27 9:47
 */
public class VanBarManager {
    private Activity activity;

    public VanBarManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * 设置状态栏颜色
     *
     * @param fullScreen 状态栏是否显示
     * @param color 绘制状态栏颜色
     */
    public void setStatusBarColor(boolean fullScreen, int color) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (fullScreen) {
            //Activity全屏显示，且状态栏被隐藏
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            //Activity全屏显示，但状态栏不会被隐藏覆盖，Activity顶端布局部分会被状态遮住
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        activity.getWindow().setStatusBarColor(color);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }

    /**
     * 设置底部导航栏颜色
     *
     * @param nav 导航栏是否透明
     * @param color 绘制导航栏颜色
     */
    public void setNavigationBarColor(boolean nav, int color) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        if (nav) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        activity.getWindow().setNavigationBarColor(color);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }

    /**
     * 设置状态栏、导航栏
     *
     * @param isNav       导航栏是否透明
     * @param navColor    导航栏颜色
     * @param fullScreen 状态栏是否显示
     * @param color    状态栏颜色
     */
    public void setTintBarColor(boolean isNav, int navColor, boolean fullScreen, int color) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (isNav) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        if (fullScreen) {
            //Activity全屏显示，且状态栏被隐藏
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            //Activity全屏显示，但状态栏不会被隐藏覆盖，Activity顶端布局部分会被状态遮住
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        activity.getWindow().setStatusBarColor(color);
        activity.getWindow().setNavigationBarColor(navColor);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    }
}
