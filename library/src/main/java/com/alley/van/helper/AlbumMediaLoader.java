package com.alley.van.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import com.alley.van.model.Album;
import com.alley.van.model.MediaInfo;
import com.alley.van.model.VanConfig;


/**
 * Load images and videos into a single cursor.
 */
public class AlbumMediaLoader extends CursorLoader {
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            "duration"};
    private static final String SELECTION_ALL =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    private static final String[] SELECTION_ALL_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };
    private static final String SELECTION_ALBUM =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
            + " AND "
            + " bucket_id=?"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    private static String[] getSelectionAlbumArgs(String albumId) {
        return new String[] {
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                albumId
        };
    }
    private static final String ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC";
    private final boolean mEnableCapture;

    private AlbumMediaLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs,
                             String sortOrder, boolean capture) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
        mEnableCapture = capture;
    }

    public static CursorLoader newInstance(Context context, Album album, boolean capture) {
        if (album.isAll()) {
            return new AlbumMediaLoader(
                    context,
                    QUERY_URI,
                    PROJECTION,
                    SELECTION_ALL,
                    SELECTION_ALL_ARGS,
                    ORDER_BY,
                    capture);
        } else {
            return new AlbumMediaLoader(
                    context,
                    QUERY_URI,
                    PROJECTION,
                    SELECTION_ALBUM,
                    getSelectionAlbumArgs(album.getId()),
                    ORDER_BY,
                    false);
        }
    }

    @Override
    public Cursor loadInBackground() {
        Cursor result = super.loadInBackground();
        if (!mEnableCapture || !VanConfig.getInstance().hasCameraFeature(getContext())) {
            return result;
        }
        MatrixCursor dummy = new MatrixCursor(PROJECTION);
        dummy.addRow(new Object[]{MediaInfo.ITEM_ID_CAPTURE, MediaInfo.ITEM_DISPLAY_NAME_CAPTURE, "", 0, 0});
        return new MergeCursor(new Cursor[]{dummy, result});
    }

    @Override
    public void onContentChanged() {
        // FIXME a dirty way to fix loading multiple times
    }
}
