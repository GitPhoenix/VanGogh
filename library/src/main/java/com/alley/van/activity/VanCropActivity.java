package com.alley.van.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.alley.van.R;
import com.alley.van.base.BaseVanActivity;
import com.alley.van.helper.VanCropType;
import com.alley.van.model.VanConfig;
import com.kevin.crop.UCrop;
import com.kevin.crop.util.BitmapLoadUtils;
import com.kevin.crop.view.CropImageView;
import com.kevin.crop.view.GestureCropImageView;
import com.kevin.crop.view.OverlayView;
import com.kevin.crop.view.TransformImageView;
import com.kevin.crop.view.UCropView;

import java.io.OutputStream;


/**
 * 裁剪图片
 *
 * @author Phoenix
 * @date 2017/5/2 16:44
 */
public class VanCropActivity extends BaseVanActivity {
    private Toolbar toolbar;
    private AppBarLayout appBar;
    private TextView toolTitle;

    private UCropView mUCropView;
    private OverlayView mOverlayView;
    private FloatingActionButton mSaveFab;
    private GestureCropImageView mGestureCropImageView;

    private Uri mOutputUri;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_crop;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mUCropView = (UCropView) findViewById(R.id.UCropView_crop);
        mSaveFab = (FloatingActionButton) findViewById(R.id.fab_crop);

        toolbar = (Toolbar) findViewById(R.id.toolbar_crop);
        appBar = (AppBarLayout) findViewById(R.id.AppBarLayout_crop);
        toolTitle = (TextView) findViewById(R.id.tv_toolbar_crop_title);

