package com.tymate.image_viewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.webkit.URLUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tymate.image_viewer.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
public class ImageViewerActivity extends Activity {

    private static final String EXTRA_IMAGE = "EXTRA_IMAGE";
    private static final String EXTRA_IMAGES = "EXTRA_IMAGES";
    private static final String EXTRA_INDEX = "EXTRA_INDEX";

    public static void start(Context context, @NonNull String image) {
        Intent starter = getStartIntent(context);
        starter.putExtra(EXTRA_IMAGE, image);
        context.startActivity(starter);
    }

    public static void start(Context context, @NonNull List<String> images) {
        start(context, images, 0);
    }

    public static void start(Context context, @NonNull List<String> images, int index) {
        Intent starter = getStartIntent(context);
        starter.putExtra(EXTRA_IMAGES, new ArrayList<>(images));
        starter.putExtra(EXTRA_INDEX, index);
        context.startActivity(starter);
    }

    public static Intent getStartIntent(Context context) {
        return new Intent(context, ImageViewerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String image = getImage();
        if (TextUtils.isEmpty(image)) {
            ViewPager viewPager = new ViewPager(this);
            setContentView(viewPager);
            viewPager.setAdapter(new ImagePagerAdapter(this, getImages()));
            viewPager.setCurrentItem(getInitialIndex());
        } else {
            PhotoView photoView = new PhotoView(this);
            setContentView(photoView);
            if (URLUtil.isValidUrl(image)) {
                Glide.with(this).load(image).apply(RequestOptions.fitCenterTransform()).into(photoView);
            } else {
                Glide.with(this).load(new File(image)).apply(RequestOptions.fitCenterTransform()).into(photoView);
            }
        }
    }

    private String getImage() {
        return getIntent().getStringExtra(EXTRA_IMAGE);
    }

    private List<String> getImages() {
        return getIntent().getStringArrayListExtra(EXTRA_IMAGES);
    }

    private int getInitialIndex() {
        return getIntent().getIntExtra(EXTRA_INDEX, 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}