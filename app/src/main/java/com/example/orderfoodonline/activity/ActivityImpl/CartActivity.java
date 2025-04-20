package com.example.orderfoodonline.activity.ActivityImpl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderfoodonline.R;
import com.example.orderfoodonline.adapter.CartAdapter;
import com.example.orderfoodonline.network.ApiClient;
import com.example.orderfoodonline.network.ApiService;
import com.example.orderfoodonline.model.CartItem;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private SharedPreferences sharedPreferences;
    private String userId ; // Bạn có thể thay thế bằng ID thực tế hoặc từ SharedPreferences

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart); // hoặc activity_cart.xml nếu đúng tên layout

        recyclerView = findViewById(R.id.recyclerCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems);
        recyclerView.setAdapter(cartAdapter);

        fetchCartItems();
    }

    private void fetchCartItems() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        Long uid = Long.parseLong(userId);
        Call<List<CartItem>> call = apiService.getCartItems(uid);

        call.enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartItems.clear();
                    cartItems.addAll(response.body());
                    cartAdapter.notifyDataSetChanged(); // Cập nhật dữ liệu vào adapter
                } else {
                    Toast.makeText(CartActivity.this, "Không lấy được giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
