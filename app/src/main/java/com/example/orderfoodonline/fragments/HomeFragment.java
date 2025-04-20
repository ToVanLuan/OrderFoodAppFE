package com.example.orderfoodonline.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.orderfoodonline.R;
import com.example.orderfoodonline.activity.ActivityImpl.CartActivity;
import com.example.orderfoodonline.activity.ActivityImpl.LoginActivity;
import com.example.orderfoodonline.adapter.RestaurantAdapter;
import com.example.orderfoodonline.imageBanner.Photo;
import com.example.orderfoodonline.imageBanner.PhotoAdapter;
import com.example.orderfoodonline.model.Restaurant;
import com.example.orderfoodonline.network.ApiClient;
import com.example.orderfoodonline.network.ApiService;
import com.example.orderfoodonline.repositoryInit.DataInitFragmentHome;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ViewPager viewPager;
    private Timer timer;
    private List<Photo> listPhoto;

    private SharedPreferences sharedPreferences;
    private String userId;
    private TextView imageCartBadge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getActivity(), "Không tìm thấy người dùng, vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
            return rootView;
        }

        // Banner
        viewPager = rootView.findViewById(R.id.viewpager);
        CircleIndicator circleIndicator = rootView.findViewById(R.id.circle_indicator);
        listPhoto = DataInitFragmentHome.listPhoto;
        PhotoAdapter photoAdapter = new PhotoAdapter(requireContext(), listPhoto);
        viewPager.setAdapter(photoAdapter);
        circleIndicator.setViewPager(viewPager);
        photoAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());
        autoSlideImage();

        // RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView_Restaurant);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        fetchRestaurants(recyclerView);

        // Badge trên icon giỏ hàng
        imageCartBadge = rootView.findViewById(R.id.imageCart_badge);
        fetchCartCountForImageIcon();

        rootView.findViewById(R.id.imageCart).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), CartActivity.class));
        });


        rootView.findViewById(R.id.imageLogout).setOnClickListener(view -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setMessage("Bạn có muốn đăng xuất tài khoản?");
            dialog.setPositiveButton("Có", (dialogInterface, i) -> {
                sharedPreferences.edit().clear().apply();
                Toast.makeText(getActivity(), "Đã đăng xuất khỏi hệ thống!", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            });
            dialog.setNegativeButton("Không", null);
            dialog.show();
        });

        return rootView;
    }

    private void fetchRestaurants(RecyclerView recyclerView) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAllRestaurants().enqueue(new Callback<List<Restaurant>>() {
            @Override
            public void onResponse(Call<List<Restaurant>> call, Response<List<Restaurant>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    recyclerView.setAdapter(new RestaurantAdapter(response.body()));
                } else {
                    Toast.makeText(requireContext(), "Không tải được danh sách nhà hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Restaurant>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void autoSlideImage() {
        if (listPhoto == null || listPhoto.isEmpty() || viewPager == null) return;
        if (timer == null) timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    int currentItem = viewPager.getCurrentItem();
                    int totalItem = listPhoto.size() - 1;
                    viewPager.setCurrentItem(currentItem < totalItem ? currentItem + 1 : 0);
                });
            }
        }, 500, 3000);
    }

    private void fetchCartCountForImageIcon() {
        try {
            long userIdLong = Long.parseLong(userId);
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            apiService.countCartItems(userIdLong).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        int count = response.body();
                        if (count > 0) {
                            imageCartBadge.setText(String.valueOf(count));
                            imageCartBadge.setVisibility(View.VISIBLE);
                        } else {
                            imageCartBadge.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    imageCartBadge.setVisibility(View.GONE);
                }
            });
        } catch (NumberFormatException e) {
            imageCartBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCartCountForImageIcon();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
