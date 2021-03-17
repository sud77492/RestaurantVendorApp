package com.nhsurveys.restaurantvendorapp.model;

public class NotificationOption {
    int notification_option_id;
    int notification_option_ques_id;
    String notification_option_name;

    public NotificationOption(int notification_option_id, int notification_option_ques_id, String notification_option_name) {
        this.notification_option_id = notification_option_id;
        this.notification_option_ques_id = notification_option_ques_id;
        this.notification_option_name = notification_option_name;
    }

    public int getNotification_option_ques_id() {
        return notification_option_ques_id;
    }

    public void setNotification_option_ques_id(int notification_option_ques_id) {
        this.notification_option_ques_id = notification_option_ques_id;
    }

    public int getNotification_option_id() {
        return notification_option_id;
    }

    public void setNotification_option_id(int notification_option_id) {
        this.notification_option_id = notification_option_id;
    }

    public String getNotification_option_name() {
        return notification_option_name;
    }

    public void setNotification_option_name(String notification_option_name) {
        this.notification_option_name = notification_option_name;
    }
}
