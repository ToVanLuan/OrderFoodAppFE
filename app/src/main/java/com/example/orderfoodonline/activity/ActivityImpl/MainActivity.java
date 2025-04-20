package com.example.orderfoodonline.activity.ActivityImpl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.orderfoodonline.R;
import com.example.orderfoodonline.fragments.AccountFragment;
import com.example.orderfoodonline.fragments.FavoriteFragment;
import com.example.orderfoodonline.fragments.HomeFragment;
import com.example.orderfoodonline.fragments.NotificationsFragment;
import com.example.orderfoodonline.fragments.OrderFragment;
import com.example.orderfoodonline.network.ApiClient;
import com.example.orderfoodonline.network.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo Toolbar
        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        // Load fragment mặc định
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, new HomeFragment())
                    .commit();
        }

        // Xử lý Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_orders) {
                selectedFragment = new OrderFragment();
            } else if (itemId == R.id.nav_favorite) {
                selectedFragment = new FavoriteFragment();
            } else if (itemId == R.id.nav_notifications) {
                selectedFragment = new NotificationsFragment();
            } else if (itemId == R.id.nav_account) {
                selectedFragment = new AccountFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        handleIntentFromOtherActivity();
    }

    private void handleIntentFromOtherActivity() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("request")) {
            String request = intent.getStringExtra("request");
            if ("restaurant_detail".equals(request)) {
                long restaurantId = intent.getLongExtra("restaurantId", -1);
                if (restaurantId != -1) {
                    Intent restaurantIntent = new Intent(this, RestaurantActivity.class);
                    restaurantIntent.putExtra("restaurantId", restaurantId);
                    startActivity(restaurantIntent);
                }
            }
        }
    }

}
