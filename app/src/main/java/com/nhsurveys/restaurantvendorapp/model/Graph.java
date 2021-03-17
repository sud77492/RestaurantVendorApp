package com.nhsurveys.restaurantvendorapp.model;

public class Graph {
    int question_id;
    String question_english, option_check, rating, rating_type;

    public Graph(int question_id, String question_english, String option_check, String rating, String rating_type) {
        this.question_id = question_id;
        this.question_english = question_english;
        this.option_check = option_check;
        this.rating = rating;
        this.rating_type = rating_type;
    }

    public int getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(int question_id) {
        this.question_id = question_id;
    }

    public String getQuestion_english() {
        return question_english;
    }

    public void setQuestion_english(String question_english) {
        this.question_english = question_english;
    }

    public String getOption_check() {
        return option_check;
    }

    public void setOption_check(String option_check) {
        this.option_check = option_check;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating_type() {
        return rating_type;
    }

    public void setRating_type(String rating_type) {
        this.rating_type = rating_type;
    }
}
