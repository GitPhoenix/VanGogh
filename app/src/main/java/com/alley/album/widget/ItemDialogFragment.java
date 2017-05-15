package com.alley.album.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alley.album.R;
import com.alley.album.adapter.ItemDialogAdapter;
import com.alley.album.base.BaseDialogFragment;
import com.alley.album.util.ScreenUtils;

import java.util.ArrayList;

/**
 * 仿QQ退出对话框
 *
 * @author Phoenix
 * @date 2016-10-17 16:23
 */
public class ItemDialogFragment extends BaseDialogFragment implements View.OnClickListener {
    /**
     * item文本
     */
    public static final String DIALOG_ITEM_CONTENT = "dialog.item.content";
    /**
     * item字体颜色
     */
    public static final String DIALOG_ITEM_COLOR = "dialog.item.color";
    /**
     * item文本字体大小
     */
    public static final String DIALOG_ITEM_CONTENT_SIZE = "dialog.item.content.size";
    /**
     * 底部按钮文本
     */
    public static final String DIALOG_CANCEL = "dialog.cancel";

    private ListView lvItem;
    private TextView tvCancel;
    private String dialogCancel;
    private ArrayList<String> listContent, listColor, listContentSize;
    private OnItemClickDialogListener onItemClickDialogListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listContent = bundle.getStringArrayList(DIALOG_ITEM_CONTENT);
        listColor = bundle.getStringArrayList(DIALOG_ITEM_COLOR);
        listContentSize = bundle.getStringArrayList(DIALOG_ITEM_CONTENT_SIZE);
        dialogCancel = bundle.getString(DIALOG_CANCEL);
    }

    @Override
    public void onStart() {
        super.onStart();
        //设置对话框显示在底部
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        //设置让对话框宽度充满屏幕
        getDialog().getWindow().setLayout(ScreenUtils.getScreenWidth(activity), getDialog().getWindow().getAttributes().height);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.view_dialog_item;
    }

    @Override
    protected void initView(View view) {
        //设置对话框弹出动画，从底部滑入，从底部滑出
        getDialog().getWindow().getAttributes().windowAnimations = R.style.Dialog_Animation;

        lvItem = (ListView) view.findViewById(R.id.lv_dialog_item);
        tvCancel = (TextView) view.findViewById(R.id.tv_dialog_item_cancel);
    }

    @Override
    protected void setSubView() {
        tvCancel.setText(dialogCancel);

        ItemDialogAdapter adapter = new ItemDialogAdapter(activity, listContent, listColor, listContentSize);
        lvItem.setAdapter(adapter);
    }

    @Override
    protected void initEvent() {
        tvCancel.setOnClickListener(this);

        lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onItemClickDialogListener != null) {
                    onItemClickDialogListener.onItemClick(position, listContent.get(position));
                }
            }
        });
    }

    @Override
    protected void onCancel() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_dialog_item_cancel:
                if (onItemClickDialogListener != null) {
                    onItemClickDialogListener.onCancel(tvCancel);
                }
                break;

            default:
                break;
        }
    }

    public interface OnItemClickDialogListener {
        /**
         * item
         *
         * @param position 点击item的索引
         * @param content  item的内容
         */
        void onItemClick(int position, String content);

        /**
         * 取消对话框
         *
         * @param tvCancel 取消按钮
         */
        void onCancel(TextView tvCancel);
    }

    /**
     * 对外开放的方法
     *
     * @param onItemClickDialogListener
     */
    public void setOnItemClickDialogListener(OnItemClickDialogListener onItemClickDialogListener) {
        this.onItemClickDialogListener = onItemClickDialogListener;
    }
}
