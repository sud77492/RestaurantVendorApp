package com.nhsurveys.restaurantvendorapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.MPPointF;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.nhsurveys.restaurantvendorapp.R;
import com.nhsurveys.restaurantvendorapp.model.SurveyPerDay;
import com.nhsurveys.restaurantvendorapp.utils.AppConfigTags;
import com.nhsurveys.restaurantvendorapp.utils.AppConfigURL;
import com.nhsurveys.restaurantvendorapp.utils.Constants;
import com.nhsurveys.restaurantvendorapp.utils.DayAxisValueFormatter;
import com.nhsurveys.restaurantvendorapp.utils.MyValueFormatter;
import com.nhsurveys.restaurantvendorapp.utils.NetworkConnection;
import com.nhsurveys.restaurantvendorapp.utils.SetTypeFace;
import com.nhsurveys.restaurantvendorapp.utils.UserDetailsPref;
import com.nhsurveys.restaurantvendorapp.utils.Utils;
import com.nhsurveys.restaurantvendorapp.utils.XYMarkerView;

public class MainActivity extends DemoBase implements SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener {
    TextView tvCountSevenDays, tvCountThirtyDays, tvSevenDaysPercent, tvThirtyDaysPercent, tvRateSeven,
            tvRateThirty, tvWelcome;
    ProgressDialog progressDialog;
    CoordinatorLayout clMain;
    ArrayList<SurveyPerDay> surveyPerDayList = new ArrayList<>();

