package com.example.orderfoodonline.activity.ActivityImpl;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.orderfoodonline.model.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FacebookAuthProvider;

import java.util.Arrays;

public class FacebookLoginActivity extends AppCompatActivity {
    private static final String TAG = FacebookLoginActivity.class.getSimpleName();
    private final Activity activity;
    private final FirebaseAuth mAuth;

    // Khởi tạo FirebaseAuth
    public FacebookLoginActivity(Activity activity) {
        this.activity = activity;
        this.mAuth = FirebaseAuth.getInstance();
    }

    // Phương thức đăng nhập Facebook
    public void loginFaceBook(CallbackManager callbackManager) {
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        // Use logInWithReadPermissions instead of logInWithPermissions
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(activity, "Login Cancel!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(activity, "Login Error!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, exception.toString());
                    }
                });
    }

    // Xử lý Access Token khi đăng nhập thành công
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Đăng nhập thành công
                            Long id = Long.parseLong(task.getResult().getUser().getUid());
                            String name = task.getResult().getUser().getDisplayName();
                            String email = task.getResult().getUser().getEmail();

                            User users = new User();
                            users.setId(id);
                            users.setUsername(name);
                            users.setEmail(email);

                            Toast.makeText(activity, "Login Successful!", Toast.LENGTH_SHORT).show();

                            // Có thể thêm logic chuyển màn hình sau khi đăng nhập thành công
                             Intent intent = new Intent(activity, MainActivity.class);
                             activity.startActivity(intent);
                        } else {
                            // Xử lý lỗi khi đăng nhập không thành công
                            Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
