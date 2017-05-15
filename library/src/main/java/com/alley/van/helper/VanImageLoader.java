package com.alley.van.helper;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

public interface VanImageLoader {

    /**
     * Load thumbnail of a static image resource.
     *
     * @param context     Context
     * @param resize      Desired size of the origin image
     * @param imageView   ImageView widget
     * @param uri         Uri of the loaded image
     */
    void loadThumbnail(Context context, int resize, ImageView imageView, Uri uri);

    /**
     * Load thumbnail of a gif image resource. You don't have to load an animated gif when it's only
     * a thumbnail tile.
     *
     * @param context     Context
     * @param resize      Desired size of the origin image
     * @param imageView   ImageView widget
     * @param uri         Uri of the loaded image
     */
    void loadAnimatedGifThumbnail(Context context, int resize, ImageView imageView, Uri uri);

    /**
     * Load a static image resource.
     *
     * @param context   Context
     * @param resizeX   Desired x-size of the origin image
     * @param resizeY   Desired y-size of the origin image
     * @param imageView ImageView widget
     * @param uri       Uri of the loaded image
     */
    void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri);

    /**
     * Load a gif image resource.
     *
     * @param context   Context
     * @param resizeX   Desired x-size of the origin image
     * @param resizeY   Desired y-size of the origin image
     * @param imageView ImageView widget
     * @param uri       Uri of the loaded image
     */
    void loadAnimatedGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri);

    /**
     * Whether this implementation supports animated gif. Just knowledge of it, convenient for users.
     *
     * @return true support animated gif, false do not support animated gif.
     */
    boolean supportAnimatedGif();
}