    UserDetailsPref userDetailsPref;
    SwipeRefreshLayout swipeRefreshLayout;
    private AccountHeader headerResult = null;
    Bundle savedInstanceState;
    private Drawer result = null;
    ImageView ivNavigation;
    private BarChart chart;
    //private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;
    List<String> xAxisValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.showLog(Log.ERROR, "Device Info", Utils.getDeviceInfo(this), true);
        initView();
        initData();
        initLogin();
        initListener();
        initVendor("");
        initDrawer();

    }

    private void initLogin() {
        if(userDetailsPref.getIntPref(MainActivity.this, UserDetailsPref.USER_ID) == 0){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(userDetailsPref.getIntPref(MainActivity.this, UserDetailsPref.USER_ID) == 0){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }else{
            tvWelcome.setText(userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.USER_RESTAURANT_NAME));
            initVendor("");
        }
    }

    private void initView() {
        tvCountSevenDays = findViewById(R.id.tvCountSevenDays);
        tvCountThirtyDays = findViewById(R.id.tvCountThirtyDays);
        tvSevenDaysPercent = findViewById(R.id.tvSevenDaysPercent);
        tvThirtyDaysPercent = findViewById(R.id.tvThirtyDaysPercent);
        tvRateSeven = findViewById(R.id.tvRateSeven);
        tvRateThirty = findViewById(R.id.tvRateThirty);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvX = findViewById(R.id.tvXMax);
        tvY = findViewById(R.id.tvYMax);
        chart = findViewById(R.id.chart1);
        //seekBarX = findViewById(R.id.seekBar1);
        //seekBarY = findViewById(R.id.seekBar2);
        clMain = findViewById(R.id.clMain);
        //swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        ivNavigation = findViewById(R.id.ivNavigation);
    }

    private void initData() {
        createChart();
        userDetailsPref = UserDetailsPref.getInstance();
        progressDialog = new ProgressDialog(this);


    }

    private void createChart() {
        /*seekBarX.setOnSeekBarChangeListener(this);
        seekBarY.setOnSeekBarChangeListener(this);*/
        chart.setOnChartValueSelectedListener(this);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(15);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));


        ValueFormatter custom = new MyValueFormatter("$");

        /*YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)*/

        /*YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)*/

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

        // setting data
        /*seekBarY.setProgress(50);
        seekBarX.setProgress(15);*/
        //setData(15, 1);
        // chart.setDrawLegend(false);
    }


    private void setData(int count, float range) {

        float start = 1f;

        ArrayList<BarEntry> values = new ArrayList<>();

        /*for (int i = (int) start; i < start + count; i++) {
            float val = (float) (Math.random() * (range + 1));

            if (Math.random() * 100 < 25) {
                values.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.ic_back)));
            } else {
                values.add(new BarEntry(i, val));
            }
        }*/
        for (int i = 0; i < count; i++) {
            values.add(new BarEntry(i, surveyPerDayList.get(i).getSurvey_per_day()));
        }

        Log.e("Values", values.toString());
        BarDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "No. Of Surveys Per Day");

            set1.setDrawIcons(false);


            int startColor1 = ContextCompat.getColor(this, android.R.color.holo_orange_light);
            int startColor2 = ContextCompat.getColor(this, android.R.color.holo_blue_light);
            int startColor3 = ContextCompat.getColor(this, android.R.color.holo_orange_light);
            int startColor4 = ContextCompat.getColor(this, android.R.color.holo_green_light);
            int startColor5 = ContextCompat.getColor(this, android.R.color.holo_red_light);
            int endColor1 = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
            int endColor2 = ContextCompat.getColor(this, android.R.color.holo_purple);
            int endColor3 = ContextCompat.getColor(this, android.R.color.holo_green_dark);
            int endColor4 = ContextCompat.getColor(this, android.R.color.holo_red_dark);
            int endColor5 = ContextCompat.getColor(this, android.R.color.holo_orange_dark);

            List<GradientColor> gradientColors = new ArrayList<>();
            gradientColors.add(new GradientColor(startColor1, endColor1));
            gradientColors.add(new GradientColor(startColor2, endColor2));
            gradientColors.add(new GradientColor(startColor3, endColor3));
            gradientColors.add(new GradientColor(startColor4, endColor4));
            gradientColors.add(new GradientColor(startColor5, endColor5));

            set1.setGradientColors(gradientColors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(tfLight);
            data.setBarWidth(0.9f);

            chart.setData(data);
        }
    }

    private void initListener() {
        /*swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               // initVendor("Swipe");
            }
        });*/
        ivNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.openDrawer();
            }
        });
    }

    private void initVendor(String check) {
        if (NetworkConnection.isNetworkAvailable(MainActivity.this)) {
            if(check.length() == 0) {
                Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            }
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_INIT, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_INIT,
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

                                        Double last_seven_days_count = jsonObj.getDouble(AppConfigTags.RESPONSE_LAST_SEVEN_DAYS);
                                        Double last_thirty_days_count = jsonObj.getDouble(AppConfigTags.RESPONSE_LAST_ONE_MONTH);
                                        Double current_seven_days_count = jsonObj.getDouble(AppConfigTags.RESPONSE_CURRENT_SEVEN_DAYS);
                                        Double current_thirty_days_count = jsonObj.getDouble(AppConfigTags.RESPONSE_CURRENT_ONE_MONTH);
                                        tvCountSevenDays.setText(jsonObj.getString(AppConfigTags.RESPONSE_CURRENT_SEVEN_DAYS));
                                        tvCountThirtyDays.setText(jsonObj.getString(AppConfigTags.RESPONSE_CURRENT_ONE_MONTH));
                                        if (current_seven_days_count > last_seven_days_count) {
                                            Double gain_seven_days = current_seven_days_count - last_seven_days_count;
                                            if (last_seven_days_count != 0) {
                                                Double percent_seven_days = Math.round((gain_seven_days / last_seven_days_count) * 100 * 100.0) / 100.0;
                                                tvSevenDaysPercent.setText(Double.toString(percent_seven_days) + "%");
                                            } else {
                                                Double percent_seven_days = 100.0;
                                                tvSevenDaysPercent.setText(Double.toString(percent_seven_days) + "%");
                                            }
                                            tvRateSeven.setText("Increased");
                                        }
                                        if (current_seven_days_count < last_seven_days_count) {
                                            Double loss_seven_days = last_seven_days_count - current_seven_days_count;
                                            if (last_seven_days_count != 0) {
                                                Double percent_seven_days = Math.round((loss_seven_days / last_seven_days_count) * 100 * 100.0) / 100.0;
                                                tvSevenDaysPercent.setText(Double.toString(percent_seven_days) + "%");
                                            } else {
                                                Double percent_seven_days = 100.0;
                                                tvSevenDaysPercent.setText(Double.toString(percent_seven_days) + "%");
                                            }

                                            tvRateSeven.setText("Decreased");
                                        }
                                        if (current_thirty_days_count > last_thirty_days_count) {
                                            Double gain_thirty_days = current_thirty_days_count - last_thirty_days_count;
                                            if (last_thirty_days_count != 0) {
                                                Double percent_thirty_days = Math.round((gain_thirty_days / last_thirty_days_count) * 100 * 100.0) / 100.0;
                                                tvThirtyDaysPercent.setText(Double.toString(percent_thirty_days) + "%");
                                            } else {
                                                Double percent_thirty_days = 100.0;
                                                tvThirtyDaysPercent.setText(Double.toString(percent_thirty_days) + "%");
                                            }
                                            tvRateThirty.setText("Increased");
                                        }
                                        if (current_thirty_days_count < last_thirty_days_count) {
                                            Double loss_thirty_days = last_thirty_days_count - current_thirty_days_count;
                                            if (last_thirty_days_count != 0) {
                                                Double percent_thirty_days = Math.round((loss_thirty_days / last_thirty_days_count) * 100 * 100.0) / 100.0;
                                                tvThirtyDaysPercent.setText(Double.toString(percent_thirty_days) + "%");
                                            } else {
                                                Double percent_thirty_days = 100.0;
                                                tvThirtyDaysPercent.setText(Double.toString(percent_thirty_days) + "%");
                                            }
                                            tvRateThirty.setText("Decreased");
                                        }
                                        surveyPerDayList.clear();
                                        JSONArray jsonArraySurveyPerDay = jsonObj.getJSONArray(AppConfigTags.SURVEY_PER_DAY);
                                        for (int j = 0; j < jsonArraySurveyPerDay.length(); j++) {
                                            JSONObject jsonObjectSurveyPerDay = jsonArraySurveyPerDay.getJSONObject(j);
                                            surveyPerDayList.add(new SurveyPerDay(jsonObjectSurveyPerDay.getInt(AppConfigTags.SURVEY_PER_DAY),
                                                    jsonObjectSurveyPerDay.getString(AppConfigTags.SURVEY_DATE)));
                                            xAxisValues.add(jsonObjectSurveyPerDay.getString(AppConfigTags.SURVEY_DATE));
                                        }
                                        Log.e("SIZE AND MAX", "" + surveyPerDayList.size() + " " + getMax(surveyPerDayList));
                                        createChart();
                                        setData(surveyPerDayList.size(), getMax(surveyPerDayList));
                                        chart.notifyDataSetChanged();
                                        chart.invalidate();

                                    } else {
                                        Utils.showSnackBar(MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss();

                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                        }
                    }) {


                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.FIREBASE_ID, userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.FIREBASE_ID));
                    params.put(AppConfigTags.DEVICE_ID, Settings.Secure.getString(getApplicationContext().getContentResolver(),
                            Settings.Secure.ANDROID_ID));
                    params.put(AppConfigTags.DEVICE_NAME, Utils.getDeviceInfo(MainActivity.this));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put (AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(MainActivity.this, UserDetailsPref.USER_LOGIN_KEY));
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

    private void initDrawer() {
        IProfile profile = new IProfile() {
            @Override
            public Object withName(String name) {
                return null;
            }

            @Override
            public StringHolder getName() {
                return null;
            }

            @Override
            public Object withEmail(String email) {
                return null;
            }

            @Override
            public StringHolder getEmail() {
                return null;
            }

            @Override
            public Object withIcon(Drawable icon) {
                return null;
            }

            @Override
            public Object withIcon(Bitmap bitmap) {
                return null;
            }

            @Override
            public Object withIcon(@DrawableRes int iconRes) {
                return null;
            }

            @Override
            public Object withIcon(String url) {
                return null;
            }

            @Override
            public Object withIcon(Uri uri) {
                return null;
            }

            @Override
            public Object withIcon(IIcon icon) {
                return null;
            }

            @Override
            public ImageHolder getIcon() {
                return null;
            }

            @Override
            public Object withSelectable(boolean selectable) {
                return null;
            }

            @Override
            public boolean isSelectable() {
                return false;
            }

            @Override
            public Object withIdentifier(long identifier) {
                return null;
            }

            @Override
            public long getIdentifier() {
                return 0;
            }
        };

        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                if (uri != null) {
                    Glide.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
                }
            }

            @Override
            public void cancel(ImageView imageView) {
               // Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.colorPrimary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_white_1000);
                }

                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()

                return super.placeholder(ctx, tag);
            }
        });

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .withTypeface(SetTypeFace.getTypeface(MainActivity.this))
                .withTypeface(SetTypeFace.getTypeface(this))
                .withPaddingBelowHeader(false)
                .withSelectionListEnabled(false)
                .withSelectionListEnabledForSingleProfile(false)
                .withProfileImagesVisible(false)
                .withOnlyMainProfileImageVisible(false)
                .withDividerBelowHeader(true)
                .withHeaderBackground(R.color.primary_dark)
                .withSavedInstance(savedInstanceState)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        //Intent intent = new Intent (MainActivity.this, MyProfileActivity.class);
                        //startActivity (intent);
                        return false;
                    }
                })
                .build();
        headerResult.addProfiles(new ProfileDrawerItem()
                .withName("")
                .withEmail(""));

        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
