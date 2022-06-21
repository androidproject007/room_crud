package com.brandshaastra.ui.model;

public class ImageModel {

    String id;
    String cat_id;
    String subcat_id;
    String image1;
    String image;
    String video_url2 = "";

    String video_url = "";
    String thumbnail = "";
    String start_date = "";
    String end_date = "";
    String status = "";
    String coming_soon = "";
    String coming_soon_text = "";

    public String getComing_soon_text() {
        return coming_soon_text;
    }

    public void setComing_soon_text(String coming_soon_text) {
        this.coming_soon_text = coming_soon_text;
    }

    public String getComing_soon() {
        return coming_soon;
    }

    public void setComing_soon(String coming_soon) {
        this.coming_soon = coming_soon;
    }

    public String getVideo_url2() {
        return video_url2;
    }

    public void setVideo_url2(String video_url2) {
        this.video_url2 = video_url2;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getSubcat_id() {
        return subcat_id;
    }

    public void setSubcat_id(String subcat_id) {
        this.subcat_id = subcat_id;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
