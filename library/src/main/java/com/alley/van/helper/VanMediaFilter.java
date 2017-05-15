package com.alley.van.helper;

import android.content.Context;

import com.alley.van.model.IncapableCause;
import com.alley.van.model.MediaInfo;

import java.util.Set;

public abstract class VanMediaFilter {
    /**
     * Convenient constant for a minimum value.
     */
    public static final int MIN = 0;
    /**
     * Convenient constant for a maximum value.
     */
    public static final int MAX = Integer.MAX_VALUE;
    /**
     * Convenient constant for 1024.
     */
    public static final int K = 1024;

    /**
     * Against what mime types this filter applies.
     */
    protected abstract Set<VanMediaType> constraintTypes();

    /**
     * Invoked for filtering each item.
     *
     * @return null if selectable, {@link IncapableCause} if not selectable.
     */
    public abstract IncapableCause filter(Context context, MediaInfo mediaInfo);

    /**
     * Whether an {@link MediaInfo} need filtering.
     */
    protected boolean needFiltering(Context context, MediaInfo mediaInfo) {
        for (VanMediaType type : constraintTypes()) {
            if (type.checkType(context.getContentResolver(), mediaInfo.getContentUri())) {
                return true;
            }
        }
        return false;
    }
}
