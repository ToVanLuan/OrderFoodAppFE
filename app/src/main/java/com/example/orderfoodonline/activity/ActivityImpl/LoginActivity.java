package com.example.orderfoodonline.activity.ActivityImpl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.orderfoodonline.R;
import com.example.orderfoodonline.model.User;
import com.example.orderfoodonline.network.ApiClient;
import com.example.orderfoodonline.network.ApiService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.facebook.CallbackManager;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private ImageButton btnGoogleSignIn, btnFaceBook;
    private TextView createAccount,fogotPassword;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private static final int RC_SIGN_IN = 100;
    private CallbackManager callbackManager;
    private FacebookLoginActivity faceBookLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_login);

        // Ánh xạ view
        edtEmail = findViewById(R.id.login_emailid);
        edtPassword = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoogleSignIn = findViewById(R.id.btn_gg);
        createAccount = findViewById(R.id.createAccount);
        btnFaceBook = findViewById(R.id.btn_fb);
        fogotPassword=findViewById(R.id.forgot_password);

        faceBookLogin = new FacebookLoginActivity(this);
        callbackManager = CallbackManager.Factory.create();


        // Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Lấy từ Firebase Console
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        firebaseAuth = FirebaseAuth.getInstance();

        // Xử lý sự kiện Google Sign-In
        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());

        fogotPassword.setOnClickListener(v -> {
            // Tạo Intent để chuyển đến màn hình ForgotPasswordActivity
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);

            // Bắt đầu activity mới
            startActivity(intent);
        });

        // Xử lí nút Fb
        btnFaceBook.setOnClickListener(v -> faceBookLogin.loginFaceBook(callbackManager));


        // Xử lý sự kiện nút Đăng ký
        createAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignnupActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện nút Đăng nhập
        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Đăng nhập Google thất bại!", Toast.LENGTH_SHORT).show();
                Log.e("Google Sign-In", "Lỗi: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        // Lấy email và username từ FirebaseUser (Google Sign-In)
                        String email = user.getEmail();
                        String username = user.getDisplayName();

                        // Gửi dữ liệu lên server để tạo tài khoản hoặc đăng nhập
                        createUserOnServer(email, username);

                        // Chuyển đến màn hình chính
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "Lỗi đăng nhập Firebase!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void createUserOnServer(String email, String username) {
        // Gửi thông tin đến API server
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        User user = new User(email, username);

        apiService.loginWithGoogle(user) // Gọi phương thức API với UserDTO
                .enqueue(new Callback<Map<String, String>>() { // Callback trả về Map<String, String>
                    @Override
                    public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                        if (response.isSuccessful()) {
                            // Xử lý khi nhận được phản hồi thành công
                            Map<String, String> responseData = response.body();
                            if (responseData != null && responseData.containsKey("userId")) {
                                String userId = responseData.get("userId");

                                // Lưu userId vào SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userId", userId);
                                editor.apply();

                                // Gọi API lấy thông tin người dùng
                                getUserProfile(userId);
                            } else {
                                Log.e("User", "Không nhận được userId từ server!");
                            }
                        } else {
                            Log.e("User", "Đăng nhập thất bại!");
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        Log.e("User", "Lỗi kết nối server: " + t.getMessage());
                    }
                });
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<Map<String, Object>> call = apiService.loginUser(email, password);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> responseData = response.body();

                    // Lấy token từ phản hồi
                    String userId = (String) responseData.get("userId");
                    if (userId == null) {
                        Toast.makeText(LoginActivity.this, "Không lấy được token!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Lưu token vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", userId);
                    editor.apply();

                    // Gọi API lấy thông tin user
                    getUserProfile(userId);
                } else {
                    Toast.makeText(LoginActivity.this, "Sai thông tin đăng nhập!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserProfile(String userId) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getUserProfile(userId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    // Lưu vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("userId", String.valueOf(user.getId()));
                    editor.putString("username", user.getUsername());
                    editor.putString("email", user.getEmail());
                    editor.putString("phone", user.getPhoneNumber());
                    editor.putString("address", user.getAddress());

                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển sang màn hình chính
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Lỗi khi lấy thông tin user!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
