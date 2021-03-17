package com.nhsurveys.restaurantvendorapp.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.nhsurveys.restaurantvendorapp.R;
import com.nhsurveys.restaurantvendorapp.model.NotificationOption;
import com.nhsurveys.restaurantvendorapp.utils.SetTypeFace;

/**
 * Created by Sudhanshu sharma l on 27/04/2017.
 */


public class NotificationOptionAdapter extends RecyclerView.Adapter<NotificationOptionAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;
    private Activity activity;
    ProgressBar progressBar;

    private ArrayList<NotificationOption> notificationOptionList;

    public NotificationOptionAdapter(Activity activity, ArrayList<NotificationOption> notificationOptionList) {
        this.activity = activity;
        this.notificationOptionList = notificationOptionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.list_item_notification_option, parent, false);
        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {//        runEnterAnimation (holder.itemView);
        final NotificationOption notificationOption = notificationOptionList.get(position);
        progressBar = new ProgressBar(activity);
        holder.tvNotificationText.setTypeface (SetTypeFace.getTypeface (activity),Typeface.BOLD);
        holder.tvNotificationText.setText(notificationOption.getNotification_option_name());
    }

    @Override
    public int getItemCount() {
        return notificationOptionList.size ();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        EditText etTotalScore;
        TextView tvNotificationText;

        public ViewHolder(View view) {
            super(view);
            etTotalScore = view.findViewById(R.id.etTotalScore);
            tvNotificationText = view.findViewById(R.id.tvNotificationText);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.e("POSITON", ""+getLayoutPosition());
            //Report report = reportList.get (getLayoutPosition());
            /*android.app.FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            AddNotificationFragment dialog = new AddNotificationFragment().newInstance(notificationOptionArray);
            dialog.setOnDialogResultListener(new AddNotificationFragment.OnDialogResultListener() {
                @Override
                public void onDismiss() {

                }
            });
            dialog.show(ft, "test");*/
        }
    }
}

