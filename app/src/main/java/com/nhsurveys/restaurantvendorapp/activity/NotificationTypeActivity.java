package com.nhsurveys.restaurantvendorapp.activity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nhsurveys.restaurantvendorapp.R;
import com.nhsurveys.restaurantvendorapp.dialogFragment.AddNotificationFragment;
import com.nhsurveys.restaurantvendorapp.utils.AppConfigTags;

public class NotificationTypeActivity extends AppCompatActivity {

    TextView tvIndividual;
    TextView tvSpecific;
    TextView tvOverall;
    ImageView ivBack;
    String notificationOptionArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_type);
        initView();
        initData();
        initBundle();
        initListener();
    }

    private void initData() {
        GradientDrawable gd = new GradientDrawable();
        //gd.setColor(0xFF00FF00); // Changes this drawbale to use a single color instead of a gradient
        gd.setCornerRadius(5);
        gd.setStroke(1, 0xFF000000);
        tvIndividual.setBackground(gd);
        tvOverall.setBackground(gd);
        tvSpecific.setBackground(gd);

    }

    private void initBundle() {
        Intent intent = getIntent();
        notificationOptionArray = intent.getStringExtra(AppConfigTags.NOTIFICATION_OPTIONS);
    }

    private void initListener() {
        tvOverall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToNotificationFragment("OVERALL");
            }
        });

        tvSpecific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToNotificationFragment("SPECIFIC");
            }
        });

        tvIndividual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToNotificationFragment("INDIVIDUAL");
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotificationTypeActivity.this, NotificationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendToNotificationFragment(String notificationType) {
        android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
        AddNotificationFragment dialog = new AddNotificationFragment().newInstance(notificationOptionArray, notificationType);
        dialog.setOnDialogResultListener(new AddNotificationFragment.OnDialogResultListener() {
            @Override
            public void onDismiss() {
                //getNotificationList("");
            }
        });
        dialog.show(ft, "test");
    }

    private void initView() {
        tvIndividual = findViewById(R.id.tvIndividual);
        tvSpecific = findViewById(R.id.tvSpecific);
        tvOverall = findViewById(R.id.tvOverall);
        ivBack = findViewById(R.id.ivBack);
    }
}
