package com.example.order_food_app.network;


import com.example.order_food_app.model.LoginRequest;
import com.example.order_food_app.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Map;

public interface ApiService {

    @POST("api/auth/register")
    Call<User> registerUser(@Body User user);

    @POST("api/auth/login")
    Call<Map<String, String>> loginUser(@Body LoginRequest request);
}
