package com.example.orderfoodonline.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfoodonline.activity.ActivityImpl.MainActivity;
import com.example.orderfoodonline.R;
import com.example.orderfoodonline.model.Restaurant;
import com.example.orderfoodonline.network.ApiClient;
import com.example.orderfoodonline.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {
    private final List<Restaurant> restaurantList;

    public RestaurantAdapter(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageRestaurant;
        private final TextView tvRestaurantName_res_cart, tvRestaurantAddress_res_cart;
        private CheckBox btnSavedShop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageRestaurant = itemView.findViewById(R.id.imageRestaurant);
            tvRestaurantName_res_cart = itemView.findViewById(R.id.tvRestaurantName_res_cart);
            tvRestaurantAddress_res_cart = itemView.findViewById(R.id.tvRestaurantAddress_res_cart);
            btnSavedShop = itemView.findViewById(R.id.btnSavedShop);
        }
    }

    @NonNull
    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.view_restaurant_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantAdapter.ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        // Lấy ảnh đầu tiên trong danh sách imageUrls (nếu có)
        if (restaurant.getImageUrls() != null && !restaurant.getImageUrls().isEmpty()) {
            String imageUrl = restaurant.getImageUrls().get(0);  // Lấy URL ảnh đầu tiên
            Log.d("GlideDebug", "Loading image URL: " + imageUrl);
            Glide.with(holder.imageRestaurant.getContext())
                    .load(imageUrl.replace("http://", "https://"))
                    .placeholder(R.drawable.img_placeholder)  // Ảnh chờ load
                    .error(R.drawable.img_error)  // Ảnh nếu lỗi tải
                    .into(holder.imageRestaurant);
        }

        holder.tvRestaurantName_res_cart.setText(restaurant.getName());
        holder.tvRestaurantAddress_res_cart.setText(restaurant.getAddress());

        holder.btnSavedShop.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Context context = holder.itemView.getContext();
                SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                String userIdStr = sharedPreferences.getString("userId", "");
                Long userId = Long.parseLong(userIdStr);
                Long restaurantId = restaurant.getId();

                ApiService apiService = ApiClient.getClient().create(ApiService.class);
                Call<Void> call = apiService.saveRestaurant(userId, restaurantId);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(holder.itemView.getContext(), "Đã lưu nhà hàng", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(holder.itemView.getContext(), "Lỗi khi lưu", Toast.LENGTH_SHORT).show();
                            holder.btnSavedShop.setChecked(false); // rollback nếu thất bại
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(holder.itemView.getContext(), "Kết nối thất bại", Toast.LENGTH_SHORT).show();
                        holder.btnSavedShop.setChecked(false);
                    }
                });
            } else {
                // Có thể xử lý "bỏ lưu" nếu có API
            }
        });


        // 👉 Thêm sự kiện click item
        holder.itemView.setOnClickListener(view -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("request", "restaurant_detail");
            intent.putExtra("restaurantId", restaurant.getId());  // Truyền ID nhà hàng
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }
}
