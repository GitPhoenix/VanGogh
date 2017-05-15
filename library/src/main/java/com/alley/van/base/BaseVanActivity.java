package com.alley.van.base;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.alley.van.R;
import com.alley.van.model.VanConfig;
import com.alley.van.util.ResUtils;
import com.alley.van.util.VanBarManager;


/**
 * 图片处理库，activity基类
 *
 * @author Phoenix
 * @date 2017/5/2 22:46
 */
public abstract class BaseVanActivity extends AppCompatActivity {
    protected String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(VanConfig.getInstance().themeID);

        TypedArray ta = getTheme().obtainStyledAttributes(new int[]{
                R.attr.colorPrimaryDark
        });
        int color = ta.getColor(0, ResUtils.getColor(getApplicationContext(), R.color.vanPrimaryDark));
        ta.recycle();
        VanBarManager barManager = new VanBarManager(this);
        barManager.setStatusBarColor(false, color);

        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());

        initView(savedInstanceState);

        setSubView();

        initEvent();
    }


    /**
     * 获取布局文件ID
     *
     * @return
     */
    protected abstract int getLayoutID();

    /**
     * 初始化视图控件
     *
     * @param savedInstanceState
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 填充视图
     */
    protected abstract void setSubView();

    /**
     * 初始化事件监听
     *
     */
    protected abstract void initEvent();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
