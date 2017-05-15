package com.alley.van.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alley.van.R;
import com.alley.van.model.Album;
import com.alley.van.model.VanConfig;

import java.io.File;

public class MediaSpinnerAdapter extends CursorAdapter {

    public MediaSpinnerAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    public MediaSpinnerAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list_spinner, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Album album = Album.valueOf(cursor);
        ((TextView) view.findViewById(R.id.album_name)).setText(album.getDisplayName(context));
        ((TextView) view.findViewById(R.id.album_media_count)).setText(String.valueOf(album.getCount()));

        // do not need to load animated Gif
        VanConfig.getInstance().imageLoader.loadThumbnail(context,
                context.getResources().getDimensionPixelSize(R.dimen.van_recycler_size),
                (ImageView) view.findViewById(R.id.album_cover),
                Uri.fromFile(new File(album.getCoverPath())));
    }
}
