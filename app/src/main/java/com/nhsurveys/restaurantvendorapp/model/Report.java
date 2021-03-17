package com.nhsurveys.restaurantvendorapp.model;

import java.util.Comparator;

public class Report {
    int survey_id;
    Double star_rating, avg_rating;
    String name, mobile, comment, status;

    public Report(int survey_id, Double star_rating, Double avg_rating, String name, String mobile, String comment, String status) {
        this.survey_id = survey_id;
        this.star_rating = star_rating;
        this.avg_rating = avg_rating;
        this.name = name;
        this.mobile = mobile;
        this.comment = comment;
        this.status = status;
    }

    public int getSurvey_id() {
        return survey_id;
    }

    public void setSurvey_id(int survey_id) {
        this.survey_id = survey_id;
    }

    public Double getStar_rating() {
        return star_rating;
    }

    public void setStar_rating(Double star_rating) {
        this.star_rating = star_rating;
    }

    public Double getAvg_rating() {
        return avg_rating;
    }

    public void setAvg_rating(Double avg_rating) {
        this.avg_rating = avg_rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Comparator<Report> idComparator = new Comparator<Report>() {
        @Override
        public int compare(Report jc1, Report jc2) {
            return (jc2.getSurvey_id() < jc1.getSurvey_id() ? -1 :
                    (jc2.getSurvey_id() == jc1.getSurvey_id() ? 0 : 1));
        }
    };

    public static Comparator<Report> ratingDescComparator = new Comparator<Report>() {
        @Override
        public int compare(Report jc1, Report jc2) {
            return (jc2.getAvg_rating() < jc1.getAvg_rating() ? -1 :
                    (jc2.getAvg_rating() == jc1.getAvg_rating() ? 0 : 1));
        }
    };

    public static Comparator<Report> ratingAscComparator = new Comparator<Report>() {
        @Override
        public int compare(Report jc1, Report jc2) {
            return (jc2.getAvg_rating() > jc1.getAvg_rating() ? -1 :
                    (jc2.getAvg_rating() == jc1.getAvg_rating() ? 0 : 1));
        }
    };

    public static Comparator<Report> nameComparator = new Comparator<Report>() {
        @Override
        public int compare(Report jc1, Report jc2) {
            return (int) (jc1.getName().compareTo(jc2.getName()));
        }
    };
}