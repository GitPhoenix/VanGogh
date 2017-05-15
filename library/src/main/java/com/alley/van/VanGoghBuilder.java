package com.alley.van;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;

import com.alley.van.activity.VanMediaActivity;
import com.alley.van.helper.VanCropType;
import com.alley.van.helper.VanImageLoader;
import com.alley.van.helper.VanMediaFilter;
import com.alley.van.helper.VanMediaType;
import com.alley.van.helper.VanToastListener;
import com.alley.van.model.VanConfig;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public final class VanGoghBuilder {
    private final VanGogh vanGogh;
    private final VanConfig vanConfig;
    private final Set<VanMediaType> mVanMediaType;
    private List<VanMediaFilter> mVanMediaFilters;

    private int themeID = R.style.VanTheme;
    private int mOrientation;

    private int rowCount = 3;
    private int maxCount = 9;
    private boolean countable;

    private boolean cropEnable;
    private int cropWidth = 100;
    private int cropHeight = 100;
    private int cropType = VanCropType.CROP_TYPE_RECTANGLE;

    private boolean cameraVisible;
    private String packageName;

    private float thumbnailScale = 0.5f;
    private VanImageLoader imageLoader;
    private VanToastListener toastListener;

    @IntDef({
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED,
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,
            ActivityInfo.SCREEN_ORIENTATION_USER,
            ActivityInfo.SCREEN_ORIENTATION_BEHIND,
            ActivityInfo.SCREEN_ORIENTATION_SENSOR,
            ActivityInfo.SCREEN_ORIENTATION_NOSENSOR,
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT,
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT,
            ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR,
            ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE,
            ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT,
            ActivityInfo.SCREEN_ORIENTATION_FULL_USER,
            ActivityInfo.SCREEN_ORIENTATION_LOCKED
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface ScreenOrientation {}

    @IntDef({
            VanCropType.CROP_TYPE_RECTANGLE,
            VanCropType.CROP_TYPE_RECTANGLE_GRID,
            VanCropType.CROP_TYPE_CIRCLE,
            VanCropType.CROP_TYPE_CIRCLE_STROKE
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface CropType {}

    /**
     * Constructs a new specification builder on the context.
     *
     * @param vanGogh  a requester context wrapper.
     * @param vanMediaType MIME type set to select.
     */
    VanGoghBuilder(VanGogh vanGogh, @NonNull Set<VanMediaType> vanMediaType) {
        this.vanGogh = vanGogh;
        mVanMediaType = vanMediaType;
        vanConfig = VanConfig.getCleanInstance();
        mOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    /**
     * Theme for media selecting Activity.
     * <p>
     * There are two built-in themes:
     * 1. com.zhihu.matisse.R.style.Matisse_Zhihu;
     * 2. com.zhihu.matisse.R.style.Matisse_Dracula
     * you can define a custom theme derived from the above ones or other themes.
     *
     * @param themeID theme resource id. Default value is com.zhihu.matisse.R.style.Matisse_Zhihu.
     * @return {@link VanGoghBuilder} for fluent API.
     */
    public VanGoghBuilder theme(@StyleRes int themeID) {
        this.themeID = themeID;
        return this;
    }

    /**
     * Show a auto-increased number or a check mark when user select media.
     *
     * @param enable true for a auto-increased number from 1, false for a check mark. Default
     *                  value is false.
     * @return {@link VanGoghBuilder} for fluent API.
     */
    public VanGoghBuilder countable(boolean enable) {
        this.countable = enable;
        return this;
    }

    public VanGoghBuilder cropEnable(boolean enable, @CropType int cropType) {
        this.cropEnable = enable;
        this.cropType = cropType;
        return this;
    }

    public VanGoghBuilder withResultSize(@IntRange(from = 100) int width, @IntRange(from = 100) int height) {
        this.cropWidth = width;
        this.cropHeight = height;
        return this;
    }

    public VanGoghBuilder toast(VanToastListener toastListener) {
        this.toastListener = toastListener;
        return this;
    }

    /**
     * Maximum selectable count.
     *
     * @param value Maximum selectable count. Default value is 1.
     * @return {@link VanGoghBuilder} for fluent API.
     */
    public VanGoghBuilder maxCount(@IntRange(from = 1) int value) {
        this.maxCount = value;
        return this;
    }

    /**
     * Add filter to filter each selecting item.
     *
     * @param vanMediaFilter {@link VanMediaFilter}
     * @return {@link VanGoghBuilder} for fluent API.
     */
    public VanGoghBuilder addFilter(VanMediaFilter vanMediaFilter) {
        if (mVanMediaFilters == null) {
            mVanMediaFilters = new ArrayList<>();
        }
        mVanMediaFilters.add(vanMediaFilter);
        return this;
    }

    /**
     * Determines whether the photo capturing is enabled or not on the media grid view.
     * <p>
     * If this value is set true, photo capturing entry will appear only on All Media's page.
     *
     * @param enable Whether to enable capturing or not. Default value is false;
     * @return {@link VanGoghBuilder} for fluent API.
     */
    public VanGoghBuilder cameraVisible(boolean enable, String packageName) {
        cameraVisible = enable;
        this.packageName = packageName;
        return this;
    }

    /**
     * Set the desired orientation of this activity.
     *
     * @param orientation An orientation constant as used in {@link ScreenOrientation}.
     *                    Default value is {@link ActivityInfo#SCREEN_ORIENTATION_PORTRAIT}.
     * @return {@link VanGoghBuilder} for fluent API.
     * @see Activity#setRequestedOrientation(int)
     */
    public VanGoghBuilder restrictOrientation(@ScreenOrientation int orientation) {
        mOrientation = orientation;
        return this;
    }

    /**
     * Set a fixed span count for the media grid. Same for different screen orientations.
     * <p>
     *
     * @param value Requested span count.
     * @return {@link VanGoghBuilder} for fluent API.
     */
    public VanGoghBuilder rowCount(@IntRange(from = 1) int value) {
        this.rowCount = value;
        return this;
    }

    /**
     * Photo thumbnail's scale compared to the View's size. It should be a float value in (0.0,
     * 1.0].
     *
     * @param value Thumbnail's scale in (0.0, 1.0]. Default value is 0.5.
     * @return {@link VanGoghBuilder} for fluent API.
     */
    public VanGoghBuilder thumbnailScale(@FloatRange(from = 0.0, to = 1.0) float value) {
        this.thumbnailScale = value;
        return this;
    }

    /**
     * Provide an image engine.
     * <p>
     * There are two built-in image engines:
     * And you can implement your own image engine.
     *
     * @param imageLoader {@link VanImageLoader}
     * @return {@link VanGoghBuilder} for fluent API.
     */
    public VanGoghBuilder imageLoader(VanImageLoader imageLoader) {
        this.imageLoader = imageLoader;
        return this;
    }

    /**
     * Start to select media and wait for result.
     *
     * @param requestCode Identity of the request Activity or Fragment.
     */
    public void forResult(int requestCode) {
        Activity activity = vanGogh.getActivity();
        if (activity == null) {
            return;
        }

        if (cropEnable) {
            vanConfig.countable = false;
            vanConfig.maxCount = 1;
        } else {
            vanConfig.countable = countable;
            vanConfig.maxCount = maxCount;
        }
        if (mVanMediaFilters != null && mVanMediaFilters.size() > 0) {
            vanConfig.vanMediaFilters = mVanMediaFilters;
        }

        vanConfig.vanMediaTypeSet = mVanMediaType;
        vanConfig.themeID = themeID;
        vanConfig.orientation = mOrientation;

        vanConfig.cameraVisible = cameraVisible;
        vanConfig.packageName = packageName;

        vanConfig.spanCount = rowCount;
        vanConfig.cropEnable = cropEnable;
        vanConfig.cropWidth = cropWidth;
        vanConfig.cropHeight = cropHeight;
        vanConfig.cropType = cropType;

        vanConfig.thumbnailScale = thumbnailScale;
        vanConfig.imageLoader = imageLoader;
        vanConfig.toastListener = toastListener;

        Intent intent = new Intent(activity, VanMediaActivity.class);
        Fragment fragment = vanGogh.getFragment();
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 直接调用系统相机拍照
     *
     * @param requestCode
     */
    public void forCamera(int requestCode) {
        Activity activity = vanGogh.getActivity();
        if (activity == null) {
            return;
        }

        vanConfig.themeID = themeID;
        vanConfig.packageName = packageName;

        vanConfig.cropEnable = cropEnable;
        vanConfig.cropType = cropType;
        vanConfig.cropWidth = cropWidth;
        vanConfig.cropHeight = cropHeight;

        vanConfig.thumbnailScale = thumbnailScale;
        vanConfig.imageLoader = imageLoader;
        vanConfig.toastListener = toastListener;
        vanConfig.dispatchCaptureIntent(activity, requestCode);
    }
}
