package com.nhsurveys.restaurantvendorapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.nhsurveys.restaurantvendorapp.R;
import com.nhsurveys.restaurantvendorapp.adapter.QuestionResponseAdapter;
import com.nhsurveys.restaurantvendorapp.model.QuestionResponse;
import com.nhsurveys.restaurantvendorapp.utils.AppConfigTags;
import com.nhsurveys.restaurantvendorapp.utils.AppConfigURL;
import com.nhsurveys.restaurantvendorapp.utils.Constants;
import com.nhsurveys.restaurantvendorapp.utils.NetworkConnection;
import com.nhsurveys.restaurantvendorapp.utils.UserDetailsPref;
import com.nhsurveys.restaurantvendorapp.utils.Utils;

public class SurveyDetailActivity extends AppCompatActivity {

    CoordinatorLayout clMain;
    TextView tvOverallStatus;
    TextView tvName;
    TextView tvMobile;
    TextView tvStarRating;
    TextView tvComment;
    ImageView ivBack;

    RecyclerView rvReportDetailList;

    ProgressDialog progressDialog;

    QuestionResponseAdapter questionResponseAdapter;

    UserDetailsPref userDetailsPref;

    int survey_id = 0;

    ArrayList<QuestionResponse> quesResponseList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_details);
        initView();
        initBundle();
        initData();
        initListener();
        surveyReportDetails();
    }

    private void initBundle() {
        Bundle b = getIntent().getExtras();
        survey_id = b.getInt(AppConfigTags.REPORT_CUSTOMER_SURVEY_ID);
        tvName.setText(b.getString(AppConfigTags.REPORT_CUSTOMER_NAME));
        tvMobile.setText(b.getString(AppConfigTags.REPORT_CUSTOMER_MOBILE));
        tvOverallStatus.setText(String.valueOf(b.getDouble(AppConfigTags.REPORT_CUSTOMER_AVERAGE_RATING)) + "-" + b.getString(AppConfigTags.REPORT_CUSTOMER_STATUS));
        tvComment.setText(b.getString(AppConfigTags.REPORT_CUSTOMER_COMMENT));
        if(b.getDouble(AppConfigTags.REPORT_CUSTOMER_STAR) != 0) {
            tvStarRating.setText(String.valueOf(b.getDouble(AppConfigTags.REPORT_CUSTOMER_STAR)));
        }else{
            tvStarRating.setText("NA");
        }

    }

    private void initView() {
        clMain = (CoordinatorLayout)findViewById(R.id.clMain);
        tvOverallStatus = (TextView)findViewById(R.id.tvOverallStatus);
        tvName = (TextView)findViewById(R.id.tvName);
        tvMobile = (TextView)findViewById(R.id.tvMobile);
        tvStarRating = (TextView)findViewById(R.id.tvStarRating);
        tvComment = (TextView)findViewById(R.id.tvComment);
        ivBack = findViewById(R.id.ivBack);
        rvReportDetailList = (RecyclerView)findViewById(R.id.rvReportDetailList);
    }

    private void initData() {
        progressDialog = new ProgressDialog(this);
        userDetailsPref = UserDetailsPref.getInstance();
        questionResponseAdapter = new QuestionResponseAdapter(SurveyDetailActivity.this, quesResponseList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvReportDetailList.setLayoutManager(mLayoutManager);
        rvReportDetailList.setItemAnimator(new DefaultItemAnimator());
        rvReportDetailList.setAdapter(questionResponseAdapter);
    }

    private void initListener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void surveyReportDetails() {
        if (NetworkConnection.isNetworkAvailable(SurveyDetailActivity.this)) {
            Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_SURVEY_DETAILS, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_SURVEY_DETAILS,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);

                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean error = jsonObj.getBoolean(AppConfigTags.ERROR);
                                    String message = jsonObj.getString(AppConfigTags.MESSAGE);
                                    if (!error) {
                                        JSONArray jsonArray = jsonObj.getJSONArray(AppConfigTags.SURVEY_DETAILS);
                                        for(int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObjectQuesResponse = jsonArray.getJSONObject(i);
                                            quesResponseList.add(new QuestionResponse(jsonObjectQuesResponse.getInt(AppConfigTags.SURVEY_DETAIL_QUES_ID),
                                                    jsonObjectQuesResponse.getString(AppConfigTags.SURVEY_DETAIL_QUES_ENGLISH),
                                                    jsonObjectQuesResponse.getString(AppConfigTags.SURVEY_DETAIL_QUES_HINDI),
                                                    jsonObjectQuesResponse.getString(AppConfigTags.SURVEY_DETAIL_RATING_STATUS)
                                                    ));
                                        }

                                        questionResponseAdapter.notifyDataSetChanged();
                                    } else {
                                        Utils.showSnackBar(SurveyDetailActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar(SurveyDetailActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar(SurveyDetailActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss();


                            //swipeRefreshLayout.setRefreshing (false);
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // swipeRefreshLayout.setRefreshing (false);
                            progressDialog.dismiss();
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            Utils.showSnackBar(SurveyDetailActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                        }
                    }) {

                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.SURVEY_ID, String.valueOf(survey_id));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(SurveyDetailActivity.this, UserDetailsPref.USER_LOGIN_KEY));
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {
            Utils.showSnackBar(this, clMain, getResources().getString(R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_go_to_settings), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                }
            });
        }
    }



}
