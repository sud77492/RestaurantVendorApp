package com.nhsurveys.restaurantvendorapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.nhsurveys.restaurantvendorapp.R;
import com.nhsurveys.restaurantvendorapp.activity.SurveyDetailActivity;
import com.nhsurveys.restaurantvendorapp.model.Report;
import com.nhsurveys.restaurantvendorapp.utils.AppConfigTags;
import com.nhsurveys.restaurantvendorapp.utils.SetTypeFace;


/**
 * Created by Rahul jain l on 27/04/2017.
 */


public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;
    private Activity activity;
    ProgressBar progressBar;

    private ArrayList<Report> reportList = new ArrayList<Report>();

    public ReportAdapter(Activity activity, ArrayList<Report> reportList) {
        this.activity = activity;
        this.reportList = reportList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.list_item_report, parent, false);
        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {//        runEnterAnimation (holder.itemView);
        final Report report = reportList.get (position);
        progressBar = new ProgressBar(activity);
        holder.tvName.setTypeface (SetTypeFace.getTypeface (activity),Typeface.BOLD);
        holder.tvName.setText (report.getName());
        holder.tvStatus.setText (String.valueOf(report.getAvg_rating()) + "-" + report.getStatus());
        if(report.getStar_rating() != 0) {
            holder.tvStarRating.setText(String.valueOf(report.getStar_rating()));
        }else{
            holder.tvStarRating.setText("NA");
        }
        holder.tvMobile.setText(report.getMobile());

    }

    @Override
    public int getItemCount() {
        return reportList.size ();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvName;
        TextView tvStarRating;
        TextView tvStatus;
        TextView tvMobile;

        public ViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvStarRating = (TextView) view.findViewById(R.id.tvStarRating);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            tvMobile = (TextView) view.findViewById(R.id.tvMobile);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.e("POSITON", ""+getLayoutPosition());
            Report report = reportList.get (getLayoutPosition());
            Intent intent = new Intent(activity, SurveyDetailActivity.class);
            Bundle b = new Bundle();
            b.putInt(AppConfigTags.REPORT_CUSTOMER_SURVEY_ID, report.getSurvey_id());
            b.putString(AppConfigTags.REPORT_CUSTOMER_NAME, report.getName());
            b.putString(AppConfigTags.REPORT_CUSTOMER_MOBILE, report.getMobile());
            b.putString(AppConfigTags.REPORT_CUSTOMER_COMMENT, report.getComment());
            b.putString(AppConfigTags.REPORT_CUSTOMER_STATUS, report.getStatus());
            b.putDouble(AppConfigTags.REPORT_CUSTOMER_AVERAGE_RATING, report.getAvg_rating());
            b.putDouble(AppConfigTags.REPORT_CUSTOMER_STAR, report.getStar_rating());
            intent.putExtras(b);
            activity.startActivity(intent);
        }
    }
}
