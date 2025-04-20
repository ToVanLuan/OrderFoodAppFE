package com.example.orderfoodonline.activity.ActivityImpl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderfoodonline.R;
import com.example.orderfoodonline.model.User;
import com.example.orderfoodonline.network.ApiClient;
import com.example.orderfoodonline.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {
    private EditText edtUsername, edtEmail, edtPhone, edtAddress;
    private Button btnSave;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile); // Sử dụng layout đúng của EditProfileActivity

        // Ánh xạ view
        edtUsername = findViewById(R.id.edt_username);
        edtEmail = findViewById(R.id.edt_email);
        edtPhone = findViewById(R.id.edt_phone);
        edtAddress = findViewById(R.id.edt_address);
        btnSave = findViewById(R.id.btn_save);

        // Nhận dữ liệu từ Intent
        user = (User) getIntent().getSerializableExtra("USER_DATA");
        if (user != null) {
            edtUsername.setText(user.getUsername());
            edtEmail.setText(user.getEmail());
            edtPhone.setText(user.getPhoneNumber());
            edtAddress.setText(user.getAddress());
        }

        btnSave.setOnClickListener(view -> updateUser());
    }

    private void updateUser() {
        Long userId = user.getId();
        if (user == null || user.getId() == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật thông tin user từ EditText
        user.setUsername(edtUsername.getText().toString().trim());
        user.setPhoneNumber(edtPhone.getText().toString().trim());
        user.setAddress(edtAddress.getText().toString().trim());

        // Gọi API cập nhật user
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.updateUser(userId, user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();

                    // Gửi kết quả thành công về AccountFragment
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);

                    finish(); // Đóng EditProfileActivity để quay về AccountFragment
                } else {
                    Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại! Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}