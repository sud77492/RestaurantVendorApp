package com.nhsurveys.restaurantvendorapp.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import com.nhsurveys.restaurantvendorapp.R;
import com.nhsurveys.restaurantvendorapp.model.Notification;
import com.nhsurveys.restaurantvendorapp.utils.SetTypeFace;

/**
 * Created by Sudhanshu sharma l on 27/04/2017.
 */


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    OnItemClickListener mItemClickListener;
    private Activity activity;
    ProgressBar progressBar;

    private ArrayList<Notification> notificationList = new ArrayList<Notification>();

    public NotificationAdapter(Activity activity, ArrayList<Notification> notificationList) {
        this.activity = activity;
        this.notificationList = notificationList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        final View sView = mInflater.inflate(R.layout.list_item_notification, parent, false);
        return new ViewHolder(sView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {//        runEnterAnimation (holder.itemView);
        final Notification notification = notificationList.get(position);
        progressBar = new ProgressBar(activity);
        holder.tvNotificationName.setTypeface (SetTypeFace.getTypeface (activity),Typeface.BOLD);
        holder.tvNotificationName.setText(notification.getNotification_name());
        holder.tvNotificationRating.setText(notification.getNotification_rating());
    }

    @Override
    public int getItemCount() {
        return notificationList.size ();
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvNotificationName;
        TextView tvNotificationRating;

        public ViewHolder(View view) {
            super(view);
            tvNotificationName = (TextView) view.findViewById(R.id.tvNotificationName);
            tvNotificationRating = (TextView) view.findViewById(R.id.tvNotificationRating);
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

