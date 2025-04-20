package com.example.orderfoodonline.model;

import java.util.List;

public class Restaurant {
    private Long id;
    private String name;
    private String address;
    private String phone;
    private List<String> imageUrls; // Danh sách URL ảnh của nhà hàng
    private List<Food> foods; // Danh sách các món ăn của nhà hàng

    // Constructor
    public Restaurant(Long id, String name, String address, String phone, List<String> imageUrls, List<Food> foods) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.imageUrls = imageUrls;
        this.foods = foods;
    }

    // Getter và Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void setFoods(List<Food> foods) {
        this.foods = foods;
    }
}
