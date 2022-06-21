package com.brandshaastra.DTO;

import java.io.Serializable;
import java.util.List;

public class CategoryDTO implements Serializable {

    String category_id;
    String category_name = "";
    String tracker = "";
    String status = "";
    List<SubCategoryDTO> allsub;

    public String getTracker() {
        return tracker;
    }

    public void setTracker(String tracker) {
        this.tracker = tracker;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SubCategoryDTO> getAllsub() {
        return allsub;
    }

    public void setAllsub(List<SubCategoryDTO> allsub) {
        this.allsub = allsub;
    }
}
