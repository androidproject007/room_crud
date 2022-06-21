package com.brandshaastra.ui.model;

public class BusinessModel {

    String b_name="";
    String b_phone="";
    String b_des="";
    String b_image="";

    public BusinessModel(String b_name, String b_phone, String b_des, String b_image) {
        this.b_name = b_name;
        this.b_phone = b_phone;
        this.b_des = b_des;
        this.b_image = b_image;
    }

    public String getB_name() {
        return b_name;
    }

    public void setB_name(String b_name) {
        this.b_name = b_name;
    }

    public String getB_phone() {
        return b_phone;
    }

    public void setB_phone(String b_phone) {
        this.b_phone = b_phone;
    }

    public String getB_des() {
        return b_des;
    }

    public void setB_des(String b_des) {
        this.b_des = b_des;
    }

    public String getB_image() {
        return b_image;
    }

    public void setB_image(String b_image) {
        this.b_image = b_image;
    }
}
