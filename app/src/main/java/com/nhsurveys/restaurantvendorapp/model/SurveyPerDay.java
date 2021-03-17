package com.nhsurveys.restaurantvendorapp.model;

public class SurveyPerDay {
    int survey_per_day;
    String survey_date;

    public SurveyPerDay(int survey_per_day, String survey_date) {
        this.survey_per_day = survey_per_day;
        this.survey_date = survey_date;
    }

    public int getSurvey_per_day() {
        return survey_per_day;
    }

    public void setSurvey_per_day(int survey_per_day) {
        this.survey_per_day = survey_per_day;
    }

    public String getSurvey_date() {
        return survey_date;
    }

    public void setSurvey_date(String survey_date) {
        this.survey_date = survey_date;
    }
}
