package com.nhsurveys.restaurantvendorapp.model;

public class Notification {
    int id;
    String notification_name, notification_rating;

    public Notification(int id, String notification_name, String notification_rating) {
        this.id = id;
        this.notification_name = notification_name;
        this.notification_rating = notification_rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotification_name() {
        return notification_name;
    }

    public void setNotification_name(String notification_name) {
        this.notification_name = notification_name;
    }

    public String getNotification_rating() {
        return notification_rating;
    }

    public void setNotification_rating(String notification_rating) {
        this.notification_rating = notification_rating;
    }
}
