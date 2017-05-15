package com.alley.album.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alley.album.R;

import java.util.ArrayList;

/**
 * item对话框adapter
 *
 * @author Phoenix
 * @date 2017/2/13 9:17
 */
public class ItemDialogAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> listContent, listColor, listContentSize;

    public ItemDialogAdapter(Context context, ArrayList<String> listContent, ArrayList<String> listColor, ArrayList<String> listContentSize) {
        this.context = context;
        this.listContent = listContent;
        this.listColor = listColor;
        this.listContentSize = listContentSize;
    }
    @Override
    public int getCount() {
        return (listContent == null) ? 0 : listContent.size();
    }

    @Override
    public Object getItem(int position) {
        return listContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_list_dialog_content, null);
            viewHolder = new ViewHolder(convertView);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvContent.setText(listContent.get(position));

        //防止空指针或集合越界
        if (listContent != null && listColor != null && listColor.size() == listContent.size()) {
            viewHolder.tvContent.setTextColor(Color.parseColor(listColor.get(position)));
        }

        //防止空指针或集合越界
        if (listContent != null && listContentSize != null && listContentSize.size() == listContent.size()) {
            viewHolder.tvContent.setTextSize(Float.valueOf(listContentSize.get(position)));
        }

        return convertView;
    }

    static class ViewHolder {
        TextView tvContent;

        public ViewHolder(View view) {
            tvContent = (TextView) view.findViewById(R.id.tv_dialog_item_content);
            view.setTag(this);
        }
    }
}
