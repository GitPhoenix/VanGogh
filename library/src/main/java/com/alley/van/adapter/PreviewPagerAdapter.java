package com.alley.van.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.alley.van.fragment.VanPreviewFragment;
import com.alley.van.model.MediaInfo;

import java.util.ArrayList;
import java.util.List;


public class PreviewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<MediaInfo> mMediaInfos = new ArrayList<>();
    private OnPrimaryItemSetListener mListener;

    public PreviewPagerAdapter(FragmentManager manager, OnPrimaryItemSetListener listener) {
        super(manager);
        mListener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        return VanPreviewFragment.newInstance(mMediaInfos.get(position));
    }

    @Override
    public int getCount() {
        return mMediaInfos.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (mListener != null) {
            mListener.onPrimaryItemSet(position);
        }
    }

    public MediaInfo getMediaItem(int position) {
        return mMediaInfos.get(position);
    }

    public void addAll(List<MediaInfo> mediaInfos) {
        mMediaInfos.addAll(mediaInfos);
    }

    interface OnPrimaryItemSetListener {

        void onPrimaryItemSet(int position);
    }

}
