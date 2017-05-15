package com.alley.van.helper;

import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.alley.van.util.PhotoMetadataUtils;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * MIME Type enumeration to restrict selectable media on the selection activity. Matisse only supports images and
 * videos.
 * <p>
 * Good example of mime types Android supports:
 * https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/MediaFile.java
 */
@SuppressWarnings("unused")
public enum VanMediaType {
    // ============== images ==============
    JPEG("image/jpeg", new HashSet<String>() {{
        add("jpg");
        add("jpeg");
    }}),
    PNG("image/png", new HashSet<String>() {{
        add("png");
    }}),
    GIF("image/gif", new HashSet<String>() {{
        add("gif");
    }}),
    BMP("image/x-ms-bmp", new HashSet<String>() {{
        add("bmp");
    }}),
    WEBP("image/webp", new HashSet<String>() {{
        add("webp");
    }}),

    // ============== videos ==============
    MPEG("video/mpeg", new HashSet<String>() {{
        add("mpeg");
        add("mpg");
    }}),
    MP4("video/mp4", new HashSet<String>() {{
        add("mp4");
        add("m4v");
    }}),
    QUICKTIME("video/quicktime", new HashSet<String>() {{
        add("mov");
    }}),
    THREEGPP("video/3gpp", new HashSet<String>() {{
        add("3gp");
        add("3gpp");
    }}),
    THREEGPP2("video/3gpp2", new HashSet<String>() {{
        add("3g2");
        add("3gpp2");
    }}),
    MKV("video/x-matroska", new HashSet<String>() {{
        add("mkv");
    }}),
    WEBM("video/webm", new HashSet<String>() {{
        add("webm");
    }}),
    TS("video/mp2ts", new HashSet<String>() {{
        add("ts");
    }}),
    AVI("video/avi", new HashSet<String>() {{
        add("avi");
    }});

    private final String mMimeTypeName;
    private final Set<String> mExtensions;

    VanMediaType(String mimeTypeName, Set<String> extensions) {
        mMimeTypeName = mimeTypeName;
        mExtensions = extensions;
    }

    public static Set<VanMediaType> ofAll() {
        return EnumSet.allOf(VanMediaType.class);
    }

    public static Set<VanMediaType> of(VanMediaType type, VanMediaType... rest) {
        return EnumSet.of(type, rest);
    }

    public static Set<VanMediaType> ofImage() {
        return EnumSet.of(JPEG, PNG, GIF, BMP, WEBP);
    }

    public static Set<VanMediaType> ofVideo() {
        return EnumSet.of(MPEG, MP4, QUICKTIME, THREEGPP, THREEGPP2, MKV, WEBM, TS, AVI);
    }

    @Override
    public String toString() {
        return mMimeTypeName;
    }

    public boolean checkType(ContentResolver resolver, Uri uri) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        if (uri == null) {
            return false;
        }
        String type = map.getExtensionFromMimeType(resolver.getType(uri));
        for (String extension : mExtensions) {
            if (extension.equals(type)) {
                return true;
            }
            String path = PhotoMetadataUtils.getPath(resolver, uri);
            if (path != null && path.toLowerCase(Locale.US).endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
