package com.example.orderfoodonline.network;

import com.example.orderfoodonline.model.CartItem;
import com.example.orderfoodonline.model.FoodSaved;
import com.example.orderfoodonline.model.FoodSize;
import com.example.orderfoodonline.model.ResaurantSaved;
import com.example.orderfoodonline.model.Restaurant;
import com.example.orderfoodonline.model.User;
import com.example.orderfoodonline.model.Food;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;
import java.util.Map;

public interface ApiService {

    @POST("api/auth/register")
    Call<User> registerUser(@Body User user, @Query("password") String password);

    @POST("/api/auth/login-gg")
    Call<Map<String, String>> loginWithGoogle(@Body User user);

    @FormUrlEncoded
    @POST("api/auth/login")
    Call<Map<String, Object>> loginUser(
            @Field("email") String email,
            @Field("password") String password
    );

    @PUT("api/auth/{userId}/update")
    Call<User> updateUser(@Path("userId") Long userId, @Body User user);

    @GET("api/auth/{userId}/profile")
    Call<User> getUserProfile(@Path("userId") String userId);

    @POST("api/auth/change-password")
    Call<Map<String, String>> changePassword(@Body Map<String, String> request);

    @POST("api/auth/forgot-password")
    Call<Map<String, String>> forgotPassword(@Query("email") String email);

    @GET("api/restaurants")
    Call<List<Restaurant>> getAllRestaurants();

    @GET("api/restaurants/{id}")
    Call<Restaurant> getRestaurantById(@Path("id") Long id);

    @GET("api/food-sizes/{foodId}/{size}")
    Call<FoodSize> getFoodSizeByFoodIdAndSize(
            @Path("foodId") Long foodId,
            @Path("size") String size

    );

    @GET("api/foods/{id}")
    Call<Food> getFoodById(@Path("id") Long id);

    @GET("api/food-sizes/{foodId}")
    Call<List<FoodSize>> getFoodSizesByFoodId(@Path("foodId") Long fodId);

    @POST("api/cart/{userId}/add")
    Call<ResponseBody> addToCart(@Path("userId") Long userId, @Body CartItem dto);

    @GET("api/cart/{userId}")
    Call<List<CartItem>> getCartItems(@Path("userId") Long userId);

    @DELETE("api/cart/{userId}/item")
    Call<ResponseBody> deleteCartItem(
            @Path("userId") Long userId,
            @Query("foodId") Long foodId,
            @Query("size") String size
    );

    @GET("api/cart/{userId}/count")
    Call<Integer> countCartItems(
            @Path("userId") Long userId
    );

    @POST("api/restaurant-saved/save")
    Call<Void> saveRestaurant(
            @Query("userId") Long userId,
            @Query("restaurantId") Long restaurantId
    );
    @GET("api/restaurant-saved/user/{userId}")
    Call<List<ResaurantSaved>> getSavedRestaurants(
            @Path("userId") Long userId
    );
    @POST("api/food-saved/save")
    Call<Void> saveFood(
            @Query("userId") Long userId,
            @Query("foodId") Long foodId,
            @Query("size") String size

    );
    @GET("api/food-saved/user/{userId}")
    Call<List<FoodSaved>> getSavedFoods(
            @Path("userId") Long userId
    );
}
