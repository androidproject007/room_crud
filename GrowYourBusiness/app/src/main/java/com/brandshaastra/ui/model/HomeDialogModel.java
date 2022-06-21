package com.brandshaastra.ui.model;

public class HomeDialogModel {

    String business_name = "";
    String business_image = "";

    public HomeDialogModel(String business_name, String business_image) {
        this.business_name = business_name;
        this.business_image = business_image;
    }

    public String getBusiness_name() {
        return business_name;
    }

    public void setBusiness_name(String business_name) {
        this.business_name = business_name;
    }

    public String getBusiness_image() {
        return business_image;
    }

    public void setBusiness_image(String business_image) {
        this.business_image = business_image;
    }
}
