package com.alley.album.base;

import android.app.Application;
import android.content.Context;

import com.alley.album.util.AppUtils;
import com.alley.album.util.DisplayToast;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;


/**
 * 此类中只做初始化，不要放入逻辑代码
 *
 * @author Phoenix
 * @date 2016-11-21 9:28
 */
public class BaseApplication extends Application {
    public static Context context;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        //Toast初始化
        DisplayToast.getInstance().init(getApplicationContext());

        //内存泄漏监控
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        refWatcher = LeakCanary.install(this);
    }

    /**
     * 获取上下文
     * @return Context
     */
    public static Context getContext() {
        return context;
    }

    /**
     * 内存泄漏检测
     *
     * @param context
     * @return
     */
    public static RefWatcher getRefWatcher(Context context) {
        BaseApplication application = (BaseApplication) context.getApplicationContext();

        if (AppUtils.isDebug(application.getApplicationContext(), AppUtils.getPackageName(application.getApplicationContext()))) {
            return application.refWatcher;
        }
        //内存泄漏检测, 发版时改为此配置
        return RefWatcher.DISABLED;
    }
}
