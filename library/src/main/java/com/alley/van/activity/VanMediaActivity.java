package com.alley.van.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alley.van.R;
import com.alley.van.adapter.MediaSpinnerAdapter;
import com.alley.van.adapter.VanMediaAdapter;
import com.alley.van.base.BasePreviewActivity;
import com.alley.van.base.BaseVanActivity;
import com.alley.van.fragment.VanMediaFragment;
import com.alley.van.model.Album;
import com.alley.van.model.AlbumCollection;
import com.alley.van.model.MediaInfo;
import com.alley.van.model.SelectedItemCollection;
import com.alley.van.model.VanConfig;
import com.alley.van.widget.VanMediaSpinner;
import com.kevin.crop.UCrop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Main Activity to display albums and media content (images/videos) in each album and also support media selecting
 * operations.
 */
public class VanMediaActivity extends BaseVanActivity implements AlbumCollection.AlbumCallbacks, AdapterView.OnItemSelectedListener, VanMediaFragment.SelectionProvider, View.OnClickListener, VanMediaAdapter.CheckStateListener, VanMediaAdapter.OnMediaClickListener, VanMediaAdapter.OnPhotoCapture {
    public static final String EXTRA_RESULT_SELECTION = "extra_result_selection";
    private static final int REQUEST_CODE_PREVIEW = 98;
    private static final int REQUEST_CODE_CAPTURE = 99;

    private Toolbar toolbar;
    private TextView tvPreview, tvApply, tvSpinner;
    private FrameLayout flNav, flContainer, flEmpty;
    private VanMediaSpinner mVanMediaSpinner;
    private MediaSpinnerAdapter mAlbumsAdapter;

    private final AlbumCollection mAlbumCollection = new AlbumCollection();
    private SelectedItemCollection mSelectedCollection = new SelectedItemCollection(this);

    @Override
    protected int getLayoutID() {
        return R.layout.activity_media;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        if (VanConfig.getInstance().needOrientationRestriction()) {
            setRequestedOrientation(VanConfig.getInstance().orientation);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_media);
        tvSpinner = (TextView) findViewById(R.id.tv_media_spinner);
        tvPreview = (TextView) findViewById(R.id.tv_media_preview);
        tvApply = (TextView) findViewById(R.id.tv_media_apply);
        flNav = (FrameLayout) findViewById(R.id.fl_media_navigation);
        flContainer = (FrameLayout) findViewById(R.id.fl_media_container);
        flEmpty = (FrameLayout) findViewById(R.id.fl_media_empty);

        mSelectedCollection.onCreate(savedInstanceState, VanConfig.getInstance());
        mAlbumCollection.onCreate(this, this);
        mAlbumCollection.onRestoreInstanceState(savedInstanceState);
        mAlbumCollection.loadAlbums();
    }

    @Override
    protected void setSubView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        if (VanConfig.getInstance().cropEnable && VanConfig.getInstance().maxCount == 1) {
            flNav.setVisibility(View.GONE);
            tvApply.setVisibility(View.GONE);
        } else {
            updateBottomToolbar();
        }

