package com.nhsurveys.restaurantvendorapp.model;

import java.util.ArrayList;
import java.util.Collections;

public class ReportSorter {
    ArrayList<Report> reportList = new ArrayList<>();

    public ReportSorter(ArrayList<Report> reportList) {
        this.reportList = reportList;
    }

    public ArrayList<Report> getSortedReportById() {
        Collections.sort(reportList, Report.idComparator);
        return reportList;
    }


    public ArrayList<Report> getSortedReportByDescRating() {
        Collections.sort(reportList, Report.ratingDescComparator);
        return reportList;
    }

    public ArrayList<Report> getSortedReportByAscRating() {
        Collections.sort(reportList, Report.ratingAscComparator);
        return reportList;
    }

    public ArrayList<Report> getSortedReportByName() {
        Collections.sort(reportList, Report.nameComparator);
        return reportList;
    }
}
