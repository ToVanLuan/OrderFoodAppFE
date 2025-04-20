package com.example.orderfoodonline.model;

import java.util.List;

public class FoodSaved {
    private Long id;
    private String foodName;
    private List<String> imageUrls;
    private String size;
    private double foodPrice;

    public FoodSaved(Long id, String foodName, List<String> imageUrls, String size, double foodPrice) {
        this.id = id;
        this.foodName = foodName;
        this.imageUrls = imageUrls;
        this.size = size;
        this.foodPrice = foodPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(double foodPrice) {
        this.foodPrice = foodPrice;
    }
}
