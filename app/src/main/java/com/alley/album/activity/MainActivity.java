package com.alley.album.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alley.album.R;
import com.alley.album.base.BaseActivity;
import com.alley.album.helper.GifSizeFilter;
import com.alley.album.helper.GlideImageLoader;
import com.alley.album.helper.VanToast;
import com.alley.album.util.MToast;
import com.alley.album.widget.ItemDialogFragment;
import com.alley.van.VanGogh;
import com.alley.van.activity.VanCropActivity;
import com.alley.van.helper.VanCropType;
import com.alley.van.helper.VanMediaFilter;
import com.alley.van.helper.VanMediaType;
import com.alley.van.model.VanConfig;
import com.bumptech.glide.Glide;
import com.kevin.crop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final int REQUEST_CODE_CAMERA = 32;

    private ImageView ivDisplay;
    private Button btnVanGogh;
    private RecyclerView recyclerView;
    private UriAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        setSubView();

        initEvent();
    }

    private void initView() {
        btnVanGogh = (Button) findViewById(R.id.btn_main_van);
        ivDisplay = (ImageView) findViewById(R.id.iv_van_display);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
    }

    private void setSubView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter = new UriAdapter());
    }

    private void initEvent() {
        btnVanGogh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //文本
        ArrayList<String> listContent = new ArrayList<String>();
        listContent.add("拍照");
        listContent.add("从相册选择");

        //文本颜色
        ArrayList<String> listColor = new ArrayList<String>();
        listColor.add("#848484");
        listColor.add("#d50201");

        //文本字体大小
        ArrayList<String> listContentSize = new ArrayList<String>();
        listContentSize.add("20");
        listContentSize.add("18");

        Bundle bundle = new Bundle();
        bundle.putBoolean(ItemDialogFragment.DIALOG_BACK, true);
        bundle.putBoolean(ItemDialogFragment.DIALOG_CANCELABLE, true);
        bundle.putBoolean(ItemDialogFragment.DIALOG_CANCELABLE_TOUCH_OUT_SIDE, true);
        bundle.putStringArrayList(ItemDialogFragment.DIALOG_ITEM_CONTENT, listContent);
        bundle.putStringArrayList(ItemDialogFragment.DIALOG_ITEM_COLOR, listColor);
        bundle.putStringArrayList(ItemDialogFragment.DIALOG_ITEM_CONTENT_SIZE, listContentSize);
        bundle.putString(ItemDialogFragment.DIALOG_CANCEL, "取消");
        final ItemDialogFragment dialogFragment = ItemDialogFragment.newInstance(ItemDialogFragment.class, bundle);
        dialogFragment.setOnItemClickDialogListener(new ItemDialogFragment.OnItemClickDialogListener() {
            @Override
            public void onItemClick(int position, String content) {
                if (position == 0) {//拍照
                    pictureForCamera();
                } else if (position == 1) {//从相册选择
                    pictureForVanGogh();
                }
                dialogFragment.dismiss();
            }

            @Override
            public void onCancel(TextView tvCancel) {
                dialogFragment.dismiss();
            }
        });
        dialogFragment.show(getSupportFragmentManager(), "");
    }

    //=====================================================核心代码===================================================================================================================
    private void pictureForCamera() {//拍照
        requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0x0001);
    }

    private void pictureForVanGogh() {//从相册选择
        requestPermission(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0x0002);
    }

    @Override
    public void permissionGrant(boolean isGranted, int requestCode) {
        super.permissionGrant(isGranted, requestCode);
        if (!isGranted) {
            return;
        }

        switch (requestCode) {
            case 0x0001://拍照
                VanGogh.from(MainActivity.this)
                        .choose(VanMediaType.ofAll())//拍照时，无效
                        .cameraVisible(true, getPackageName())//拍照时，第一个参数无效
                        .withResultSize(1024, 1024)
                        .cropEnable(true, VanCropType.CROP_TYPE_RECTANGLE)//第一个参数为FALSE时，第二个参数无效
                        .theme(R.style.VanTheme_ActivityAnimation)
                        .thumbnailScale(0.85f)
                        .toast(new VanToast())
                        .imageLoader(new GlideImageLoader())
                        .forCamera(REQUEST_CODE_CAMERA);
                break;

            case 0x0002://从相册选择
                VanGogh.from(MainActivity.this)
                        .choose(VanMediaType.ofAll())
                        .countable(true)//若开启裁剪，则无效
                        .maxCount(9)
                        .rowCount(3)
                        .cameraVisible(true, getPackageName())//第一个参数为FALSE时，第二个参数无效
                        .withResultSize(1024, 1024)
                        .cropEnable(false, VanCropType.CROP_TYPE_RECTANGLE)//第一个参数为TRUE时，则可选中数量被设为1，此时maxSelectable(9)无效；第一个参数为FALSE时，第二个参数无效
                        .theme(R.style.VanTheme_Dracula)
                        .addFilter(new GifSizeFilter(320, 320, 5 * VanMediaFilter.K * VanMediaFilter.K))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.85f)
                        .toast(new VanToast())
                        .imageLoader(new GlideImageLoader())
                        .forResult(REQUEST_CODE_CHOOSE);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == UCrop.REQUEST_CROP) {//拍照并裁剪成功
            handleCropResult(data);
        } else if (requestCode == UCrop.RESULT_ERROR) {//拍照并裁剪失败
            handleCropError(data);
        } else if (requestCode == REQUEST_CODE_CHOOSE) {//从相册选择
            mAdapter.setData(VanGogh.obtainResult(data));
            display(VanGogh.obtainResult(data).get(0));
        } else if (requestCode == REQUEST_CODE_CAMERA) {//拍照
            Uri contentUri = VanGogh.obtainCamera();
            if (contentUri == null) {
                return;
            }

            if (!VanConfig.getInstance().cropEnable) {
                ArrayList<Uri> selected = new ArrayList<>();
                selected.add(contentUri);

                mAdapter.setData(selected);
                display(contentUri);
            } else {//拍照之后跳转到裁剪页面
                startCropActivity(contentUri);
            }
        }
    }

    /**
     * 处理剪切成功的返回值
     *
     * @param result
     */
    private void handleCropResult(Intent result) {
        final Uri resultUri = UCrop.getOutput(result);

        String filePath = resultUri.getEncodedPath();
        String imagePath = Uri.decode(filePath);

        if (resultUri == null) {
            return;
        }

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivDisplay.setImageBitmap(bitmap);
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
            MToast.shortToast("无法裁剪图片");
        }
    }

    /**
     * 跳转到裁剪页面
     *
     * @param source 需要裁剪的图片
     */
    private void startCropActivity(Uri source) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String imageFileName = "IMG_" + dateFormat.format(new Date());

        Uri uri = Uri.fromFile(new File(getCacheDir(), imageFileName.concat(".png")));
        UCrop.of(source, uri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(1024, 1024)
                .withTargetActivity(VanCropActivity.class)
                .start(this);
    }
    //=====================================================核心代码===================================================================================================================

    private void display(Uri uri) {
        Glide.with(this)
                .load(uri)
                .asBitmap()  // some .jpeg files are actually gif
                .centerCrop()
                .into(ivDisplay);
    }

    private static class UriAdapter extends RecyclerView.Adapter<UriAdapter.UriViewHolder> {

        private List<Uri> mUris;

        void setData(List<Uri> uris) {
            mUris = uris;
            notifyDataSetChanged();
        }

        @Override
        public UriViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UriViewHolder((TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_uri, parent, false));
        }

        @Override
        public void onBindViewHolder(UriViewHolder holder, int position) {
            Uri uri = mUris.get(position);
            holder.mUri.setText(uri.toString());
        }

        @Override
        public int getItemCount() {
            return mUris == null ? 0 : mUris.size();
        }

        static class UriViewHolder extends RecyclerView.ViewHolder {

            private TextView mUri;

            UriViewHolder(TextView uri) {
                super(uri);
                mUri = uri;
            }
        }
    }
}
