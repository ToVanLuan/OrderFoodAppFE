package com.example.orderfoodonline.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.orderfoodonline.R;
import com.example.orderfoodonline.network.ApiClient;
import com.example.orderfoodonline.network.ApiService;
import com.example.orderfoodonline.model.ResaurantSaved;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.orderfoodonline.model.FoodSaved;

public class FavoriteFragment extends Fragment {

    private LinearLayout layoutSaved;
    private LinearLayout btnSavedFood, btnSavedRestaurant;
    private TextView tvSavedFood, tvSavedRestaurant;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved, container, false);
        layoutSaved = view.findViewById(R.id.layout_saved);

        btnSavedFood = view.findViewById(R.id.btn_saved_food);
        btnSavedRestaurant = view.findViewById(R.id.btn_saved_restaurant);
        tvSavedFood = view.findViewById(R.id.tv_saved_food);
        tvSavedRestaurant = view.findViewById(R.id.tv_saved_restaurant);

        loadSavedRestaurants(); // Default

        btnSavedFood.setOnClickListener(v -> {
            highlightSelectedTab(true);
            loadSavedFoods();
        });

        btnSavedRestaurant.setOnClickListener(v -> {
            highlightSelectedTab(false);
            loadSavedRestaurants();
        });

        return view;
    }

    private void highlightSelectedTab(boolean isFoodSelected) {
        if (isFoodSelected) {
            btnSavedFood.setBackgroundColor(getResources().getColor(R.color.light_blue));
            btnSavedRestaurant.setBackgroundColor(getResources().getColor(R.color.silver));
        } else {
            btnSavedRestaurant.setBackgroundColor(getResources().getColor(R.color.light_blue));
            btnSavedFood.setBackgroundColor(getResources().getColor(R.color.silver));
        }
    }

    private void loadSavedRestaurants() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userIdStr = sharedPreferences.getString("userId", "");

        if (userIdStr.isEmpty()) {
            Toast.makeText(getContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = Long.parseLong(userIdStr);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<ResaurantSaved>> call = apiService.getSavedRestaurants(userId);

        call.enqueue(new Callback<List<ResaurantSaved>>() {
            @Override
            public void onResponse(Call<List<ResaurantSaved>> call, Response<List<ResaurantSaved>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displaySavedRestaurants(response.body());
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy nhà hàng đã lưu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ResaurantSaved>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySavedRestaurants(List<ResaurantSaved> restaurants) {
        layoutSaved.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (ResaurantSaved restaurant : restaurants) {
            View cardView = inflater.inflate(R.layout.view_restaurant_card, layoutSaved, false);

            TextView nameText = cardView.findViewById(R.id.tvRestaurantName_res_cart);
            TextView addressText = cardView.findViewById(R.id.tvRestaurantAddress_res_cart);
            ImageView image = cardView.findViewById(R.id.imageRestaurant);

            nameText.setText(restaurant.getName());
            addressText.setText(restaurant.getAddress());

            if (restaurant.getImageUrls() != null && !restaurant.getImageUrls().isEmpty()) {
                Glide.with(getContext())
                        .load(restaurant.getImageUrls().get(0).replace("http://", "https://"))
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_error)
                        .into(image);
            }

            layoutSaved.addView(cardView);
        }
    }

    private void loadSavedFoods() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userIdStr = sharedPreferences.getString("userId", "");

        if (userIdStr.isEmpty()) {
            Toast.makeText(getContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        Long userId = Long.parseLong(userIdStr);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<FoodSaved>> call = apiService.getSavedFoods(userId);

        call.enqueue(new Callback<List<FoodSaved>>() {
            @Override
            public void onResponse(Call<List<FoodSaved>> call, Response<List<FoodSaved>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displaySavedFoods(response.body());
                } else {
                    Toast.makeText(getContext(), "Không tìm thấy món ăn đã lưu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<FoodSaved>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaySavedFoods(List<FoodSaved> foods) {
        layoutSaved.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (FoodSaved food : foods) {
            View cardView = inflater.inflate(R.layout.view_food_saved_card, layoutSaved, false);

            TextView name = cardView.findViewById(R.id.tvFoodNameSaved);
            TextView size = cardView.findViewById(R.id.tvFoodSavedSize);
            TextView price = cardView.findViewById(R.id.tvFoodSavedPrice);
            ImageView image = cardView.findViewById(R.id.imageSaveFood);

            name.setText(food.getFoodName());
            size.setText(food.getSize());
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            price.setText(formatter.format(food.getFoodPrice()));

            if (food.getImageUrls() != null && !food.getImageUrls().isEmpty()) {
                Glide.with(getContext())
                        .load(food.getImageUrls().get(0).replace("http://", "https://"))
                        .placeholder(R.drawable.img_placeholder)
                        .error(R.drawable.img_error)
                        .into(image);
            }

            layoutSaved.addView(cardView);
        }
    }
}
