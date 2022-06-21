package com.brandshaastra.DTO;

public class SubCategoryDTO {

    String subcatgory_id;
    String cat_name;
    String catid;
    String subcatgory_name = "";
    String image = "";
    String status = "";

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getSubcatgory_id() {
        return subcatgory_id;
    }

    public void setSubcatgory_id(String subcatgory_id) {
        this.subcatgory_id = subcatgory_id;
    }

    public String getCatid() {
        return catid;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public String getSubcatgory_name() {
        return subcatgory_name;
    }

    public void setSubcatgory_name(String subcatgory_name) {
        this.subcatgory_name = subcatgory_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
