package com.alley.van.helper;

import android.support.annotation.StringRes;

public interface VanToastListener {

    /**
     * toast提示信息
     *
     * @param content
     */
    void display(String content);

    /**
     * toast提示信息
     *
     * @param content
     */
    void display(@StringRes int content);

    /**
     * 取消消息显示
     */
    void cancel();
}
