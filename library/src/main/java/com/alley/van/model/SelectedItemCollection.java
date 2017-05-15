package com.alley.van.model;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.alley.van.R;
import com.alley.van.util.PhotoMetadataUtils;
import com.alley.van.widget.CheckView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class SelectedItemCollection {

    public static final String STATE_SELECTION = "state_selection", STATE_COLLECTION_TYPE = "state_collection_type";
    private final Context mContext;
    private Set<MediaInfo> mMediaInfos;
    private VanConfig mSpec;

    /**
     * Empty collection
     */
    public static final int COLLECTION_UNDEFINED = 0x00;
    /**
     * Collection only with images
     */
    public static final int COLLECTION_IMAGE = 0x01;
    /**
     * Collection only with videos
     */
    public static final int COLLECTION_VIDEO = 0x01 << 1;
    /**
     * Collection with images and videos.
     *
     * Not supported currently.
     */
    public static final int COLLECTION_MIXED = COLLECTION_IMAGE | COLLECTION_VIDEO;

    private int mCollectionType = COLLECTION_UNDEFINED;

    public SelectedItemCollection(Context context) {
        mContext = context;
    }

    public void onCreate(Bundle bundle, VanConfig spec) {
        if (bundle == null) {
            mMediaInfos = new LinkedHashSet<>();
        } else {
            List<MediaInfo> saved = bundle.getParcelableArrayList(STATE_SELECTION);
            mMediaInfos = new LinkedHashSet<>(saved);
            mCollectionType = bundle.getInt(STATE_COLLECTION_TYPE, COLLECTION_UNDEFINED);
        }
        mSpec = spec;
    }

    public void setDefaultSelection(List<MediaInfo> uris) {
        mMediaInfos.addAll(uris);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mMediaInfos));
        outState.putInt(STATE_COLLECTION_TYPE, mCollectionType);
    }

    public Bundle getDataWithBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(STATE_SELECTION, new ArrayList<>(mMediaInfos));
        bundle.putInt(STATE_COLLECTION_TYPE, mCollectionType);
        return bundle;
    }

    public boolean add(MediaInfo mediaInfo) {
        if (typeConflict(mediaInfo)) {
            throw new IllegalArgumentException("Can't select images and videos at the same time.");
        }
        if (mCollectionType == COLLECTION_UNDEFINED) {
            if (mediaInfo.isImage()) {
                mCollectionType = COLLECTION_IMAGE;
            } else if (mediaInfo.isVideo()) {
                mCollectionType = COLLECTION_VIDEO;
            }
        }
        return mMediaInfos.add(mediaInfo);
    }

    public boolean remove(MediaInfo mediaInfo) {
        boolean result = mMediaInfos.remove(mediaInfo);
        if (mMediaInfos.size() == 0) {
            mCollectionType = COLLECTION_UNDEFINED;
        }
        return result;
    }

    public void overwrite(ArrayList<MediaInfo> mediaInfos, int collectionType) {
        if (mediaInfos.size() == 0) {
            mCollectionType = COLLECTION_UNDEFINED;
        } else {
            mCollectionType = collectionType;
        }
        mMediaInfos.clear();
        mMediaInfos.addAll(mediaInfos);
    }


    public List<MediaInfo> asList() {
        return new ArrayList<>(mMediaInfos);
    }

    public List<Uri> asListOfUri() {
        List<Uri> uris = new ArrayList<>();
        for (MediaInfo mediaInfo : mMediaInfos) {
            uris.add(mediaInfo.getContentUri());
        }
        return uris;
    }

    public boolean isEmpty() {
        return mMediaInfos == null || mMediaInfos.isEmpty();
    }

    public boolean isSelected(MediaInfo mediaInfo) {
        return mMediaInfos.contains(mediaInfo);
    }

    public IncapableCause isAcceptable(MediaInfo mediaInfo) {
        if (maxSelectableReached()) {
            return new IncapableCause(mContext.getString(R.string.error_over_count, mSpec.maxCount));
        } else if (typeConflict(mediaInfo)) {
            return new IncapableCause(mContext.getString(R.string.error_type_conflict));
        }

        return PhotoMetadataUtils.isAcceptable(mContext, mediaInfo);
    }

    public boolean maxSelectableReached() {
        return mMediaInfos.size() == mSpec.maxCount;
    }

    public int getCollectionType() {
        return mCollectionType;
    }

    /**
     * Determine whether there will be conflict media types. A user can't select images and videos at the same time.
     */
    public boolean typeConflict(MediaInfo mediaInfo) {
        return (mediaInfo.isImage() && (mCollectionType == COLLECTION_VIDEO || mCollectionType == COLLECTION_MIXED))
                || (mediaInfo.isVideo() && (mCollectionType == COLLECTION_IMAGE || mCollectionType == COLLECTION_MIXED));
    }

    public int count() {
        return mMediaInfos.size();
    }

    public int maxSelectable() {
        return mSpec.maxCount;
    }

    public int checkedNumOf(MediaInfo mediaInfo) {
        int index = new ArrayList<>(mMediaInfos).indexOf(mediaInfo);
        return index == -1 ? CheckView.UNCHECKED : index + 1;
    }
}
