package com.alley.album.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alley.album.R;


/**
 * 自定义toast, 使用的时候请在Application 中进行初始化
 *
 * @author Phoenix
 * @date 2016-8-3 14:57
 */
public class DisplayToast {
	private Toast toast;
	private TextView tvToast;
	
	private DisplayToast() {}

	public static DisplayToast getInstance() {
		return DisplayToastHolder.INSTANCE;
	}

	private static class DisplayToastHolder {
		private static final DisplayToast INSTANCE = new DisplayToast();
	}

	/**
	 * 初始化，单列
	 *
	 * @param context 应用级上下文
	 */
	public void init(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.view_toast, null);
		tvToast = (TextView) view.findViewById(R.id.tv_toast);
		toast = new Toast(context);
		toast.setView(view);
	}
	
	public void display(CharSequence content, int duration) {
		if (TextUtils.isEmpty(content)) {
			return;
		}
		tvToast.setText(content);
		toast.setDuration(duration);
		toast.show();
	}
	
	public void display(@StringRes int resId, int duration) {
		tvToast.setText(resId);
		toast.setDuration(duration);
		toast.show();
	}

	public void dismiss() {
		toast.cancel();
	}
}
