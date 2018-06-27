package com.example.crowderia.eatit.Model;

import java.util.List;

/**
 * Created by crowderia on 12/21/2017.
 */

public class Request {

    private String name, phone, address, total, status, comment;
    private List<Order> foods; //list of food order

    public Request() {
    }

    public Request(String name, String phone, String address, String total, List<Order> foods, String status, String comment) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.total = total;
        this.foods = foods;
        this.comment = comment;
        this.status = "0"; //0: placed, 1: shipping, 2:shipped
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
