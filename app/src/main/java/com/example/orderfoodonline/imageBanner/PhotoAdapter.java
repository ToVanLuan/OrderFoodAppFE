package com.example.orderfoodonline.imageBanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.orderfoodonline.R;

import java.util.List;

public class PhotoAdapter extends PagerAdapter {
    private final Context context;
    private final List<Photo> listPhoto;

    public PhotoAdapter(Context context, List<Photo> listPhoto) {
        this.context = context;
        this.listPhoto = listPhoto;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater
                .from(container.getContext())
                .inflate(R.layout.item_photo_slider, container, false);

        ImageView imagePhoto = view.findViewById(R.id.image_photo);
        Photo photo = listPhoto.get(position);
        if (photo != null) {
            Glide.with(context)
                    .load(photo.getResourceId())
                    .into(imagePhoto);
        }

        // add view to viewgroup
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return (listPhoto != null) ? listPhoto.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
