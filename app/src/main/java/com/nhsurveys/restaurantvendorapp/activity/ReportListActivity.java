package com.nhsurveys.restaurantvendorapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
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
import java.util.Map;

import com.nhsurveys.restaurantvendorapp.R;
import com.nhsurveys.restaurantvendorapp.adapter.ReportAdapter;
import com.nhsurveys.restaurantvendorapp.model.Report;
import com.nhsurveys.restaurantvendorapp.model.ReportSorter;
import com.nhsurveys.restaurantvendorapp.utils.AppConfigTags;
import com.nhsurveys.restaurantvendorapp.utils.AppConfigURL;
import com.nhsurveys.restaurantvendorapp.utils.Constants;
import com.nhsurveys.restaurantvendorapp.utils.NetworkConnection;
import com.nhsurveys.restaurantvendorapp.utils.SetTypeFace;
import com.nhsurveys.restaurantvendorapp.utils.UserDetailsPref;
import com.nhsurveys.restaurantvendorapp.utils.Utils;

public class ReportListActivity extends AppCompatActivity {
    ProgressDialog progressDialog;
    CoordinatorLayout clMain;
    ArrayList<Report> reportList = new ArrayList<>();
    ReportAdapter reportAdapter;
    UserDetailsPref userDetailsPref;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView rvReportList;
    ReportSorter reportSorter;

