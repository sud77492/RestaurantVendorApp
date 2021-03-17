package com.nhsurveys.restaurantvendorapp.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.nhsurveys.restaurantvendorapp.R;
import com.nhsurveys.restaurantvendorapp.model.QuestionResponse;
import com.nhsurveys.restaurantvendorapp.utils.SetTypeFace;


/**
 * Created by Rahul jain l on 27/04/2017.
 */


public class QuestionResponseAdapter extends RecyclerView.Adapter<QuestionResponseAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;
    private Activity activity;
    ProgressBar progressBar;

    private ArrayList<QuestionResponse> quesResponseList = new ArrayList<>();

    public QuestionResponseAdapter(Activity activity, ArrayList<QuestionResponse> quesResponseList) {
        this.activity = activity;
        this.quesResponseList = quesResponseList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.list_item_report_detail, parent, false);
        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {//        runEnterAnimation (holder.itemView);
        final QuestionResponse questionResponse = quesResponseList.get (position);
        progressBar = new ProgressBar(activity);
        holder.tvQuestion.setTypeface (SetTypeFace.getTypeface (activity));

        holder.tvQuestion.setText (questionResponse.getQues_english());
        holder.tvStatus.setText (questionResponse.getStatus());


    }

    @Override
    public int getItemCount() {
        return quesResponseList.size ();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvQuestion;
        TextView tvStatus;

        public ViewHolder(View view) {
            super(view);
            tvQuestion = (TextView) view.findViewById(R.id.tvQuestion);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.e("POSITON", ""+getLayoutPosition());

        }
    }
}
