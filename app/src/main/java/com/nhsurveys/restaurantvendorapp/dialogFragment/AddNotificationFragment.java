package com.nhsurveys.restaurantvendorapp.dialogFragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.nhsurveys.restaurantvendorapp.R;
import com.nhsurveys.restaurantvendorapp.activity.NotificationActivity;
import com.nhsurveys.restaurantvendorapp.adapter.NotificationOptionAdapter;
import com.nhsurveys.restaurantvendorapp.model.NotificationOption;
import com.nhsurveys.restaurantvendorapp.utils.AppConfigTags;
import com.nhsurveys.restaurantvendorapp.utils.AppConfigURL;
import com.nhsurveys.restaurantvendorapp.utils.Constants;
import com.nhsurveys.restaurantvendorapp.utils.NetworkConnection;
import com.nhsurveys.restaurantvendorapp.utils.UserDetailsPref;
import com.nhsurveys.restaurantvendorapp.utils.Utils;

public class AddNotificationFragment extends DialogFragment {
    RelativeLayout rlOverall;
    RelativeLayout rlIndividual;
    RelativeLayout rlSpecific;
    RecyclerView rvIndividual;
    EditText etSpecific;
    EditText etOverall;
    EditText etSpecificRating;
    EditText etOverallRating;
    Button btAddNotification;
    ProgressDialog progressDialog;
    UserDetailsPref userDetailsPref;
    OnDialogResultListener onDialogResultListener;
    ImageView ivCancel;
    String notificationOptionArray;
    String notificationType;
    ArrayList<NotificationOption> notificationOptionList = new ArrayList<>();
    ArrayList<String> notificationOptionListString = new ArrayList<>();
    NotificationOptionAdapter notificationOptionAdapter;
    String notificationId = "";
    String questionId = "";

