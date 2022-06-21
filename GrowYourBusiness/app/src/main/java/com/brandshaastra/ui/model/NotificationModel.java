package com.brandshaastra.ui.model;

public class NotificationModel {

    String user_notification ="";
    String user_id ="";
    String title ="";
    String msg="";
    String created_at ="";

    public String getUser_notification() {
        return user_notification;
    }

    public void setUser_notification(String user_notification) {
        this.user_notification = user_notification;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
