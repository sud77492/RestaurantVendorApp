package com.nhsurveys.restaurantvendorapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.nhsurveys.restaurantvendorapp.R;
import com.nhsurveys.restaurantvendorapp.model.Graph;
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

public class GraphActivity extends DemoBase implements OnChartValueSelectedListener {
    private CoordinatorLayout clMain;
    private PieChart pieChart;
    private BarChart barChart;
    TextView tvPieChart;
    TextView tvBarChart;
    ProgressDialog progressDialog;
    UserDetailsPref userDetailsPref;
    ArrayList<Graph> graphList = new ArrayList<>();
    ArrayList<String> barLabelList = new ArrayList<>();
    private AccountHeader headerResult = null;
    Bundle savedInstanceState;
    private Drawer result = null;
    ImageView ivNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_graph);

        setTitle("PieChartActivity");
        initView();
        initData();

        graphDataFromServer();
        initListener();
        initDrawer();
    }

    private void initListener() {
        ivNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.openDrawer();
            }
        });
    }

    private void initData() {

        progressDialog = new ProgressDialog(this);
        userDetailsPref = UserDetailsPref.getInstance();
    }

    private void initView() {
        clMain = findViewById(R.id.clMain);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);
        tvPieChart = findViewById(R.id.tvPieChart);
        tvBarChart = findViewById(R.id.tvBarChart);
        ivNavigation = (ImageView) findViewById(R.id.ivNavigation);
    }

    private void pieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setCenterTextTypeface(tfLight);
        pieChart.setCenterText(generateCenterSpannableText());

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(false);

        pieChart.setRotationAngle(0);
        // enable rotation of the pieChart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // pieChart.setUnit(" €");
        // pieChart.setDrawUnitsInChart(true);

        // add a selection listener
        pieChart.setOnChartValueSelectedListener(this);

        pieChart.animateY(1400, Easing.EaseInOutQuad);
        // pieChart.spin(2000, 0, 360);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTypeface(tfRegular);
        pieChart.setEntryLabelTextSize(12f);

    }

    private void setPieData(int count, float range) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the pieChart.
        /*for (int i = 0; i < count; i++) {
            entries.add(new PieEntry((float) ((Math.random() * range) + range / 5),
                    parties[i % parties.length],
                    getResources().getDrawable(R.drawable.ic_back)));
        }*/
