package com.brandshaastra.ui.model;

public class ServiceModel {

    String service_img = "";
    String service_des = "";
    String service_price = "";
    String service_strike = "";

    public ServiceModel(String service_img, String service_des) {
        this.service_img = service_img;
        this.service_des = service_des;
    }

    public String getService_img() {
        return service_img;
    }

    public void setService_img(String service_img) {
        this.service_img = service_img;
    }

    public String getService_des() {
        return service_des;
    }

    public void setService_des(String service_des) {
        this.service_des = service_des;
    }

    public String getService_price() {
        return service_price;
    }

    public void setService_price(String service_price) {
        this.service_price = service_price;
    }

    public String getService_strike() {
        return service_strike;
    }

    public void setService_strike(String service_strike) {
        this.service_strike = service_strike;
    }
}