        mGestureCropImageView = mUCropView.getCropImageView();
        mOverlayView = mUCropView.getOverlayView();
    }

    @Override
    protected void setSubView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");

        setCropView();
        setImageData(getIntent());
    }

    @Override
    protected void initEvent() {
        mGestureCropImageView.setTransformImageListener(mImageListener);
        mSaveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropAndSaveImage();
            }
        });
    }

    /**
     * 初始化裁剪View
     */
    private void setCropView() {
        switch (VanConfig.getInstance().cropType) {
            case VanCropType.CROP_TYPE_RECTANGLE:
                setCropRectangle(VanCropType.CROP_TYPE_RECTANGLE);
                break;

            case VanCropType.CROP_TYPE_RECTANGLE_GRID:
                setCropRectangle(VanCropType.CROP_TYPE_RECTANGLE_GRID);
                break;

            case VanCropType.CROP_TYPE_CIRCLE:
                setCropCircle(VanCropType.CROP_TYPE_CIRCLE);
                break;

            case VanCropType.CROP_TYPE_CIRCLE_STROKE:
                setCropCircle(VanCropType.CROP_TYPE_CIRCLE_STROKE);
                break;

            default:
                break;
        }
    }

    private void setCropRectangle(int cropType) {
        // 设置允许缩放
        mGestureCropImageView.setScaleEnabled(true);
        // 设置禁止旋转
        mGestureCropImageView.setRotateEnabled(false);
        // 设置外部阴影颜色
        mOverlayView.setDimmedColor(Color.parseColor("#A0000000"));
        // 设置周围阴影是否为椭圆(如果false则为矩形)
        mOverlayView.setOvalDimmedLayer(false);
        // 设置显示裁剪边框
        mOverlayView.setShowCropFrame(true);

        if (cropType == VanCropType.CROP_TYPE_RECTANGLE_GRID) {
            // 设置不显示裁剪网格
            mOverlayView.setShowCropGrid(true);
            // 设置裁剪网格的行数
            mOverlayView.setCropGridRowCount(2);
            // 设置裁剪网格的列数
            mOverlayView.setCropGridColumnCount(2);
        } else {
            // 设置不显示裁剪网格
            mOverlayView.setShowCropGrid(false);
        }
    }

    private void setCropCircle(int cropType) {
        // 设置允许缩放
        mGestureCropImageView.setScaleEnabled(true);
        // 设置禁止旋转
        mGestureCropImageView.setRotateEnabled(false);
        // 设置外部阴影颜色
        mOverlayView.setDimmedColor(Color.parseColor("#A0000000"));
        // 设置周围阴影是否为椭圆(如果false则为矩形)
        mOverlayView.setOvalDimmedLayer(true);
        // 设置不显示裁剪网格
        mOverlayView.setShowCropGrid(false);

        if (cropType == VanCropType.CROP_TYPE_CIRCLE) {
            // 设置显示裁剪边框
            mOverlayView.setShowCropFrame(true);
        } else {
            // 设置显示裁剪边框
            mOverlayView.setShowCropFrame(false);
        }
    }

    private void setImageData(Intent intent) {
        Uri inputUri = intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI);
        mOutputUri = intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);

        if (inputUri != null && mOutputUri != null) {
            try {
                mGestureCropImageView.setImageUri(inputUri);
            } catch (Exception e) {
                setResultException(e);
                finish();
            }
        } else {
            setResultException(new NullPointerException("Both input and output Uri must be specified"));
            finish();
        }

        // 设置裁剪宽高比
        if (intent.getBooleanExtra(UCrop.EXTRA_ASPECT_RATIO_SET, false)) {
            float aspectRatioX = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_X, 0);
            float aspectRatioY = intent.getFloatExtra(UCrop.EXTRA_ASPECT_RATIO_Y, 0);

            if (aspectRatioX > 0 && aspectRatioY > 0) {
                mGestureCropImageView.setTargetAspectRatio(aspectRatioX / aspectRatioY);
            } else {
                mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
            }
        }

        // 设置裁剪的最大宽高
        if (intent.getBooleanExtra(UCrop.EXTRA_MAX_SIZE_SET, false)) {
            int maxSizeX = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_X, 0);
            int maxSizeY = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_Y, 0);

            if (maxSizeX > 0 && maxSizeY > 0) {
                mGestureCropImageView.setMaxResultImageSizeX(maxSizeX);
                mGestureCropImageView.setMaxResultImageSizeY(maxSizeY);
            } else {
                Log.w(TAG, "EXTRA_MAX_SIZE_X and EXTRA_MAX_SIZE_Y must be greater than 0");
            }
        }
    }

    /**
     * 裁剪图片，并保存
     */
    private void cropAndSaveImage() {
        OutputStream outputStream = null;
        try {
            final Bitmap croppedBitmap = mGestureCropImageView.cropImage();
            if (croppedBitmap != null) {
                outputStream = getContentResolver().openOutputStream(mOutputUri);
                croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
                croppedBitmap.recycle();

                setResultUri(mOutputUri, mGestureCropImageView.getTargetAspectRatio());
                finish();
            } else {
                setResultException(new NullPointerException("CropImageView.cropImage() returned null."));
            }
        } catch (Exception e) {
            setResultException(e);
            finish();
        } finally {
            BitmapLoadUtils.close(outputStream);
        }
    }

    /**
     * 裁剪图片成功
     *
     * @param uri
     * @param resultAspectRatio
     */
    private void setResultUri(Uri uri, float resultAspectRatio) {
        Intent intent = new Intent();
        intent.putExtra(UCrop.EXTRA_OUTPUT_URI, uri);
        intent.putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio);
        setResult(RESULT_OK, intent);
    }

    /**
     * 裁剪图片失败
     *
     * @param throwable
     */
    private void setResultException(Throwable throwable) {
        Intent intent = new Intent();
        intent.putExtra(UCrop.EXTRA_ERROR, throwable);
        setResult(UCrop.RESULT_ERROR, intent);
    }

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {

        }

        @Override
        public void onScale(float currentScale) {

        }

        @Override
        public void onLoadComplete() {
            Animation fadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.crop_fade_in);
            fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mUCropView.setVisibility(View.VISIBLE);
                    mGestureCropImageView.setImageToWrapCropBounds();
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            mUCropView.startAnimation(fadeInAnimation);
        }

        @Override
        public void onLoadFailure(Exception e) {
            setResultException(e);
            finish();
        }
    };
}
