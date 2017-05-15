package com.alley.van.model;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import com.alley.van.helper.VanMediaType;


public class MediaInfo implements Parcelable {
    public static final Creator<MediaInfo> CREATOR = new Creator<MediaInfo>() {
        @Override
        @Nullable
        public MediaInfo createFromParcel(Parcel source) {
            return new MediaInfo(source);
        }

        @Override
        public MediaInfo[] newArray(int size) {
            return new MediaInfo[size];
        }
    };
    public static final long ITEM_ID_CAPTURE = -1;
    public static final String ITEM_DISPLAY_NAME_CAPTURE = "Capture";
    public final long id;
    public final String mimeType;
    public final Uri uri;
    public final long size;
    public final long duration; // only for video, in ms

    private MediaInfo(long id, String mimeType, long size, long duration) {
        this.id = id;
        this.mimeType = mimeType;
        Uri contentUri;
        if (isImage()) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (isVideo()) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            // ?
            contentUri = MediaStore.Files.getContentUri("external");
        }
        this.uri = ContentUris.withAppendedId(contentUri, id);
        this.size = size;
        this.duration = duration;
    }

    private MediaInfo(Parcel source) {
        id = source.readLong();
        mimeType = source.readString();
        uri = source.readParcelable(Uri.class.getClassLoader());
        size = source.readLong();
        duration = source.readLong();
    }

    public static MediaInfo valueOf(Cursor cursor) {
        return new MediaInfo(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)),
                cursor.getLong(cursor.getColumnIndex("duration")));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(mimeType);
        dest.writeParcelable(uri, 0);
        dest.writeLong(size);
        dest.writeLong(duration);
    }

    public Uri getContentUri() {
        return uri;
    }

    public boolean isCapture() {
        return id == ITEM_ID_CAPTURE;
    }

    public boolean isImage() {
        return mimeType.equals(VanMediaType.JPEG.toString())
                || mimeType.equals(VanMediaType.PNG.toString())
                || mimeType.equals(VanMediaType.GIF.toString())
                || mimeType.equals(VanMediaType.BMP.toString())
                || mimeType.equals(VanMediaType.WEBP.toString());
    }

    public boolean isGif() {
        return mimeType.equals(VanMediaType.GIF.toString());
    }

    public boolean isVideo() {
        return mimeType.equals(VanMediaType.MPEG.toString())
                || mimeType.equals(VanMediaType.MP4.toString())
                || mimeType.equals(VanMediaType.QUICKTIME.toString())
                || mimeType.equals(VanMediaType.THREEGPP.toString())
                || mimeType.equals(VanMediaType.THREEGPP2.toString())
                || mimeType.equals(VanMediaType.MKV.toString())
                || mimeType.equals(VanMediaType.WEBM.toString())
                || mimeType.equals(VanMediaType.TS.toString())
                || mimeType.equals(VanMediaType.AVI.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MediaInfo)) {
            return false;
        }

        MediaInfo other = (MediaInfo) obj;
        return other.uri.equals(uri);
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Long.valueOf(id).hashCode();
        result = 31 * result + mimeType.hashCode();
        result = 31 * result + uri.hashCode();
        result = 31 * result + Long.valueOf(size).hashCode();
        result = 31 * result + Long.valueOf(duration).hashCode();
        return result;
    }
}