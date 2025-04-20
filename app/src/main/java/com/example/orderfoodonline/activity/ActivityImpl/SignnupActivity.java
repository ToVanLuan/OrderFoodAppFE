package com.example.orderfoodonline.activity.ActivityImpl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderfoodonline.R;
import com.example.orderfoodonline.model.User;
import com.example.orderfoodonline.network.ApiClient;
import com.example.orderfoodonline.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignnupActivity extends AppCompatActivity {
    private EditText edtName, edtEmail, edtPassword, edtAddress, edtPhone;
    private Button btnRegister;
    private TextView loginAccount;
    private CheckBox chkTerms;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        chkTerms = findViewById(R.id.terms_conditions);

        loginAccount = findViewById(R.id.already_user);
        loginAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignnupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        edtName = findViewById(R.id.fullName);
        edtEmail = findViewById(R.id.userEmailId);
        edtPassword = findViewById(R.id.password);
        edtPhone = findViewById(R.id.mobileNumber);
        edtAddress = findViewById(R.id.address);
        btnRegister = findViewById(R.id.signUpBtn);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(SignnupActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!chkTerms.isChecked()) {
            Toast.makeText(SignnupActivity.this, "Bạn phải đồng ý với điều khoản trước khi đăng ký!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng User để gửi lên server
        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setPhoneNumber(phone);
        user.setAddress(address);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.registerUser(user, password);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SignnupActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignnupActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignnupActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("Register Error", t.getMessage());
                Toast.makeText(SignnupActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}