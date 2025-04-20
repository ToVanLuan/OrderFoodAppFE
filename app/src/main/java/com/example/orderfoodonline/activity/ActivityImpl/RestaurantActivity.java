package com.example.orderfoodonline.activity.ActivityImpl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.orderfoodonline.R;
import com.example.orderfoodonline.model.Food;
import com.example.orderfoodonline.model.FoodSize;
import com.example.orderfoodonline.model.Restaurant;
import com.example.orderfoodonline.network.ApiClient;
import com.example.orderfoodonline.network.ApiService;
import java.text.DecimalFormat;


import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantActivity extends AppCompatActivity {

    private ImageView imageRestaurant;
    private TextView tvRestaurantName, tvRestaurantAddress, tvRestaurantPhone;
    private LinearLayout foodCartContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        // Khởi tạo view
        imageRestaurant = findViewById(R.id.imageRestaurant_category);
        tvRestaurantName = findViewById(R.id.tvRestaurantName_category);
        tvRestaurantAddress = findViewById(R.id.tvRestaurantAddress_category);
        tvRestaurantPhone = findViewById(R.id.tvRestaurantPhone_category);
        foodCartContainer = findViewById(R.id.foodCartContainer);

        ImageView backButton = findViewById(R.id.imageCartC);  // Nút back
        backButton.setOnClickListener(v -> onBackPressed());

        // Nhận ID từ Intent
        long restaurantId = getIntent().getLongExtra("restaurantId", 1);
        fetchRestaurantInfo(restaurantId);
    }

    private void fetchRestaurantInfo(Long id) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Restaurant> call = apiService.getRestaurantById(id);

        call.enqueue(new Callback<Restaurant>() {
            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Restaurant restaurant = response.body();

                    // Hiển thị thông tin nhà hàng
                    tvRestaurantName.setText(restaurant.getName());
                    tvRestaurantAddress.setText(restaurant.getAddress());
                    tvRestaurantPhone.setText("Số điện thoại: "+ restaurant.getPhone());

                    if (restaurant.getImageUrls() != null && !restaurant.getImageUrls().isEmpty()) {
                        String imageUrl = restaurant.getImageUrls().get(0);
                        Glide.with(RestaurantActivity.this)
                                .load(imageUrl.replace("http://", "https://"))
                                .placeholder(R.drawable.img_placeholder)
                                .error(R.drawable.img_error)
                                .into(imageRestaurant);
                    }

                    // Hiển thị danh sách món ăn
                    List<Food> foodList = restaurant.getFoods();
                    if (foodList != null && !foodList.isEmpty()) {
                        LayoutInflater inflater = LayoutInflater.from(RestaurantActivity.this);
                        foodCartContainer.removeAllViews(); // Xóa trước khi thêm

                        for (Food food : foodList) {
                            View foodItemView = inflater.inflate(R.layout.view_food_card, foodCartContainer, false);

                            ImageView imageFood = foodItemView.findViewById(R.id.imageFood);
                            TextView tvNameFood = foodItemView.findViewById(R.id.tvNameFood);
                            TextView tvSizeFood = foodItemView.findViewById(R.id.tvSizeFood);
                            TextView tvPriceFood = foodItemView.findViewById(R.id.tvPriceFood);
                            TextView tvFoodtype = foodItemView.findViewById(R.id.tvFoodRestaurantName);

                            tvNameFood.setText(food.getName());
                            tvFoodtype.setText(food.getType());

                            if (food.getImageUrls() != null && !food.getImageUrls().isEmpty()) {
                                Glide.with(RestaurantActivity.this)
                                        .load(food.getImageUrls().get(0).replace("http://", "https://"))
                                        .placeholder(R.drawable.img_placeholder)
                                        .error(R.drawable.img_error)
                                        .into(imageFood);
                            }
                            String defaultSize = "Thường";

                            ApiService apiService = ApiClient.getClient().create(ApiService.class);
                            apiService.getFoodSizeByFoodIdAndSize(food.getId(), defaultSize)
                                    .enqueue(new Callback<FoodSize>() {
                                        @Override
                                        public void onResponse(Call<FoodSize> call, Response<FoodSize> response) {
                                            if (response.isSuccessful() && response.body() != null) {
                                                FoodSize foodSize = response.body();
                                                tvSizeFood.setText("Size: " + foodSize.getSize());

                                                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                                                tvPriceFood.setText(formatter.format(foodSize.getPrice()));

                                            } else {
                                                tvSizeFood.setText("Size S");
                                                tvPriceFood.setText("18000");
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<FoodSize> call, Throwable t) {
                                            tvSizeFood.setText("Lỗi size");
                                            tvPriceFood.setText("Lỗi giá");
                                        }
                                    });

                            foodCartContainer.addView(foodItemView);
                            foodItemView.setOnClickListener(view -> {
                                Intent intent = new Intent(RestaurantActivity.this, FoodDetailActivity.class);
                                intent.putExtra("foodId", food.getId());
                                intent.putExtra("restaurantId", restaurant.getId());
                                startActivity(intent);
                            });

                        }

                    } else {
                        Toast.makeText(RestaurantActivity.this, "Nhà hàng chưa có món ăn nào", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(RestaurantActivity.this, "Không tìm thấy nhà hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                Toast.makeText(RestaurantActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
