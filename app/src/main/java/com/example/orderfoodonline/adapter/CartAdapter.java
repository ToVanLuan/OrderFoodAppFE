package com.example.orderfoodonline.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderfoodonline.R;
import com.example.orderfoodonline.model.CartItem;
import com.example.orderfoodonline.network.ApiClient;
import com.example.orderfoodonline.network.ApiService;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_cart_card, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.tvFoodName.setText(item.getFoodName());
        holder.tvFoodSize.setText(item.getSize());
        holder.tvFoodQuantity.setText(String.valueOf(item.getQuantity()));
        double totalPrice = item.getFoodPrice() * item.getQuantity();
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvFoodPrice.setText(formatter.format(totalPrice));

        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl().replace("http://", "https://"))
                    .placeholder(R.drawable.img_placeholder)
                    .error(R.drawable.img_error)
                    .into(holder.ivFoodImage);
        } else {
            holder.ivFoodImage.setImageResource(R.drawable.img_placeholder);
        }

        holder.btnAddQuantity.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            item.setQuantity(newQuantity);
            notifyItemChanged(position);
        });

        holder.btnSubQuantity.setOnClickListener(v -> {
            int currentQuantity = item.getQuantity();
            if (currentQuantity > 1) {
                item.setQuantity(currentQuantity - 1);
                notifyItemChanged(position);
            }
        });

        holder.btnDeleteItem.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String userIdStr = sharedPreferences.getString("userId", null);

            if (userIdStr != null) {
                Long userId = Long.parseLong(userIdStr);
                Long foodId = item.getFoodId();
                String size = item.getSize();
                removeCartItem(context, userId, foodId, size, position);
            }
        });
    }

    private void removeCartItem(Context context, Long userId, Long foodId, String size, int position) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.deleteCartItem(userId, foodId, size);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    cartItems.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Đã xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Xóa thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView ivFoodImage;
        TextView tvFoodName, tvFoodSize, tvFoodPrice, tvFoodQuantity;
        ImageView btnSubQuantity, btnAddQuantity, btnDeleteItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodImage = itemView.findViewById(R.id.imageCartFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodNameCart);
            tvFoodSize = itemView.findViewById(R.id.tvFoodSizeCart);
            tvFoodPrice = itemView.findViewById(R.id.tvFoodPriceCart);
            tvFoodQuantity = itemView.findViewById(R.id.tvFoodQuantity_Cart);
            btnSubQuantity = itemView.findViewById(R.id.btnSubQuantity_Cart);
            btnAddQuantity = itemView.findViewById(R.id.btnAddQuantity_Cart);
            btnDeleteItem = itemView.findViewById(R.id.btnDeleteCartItem);
        }
    }
}
