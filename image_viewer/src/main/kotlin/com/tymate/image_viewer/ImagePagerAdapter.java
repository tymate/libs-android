package com.tymate.image_viewer;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.tymate.image_viewer.photoview.PhotoView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

/**
 * Created by Aurélien Cocq
 * aurelien@tymate.com
 */
class ImagePagerAdapter extends PagerAdapter {

    private final List<String> images;
    private final Context context;

    ImagePagerAdapter(Context context, @NonNull List<String> images) {
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NotNull View view, @NotNull Object object) {
        return view == object;
    }

    @Override
    public void setPrimaryItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
        try {
            super.setPrimaryItem(container, position, object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(context);
        String image = getImage(position);
        if (image == null) {
            return photoView;
        }
        Glide.with(context).load(image)
                .apply(RequestOptions.fitCenterTransform())
                .into(photoView);
        container.addView(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(@NotNull ViewGroup container, int position, @NotNull Object object) {
        try {
            container.removeView((View) object);
        } catch (Exception e) {
            Log.w("ImageViewer", e);
        }
    }

    public String getImage(int position) {
        try {
            return images.get(position);
        } catch (Exception e) {
            return null;
        }
    }
}