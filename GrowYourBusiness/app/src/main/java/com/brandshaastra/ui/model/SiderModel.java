package com.brandshaastra.ui.model;

public class SiderModel {

    private String slider_image, slider_id, link, category_id,category_name;

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getId() {
        return slider_id;
    }

    public String getSlider_image() {
        return slider_image;
    }

    public void setId(String id) {
        this.slider_id = id;
    }

    public void setSlider_image(String slider_image) {
        this.slider_image = slider_image;
    }
}
