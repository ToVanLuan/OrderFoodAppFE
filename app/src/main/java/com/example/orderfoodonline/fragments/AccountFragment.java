package com.example.orderfoodonline.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.orderfoodonline.R;
import com.example.orderfoodonline.activity.ActivityImpl.ChangePasswordActivity;
import com.example.orderfoodonline.activity.ActivityImpl.EditProfileActivity;
import com.example.orderfoodonline.activity.ActivityImpl.LoginActivity;
import com.example.orderfoodonline.model.User;
import com.example.orderfoodonline.network.ApiClient;
import com.example.orderfoodonline.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {
    private TextView tvUsername, tvEmail, tvEditProfile, tvChangePassword;
    private Button btnLogout;
    private SharedPreferences sharedPreferences;
    String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Ánh xạ view
        tvUsername = view.findViewById(R.id.tv_username);
        tvEmail = view.findViewById(R.id.tv_email);
        btnLogout = view.findViewById(R.id.btn_logout);
        tvEditProfile = view.findViewById(R.id.tv_edit_profile);
        tvChangePassword=view.findViewById(R.id.tv_change_password);

        // Lấy dữ liệu user từ SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        String username = sharedPreferences.getString("username", "Người dùng");
        String email = sharedPreferences.getString("email", "email@example.com");
        String phone = sharedPreferences.getString("phone", "");
        String address = sharedPreferences.getString("address", "");

        // Hiển thị dữ liệu
        tvUsername.setText(username);
        tvEmail.setText(email);

        // Xử lý nút đăng xuất
        btnLogout.setOnClickListener(v -> logoutUser());

        tvChangePassword.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
            startActivity(intent);
        });


        // Chuyển sang màn hình chỉnh sửa thông tin cá nhân
        tvEditProfile.setOnClickListener(v -> {

            User currentUser = new User(Long.parseLong(userId), username, email, phone, address);
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("USER_DATA", currentUser);
            startActivity(intent);
        });
        return view;
    }


    private void logoutUser() {
        // Xóa dữ liệu user
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData(); // Gọi API lấy lại dữ liệu người dùng mới
    }

    private void loadUserData() {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(getActivity(), "Không tìm thấy ID người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getUserProfile(userId); // Gọi API lấy thông tin user mới

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    tvUsername.setText(user.getUsername()); // Cập nhật giao diện
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(getActivity(), "Lỗi tải dữ liệu: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