    public AddNotificationFragment newInstance(String notificationOptionArray, String notificationType) {
        AddNotificationFragment addNotificationFragment = new AddNotificationFragment();
        Bundle args = new Bundle();
        args.putString(AppConfigTags.NOTIFICATION_OPTIONS, notificationOptionArray);
        args.putString(AppConfigTags.NOTIFICATION_TYPE, notificationType);
        addNotificationFragment.setArguments(args);
        return addNotificationFragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

        Window window = getDialog().getWindow();
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                    //This is the filter
                    if (event.getAction() != KeyEvent.ACTION_UP)
                        return true;
                    else {
                        getDialog().dismiss();
                        //Hide your keyboard here!!!!!!
                        return true; // pretend we've processed it
                    }
                } else
                    return false; // pass on to be processed as normal
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.dialog_fragment_add_notification, container, false);
        initView(root);
        initBundle();
        initData();
        initListener();
        return root;
    }

    private void initView(View root) {
        rlOverall = root.findViewById(R.id.rlOverall);
        rlSpecific = root.findViewById(R.id.rlSpecific);
        rlIndividual = root.findViewById(R.id.rlIndividual);
        rvIndividual = root.findViewById(R.id.rvIndividual);
        etSpecificRating = root.findViewById(R.id.etSpecificRating);
        etSpecific = root.findViewById(R.id.etSpecific);
        etOverall = root.findViewById(R.id.etOverall);
        etOverallRating = root.findViewById(R.id.etOverallRating);
        btAddNotification = (Button) root.findViewById(R.id.btAddNotification);
        ivCancel = root.findViewById(R.id.ivCancel);
    }

    private void initBundle(){
        notificationOptionArray = getArguments().getString(AppConfigTags.NOTIFICATION_OPTIONS);
        notificationType = getArguments().getString(AppConfigTags.NOTIFICATION_TYPE);
    }

    private void initData(){
        progressDialog = new ProgressDialog(getActivity());
        userDetailsPref = UserDetailsPref.getInstance();

        try {
            JSONArray jsonArray = new JSONArray(notificationOptionArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                notificationOptionList.add(new NotificationOption(jsonObject.getInt(AppConfigTags.NOTIFICATION_OPTION_ID),
                        jsonObject.getInt(AppConfigTags.NOTIFICATION_OPTION_QUES_ID),
                        jsonObject.getString(AppConfigTags.NOTIFICATION_OPTION_NAME)
                ));
                notificationOptionListString.add(jsonObject.getString(AppConfigTags.NOTIFICATION_OPTION_NAME));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (notificationType) {
            case "OVERALL":
                rlOverall.setVisibility(View.VISIBLE);
                rlIndividual.setVisibility(View.GONE);
                rlSpecific.setVisibility(View.GONE);
                break;

            case "INDIVIDUAL":
                rlOverall.setVisibility(View.GONE);
                rlIndividual.setVisibility(View.VISIBLE);
                rlSpecific.setVisibility(View.GONE);

                notificationOptionAdapter = new NotificationOptionAdapter(getActivity(), notificationOptionList);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                rvIndividual.setLayoutManager(mLayoutManager);
                rvIndividual.setItemAnimator(new DefaultItemAnimator());
                rvIndividual.setAdapter(notificationOptionAdapter);

                break;

            case "SPECIFIC":

                rlOverall.setVisibility(View.GONE);
                rlIndividual.setVisibility(View.GONE);
                rlSpecific.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initListener(){
        btAddNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (notificationType) {
                    case "OVERALL":
                        if (!etOverall.getText().toString().equalsIgnoreCase("") && !etOverall.getText().toString().equalsIgnoreCase("")) {
                            addNotification(etOverallRating.getText().toString().trim(), "OVERALL", "3", "0", "0");

                        } else {
                            Utils.showToast(getActivity(), "Please fill all the fields", true);
                        }
                        break;

                    case "INDIVIDUAL":
                        String value = "";
                        String id = "";
                        String ques_id = "";
                        for (int i = 0; i < notificationOptionList.size(); i++) {
                            View view = rvIndividual.getChildAt(i);
                            EditText etTotalScore = (EditText) view.findViewById(R.id.etTotalScore);
                            String name = etTotalScore.getText().toString();
                            Log.e("Value", name);
                            if (i == 0) {
                                value = name;
                                id = String.valueOf(notificationOptionList.get(i).getNotification_option_id());
                                ques_id = String.valueOf(notificationOptionList.get(i).getNotification_option_ques_id());

                            } else {
                                value = value + "," + name;
                                id = id + "," + String.valueOf(notificationOptionList.get(i).getNotification_option_id());
                                ques_id = id + "," + String.valueOf(notificationOptionList.get(i).getNotification_option_ques_id());
                            }
                            addNotification(value, "INDIVIDUAL", "2", id, ques_id);

                        }
                        break;

                    case "SPECIFIC":
                        if (!etSpecific.getText().toString().equalsIgnoreCase("") && !etSpecific.getText().toString().equalsIgnoreCase("")) {
                            addNotification(etSpecificRating.getText().toString().trim(), "SPECIFIC", "1", notificationId, questionId);
                        } else {
                            Utils.showToast(getActivity(), "Please fill all the fields", true);
                        }
                        break;
                }

                /*
                if(!etAddNotification.getText().toString().equalsIgnoreCase("") && !etTotalScore.getText().toString().equalsIgnoreCase("")){
                    addNotification();

                } else {
                    Utils.showToast(getActivity(), "Please fill all the fields", true);
                }*/
            }
        });

        etSpecific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getActivity())
                        .items(notificationOptionListString)
                        .alwaysCallInputCallback()
                        .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which,
                                                       CharSequence text) {
                                etSpecific.setText(text.toString());
                                notificationId = String.valueOf(notificationOptionList.get(which).getNotification_option_id());
                                questionId = String.valueOf(notificationOptionList.get(which).getNotification_option_ques_id());
                                // authEditText.setTextLength(Convert.toInt(text.toString()));
                                return true;
                            }
                        }).show();
            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    private void addNotification(final String notificationRating, final String notificationType, final String notificationTypeValue, final String notificationId, final String questionId) {
        if (NetworkConnection.isNetworkAvailable(getActivity())) {
            Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_ADD_NOTIFICATION, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_ADD_NOTIFICATION,
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
                                        getDialog().dismiss();
                                        Intent intent = new Intent(getActivity(), NotificationActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Utils.showToast(getActivity(), message, true);
                                        //Utils.showSnackBar(getActivity(), clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    //Utils.showToast(LoginActivity.this, "API ERROR", true);
                                    //Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                //Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                Utils.showToast(getActivity(), "API ERROR", true);
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
                            Utils.showToast(getActivity(), "API ERROR", true);
                            //Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                        }
                    }) {


                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.NOTIFICATION_RATING, notificationRating);
                    params.put(AppConfigTags.NOTIFICATION_TYPE, notificationType);
                    params.put(AppConfigTags.NOTIFICATION_TYPE_VALUE, notificationTypeValue);
                    params.put(AppConfigTags.NOTIFICATION_ID, notificationId);
                    params.put(AppConfigTags.QUESTION_ID, questionId);
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(getActivity(), AppConfigTags.USER_LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {
            Utils.showToast(getActivity(), "API ERROR", true);
        }
    }

    public void setOnDialogResultListener(OnDialogResultListener listener) {
        this.onDialogResultListener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDialogResultListener != null) {
            onDialogResultListener.onDismiss();
        }
        Utils.hideSoftKeyboard(getActivity());
    }


    public interface OnDialogResultListener {
        public abstract void onDismiss();

    }

}
