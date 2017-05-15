package com.alley.van.activity;

import android.database.Cursor;
import android.os.Bundle;

import com.alley.van.adapter.PreviewPagerAdapter;
import com.alley.van.base.BasePreviewActivity;
import com.alley.van.model.Album;
import com.alley.van.model.AlbumMediaCollection;
import com.alley.van.model.MediaInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * 图片预览
 */
public class VanPreviewActivity extends BasePreviewActivity implements AlbumMediaCollection.AlbumMediaCallbacks {
    public static final String EXTRA_ALBUM = "extra_album";
    public static final String EXTRA_ITEM = "extra_item";

    private AlbumMediaCollection mCollection = new AlbumMediaCollection();
    private boolean mIsAlreadySetPosition;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

    }

    @Override
    protected void setSubView() {
        super.setSubView();
        mCollection.onCreate(this, this);
        Album album = getIntent().getParcelableExtra(EXTRA_ALBUM);
        mCollection.load(album);
        MediaInfo mediaInfo = getIntent().getParcelableExtra(EXTRA_ITEM);
        if (vanConfig.countable) {
            mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(mediaInfo));
        } else {
            mCheckView.setChecked(mSelectedCollection.isSelected(mediaInfo));
        }
        updateSize(mediaInfo);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCollection.onDestroy();
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        List<MediaInfo> mediaInfos = new ArrayList<>();
        while (cursor.moveToNext()) {
            mediaInfos.add(MediaInfo.valueOf(cursor));
        }
        PreviewPagerAdapter adapter = (PreviewPagerAdapter) viewPager.getAdapter();
        adapter.addAll(mediaInfos);
        adapter.notifyDataSetChanged();
        if (!mIsAlreadySetPosition) {
            //onAlbumMediaLoad is called many times..
            mIsAlreadySetPosition = true;
            MediaInfo selected = getIntent().getParcelableExtra(EXTRA_ITEM);
            int selectedIndex = mediaInfos.indexOf(selected);
            viewPager.setCurrentItem(selectedIndex, false);
            mPreviousPos = selectedIndex;
        }
    }

    @Override
    public void onAlbumMediaReset() {

    }
}
