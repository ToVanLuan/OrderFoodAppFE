package com.example.orderfoodonline.model;

import java.io.Serializable;
import java.util.Set;

public class User implements Serializable {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;

    public User(){}
    public User(Long id,String username, String email, String phoneNumber, String address, String password) {
       this.id=id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.password = password;
    }
    public User(Long id,String username, String email, String phoneNumber, String address) {
        this.id=id;
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
    public User(String email, String username){
        this.email=email;
        this.username=username;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getAddress() {
        return address;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setAddress(String address) {
        this.address = address;
    }

}

