package com.example.orderfoodonline.model;

import java.util.List;

public class CartItem {
    private Long foodId;
    private String foodName;
    private String size;
    private int quantity;
    private double foodPrice;
    private String imageUrl;

    public CartItem(Long foodId, String foodName, String imageUrl, String size,int quantity ,double foodPrice) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.size = size;
        this.quantity = quantity;
        this.foodPrice = foodPrice;
        this.imageUrl = imageUrl;
    }

    public CartItem(Long foodId, String size, int quantity) {
        this.foodId = foodId;
        this.size = size;
        this.quantity = quantity;
    }

    // Getter và Setter cho các trường trên
    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String foodSize) {
        this.size = foodSize;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrls(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
