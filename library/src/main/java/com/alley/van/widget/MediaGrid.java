package com.alley.van.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alley.van.R;
import com.alley.van.model.MediaInfo;
import com.alley.van.model.VanConfig;


public class MediaGrid extends SquareFrameLayout implements View.OnClickListener {
    private ImageView mThumbnail;
    private CheckView mCheckView;
    private ImageView mGifTag;
    private TextView mVideoDuration;

    private MediaInfo mMedia;
    private PreBindInfo mPreBindInfo;
    private OnMediaGridClickListener mListener;

    public MediaGrid(Context context) {
        super(context);
        init(context);
    }

    public MediaGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_media_check, this, true);

        mThumbnail = (ImageView) findViewById(R.id.media_thumbnail);
        mCheckView = (CheckView) findViewById(R.id.check_view);
        mGifTag = (ImageView) findViewById(R.id.gif);
        mVideoDuration = (TextView) findViewById(R.id.video_duration);

        mThumbnail.setOnClickListener(this);
        mCheckView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            if (v == mThumbnail) {
                mListener.onThumbnailClicked(mThumbnail, mMedia, mPreBindInfo.mViewHolder);
            } else if (v == mCheckView) {
                mListener.onCheckViewClicked(mCheckView, mMedia, mPreBindInfo.mViewHolder);
            }
        }
    }

    public void preBindMedia(PreBindInfo info) {
        mPreBindInfo = info;
    }

    public void bindMedia(MediaInfo mediaInfo) {
        mMedia = mediaInfo;
        setGifTag();
        initCheckView();
        setImage();
        setVideoDuration();
    }

    public MediaInfo getMedia() {
        return mMedia;
    }

    private void setGifTag() {
        mGifTag.setVisibility(mMedia.isGif() ? View.VISIBLE : View.GONE);
    }

    private void initCheckView() {
        mCheckView.setCountable(mPreBindInfo.mCheckViewCountable);
    }

    public void setCheckEnabled(boolean enabled) {
        mCheckView.setEnabled(enabled);
    }

    public void setCheckVisibility(int visibility) {
        mCheckView.setVisibility(visibility);
    }

    public void setCheckedNum(int checkedNum) {
        mCheckView.setCheckedNum(checkedNum);
    }

    public void setChecked(boolean checked) {
        mCheckView.setChecked(checked);
    }

    private void setImage() {
        if (mMedia.isGif()) {
            VanConfig.getInstance().imageLoader.loadAnimatedGifThumbnail(getContext(), mPreBindInfo.mResize, mThumbnail, mMedia.getContentUri());
        } else {
            VanConfig.getInstance().imageLoader.loadThumbnail(getContext(), mPreBindInfo.mResize, mThumbnail, mMedia.getContentUri());
        }
    }

    private void setVideoDuration() {
        if (mMedia.isVideo()) {
            mVideoDuration.setVisibility(VISIBLE);
            mVideoDuration.setText(DateUtils.formatElapsedTime(mMedia.duration / 1000));
        } else {
            mVideoDuration.setVisibility(GONE);
        }
    }

    public void setOnMediaGridClickListener(OnMediaGridClickListener listener) {
        mListener = listener;
    }

    public void removeOnMediaGridClickListener() {
        mListener = null;
    }

    public interface OnMediaGridClickListener {

        void onThumbnailClicked(ImageView thumbnail, MediaInfo mediaInfo, RecyclerView.ViewHolder holder);

        void onCheckViewClicked(CheckView checkView, MediaInfo mediaInfo, RecyclerView.ViewHolder holder);
    }

    public static class PreBindInfo {
        int mResize;
        boolean mCheckViewCountable;
        RecyclerView.ViewHolder mViewHolder;

        public PreBindInfo(int resize, boolean checkViewCountable, RecyclerView.ViewHolder viewHolder) {
            mResize = resize;
            mCheckViewCountable = checkViewCountable;
            mViewHolder = viewHolder;
        }
    }

}
