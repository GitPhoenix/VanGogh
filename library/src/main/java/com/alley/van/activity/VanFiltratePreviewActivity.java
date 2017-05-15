package com.alley.van.activity;

import android.os.Bundle;

import com.alley.van.base.BasePreviewActivity;
import com.alley.van.model.MediaInfo;
import com.alley.van.model.SelectedItemCollection;

import java.util.List;


public class VanFiltratePreviewActivity extends BasePreviewActivity {

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

    }

    @Override
    protected void setSubView() {
        super.setSubView();
        Bundle bundle = getIntent().getBundleExtra(EXTRA_DEFAULT_BUNDLE);
        List<MediaInfo> selected = bundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
        mAdapter.addAll(selected);
        mAdapter.notifyDataSetChanged();
        if (vanConfig.countable) {
            mCheckView.setCheckedNum(1);
        } else {
            mCheckView.setChecked(true);
        }
        mPreviousPos = 0;
        updateSize(selected.get(0));
    }

    @Override
    protected void initEvent() {
        super.initEvent();
    }
}
