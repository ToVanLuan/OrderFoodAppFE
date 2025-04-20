package com.example.orderfoodonline.activity.ActivityImpl;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.orderfoodonline.R;
import com.example.orderfoodonline.model.CartItem;
import com.example.orderfoodonline.model.Food;
import com.example.orderfoodonline.model.FoodSize;
import com.example.orderfoodonline.model.Restaurant;
import com.example.orderfoodonline.network.ApiClient;
import com.example.orderfoodonline.network.ApiService;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodDetailActivity extends AppCompatActivity {

    private ImageView foodImage, btnBack, btnAddQuantityFood, btnSubQuantityFood ;
    private TextView foodName, foodDescription, selectedPrice, nameSize1, nameSize2, nameSize3, tvPriceSize1, tvPriceSize2, tvPriceSize3;
    private TextView tvRestaurantName, tvRestaurantAddress, tvFoodQuantityFood;
    private Long foodId;
    private List<FoodSize> foodSizeList;
    private LinearLayout layoutSize1, layoutSize2, layoutSize3;
    private int currentQuantity = 1;
    private int selectedSizeIndex = 0;
    private CheckBox saveFood;
    private SharedPreferences sharedPreferences;
    private LinearLayout btnAddToCart;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        // Ánh xạ view
        foodImage = findViewById(R.id.image);
        foodName = findViewById(R.id.tvFoodName);
        foodDescription = findViewById(R.id.tvDescription);
        selectedPrice = findViewById(R.id.tvPrice);
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        tvRestaurantAddress = findViewById(R.id.tvRestaurantAddress);
        nameSize1 = findViewById(R.id.name_size1);
        nameSize2 = findViewById(R.id.name_size2);
        nameSize3 = findViewById(R.id.name_size3);
        tvPriceSize1 = findViewById(R.id.tvPriceSize1);
        tvPriceSize2 = findViewById(R.id.tvPriceSize2);
        tvPriceSize3 = findViewById(R.id.tvPriceSize3);


        layoutSize1 = findViewById(R.id.layout_size_1);
        layoutSize2 = findViewById(R.id.layout_size_2);
        layoutSize3 = findViewById(R.id.layout_size_3);

        saveFood = findViewById(R.id.checkBoxFavorite);

        btnAddQuantityFood=findViewById(R.id.btnAddQuantity_Food);
        btnSubQuantityFood=findViewById(R.id.btnSubQuantity_Food);
        tvFoodQuantityFood=findViewById(R.id.tvFoodQuantity_Food);
        // Hiển thị số lượng ban đầu
        tvFoodQuantityFood.setText(String.valueOf(currentQuantity));

// Nút tăng
        btnAddQuantityFood.setOnClickListener(v -> {
            currentQuantity++;
            tvFoodQuantityFood.setText(String.valueOf(currentQuantity));
            updateSelectedPrice(selectedSizeIndex); // Cập nhật lại giá
        });

// Nút giảm
        btnSubQuantityFood.setOnClickListener(v -> {
            if (currentQuantity > 1) {
                currentQuantity--;
                tvFoodQuantityFood.setText(String.valueOf(currentQuantity));
                updateSelectedPrice(selectedSizeIndex);
            }
        });

        btnBack=findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> onBackPressed());

        foodId = getIntent().getLongExtra("foodId", 1);

        if (foodId != 1) {
            fetchFoodInfo(foodId);
        } else {
            Toast.makeText(this, "Không tìm thấy món ăn", Toast.LENGTH_SHORT).show();
        }

        layoutSize1.setOnClickListener(v -> updateSizeSelected(0));
        layoutSize2.setOnClickListener(v -> updateSizeSelected(1));
        layoutSize3.setOnClickListener(v -> updateSizeSelected(2));

        btnAddToCart=findViewById(R.id.btnAddToCart);
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
         userId = sharedPreferences.getString("userId", "");
        btnAddToCart.setOnClickListener(v ->{
            fetchCartAdd(userId);

        });
        saveFood.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Long uid = Long.parseLong(userId);

                if (foodSizeList != null && !foodSizeList.isEmpty()) {
                    String selectedSize = foodSizeList.get(selectedSizeIndex).getSize();
                    saveFoodToServer(uid, foodId, selectedSize);
                }
            }
        });
    }



    private void updateSizeSelected(int index) {
        selectedSizeIndex = index; // lưu lại index đã chọn

        layoutSize1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray)));
        layoutSize2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray)));
        layoutSize3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.gray)));

        if (index == 0) {
            layoutSize1.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));
        } else if (index == 1) {
            layoutSize2.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));
        } else if (index == 2) {
            layoutSize3.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.red)));
        }

        updateSelectedPrice(index);
    }
    private void saveFoodToServer(Long userId, Long foodId, String size) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.saveFood(userId, foodId, size).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FoodDetailActivity.this, "Đã lưu món ăn yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FoodDetailActivity.this, "Không thể lưu món ăn", Toast.LENGTH_SHORT).show();
                    saveFood.setChecked(false); // Bỏ chọn nếu lỗi
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Lỗi kết nối khi lưu món ăn", Toast.LENGTH_SHORT).show();
                saveFood.setChecked(false); // Bỏ chọn nếu lỗi
            }
        });
    }



    private void fetchFoodInfo(Long id) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getFoodById(id).enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Food food = response.body();
                    foodName.setText(food.getName());
                    foodDescription.setText(food.getDescription());

                    if (food.getImageUrls() != null && !food.getImageUrls().isEmpty()) {
                        Glide.with(FoodDetailActivity.this)
                                .load(food.getImageUrls().get(0).replace("http://", "https://"))
                                .placeholder(R.drawable.img_placeholder)
                                .into(foodImage);
                    }

                    long restaurantId = getIntent().getLongExtra("restaurantId", 1);
                    fetchRestaurantInfo(restaurantId);
                    fetchFoodSizes(food.getId());

                } else {
                    Toast.makeText(FoodDetailActivity.this, "Không lấy được thông tin món ăn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Lỗi tải món ăn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchRestaurantInfo(Long restaurantId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getRestaurantById(restaurantId).enqueue(new Callback<Restaurant>() {
            @Override
            public void onResponse(Call<Restaurant> call, Response<Restaurant> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Restaurant restaurant = response.body();
                    tvRestaurantName.setText(restaurant.getName());
                    tvRestaurantAddress.setText(restaurant.getAddress());
                } else {
                    Toast.makeText(FoodDetailActivity.this, "Không lấy được thông tin nhà hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Restaurant> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Lỗi tải nhà hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFoodSizes(Long foodId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        apiService.getFoodSizesByFoodId(foodId).enqueue(new Callback<List<FoodSize>>() {
            @Override
            public void onResponse(Call<List<FoodSize>> call, Response<List<FoodSize>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    foodSizeList = response.body();

                    NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

                    switch (foodSizeList.size()) {
                        case 1:
                            layoutSize1.setVisibility(View.GONE);
                            layoutSize2.setVisibility(View.GONE);
                            layoutSize3.setVisibility(View.VISIBLE);

                            FoodSize size3 = foodSizeList.get(0);
                            nameSize3.setText(size3.getSize());
                            tvPriceSize3.setText(formatter.format(size3.getPrice()));

                            updateSizeSelected(0); // chọn mặc định size duy nhất
                            break;

                        case 2:
                            layoutSize1.setVisibility(View.GONE);
                            layoutSize2.setVisibility(View.VISIBLE);
                            layoutSize3.setVisibility(View.VISIBLE);

                            FoodSize size2 = foodSizeList.get(0);
                            FoodSize size3_2 = foodSizeList.get(1);

                            nameSize2.setText(size2.getSize());
                            tvPriceSize2.setText(formatter.format(size2.getPrice()));
                            nameSize3.setText(size3_2.getSize());
                            tvPriceSize3.setText(formatter.format(size3_2.getPrice()));

                            updateSizeSelected(0); // chọn mặc định size đầu tiên
                            break;

                        default:
                            layoutSize1.setVisibility(View.VISIBLE);
                            layoutSize2.setVisibility(View.VISIBLE);
                            layoutSize3.setVisibility(View.VISIBLE);

                            FoodSize size1 = foodSizeList.get(0);
                            FoodSize size2_2 = foodSizeList.get(1);
                            FoodSize size3_3 = foodSizeList.get(2);

                            nameSize1.setText(size1.getSize());
                            tvPriceSize1.setText(formatter.format(size1.getPrice()));
                            nameSize2.setText(size2_2.getSize());
                            tvPriceSize2.setText(formatter.format(size2_2.getPrice()));
                            nameSize3.setText(size3_3.getSize());
                            tvPriceSize3.setText(formatter.format(size3_3.getPrice()));

                            updateSizeSelected(0); // chọn mặc định size đầu tiên
                            break;
                    }

                    updateSizeSelected(0); // chọn size đầu tiên mặc định

                } else {
                    Toast.makeText(FoodDetailActivity.this, "Không có size cho món ăn", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<List<FoodSize>> call, Throwable t) {
                Toast.makeText(FoodDetailActivity.this, "Lỗi tải size", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void fetchCartAdd(String userId) {
        if (foodSizeList == null || foodSizeList.isEmpty()) {
            Toast.makeText(this, "Chưa chọn size món ăn", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Long uid = Long.parseLong(userId);
            FoodSize selectedSize = foodSizeList.get(selectedSizeIndex);
            Long foodId = selectedSize.getFoodId();

            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<Food> foodCall = apiService.getFoodById(foodId);

            foodCall.enqueue(new Callback<Food>() {
                @Override
                public void onResponse(Call<Food> call, Response<Food> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Food food = response.body();

                        // Lấy danh sách ảnh từ Food
                        List<String> imageUrls = food.getImageUrls();

                        // Tạo đối tượng CartItem
                        CartItem cartItem = new CartItem(
                                foodId,
                                food.getName(),
                                imageUrls.get(0),
                                selectedSize.getSize(),
                                currentQuantity,
                                selectedSize.getPrice()
                        );

                        // Gửi yêu cầu thêm món vào giỏ hàng
                        Call<ResponseBody> callAdd = apiService.addToCart(uid, cartItem);
                        callAdd.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    try {
                                        String message = response.body().string();
                                        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                                                .setAction("Xem giỏ hàng", view -> {
                                                    startActivity(new Intent(FoodDetailActivity.this, CartActivity.class));
                                                })
                                                .setActionTextColor(getResources().getColor(R.color.colorPrimary))
                                                .setBackgroundTint(getResources().getColor(R.color.colorSuccess))
                                                .setTextColor(getResources().getColor(R.color.white))
                                                .show();
                                    } catch (IOException e) {
                                        Toast.makeText(FoodDetailActivity.this, "Không đọc được phản hồi từ máy chủ", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Snackbar.make(findViewById(android.R.id.content), "Thêm vào giỏ hàng thất bại", Snackbar.LENGTH_SHORT)
                                            .setBackgroundTint(getResources().getColor(R.color.colorError))
                                            .setTextColor(getResources().getColor(R.color.white))
                                            .show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(FoodDetailActivity.this, "Lỗi kết nối khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(FoodDetailActivity.this, "Không tìm thấy thông tin món ăn", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Food> call, Throwable t) {
                    Toast.makeText(FoodDetailActivity.this, "Lỗi kết nối khi lấy thông tin món ăn", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Lỗi ID người dùng", Toast.LENGTH_SHORT).show();
        }
    }



    private void updateSelectedPrice(int index) {
        if (foodSizeList != null && index >= 0 && index < foodSizeList.size()) {
            double basePrice = foodSizeList.get(index).getPrice();
            double totalPrice = basePrice * currentQuantity;

            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            selectedPrice.setText("Giá: " + formatter.format(totalPrice));
        }
    }
}