//        Log.e("GRAPH", "" + graphList.get(graphList.size() - 1).getOption_check());
        /*if (graphList.get(graphList.size() - 1).getOption_check().equalsIgnoreCase("DIFFERENT")) {
            entries.add(new PieEntry(14, "Ilala"));
            entries.add(new PieEntry(16, "Temeke"));
            entries.add(new PieEntry(66, "Kinondoni"));
        }*/
        if (graphList.size() > 0) {
            Log.e("Graph", "" + graphList.size());
            if (graphList.get(graphList.size() - 1).getOption_check().equalsIgnoreCase("DIFFERENT")) {
                List<String> values = Arrays.asList(graphList.get(graphList.size() - 1).getRating().split("\\s*,\\s*"));
                List<String> legends = Arrays.asList(graphList.get(graphList.size() - 1).getRating_type().split("\\s*,\\s*"));
                int j = 0;
                for (String v : values) {
                    //entries.add(new PieEntry(10, "Sud"));
                    entries.add(new PieEntry(Float.valueOf(v), legends.get(j)));
                    j++;
                }
            }

            PieDataSet dataSet = new PieDataSet(entries, "Hear From");

            dataSet.setDrawIcons(false);

            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);

            // add a lot of colors

            ArrayList<Integer> colors = new ArrayList<>();

            for (int c : ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.JOYFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.LIBERTY_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            colors.add(ColorTemplate.getHoloBlue());

            dataSet.setColors(colors);
            //dataSet.setSelectionShift(0f);

            PieData data = new PieData(dataSet);
            data.setValueFormatter(new PercentFormatter(pieChart));
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.WHITE);
            data.setValueTypeface(tfLight);
            pieChart.setData(data);

            // undo all highlights
            pieChart.highlightValues(null);

            pieChart.invalidate();
        }
    }


    private void barChart() {

        barChart.setOnChartValueSelectedListener(this);

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        barChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the barChart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);
        // barChart.setDrawYLabels(false);
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(barChart);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(15);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(barLabelList));
        //xAxis.setValueFormatter(xAxisFormatter);

        ValueFormatter custom = new MyValueFormatter("$");

        /*YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)*/

        /*YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)*/

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(barChart); // For bounds control
        barChart.setMarker(mv); // Set the marker to the barChart

        // setting data
        /*seekBarY.setProgress(50);
        seekBarX.setProgress(15);*/
        //setBarData(5, 1);
        // barChart.setDrawLegend(false);
    }

    private void setBarData(int count, float range) {

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
        int j = 0;
        for (int i = 0; i < graphList.size(); i++) {
            if (graphList.get(i).getOption_check().equalsIgnoreCase("SAME")) {
                values.add(new BarEntry(j, Float.parseFloat(graphList.get(i).getRating())));
                j++;
            }
        }

        Log.e("Values", values.toString());
        BarDataSet set1;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();

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

            barChart.setData(data);
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.viewGithub: {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/com/xxmassdeveloper/mpchartexample/PieChartActivity.java"));
                startActivity(i);
                break;
            }
            case R.id.actionToggleValues: {
                for (IDataSet<?> set : pieChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                pieChart.invalidate();
                break;
            }
            case R.id.actionToggleIcons: {
                for (IDataSet<?> set : pieChart.getData().getDataSets())
                    set.setDrawIcons(!set.isDrawIconsEnabled());

                pieChart.invalidate();
                break;
            }
            case R.id.actionToggleHole: {
                if (pieChart.isDrawHoleEnabled())
                    pieChart.setDrawHoleEnabled(false);
                else
                    pieChart.setDrawHoleEnabled(true);
                pieChart.invalidate();
                break;
            }
            case R.id.actionToggleMinAngles: {
                if (pieChart.getMinAngleForSlices() == 0f)
                    pieChart.setMinAngleForSlices(36f);
                else
                    pieChart.setMinAngleForSlices(0f);
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
                break;
            }
            case R.id.actionToggleCurvedSlices: {
                boolean toSet = !pieChart.isDrawRoundedSlicesEnabled() || !pieChart.isDrawHoleEnabled();
                pieChart.setDrawRoundedSlices(toSet);
                if (toSet && !pieChart.isDrawHoleEnabled()) {
                    pieChart.setDrawHoleEnabled(true);
                }
                if (toSet && pieChart.isDrawSlicesUnderHoleEnabled()) {
                    pieChart.setDrawSlicesUnderHole(false);
                }
                pieChart.invalidate();
                break;
            }
            case R.id.actionDrawCenter: {
                if (pieChart.isDrawCenterTextEnabled())
                    pieChart.setDrawCenterText(false);
                else
                    pieChart.setDrawCenterText(true);
                pieChart.invalidate();
                break;
            }
            case R.id.actionToggleXValues: {

                pieChart.setDrawEntryLabels(!pieChart.isDrawEntryLabelsEnabled());
                pieChart.invalidate();
                break;
            }
            case R.id.actionTogglePercent:
                pieChart.setUsePercentValues(!pieChart.isUsePercentValuesEnabled());
                pieChart.invalidate();
                break;
            case R.id.animateX: {
                pieChart.animateX(1400);
                break;
            }
            case R.id.animateY: {
                pieChart.animateY(1400);
                break;
            }
            case R.id.animateXY: {
                pieChart.animateXY(1400, 1400);
                break;
            }
            case R.id.actionToggleSpin: {
                pieChart.spin(1000, pieChart.getRotationAngle(), pieChart.getRotationAngle() + 360, Easing.EaseInOutCubic);
                break;
            }
            case R.id.actionSave: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery();
                } else {
                    requestStoragePermission(pieChart);
                }
                break;
            }
        }
        return true;
    }
