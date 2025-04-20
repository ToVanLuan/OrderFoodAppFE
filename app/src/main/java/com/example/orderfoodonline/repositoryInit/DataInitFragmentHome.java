package com.example.orderfoodonline.repositoryInit;

import com.example.orderfoodonline.R;
import com.example.orderfoodonline.imageBanner.Photo;

import java.util.ArrayList;
import java.util.List;

public class DataInitFragmentHome {
    // list ảnh hiển thị trong home slider
    public static final List<Photo> listPhoto = new ArrayList<>();
    static {
        listPhoto.add(new Photo(R.drawable.image_banh_my));
        listPhoto.add(new Photo(R.drawable.quan_com_tam_phuc_map));
        listPhoto.add(new Photo(R.drawable.image_3));
        listPhoto.add(new Photo(R.drawable.image_4));
        listPhoto.add(new Photo(R.drawable.banner_rice));
        listPhoto.add(new Photo(R.drawable.quan_com_tam_phuc_map));
        listPhoto.add(new Photo(R.drawable.quan_xien_ban_phung_khoang));
        listPhoto.add(new Photo(R.drawable.quan_banh_my_cho_phung_khoang));
    }
}