//                .withToolbar (toolbar)
//                .withItemAnimator (new AlphaCrossFadeAnimator ())

                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withTypeface(SetTypeFace.getTypeface(MainActivity.this)),
                        new PrimaryDrawerItem().withName("Report List").withIcon(FontAwesome.Icon.faw_list).withIdentifier(2).withTypeface(SetTypeFace.getTypeface(MainActivity.this)),
                        new PrimaryDrawerItem().withName("Graph Report").withIcon(FontAwesome.Icon.faw_bar_chart).withIdentifier(3).withTypeface(SetTypeFace.getTypeface(MainActivity.this)),
                        new PrimaryDrawerItem().withName("Notifications").withIcon(FontAwesome.Icon.faw_flag_checkered).withIdentifier(4).withTypeface(SetTypeFace.getTypeface(MainActivity.this)),
                        new PrimaryDrawerItem().withName("My Account").withIcon(FontAwesome.Icon.faw_user_circle).withIdentifier(5).withTypeface(SetTypeFace.getTypeface(MainActivity.this)),
                        new PrimaryDrawerItem().withName("About").withIcon(FontAwesome.Icon.faw_address_book).withIdentifier(6).withTypeface(SetTypeFace.getTypeface(MainActivity.this)),
                        new PrimaryDrawerItem().withName("Support & Feedback").withIcon(FontAwesome.Icon.faw_question_circle).withIdentifier(7).withTypeface(SetTypeFace.getTypeface(MainActivity.this)),
                        new PrimaryDrawerItem().withName("Sign Out").withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(8).withTypeface(SetTypeFace.getTypeface(MainActivity.this))
                )
                .withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier()) {
                            case 1:
                                break;


                            case 2:
                                Intent intentReportList = new Intent(MainActivity.this, ReportListActivity.class);
                                startActivity(intentReportList);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;


                            case 3:
                                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;

                            case 4:
                                Intent intentNotification = new Intent(MainActivity.this, NotificationActivity.class);
                                startActivity(intentNotification);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;

                            case 5:
                                Intent intentMyAccount = new Intent(MainActivity.this, MyAccountActivity.class);
                                startActivity(intentMyAccount);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;


                            case 6:
                                Intent intentAbout = new Intent(MainActivity.this, AboutActivity.class);
                                startActivity(intentAbout);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;

                            case 7:
                                new MaterialDialog.Builder(MainActivity.this)
                                        .title("Provide Feedback")
                                        .content("If you want to give feedback, then please click SEND FEEDBACK")
                                        .positiveText("SEND FEEDBACK")
                                        .neutralText("SUPPORT")
                                        .negativeText("CLOSE")
                                        .typeface(SetTypeFace.getTypeface(MainActivity.this), SetTypeFace.getTypeface(MainActivity.this))
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            final MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(MainActivity.this)
                                                                    .content("Please provide the feedback")
                                                                    .contentColor(getResources().getColor(R.color.app_text_color_dark))
                                                                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                                                                    .typeface(SetTypeFace.getTypeface(MainActivity.this), SetTypeFace.getTypeface(MainActivity.this))
                                                                    .alwaysCallInputCallback()
                                                                    .canceledOnTouchOutside(true)
                                                                    .cancelable(true)
                                                                    .positiveText("OK")
                                                                    .negativeText("CANCEL");

                                                            mBuilder.input(null, null, new MaterialDialog.InputCallback() {
                                                                @Override
                                                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                                                    if (input.toString().length() > 0) {
                                                                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                                                                    } else {
                                                                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                                                    }
                                                                }
                                                            });

                                                            mBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
                                                                @Override
                                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                    if (dialog.getInputEditText().getText().toString().length() > 0) {
                                                                        sendAppFeedback(dialog.getInputEditText().getText().toString());
                                                                    }
                                                                }
                                                            });

                                                            MaterialDialog dialog2 = mBuilder.build();

                                                            dialog2.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                                                            dialog2.show();
                                                        }
                                                    }
                                        )
                                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                dialog.dismiss();
                                                /*Intent gmail = new Intent(Intent.ACTION_SEND);
                                                gmail.setClassName("com.google.android.gm", "com.google.andro‌​id.gm.ComposeActivit‌​yGmail");
                                                //gmail.putExtra(Intent.EXTRA_EMAIL, new String[] { "myEmail@email.com" });
                                                gmail.setData(Uri.parse("sameersharma078626@gmail.com"));
                                                gmail.setType("plain/text");
                                                startActivity(gmail);*/


                                                /* Create the Intent */
                                                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

                                                /* Fill it with Data */
                                                emailIntent.setType("plain/text");
                                                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"sameersharma078626@gmail.com"});
                                                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
                                                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Text");

                                                /* Send it off to the Activity-Chooser */
                                                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                                            }
                                        })
                                        .show();
                                break;
                            //Intent intentHelpSupport =  new Intent(MainActivity.this, )

                            case 8:
                                showLogOutDialog();
                                break;


                        }
                        return false;
                    }
                })
                .build();
    }

    private void showLogOutDialog() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .limitIconToDefaultSize()
                .content(getResources().getText(R.string.dialog_text_logout))
                .canceledOnTouchOutside(false)
                .cancelable(false)
                .positiveText(getResources().getText(R.string.dialog_action_yes))
                .negativeText(getResources().getText(R.string.dialog_action_no))
                .typeface(SetTypeFace.getTypeface(MainActivity.this), SetTypeFace.getTypeface(MainActivity.this))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        userDetailsPref.putIntPref(MainActivity.this, UserDetailsPref.USER_ID, 0);
                        userDetailsPref.putStringPref(MainActivity.this, UserDetailsPref.USER_EMAIL, "");
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }).build();
        dialog.show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        /* tvX.setText(String.valueOf(seekBarX.getProgress()));
        tvY.setText(String.valueOf(seekBarY.getProgress()));

        setData(seekBarX.getProgress(), seekBarY.getProgress());
        chart.invalidate();*/
    }

    @Override
    protected void saveToGallery() {
        saveToGallery(chart, "BarChartActivity");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private final RectF onValueSelectedRectF = new RectF();

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = onValueSelectedRectF;
        chart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = chart.getPosition(e, YAxis.AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + chart.getLowestVisibleX() + ", high: "
                        + chart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {
    }

    public int getMax(ArrayList<SurveyPerDay> perDay) {

        int max = perDay.get(0).getSurvey_per_day();
        for (int i = 0; i < perDay.size(); i++) {
            if (perDay.get(i).getSurvey_per_day() > max) {
                max = perDay.get(i).getSurvey_per_day();
            }
        }
        return max;
    }

    private void sendAppFeedback(final String feedback) {
        if (NetworkConnection.isNetworkAvailable(MainActivity.this)) {
            Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_ADD_FEEDBACK, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_ADD_FEEDBACK,
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
                                        Utils.showSnackBar(MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    } else {
                                        Utils.showSnackBar(MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss();
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            Utils.showSnackBar(MainActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.FEEDBACK_MESSAGE, feedback);
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(MainActivity.this, AppConfigTags.USER_LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {
            Utils.showToast(MainActivity.this, "API ERROR", true);
        }
    }
}