*/

    @Override
    protected void saveToGallery() {
        saveToGallery(pieChart, "PieChartActivity");
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    private void graphDataFromServer() {
        if (NetworkConnection.isNetworkAvailable(GraphActivity.this)) {
            Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_GRAPH_LIST, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.GET, AppConfigURL.URL_GRAPH_LIST,
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
                                        graphList.clear();
                                        JSONArray jsonArrayGraphList = jsonObj.getJSONArray(AppConfigTags.QUESTIONS);
                                        for (int i = 0; i < jsonArrayGraphList.length(); i++) {
                                            JSONObject jsonObjectGraphList = jsonArrayGraphList.getJSONObject(i);
                                            graphList.add(new Graph(jsonObjectGraphList.getInt(AppConfigTags.QUESTION_ID),
                                                    jsonObjectGraphList.getString(AppConfigTags.QUESTION_ENGLISH),
                                                    jsonObjectGraphList.getString(AppConfigTags.OPTION_CHECK),
                                                    jsonObjectGraphList.getString(AppConfigTags.RATING),
                                                    jsonObjectGraphList.getString(AppConfigTags.RATING_TYPE)));

                                            if (jsonObjectGraphList.getString(AppConfigTags.OPTION_CHECK).equalsIgnoreCase("SAME")) {
                                                barLabelList.add(jsonObjectGraphList.getString(AppConfigTags.RATING_TYPE));
                                            }
                                        }
                                        pieChart();
                                        setPieData(3, 3);
                                        pieChart.notifyDataSetChanged();
                                        pieChart.invalidate();
                                        barChart();
                                        setBarData(5, 5);
                                        barChart.notifyDataSetChanged();
                                        barChart.invalidate();


                                    } else {
                                        Utils.showSnackBar(GraphActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar(GraphActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar(GraphActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
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
                            Utils.showSnackBar(GraphActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                        }
                    }) {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(GraphActivity.this, UserDetailsPref.USER_LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
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
                .withTypeface(SetTypeFace.getTypeface(GraphActivity.this))
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
                        //Intent intent = new Intent (GraphActivity.this, MyProfileActivity.class);
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
                        new PrimaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withTypeface(SetTypeFace.getTypeface(GraphActivity.this)),
                        new PrimaryDrawerItem().withName("Report List").withIcon(FontAwesome.Icon.faw_list).withIdentifier(2).withTypeface(SetTypeFace.getTypeface(GraphActivity.this)),
                        new PrimaryDrawerItem().withName("Graph Report").withIcon(FontAwesome.Icon.faw_bar_chart).withIdentifier(3).withTypeface(SetTypeFace.getTypeface(GraphActivity.this)),
                        new PrimaryDrawerItem().withName("Notifications").withIcon(FontAwesome.Icon.faw_flag_checkered).withIdentifier(4).withTypeface(SetTypeFace.getTypeface(GraphActivity.this)),
                        new PrimaryDrawerItem().withName("My Account").withIcon(FontAwesome.Icon.faw_user_circle).withIdentifier(5).withTypeface(SetTypeFace.getTypeface(GraphActivity.this)),
                        new PrimaryDrawerItem().withName("About").withIcon(FontAwesome.Icon.faw_address_book).withIdentifier(6).withTypeface(SetTypeFace.getTypeface(GraphActivity.this)),
                        new PrimaryDrawerItem().withName("Support & Feedback").withIcon(FontAwesome.Icon.faw_question_circle).withIdentifier(7).withTypeface(SetTypeFace.getTypeface(GraphActivity.this)),
                        new PrimaryDrawerItem().withName("Sign Out").withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(8).withTypeface(SetTypeFace.getTypeface(GraphActivity.this))
                )
                .withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier()) {
                            case 1:
                                Intent intent = new Intent(GraphActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;


                            case 2:
                                Intent intentReportList = new Intent(GraphActivity.this, ReportListActivity.class);
                                startActivity(intentReportList);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;


                            case 3:

                                break;

                            case 4:
                                Intent intentNotification = new Intent(GraphActivity.this, NotificationActivity.class);
                                startActivity(intentNotification);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;

                            case 5:
                                Intent intentMyAccount = new Intent(GraphActivity.this, MyAccountActivity.class);
                                startActivity(intentMyAccount);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;


                            case 6:
                                Intent intentAbout = new Intent(GraphActivity.this, AboutActivity.class);
                                startActivity(intentAbout);
                                break;

                            case 7:
                                new MaterialDialog.Builder(GraphActivity.this)
                                        .title("Provide Feedback")
                                        .content("If you want to give feedback, then please click SEND FEEDBACK")
                                        .positiveText("SEND FEEDBACK")
                                        .neutralText("SUPPORT")
                                        .negativeText("CLOSE")
                                        .typeface(SetTypeFace.getTypeface(GraphActivity.this), SetTypeFace.getTypeface(GraphActivity.this))
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            final MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(GraphActivity.this)
                                                                    .content("Please provide the feedback")
                                                                    .contentColor(getResources().getColor(R.color.app_text_color_dark))
                                                                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                                                                    .typeface(SetTypeFace.getTypeface(GraphActivity.this), SetTypeFace.getTypeface(GraphActivity.this))
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
                            //Intent intentHelpSupport =  new Intent(GraphActivity.this, )

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
                .typeface(SetTypeFace.getTypeface(GraphActivity.this), SetTypeFace.getTypeface(GraphActivity.this))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        userDetailsPref.putIntPref(GraphActivity.this, UserDetailsPref.USER_ID, 0);
                        userDetailsPref.putStringPref(GraphActivity.this, UserDetailsPref.USER_EMAIL, "");
                        Intent intent = new Intent(GraphActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }).build();
        dialog.show();
    }

    private void sendAppFeedback(final String feedback) {
        if (NetworkConnection.isNetworkAvailable(GraphActivity.this)) {
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
                                        Utils.showSnackBar(GraphActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    } else {
                                        Utils.showSnackBar(GraphActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar(GraphActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar(GraphActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
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
                            Utils.showSnackBar(GraphActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
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
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(GraphActivity.this, AppConfigTags.USER_LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {
            Utils.showToast(GraphActivity.this, "API ERROR", true);
        }
    }
}
