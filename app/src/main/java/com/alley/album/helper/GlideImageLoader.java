package com.alley.album.helper;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.alley.van.helper.VanImageLoader;
import com.bumptech.glide.Glide;


/**
 * {@link VanImageLoader} implementation using Glide.
 */

public class GlideImageLoader implements VanImageLoader {

    @Override
    public void loadThumbnail(Context context, int resize, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
                .into(imageView);
    }

    @Override
    public void loadAnimatedGifThumbnail(Context context, int resize, ImageView imageView,
                                         Uri uri) {
        Glide.with(context)
                .load(uri)
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
                .into(imageView);
    }

    @Override
    public void loadAnimatedGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }

}
