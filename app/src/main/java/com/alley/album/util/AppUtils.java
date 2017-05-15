package com.alley.album.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


/**
 * 有关APP的信息包括版本号，版本名，签名，安装路径等
 *
 * @author Phoenix
 * @date 2016-10-17 16:22
 */
public class AppUtils {

    private AppUtils() {
    }

    /**
     * 获取应用包名
     *
     * @param context 上下文信息
     * @return 包名
     */
    public static String getPackageName(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Should not be null");
        }
        return context.getPackageName();
    }

    /**
     * 判断是否是Debug
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isDebug(Context context, String packageName) {
        try {
            PackageInfo pkginfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if (pkginfo != null) {
                ApplicationInfo info = pkginfo.applicationInfo;
                return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            }
        } catch (Exception e) {
        }
        return false;
    }
}