    Button btFilter;
    ArrayList<Report> tempReportList = new ArrayList<>();
    int position = 0;
    private AccountHeader headerResult = null;
    Bundle savedInstanceState;
    private Drawer result = null;
    ImageView ivNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportlist);
        Utils.showLog(Log.ERROR, "Device Info", Utils.getDeviceInfo(this), true);
        initView();
        initData();
        initListener();
        initVendor("");
        initDrawer();
    }


    @Override
    public void onResume() {
        super.onResume();
        initVendor("");
    }


    private void initView() {
        clMain = (CoordinatorLayout) findViewById(R.id.clMain);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        rvReportList = findViewById(R.id.rvReportList);
        ivNavigation = findViewById(R.id.ivNavigation);
        btFilter = findViewById(R.id.btFilter);
    }

    private void initData() {
        reportSorter = new ReportSorter(reportList);
        userDetailsPref = UserDetailsPref.getInstance();
        progressDialog = new ProgressDialog(this);
        reportAdapter = new ReportAdapter(this, reportList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvReportList.setLayoutManager(mLayoutManager);
        rvReportList.setItemAnimator(new DefaultItemAnimator());
        rvReportList.setAdapter(reportAdapter);

    }

    private void initListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initVendor("Swipe");
            }
        });

        btFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDropdown();
            }
        });

        ivNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.openDrawer();
            }
        });
    }

    private void initVendor(String check) {
        if (NetworkConnection.isNetworkAvailable(ReportListActivity.this)) {
            if (check.length() == 0) {
                Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            }
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_REPORT_LIST, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.GET, AppConfigURL.URL_REPORT_LIST,
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
                                        reportList.clear();
                                        JSONArray jsonArrayReport = jsonObj.getJSONArray(AppConfigTags.REPORTS);
                                        for (int i = 0; i < jsonArrayReport.length(); i++) {
                                            JSONObject jsonObjReport = jsonArrayReport.getJSONObject(i);
                                            reportList.add(new Report(jsonObjReport.getInt(AppConfigTags.REPORT_CUSTOMER_SURVEY_ID),
                                                    jsonObjReport.getDouble(AppConfigTags.REPORT_CUSTOMER_STAR),
                                                    jsonObjReport.getDouble(AppConfigTags.REPORT_CUSTOMER_AVERAGE_RATING),
                                                    jsonObjReport.getString(AppConfigTags.REPORT_CUSTOMER_NAME),
                                                    jsonObjReport.getString(AppConfigTags.REPORT_CUSTOMER_MOBILE),
                                                    jsonObjReport.getString(AppConfigTags.REPORT_CUSTOMER_COMMENT),
                                                    jsonObjReport.getString(AppConfigTags.REPORT_CUSTOMER_STATUS)));
                                        }
                                        swipeRefreshLayout.setRefreshing(false);
                                        reportAdapter.notifyDataSetChanged();
                                    } else {
                                        Utils.showSnackBar(ReportListActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar(ReportListActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar(ReportListActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            swipeRefreshLayout.setRefreshing(false);
                            progressDialog.dismiss();
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            Utils.showSnackBar(ReportListActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.FIREBASE_ID, userDetailsPref.getStringPref(ReportListActivity.this, UserDetailsPref.FIREBASE_ID));
                    params.put(AppConfigTags.DEVICE_ID, Settings.Secure.getString(getApplicationContext().getContentResolver(),
                            Settings.Secure.ANDROID_ID));
                    params.put(AppConfigTags.DEVICE_NAME, Utils.getDeviceInfo(ReportListActivity.this));
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(ReportListActivity.this, UserDetailsPref.USER_LOGIN_KEY));
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

    private void showDropdown() {
        ArrayList<String> filter = new ArrayList<String>();
        filter.add("Newest First");
        filter.add("Rating-High to Low");
        filter.add("Rating-Low to High");
        filter.add("By A to Z");

        new MaterialDialog.Builder(this)
                .items(filter)
                .title("Sort By")
                .typeface(SetTypeFace.getTypeface(this), SetTypeFace.getTypeface(this))
                .itemsCallbackSingleChoice(
                        position, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                position = which;
                                switch (position) {
                                    case 0:
                                        tempReportList = reportSorter.getSortedReportById();
                                        break;

                                    case 1:
                                        tempReportList = reportSorter.getSortedReportByDescRating();
                                        break;

                                    case 2:
                                        tempReportList = reportSorter.getSortedReportByAscRating();
                                        break;

                                    case 3:
                                        tempReportList = reportSorter.getSortedReportByName();

                                        break;


                                }

                                reportAdapter = new ReportAdapter(ReportListActivity.this, tempReportList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                rvReportList.setLayoutManager(mLayoutManager);
                                rvReportList.setItemAnimator(new DefaultItemAnimator());
                                rvReportList.setAdapter(reportAdapter);

                                return true;
                            }
                        })
                .show();
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
                .withTypeface(SetTypeFace.getTypeface(ReportListActivity.this))
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
                        //Intent intent = new Intent (ReportListActivity.this, MyProfileActivity.class);
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
                        new PrimaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withTypeface(SetTypeFace.getTypeface(ReportListActivity.this)),
                        new PrimaryDrawerItem().withName("Report List").withIcon(FontAwesome.Icon.faw_list).withIdentifier(2).withTypeface(SetTypeFace.getTypeface(ReportListActivity.this)),
                        new PrimaryDrawerItem().withName("Graph Report").withIcon(FontAwesome.Icon.faw_bar_chart).withIdentifier(3).withTypeface(SetTypeFace.getTypeface(ReportListActivity.this)),
                        new PrimaryDrawerItem().withName("Notifications").withIcon(FontAwesome.Icon.faw_flag_checkered).withIdentifier(4).withTypeface(SetTypeFace.getTypeface(ReportListActivity.this)),
                        new PrimaryDrawerItem().withName("My Account").withIcon(FontAwesome.Icon.faw_user_circle).withIdentifier(5).withTypeface(SetTypeFace.getTypeface(ReportListActivity.this)),
                        new PrimaryDrawerItem().withName("About").withIcon(FontAwesome.Icon.faw_address_book).withIdentifier(6).withTypeface(SetTypeFace.getTypeface(ReportListActivity.this)),
                        new PrimaryDrawerItem().withName("Support & Feedback").withIcon(FontAwesome.Icon.faw_question_circle).withIdentifier(7).withTypeface(SetTypeFace.getTypeface(ReportListActivity.this)),
                        new PrimaryDrawerItem().withName("Sign Out").withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(8).withTypeface(SetTypeFace.getTypeface(ReportListActivity.this))
                )
                .withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier()) {
                            case 1:
                                Intent intent = new Intent(ReportListActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;


                            case 2:

                                break;


                            case 3:
                                Intent intentGraph = new Intent(ReportListActivity.this, GraphActivity.class);
                                startActivity(intentGraph);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;

                            case 4:
                                Intent intentNotification = new Intent(ReportListActivity.this, NotificationActivity.class);
                                startActivity(intentNotification);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;

                            case 5:
                                Intent intentMyAccount = new Intent(ReportListActivity.this, MyAccountActivity.class);
                                startActivity(intentMyAccount);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;


                            case 6:
                                Intent intentAbout = new Intent(ReportListActivity.this, AboutActivity.class);
                                startActivity(intentAbout);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;

                            case 7:
                                new MaterialDialog.Builder(ReportListActivity.this)
                                        .title("Provide Feedback")
                                        .content("If you want to give feedback, then please click SEND FEEDBACK")
                                        .positiveText("SEND FEEDBACK")
                                        .neutralText("SUPPORT")
                                        .negativeText("CLOSE")
                                        .typeface(SetTypeFace.getTypeface(ReportListActivity.this), SetTypeFace.getTypeface(ReportListActivity.this))
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            final MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(ReportListActivity.this)
                                                                    .content("Please provide the feedback")
                                                                    .contentColor(getResources().getColor(R.color.app_text_color_dark))
                                                                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                                                                    .typeface(SetTypeFace.getTypeface(ReportListActivity.this), SetTypeFace.getTypeface(ReportListActivity.this))
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
                            //Intent intentHelpSupport =  new Intent(ReportListActivity.this, )

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
                .typeface(SetTypeFace.getTypeface(ReportListActivity.this), SetTypeFace.getTypeface(ReportListActivity.this))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        userDetailsPref.putIntPref(ReportListActivity.this, UserDetailsPref.USER_ID, 0);
                        userDetailsPref.putStringPref(ReportListActivity.this, UserDetailsPref.USER_EMAIL, "");
                        Intent intent = new Intent(ReportListActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }).build();
        dialog.show();
    }

    private void sendAppFeedback(final String feedback) {
        if (NetworkConnection.isNetworkAvailable(ReportListActivity.this)) {
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
                                        Utils.showSnackBar(ReportListActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    } else {
                                        Utils.showSnackBar(ReportListActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar(ReportListActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar(ReportListActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
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
                            Utils.showSnackBar(ReportListActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
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
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(ReportListActivity.this, AppConfigTags.USER_LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {
            Utils.showToast(ReportListActivity.this, "API ERROR", true);
        }
    }
}
