package com.alley.van.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.StyleRes;
import android.support.v4.content.FileProvider;

import com.alley.van.helper.VanImageLoader;
import com.alley.van.helper.VanMediaFilter;
import com.alley.van.helper.VanMediaType;
import com.alley.van.helper.VanToastListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class VanConfig {
    public Set<VanMediaType> vanMediaTypeSet;
    public List<VanMediaFilter> vanMediaFilters;
    @StyleRes
    public int themeID;
    public int orientation;
    public boolean countable;
    public int maxCount;
    public int spanCount;

    public boolean cropEnable;
    public int cropType;
    public int cropWidth;
    public int cropHeight;

    public boolean cameraVisible;
    public String packageName;

    private Uri uriImage;
    public float thumbnailScale;
    public VanImageLoader imageLoader;
    public VanToastListener toastListener;

    private VanConfig() {
    }

    public static VanConfig getInstance() {
        return VanConfigHolder.INSTANCE;
    }

    private static final class VanConfigHolder {
        private static final VanConfig INSTANCE = new VanConfig();
    }

    public static VanConfig getCleanInstance() {
        VanConfig vanConfig = getInstance();
        vanConfig.reset();
        return vanConfig;
    }

    void reset() {
        vanMediaTypeSet = null;
        vanMediaFilters = null;

        themeID = 0;
        orientation = 0;

        maxCount = 0;
        spanCount = 0;
        countable = false;

        cameraVisible = false;
        packageName = null;

        cropEnable = false;
        cropWidth = 0;
        cropHeight = 0;
        cropType = 0;

        uriImage = null;
        thumbnailScale = 0.0f;
        imageLoader = null;
        toastListener = null;
    }

    public boolean needOrientationRestriction() {
        return orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    /**
     * Checks whether the device has a camera feature or not.
     *
     * @param context a context to check for camera feature.
     * @return true if the device has a camera feature. false otherwise.
     */
    public boolean hasCameraFeature(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public void dispatchCaptureIntent(Activity activity, int requestCode) {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (captureIntent.resolveActivity(activity.getPackageManager()) == null) {
            return;
        }
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (photoFile == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            captureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //通过FileProvider创建一个content类型的Uri
            uriImage = FileProvider.getUriForFile(activity, packageName + ".fileProvider", photoFile);
        } else {
            uriImage = Uri.parse("file://" + photoFile.getAbsolutePath());
        }

        // 默认情况下，即不需要指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 照相机有自己默认的存储路径，拍摄的照片将返回一个缩略图。如果想访问原始图片，
        // 可以通过dat extra能够得到原始图片位置。即，如果指定了目标uri，data就没有数据，
        // 如果没有指定uri，则data就返回有数据！
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
        activity.startActivityForResult(captureIntent, requestCode);
    }

    private File createImageFile() throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String imageFileName = "IMG_" + dateFormat.format(new Date());

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(storageDir + File.separator + imageFileName + ".png");
    }

    public Uri getCurrentPhotoPath() {
        return uriImage;
    }
}
