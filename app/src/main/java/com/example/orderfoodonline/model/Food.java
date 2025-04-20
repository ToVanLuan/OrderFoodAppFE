package com.example.orderfoodonline.model;

import java.util.List;

public class Food {
    private Long id; // ID của món ăn
    private String name; // Tên món ăn
    private String type; // Loại món ăn (ví dụ: món chính, món tráng miệng, v.v.)
    private List<String> imageUrls; // Danh sách URL của các hình ảnh món ăn
    private String description; // Mô tả món ăn

    // Constructor
    public Food(Long id, String name, String type, List<String> imageUrls, String description) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.imageUrls = imageUrls;
        this.description = description;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

