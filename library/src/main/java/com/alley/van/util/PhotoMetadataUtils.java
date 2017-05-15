package com.alley.van.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import com.alley.van.R;
import com.alley.van.helper.ExifInterfaceCompat;
import com.alley.van.helper.VanMediaFilter;
import com.alley.van.helper.VanMediaType;
import com.alley.van.model.IncapableCause;
import com.alley.van.model.MediaInfo;
import com.alley.van.model.VanConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

public final class PhotoMetadataUtils {
    public static final String TAG = PhotoMetadataUtils.class.getSimpleName();
    private static final int MAX_WIDTH = 1600;
    private static final String SCHEME_CONTENT = "content";

    private PhotoMetadataUtils() {
        throw new AssertionError("oops! the utility class is about to be instantiated...");
    }

    public static int getPixelsCount(ContentResolver resolver, Uri uri) {
        Point size = getBitmapBound(resolver, uri);
        return size.x * size.y;
    }

    public static Point getBitmapSize(Uri uri, Activity activity) {
        ContentResolver resolver = activity.getContentResolver();
        Point imageSize = getBitmapBound(resolver, uri);
        int w = imageSize.x;
        int h = imageSize.y;
        if (PhotoMetadataUtils.shouldRotate(resolver, uri)) {
            w = imageSize.y;
            h = imageSize.x;
        }
        if (h == 0) return new Point(MAX_WIDTH, MAX_WIDTH);
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float screenWidth = (float) metrics.widthPixels;
        float screenHeight = (float) metrics.heightPixels;
        float widthScale = screenWidth / w;
        float heightScale = screenHeight / h;
        if (widthScale > heightScale) {
            return new Point((int) (w * widthScale), (int) (h * heightScale));
        }
        return new Point((int) (w * widthScale), (int) (h * heightScale));
    }

    public static Point getBitmapBound(ContentResolver resolver, Uri uri) {
        InputStream is = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            is = resolver.openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
            int width = options.outWidth;
            int height = options.outHeight;
            return new Point(width, height);
        } catch (FileNotFoundException e) {
            return new Point(0, 0);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getPath(ContentResolver resolver, Uri uri) {
        if (uri == null) {
            return null;
        }

        if (SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, new String[]{MediaStore.Images.ImageColumns.DATA},
                        null, null, null);
                if (cursor == null || !cursor.moveToFirst()) {
                    return null;
                }
                return cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return uri.getPath();
    }

    public static IncapableCause isAcceptable(Context context, MediaInfo mediaInfo) {
        if (!isSelectableType(context, mediaInfo)) {
            return new IncapableCause(context.getString(R.string.error_file_type));
        }

        if (VanConfig.getInstance().vanMediaFilters != null) {
            for (VanMediaFilter vanMediaFilter : VanConfig.getInstance().vanMediaFilters) {
                IncapableCause IncapableCause = vanMediaFilter.filter(context, mediaInfo);
                if (IncapableCause != null) {
                    return IncapableCause;
                }
            }
        }
        return null;
    }

    public static boolean isSelectableType(Context context, MediaInfo mediaInfo) {
        if (context == null) {
            return false;
        }

        ContentResolver resolver = context.getContentResolver();
        for (VanMediaType type : VanConfig.getInstance().vanMediaTypeSet) {
            if (type.checkType(resolver, mediaInfo.getContentUri())) {
                return true;
            }
        }
        return false;
    }

    public static boolean shouldRotate(ContentResolver resolver, Uri uri) {
        ExifInterface exif;
        try {
            exif = ExifInterfaceCompat.newInstance(getPath(resolver, uri));
        } catch (IOException e) {
            Log.e(TAG, "could not read exif info of the image: " + uri);
            return false;
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        return orientation == ExifInterface.ORIENTATION_ROTATE_90
                || orientation == ExifInterface.ORIENTATION_ROTATE_270;
    }

    public static float getSizeInMB(long sizeInBytes) {
        return Float.valueOf(new DecimalFormat("0.0").format((float) sizeInBytes / 1024 / 1024));
    }
}