        mAlbumsAdapter = new MediaSpinnerAdapter(this, null, false);
        mVanMediaSpinner = new VanMediaSpinner(this);
        mVanMediaSpinner.setSelectedTextView(tvSpinner);
        mVanMediaSpinner.setPopupAnchorView(flNav);
        mVanMediaSpinner.setAdapter(mAlbumsAdapter);
    }

    @Override
    protected void initEvent() {
        tvPreview.setOnClickListener(this);
        tvApply.setOnClickListener(this);
        mVanMediaSpinner.setOnItemSelectedListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
        mAlbumCollection.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlbumCollection.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == UCrop.REQUEST_CROP) {//裁剪成功
            handleCropResult(data);
        } else if (requestCode == UCrop.RESULT_ERROR) {//裁剪失败
            handleCropError(data);
        } else if (requestCode == REQUEST_CODE_CAPTURE) {//拍照
            Uri contentUri = VanConfig.getInstance().getCurrentPhotoPath();

            if (VanConfig.getInstance().cropEnable && VanConfig.getInstance().maxCount == 1) {//拍照之后跳转到裁剪页面
                startCropActivity(contentUri);
            } else {
                ArrayList<Uri> selected = new ArrayList<>();
                selected.add(contentUri);

                Intent result = new Intent();
                result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selected);
                setResult(RESULT_OK, result);
                finish();
            }
        } else if (requestCode == REQUEST_CODE_PREVIEW) {//预览
            Bundle resultBundle = data.getBundleExtra(BasePreviewActivity.EXTRA_RESULT_BUNDLE);
            ArrayList<MediaInfo> selected = resultBundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
            int collectionType = resultBundle.getInt(SelectedItemCollection.STATE_COLLECTION_TYPE, SelectedItemCollection.COLLECTION_UNDEFINED);
            if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false)) {
                Intent result = new Intent();
                ArrayList<Uri> selectedUris = new ArrayList<>();
                if (selected != null) {
                    for (MediaInfo mediaInfo : selected) {
                        selectedUris.add(mediaInfo.getContentUri());
                    }
                }
                result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selectedUris);
                setResult(RESULT_OK, result);
                finish();
            } else {
                mSelectedCollection.overwrite(selected, collectionType);
                Fragment mediaSelectionFragment = getSupportFragmentManager().findFragmentByTag(VanMediaFragment.class.getSimpleName());
                if (mediaSelectionFragment instanceof VanMediaFragment) {
                    ((VanMediaFragment) mediaSelectionFragment).refreshMediaGrid();
                }
                updateBottomToolbar();
            }
        }
    }

    /**
     * 处理剪切成功的返回值
     *
     * @param result
     */
    private void handleCropResult(Intent result) {
        ArrayList<Uri> uriList = new ArrayList<>();
        uriList.add(UCrop.getOutput(result));

        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, uriList);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 处理剪切失败的返回值
     *
     * @param result
     */
    private void handleCropError(Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
        } else {
            VanConfig.getInstance().toastListener.display("无法剪切选择图片");
        }
    }

    private void updateBottomToolbar() {
        int selectedCount = mSelectedCollection.count();
        if (selectedCount == 0) {
            tvPreview.setEnabled(false);
            tvApply.setEnabled(false);
            tvPreview.setAlpha(0.4f);
            tvApply.setAlpha(0.4f);
            tvApply.setText(getString(R.string.van_navigation_apply_disable));
        } else {
            tvPreview.setEnabled(true);
            tvApply.setEnabled(true);
            tvPreview.setAlpha(1.0f);
            tvApply.setAlpha(1.0f);
            tvApply.setText(getString(R.string.van_navigation_apply, selectedCount));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_media_preview) {
            Intent intent = new Intent(this, VanFiltratePreviewActivity.class);
            intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
            startActivityForResult(intent, REQUEST_CODE_PREVIEW);

        } else if (v.getId() == R.id.tv_media_apply) {
            Intent result = new Intent();
            ArrayList<Uri> selectedUris = (ArrayList<Uri>) mSelectedCollection.asListOfUri();
            result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selectedUris);
            setResult(RESULT_OK, result);
            finish();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mAlbumCollection.setStateCurrentSelection(position);
        mAlbumsAdapter.getCursor().moveToPosition(position);
        Album album = Album.valueOf(mAlbumsAdapter.getCursor());
        if (album.isAll() && VanConfig.getInstance().cameraVisible) {
            album.addCaptureCount();
        }
        onAlbumSelected(album);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onAlbumLoad(final Cursor cursor) {
        mAlbumsAdapter.swapCursor(cursor);
        // select default album.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                cursor.moveToPosition(mAlbumCollection.getCurrentSelection());
                mVanMediaSpinner.setSelection(VanMediaActivity.this, mAlbumCollection.getCurrentSelection());
                Album album = Album.valueOf(cursor);
                if (album.isAll() && VanConfig.getInstance().cameraVisible) {
                    album.addCaptureCount();
                }
                onAlbumSelected(album);
            }
        });
    }

    @Override
    public void onAlbumReset() {
        mAlbumsAdapter.swapCursor(null);
    }

    private void onAlbumSelected(Album album) {
        if (album.isAll() && album.isEmpty()) {
            flContainer.setVisibility(View.GONE);
            flEmpty.setVisibility(View.VISIBLE);
        } else {
            flContainer.setVisibility(View.VISIBLE);
            flEmpty.setVisibility(View.GONE);
            Fragment fragment = VanMediaFragment.newInstance(album);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_media_container, fragment, VanMediaFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public void onUpdate() {
        // notify bottom toolbar that check state changed.
        updateBottomToolbar();
    }

    @Override
    public void onMediaClick(Album album, MediaInfo mediaInfo, int adapterPosition) {
        VanConfig vanConfig = VanConfig.getInstance();
        if (vanConfig.cropEnable && vanConfig.maxCount == 1) {
            startCropActivity(mediaInfo.getContentUri());
            return;
        }

        Intent intent = new Intent(this, VanPreviewActivity.class);
        intent.putExtra(VanPreviewActivity.EXTRA_ALBUM, album);
        intent.putExtra(VanPreviewActivity.EXTRA_ITEM, mediaInfo);
        intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
        startActivityForResult(intent, REQUEST_CODE_PREVIEW);
    }

    /**
     * 跳转到裁剪页面
     *
     * @param source 需要裁剪的图片
     */
    private void startCropActivity(Uri source) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String cacheFileName = "IMG_" + dateFormat.format(new Date());

        Uri uri = Uri.fromFile(new File(getCacheDir(), cacheFileName + ".jpeg"));
        UCrop.of(source, uri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(VanConfig.getInstance().cropWidth, VanConfig.getInstance().cropHeight)
                .withTargetActivity(VanCropActivity.class)
                .start(VanMediaActivity.this);
    }

    @Override
    public SelectedItemCollection provideSelectedItemCollection() {
        return mSelectedCollection;
    }

    @Override
    public void capture() {
        VanConfig.getInstance().dispatchCaptureIntent(this, REQUEST_CODE_CAPTURE);
    }
}
