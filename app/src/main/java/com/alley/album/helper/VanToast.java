package com.alley.album.helper;


import android.support.annotation.StringRes;

import com.alley.album.util.MToast;
import com.alley.van.helper.VanToastListener;


public class VanToast implements VanToastListener {

    @Override
    public void display(String content) {
        MToast.shortToast(content);
    }

    @Override
    public void display(@StringRes int content) {
        MToast.shortToast(content);
    }

    @Override
    public void cancel() {
        MToast.cancel();
    }
}
