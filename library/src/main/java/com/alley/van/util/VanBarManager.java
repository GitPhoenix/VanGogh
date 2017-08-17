package com.alley.van.util;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

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
    private int formerColor = -1;

    public VanBarManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * 设置状态栏颜色
     *
     * @param color 绘制状态栏颜色
     */
    public void setStatusBarColor(@ColorInt int color) {
        if (formerColor == color || color < 0) {
            return;
        }
        formerColor = color;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //4.4.2-5.0
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            addStatusBarView(color);
            return;
        }

        View decorView = window.getDecorView();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //5.0+
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            //            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(color);
            return;
        }

        //Activity全屏显示，但状态栏不会被隐藏覆盖，Activity顶端布局部分会被状态遮住
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //要使它生效，必须设置FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS属性，并且FLAG_TRANSLUCENT_STATUS没有设置。
        window.setStatusBarColor(color);
    }

    /**
     * 设置底部导航栏颜色
     *
     * @param nav 导航栏是否透明
     * @param color 绘制导航栏颜色
     */
    public void setNavigationBarColor(boolean nav, @ColorInt int color) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        if (nav) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().setNavigationBarColor(color);
    }

    /**
     * 当系统小于19的时候，设置状态栏颜色
     *
     * @param color
     */
    private void addStatusBarView(@ColorInt int color) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));

        View statusBarView = new View(activity);
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);

        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        decorView.addView(statusBarView);
    }

    /**
     * 注意不是设置ContentView的FitsSystemWindows, 而是设置ContentView的第一个子 View
     * 预留出系统 View 的空间。
     * 设置ContentView的FitsSystemWindows，Toast显示不正常，它的文字会超出黑色背景之外。
     *
     * @param fitSystemWindows
     */
    public void setFitSystemWindows(boolean fitSystemWindows) {
        Window window = activity.getWindow();
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            mChildView.setFitsSystemWindows(fitSystemWindows);
        }
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
