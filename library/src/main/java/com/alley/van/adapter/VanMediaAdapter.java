package com.alley.van.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alley.van.R;
import com.alley.van.model.Album;
import com.alley.van.model.IncapableCause;
import com.alley.van.model.MediaInfo;
import com.alley.van.model.SelectedItemCollection;
import com.alley.van.model.VanConfig;
import com.alley.van.widget.CheckView;
import com.alley.van.widget.MediaGrid;


public class VanMediaAdapter extends RecyclerViewCursorAdapter<RecyclerView.ViewHolder> implements MediaGrid.OnMediaGridClickListener {
    private static final int VIEW_TYPE_CAPTURE = 0x01;
    private static final int VIEW_TYPE_MEDIA = 0x02;
    private final SelectedItemCollection mSelectedCollection;
    private VanConfig mSelectionSpec;
    private CheckStateListener mCheckStateListener;
    private OnMediaClickListener mOnMediaClickListener;
    private RecyclerView mRecyclerView;
    private int mImageResize;

    public VanMediaAdapter(Context context, SelectedItemCollection selectedCollection, RecyclerView recyclerView) {
        super(null);
        mSelectionSpec = VanConfig.getInstance();
        mSelectedCollection = selectedCollection;

        mRecyclerView = recyclerView;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_CAPTURE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_camera, parent, false);
            CaptureViewHolder holder = new CaptureViewHolder(v);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getContext() instanceof OnPhotoCapture) {
                        ((OnPhotoCapture) v.getContext()).capture();
                    }
                }
            });
            return holder;
        } else if (viewType == VIEW_TYPE_MEDIA) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_picture, parent, false);
            return new MediaViewHolder(v);
        }
        return null;
    }

    @Override
    protected void onBindViewHolder(final RecyclerView.ViewHolder holder, Cursor cursor) {
        if (holder instanceof CaptureViewHolder) {
            CaptureViewHolder captureViewHolder = (CaptureViewHolder) holder;
            Drawable[] drawables = captureViewHolder.mHint.getCompoundDrawables();
            TypedArray ta = holder.itemView.getContext().getTheme().obtainStyledAttributes(new int[]{
                            R.attr.vanCamera
            });
            int color = ta.getColor(0, 0);
            ta.recycle();
            for (Drawable drawable : drawables) {
                if (drawable != null) {
                    drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
            }
        } else if (holder instanceof MediaViewHolder) {
            MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;

            final MediaInfo mediaInfo = MediaInfo.valueOf(cursor);
            mediaViewHolder.mMediaGrid.preBindMedia(new MediaGrid.PreBindInfo(getImageResize(mediaViewHolder.mMediaGrid.getContext()), mSelectionSpec.countable, holder));
            mediaViewHolder.mMediaGrid.bindMedia(mediaInfo);
            mediaViewHolder.mMediaGrid.setOnMediaGridClickListener(this);
            setCheckStatus(mediaInfo, mediaViewHolder.mMediaGrid);
        }
    }

    private void setCheckStatus(MediaInfo mediaInfo, MediaGrid mediaGrid) {
        if (mSelectionSpec.cropEnable && mSelectionSpec.maxCount == 1) {
            mediaGrid.setCheckVisibility(View.INVISIBLE);
        } else if (mSelectionSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(mediaInfo);
            if (checkedNum > 0) {
                mediaGrid.setCheckEnabled(true);
                mediaGrid.setCheckedNum(checkedNum);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    mediaGrid.setCheckEnabled(false);
                    mediaGrid.setCheckedNum(CheckView.UNCHECKED);
                } else {
                    mediaGrid.setCheckEnabled(true);
                    mediaGrid.setCheckedNum(checkedNum);
                }
            }
        } else {
            boolean selected = mSelectedCollection.isSelected(mediaInfo);
            if (selected) {
                mediaGrid.setCheckEnabled(true);
                mediaGrid.setChecked(true);
            } else {
                if (mSelectedCollection.maxSelectableReached()) {
                    mediaGrid.setCheckEnabled(false);
                    mediaGrid.setChecked(false);
                } else {
                    mediaGrid.setCheckEnabled(true);
                    mediaGrid.setChecked(false);
                }
            }
        }
    }

    @Override
    public void onThumbnailClicked(ImageView thumbnail, MediaInfo mediaInfo, RecyclerView.ViewHolder holder) {
        if (mOnMediaClickListener != null) {
            mOnMediaClickListener.onMediaClick(null, mediaInfo, holder.getAdapterPosition());
        }
    }

    @Override
    public void onCheckViewClicked(CheckView checkView, MediaInfo mediaInfo, RecyclerView.ViewHolder holder) {
        if (mSelectionSpec.countable) {
            int checkedNum = mSelectedCollection.checkedNumOf(mediaInfo);
            if (checkedNum == CheckView.UNCHECKED) {
                if (assertAddSelection(holder.itemView.getContext(), mediaInfo)) {
                    mSelectedCollection.add(mediaInfo);
                    notifyCheckStateChanged();
                }
            } else {
                mSelectedCollection.remove(mediaInfo);
                notifyCheckStateChanged();
            }
        } else {
            if (mSelectedCollection.isSelected(mediaInfo)) {
                mSelectedCollection.remove(mediaInfo);
                notifyCheckStateChanged();
            } else {
                if (assertAddSelection(holder.itemView.getContext(), mediaInfo)) {
                    mSelectedCollection.add(mediaInfo);
                    notifyCheckStateChanged();
                }
            }
        }
    }

    private void notifyCheckStateChanged() {
        notifyDataSetChanged();
        if (mCheckStateListener != null) {
            mCheckStateListener.onUpdate();
        }
    }

    @Override
    public int getItemViewType(int position, Cursor cursor) {
        return MediaInfo.valueOf(cursor).isCapture() ? VIEW_TYPE_CAPTURE : VIEW_TYPE_MEDIA;
    }

    private boolean assertAddSelection(Context context, MediaInfo mediaInfo) {
        IncapableCause cause = mSelectedCollection.isAcceptable(mediaInfo);
        IncapableCause.handleCause(context, cause);
        return cause == null;
    }

    public void registerCheckStateListener(CheckStateListener listener) {
        mCheckStateListener = listener;
    }

    public void unregisterCheckStateListener() {
        mCheckStateListener = null;
    }

    public void registerOnMediaClickListener(OnMediaClickListener listener) {
        mOnMediaClickListener = listener;
    }

    public void unregisterOnMediaClickListener() {
        mOnMediaClickListener = null;
    }

    public void refreshSelection() {
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        if (first == -1 || last == -1) {
            return;
        }
        Cursor cursor = getCursor();
        for (int i = first; i <= last; i++) {
            RecyclerView.ViewHolder holder = mRecyclerView.findViewHolderForAdapterPosition(first);
            if (holder instanceof MediaViewHolder) {
                if (cursor.moveToPosition(i)) {
                    setCheckStatus(MediaInfo.valueOf(cursor), ((MediaViewHolder) holder).mMediaGrid);
                }
            }
        }
    }

    private int getImageResize(Context context) {
        if (mImageResize == 0) {
            RecyclerView.LayoutManager lm = mRecyclerView.getLayoutManager();
            int spanCount = ((GridLayoutManager) lm).getSpanCount();
            int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
            int availableWidth = screenWidth - context.getResources().getDimensionPixelSize(
                    R.dimen.van_recycler_spacing) * (spanCount - 1);
            mImageResize = availableWidth / spanCount;
            mImageResize = (int) (mImageResize * mSelectionSpec.thumbnailScale);
        }
        return mImageResize;
    }

    public interface CheckStateListener {
        void onUpdate();
    }

    public interface OnMediaClickListener {
        void onMediaClick(Album album, MediaInfo mediaInfo, int adapterPosition);
    }

    public interface OnPhotoCapture {
        void capture();
    }

    private static class MediaViewHolder extends RecyclerView.ViewHolder {

        private MediaGrid mMediaGrid;

        MediaViewHolder(View itemView) {
            super(itemView);
            mMediaGrid = (MediaGrid) itemView;
        }
    }

    private static class CaptureViewHolder extends RecyclerView.ViewHolder {

        private TextView mHint;

        CaptureViewHolder(View itemView) {
            super(itemView);

            mHint = (TextView) itemView.findViewById(R.id.tv_recycler_camera);
        }
    }

}
