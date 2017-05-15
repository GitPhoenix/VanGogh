package com.alley.van.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alley.van.R;
import com.alley.van.model.MediaInfo;
import com.alley.van.model.VanConfig;
import com.alley.van.util.PhotoMetadataUtils;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class VanPreviewFragment extends Fragment {
    private static final String ARGS_ITEM = "args_item";

    public static VanPreviewFragment newInstance(MediaInfo mediaInfo) {
        VanPreviewFragment fragment = new VanPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_ITEM, mediaInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_preview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MediaInfo mediaInfo = getArguments().getParcelable(ARGS_ITEM);
        if (mediaInfo == null) {
            return;
        }

        View videoPlayButton = view.findViewById(R.id.video_play_button);
        if (mediaInfo.isVideo()) {
            videoPlayButton.setVisibility(View.VISIBLE);
            videoPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(mediaInfo.uri, "video/*");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        VanConfig.getInstance().toastListener.display(R.string.error_no_video_activity);
                    }
                }
            });
        } else {
            videoPlayButton.setVisibility(View.GONE);
        }

        ImageViewTouch image = (ImageViewTouch)view.findViewById(R.id.image_view);
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        Point size = PhotoMetadataUtils.getBitmapSize(mediaInfo.getContentUri(), getActivity());
        if (mediaInfo.isGif()) {
            VanConfig.getInstance().imageLoader.loadAnimatedGifImage(getContext(), size.x, size.y, image,
                    mediaInfo.getContentUri());
        } else {
            VanConfig.getInstance().imageLoader.loadImage(getContext(), size.x, size.y, image,
                    mediaInfo.getContentUri());
        }
    }

    public void resetView() {
        if (getView() != null) {
            ((ImageViewTouch) getView().findViewById(R.id.image_view)).resetMatrix();
        }
    }
}
