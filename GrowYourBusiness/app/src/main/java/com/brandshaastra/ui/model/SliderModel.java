package com.brandshaastra.ui.model;

public class SliderModel {


    String plan_id;
    String plan_days;
    String plan_price;
    String plan_image;
    String plan_name;
    String status;
    String activate;


    public String getPlan_name() {
        return plan_name;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActivate() {
        return activate;
    }

    public void setActivate(String activate) {
        this.activate = activate;
    }

    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getPlan_days() {
        return plan_days;
    }

    public void setPlan_days(String plan_days) {
        this.plan_days = plan_days;
    }

    public String getPlan_price() {
        return plan_price;
    }

    public void setPlan_price(String plan_price) {
        this.plan_price = plan_price;
    }

    public String getPlan_image() {
        return plan_image;
    }

    public void setPlan_image(String plan_image) {
        this.plan_image = plan_image;
    }
}
