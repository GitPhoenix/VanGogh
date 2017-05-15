package com.alley.van.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.alley.van.R;
import com.alley.van.adapter.PreviewPagerAdapter;
import com.alley.van.fragment.VanPreviewFragment;
import com.alley.van.model.IncapableCause;
import com.alley.van.model.MediaInfo;
import com.alley.van.model.SelectedItemCollection;
import com.alley.van.model.VanConfig;
import com.alley.van.util.PhotoMetadataUtils;
import com.alley.van.widget.CheckView;


public abstract class BasePreviewActivity extends BaseVanActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    public static final String EXTRA_DEFAULT_BUNDLE = "extra_default_bundle";
    public static final String EXTRA_RESULT_BUNDLE = "extra_result_bundle";
    public static final String EXTRA_RESULT_APPLY = "extra_result_apply";

    protected ViewPager viewPager;
    protected CheckView mCheckView;
    protected TextView tvBack;
    protected TextView tvApply;
    protected TextView tvSize;

    protected VanConfig vanConfig;
    protected PreviewPagerAdapter mAdapter;

    protected int mPreviousPos = -1;
    protected final SelectedItemCollection mSelectedCollection = new SelectedItemCollection(this);

    @Override
    protected int getLayoutID() {
        return R.layout.activity_preview;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        vanConfig = VanConfig.getInstance();
        if (vanConfig.needOrientationRestriction()) {
            setRequestedOrientation(vanConfig.orientation);
        }

        if (savedInstanceState == null) {
            mSelectedCollection.onCreate(getIntent().getBundleExtra(EXTRA_DEFAULT_BUNDLE), vanConfig);
        } else {
            mSelectedCollection.onCreate(savedInstanceState, vanConfig);
        }

        tvBack = (TextView) findViewById(R.id.tv_preview_back);
        tvApply = (TextView) findViewById(R.id.tv_preview_apply);
        tvSize = (TextView) findViewById(R.id.tv_preview_size);
        viewPager = (ViewPager) findViewById(R.id.viewPager_preview);
        mCheckView = (CheckView) findViewById(R.id.check_view);
        updateApplyButton();
    }

    @Override
    protected void setSubView() {
        mAdapter = new PreviewPagerAdapter(getSupportFragmentManager(), null);
        viewPager.setAdapter(mAdapter);
        mCheckView.setCountable(vanConfig.countable);
    }

    @Override
    protected void initEvent() {
        tvBack.setOnClickListener(this);
        tvApply.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);

        mCheckView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MediaInfo mediaInfo = mAdapter.getMediaItem(viewPager.getCurrentItem());
                if (mSelectedCollection.isSelected(mediaInfo)) {
                    mSelectedCollection.remove(mediaInfo);
                    if (vanConfig.countable) {
                        mCheckView.setCheckedNum(CheckView.UNCHECKED);
                    } else {
                        mCheckView.setChecked(false);
                    }
                } else {
                    if (assertAddSelection(mediaInfo)) {
                        mSelectedCollection.add(mediaInfo);
                        if (vanConfig.countable) {
                            mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(mediaInfo));
                        } else {
                            mCheckView.setChecked(true);
                        }
                    }
                }
                updateApplyButton();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mSelectedCollection.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        sendBackResult(false);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_preview_back) {
            onBackPressed();
        } else if (v.getId() == R.id.tv_preview_apply) {
            sendBackResult(true);
            finish();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        PreviewPagerAdapter adapter = (PreviewPagerAdapter) viewPager.getAdapter();
        if (mPreviousPos != -1 && mPreviousPos != position) {
            ((VanPreviewFragment) adapter.instantiateItem(viewPager, mPreviousPos)).resetView();

            MediaInfo mediaInfo = adapter.getMediaItem(position);
            if (vanConfig.countable) {
                int checkedNum = mSelectedCollection.checkedNumOf(mediaInfo);
                mCheckView.setCheckedNum(checkedNum);
                if (checkedNum > 0) {
                    mCheckView.setEnabled(true);
                } else {
                    mCheckView.setEnabled(!mSelectedCollection.maxSelectableReached());
                }
            } else {
                boolean checked = mSelectedCollection.isSelected(mediaInfo);
                mCheckView.setChecked(checked);
                if (checked) {
                    mCheckView.setEnabled(true);
                } else {
                    mCheckView.setEnabled(!mSelectedCollection.maxSelectableReached());
                }
            }
            updateSize(mediaInfo);
        }
        mPreviousPos = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void updateApplyButton() {
        int selectedCount = mSelectedCollection.count();
        if (selectedCount == 0) {
            tvApply.setText(R.string.van_navigation_apply_disable);
            tvApply.setEnabled(false);
            tvApply.setAlpha(0.4f);
        } else {
            tvApply.setEnabled(true);
            tvApply.setAlpha(1.0f);
            tvApply.setText(getString(R.string.van_navigation_apply, selectedCount));
        }
    }

    protected void updateSize(MediaInfo mediaInfo) {
        if (mediaInfo.isGif()) {
            tvSize.setVisibility(View.VISIBLE);
            tvSize.setText(PhotoMetadataUtils.getSizeInMB(mediaInfo.size) + "M");
        } else {
            tvSize.setVisibility(View.GONE);
        }
    }

    protected void sendBackResult(boolean apply) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_BUNDLE, mSelectedCollection.getDataWithBundle());
        intent.putExtra(EXTRA_RESULT_APPLY, apply);
        setResult(Activity.RESULT_OK, intent);
    }

    private boolean assertAddSelection(MediaInfo mediaInfo) {
        IncapableCause cause = mSelectedCollection.isAcceptable(mediaInfo);
        IncapableCause.handleCause(this, cause);
        return cause == null;
    }
}
