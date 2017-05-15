package com.alley.van;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.alley.van.activity.VanMediaActivity;
import com.alley.van.helper.VanMediaType;
import com.alley.van.model.VanConfig;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

public final class VanGogh {
    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;

    private VanGogh(Activity activity) {
        this(activity, null);
    }

    private VanGogh(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    public static VanGogh from(Activity activity) {
        return new VanGogh(activity);
    }

    public static VanGogh from(Fragment fragment) {
        return new VanGogh(fragment.getActivity(), fragment);
    }

    public static List<Uri> obtainResult(Intent data) {
        return data.getParcelableArrayListExtra(VanMediaActivity.EXTRA_RESULT_SELECTION);
    }

    public static Uri obtainCamera() {
        return VanConfig.getInstance().getCurrentPhotoPath();
    }

    public VanGoghBuilder choose(Set<VanMediaType> vanMediaType) {
        return new VanGoghBuilder(this, vanMediaType);
    }

    @Nullable
    Activity getActivity() {
        return mContext.get();
    }

    @Nullable
    Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }

}
