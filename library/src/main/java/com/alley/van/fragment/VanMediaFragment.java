package com.alley.van.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alley.van.R;
import com.alley.van.adapter.VanMediaAdapter;
import com.alley.van.model.Album;
import com.alley.van.model.AlbumMediaCollection;
import com.alley.van.model.MediaInfo;
import com.alley.van.model.SelectedItemCollection;
import com.alley.van.model.VanConfig;
import com.alley.van.widget.MediaGridInset;


public class VanMediaFragment extends Fragment implements AlbumMediaCollection.AlbumMediaCallbacks,
        VanMediaAdapter.CheckStateListener, VanMediaAdapter.OnMediaClickListener {
    public static final String EXTRA_ALBUM = "extra_album";

    private RecyclerView mRecyclerView;
    private VanMediaAdapter mAdapter;
    private SelectionProvider mSelectionProvider;

    private VanMediaAdapter.CheckStateListener mCheckStateListener;
    private VanMediaAdapter.OnMediaClickListener mOnMediaClickListener;
    private final AlbumMediaCollection mAlbumMediaCollection = new AlbumMediaCollection();

    public static VanMediaFragment newInstance(Album album) {
        VanMediaFragment fragment = new VanMediaFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ALBUM, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SelectionProvider) {
            mSelectionProvider = (SelectionProvider) context;
        } else {
            throw new IllegalStateException("Context must implement SelectionProvider.");
        }
        if (context instanceof VanMediaAdapter.CheckStateListener) {
            mCheckStateListener = (VanMediaAdapter.CheckStateListener) context;
        }
        if (context instanceof VanMediaAdapter.OnMediaClickListener) {
            mOnMediaClickListener = (VanMediaAdapter.OnMediaClickListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_van_media);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Album album = getArguments().getParcelable(EXTRA_ALBUM);

        mAdapter = new VanMediaAdapter(getContext(),
                mSelectionProvider.provideSelectedItemCollection(), mRecyclerView);
        mAdapter.registerCheckStateListener(this);
        mAdapter.registerOnMediaClickListener(this);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), VanConfig.getInstance().spanCount));

        int spacing = getResources().getDimensionPixelSize(R.dimen.van_recycler_spacing);
        mRecyclerView.addItemDecoration(new MediaGridInset(VanConfig.getInstance().spanCount, spacing, false));
        mRecyclerView.setAdapter(mAdapter);
        mAlbumMediaCollection.onCreate(getActivity(), this);
        mAlbumMediaCollection.load(album, VanConfig.getInstance().cameraVisible);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAlbumMediaCollection.onDestroy();
    }

    public void refreshMediaGrid() {
        mAdapter.notifyDataSetChanged();
    }

    public void refreshSelection() {
        mAdapter.refreshSelection();
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onAlbumMediaReset() {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onUpdate() {
        // notify outer Activity that check state changed
        if (mCheckStateListener != null) {
            mCheckStateListener.onUpdate();
        }
    }

    @Override
    public void onMediaClick(Album album, MediaInfo mediaInfo, int adapterPosition) {
        if (mOnMediaClickListener != null) {
            mOnMediaClickListener.onMediaClick((Album) getArguments().getParcelable(EXTRA_ALBUM), mediaInfo, adapterPosition);
        }
    }

    public interface SelectionProvider {
        SelectedItemCollection provideSelectedItemCollection();
    }
}
