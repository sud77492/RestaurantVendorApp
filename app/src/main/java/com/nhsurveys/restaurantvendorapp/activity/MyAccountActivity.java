package com.nhsurveys.restaurantvendorapp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.nhsurveys.restaurantvendorapp.R;
import com.nhsurveys.restaurantvendorapp.utils.AppConfigTags;
import com.nhsurveys.restaurantvendorapp.utils.AppConfigURL;
import com.nhsurveys.restaurantvendorapp.utils.Constants;
import com.nhsurveys.restaurantvendorapp.utils.NetworkConnection;
import com.nhsurveys.restaurantvendorapp.utils.SetTypeFace;
import com.nhsurveys.restaurantvendorapp.utils.UserDetailsPref;
import com.nhsurveys.restaurantvendorapp.utils.Utils;

public class MyAccountActivity extends AppCompatActivity {
    CoordinatorLayout clMain;
    EditText etName, etMobile;
    TextView tvRestaurantName, tvEmail;
    Button btUpdate;
    UserDetailsPref userDetailsPref;
    ProgressDialog progressDialog;
    private AccountHeader headerResult = null;
    Bundle savedInstanceState;
    private Drawer result = null;
    ImageView ivNavigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initView();
        initData();
        initListener();
        initDrawer();
    }

    private void initView() {
        clMain = findViewById(R.id.clMain);
        etName = findViewById(R.id.etName);
        tvEmail = findViewById(R.id.tvEmail);
        etMobile = findViewById(R.id.etMobile);
        ivNavigation = findViewById(R.id.ivNavigation);
        tvRestaurantName = findViewById(R.id.tvRestaurantName);
        btUpdate = findViewById(R.id.btUpdate);
    }

    private void initData() {
        userDetailsPref = UserDetailsPref.getInstance();
        progressDialog = new ProgressDialog(this);
        etName.setText(userDetailsPref.getStringPref(this, UserDetailsPref.USER_NAME));
        tvEmail.setText(userDetailsPref.getStringPref(this, UserDetailsPref.USER_EMAIL));
        tvRestaurantName.setText(userDetailsPref.getStringPref(this, UserDetailsPref.USER_RESTAURANT_NAME));
        etMobile.setText(userDetailsPref.getStringPref(this, UserDetailsPref.USER_MOBILE));
    }

    private void initListener() {
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        ivNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.openDrawer();
            }
        });
    }

    private void updateProfile() {
        if (NetworkConnection.isNetworkAvailable(MyAccountActivity.this)) {
            Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_UPDATE_PROFILE, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_UPDATE_PROFILE,
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
                                        Utils.showSnackBar(MyAccountActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    } else {
                                        Utils.showSnackBar(MyAccountActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar(MyAccountActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar(MyAccountActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
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
                            Utils.showSnackBar(MyAccountActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                        }
                    }) {


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.USER_NAME, etName.getText().toString().trim());
                    params.put(AppConfigTags.USER_MOBILE, etMobile.getText().toString().trim());
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(MyAccountActivity.this, AppConfigTags.USER_LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {
            Utils.showToast(MyAccountActivity.this, "API ERROR", true);
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
                .withTypeface(SetTypeFace.getTypeface(MyAccountActivity.this))
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
                        //Intent intent = new Intent (MyAccountActivity.this, MyProfileActivity.class);
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
                        new PrimaryDrawerItem().withName("Home").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withTypeface(SetTypeFace.getTypeface(MyAccountActivity.this)),
                        new PrimaryDrawerItem().withName("Report List").withIcon(FontAwesome.Icon.faw_list).withIdentifier(2).withTypeface(SetTypeFace.getTypeface(MyAccountActivity.this)),
                        new PrimaryDrawerItem().withName("Graph Report").withIcon(FontAwesome.Icon.faw_bar_chart).withIdentifier(3).withTypeface(SetTypeFace.getTypeface(MyAccountActivity.this)),
                        new PrimaryDrawerItem().withName("Notifications").withIcon(FontAwesome.Icon.faw_flag_checkered).withIdentifier(4).withTypeface(SetTypeFace.getTypeface(MyAccountActivity.this)),
                        new PrimaryDrawerItem().withName("My Account").withIcon(FontAwesome.Icon.faw_user_circle).withIdentifier(5).withTypeface(SetTypeFace.getTypeface(MyAccountActivity.this)),
                        new PrimaryDrawerItem().withName("About").withIcon(FontAwesome.Icon.faw_address_book).withIdentifier(6).withTypeface(SetTypeFace.getTypeface(MyAccountActivity.this)),
                        new PrimaryDrawerItem().withName("Support & Feedback").withIcon(FontAwesome.Icon.faw_question_circle).withIdentifier(7).withTypeface(SetTypeFace.getTypeface(MyAccountActivity.this)),
                        new PrimaryDrawerItem().withName("Sign Out").withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(8).withTypeface(SetTypeFace.getTypeface(MyAccountActivity.this))
                )
                .withSavedInstance(savedInstanceState)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier()) {
                            case 1:
                                Intent intent = new Intent(MyAccountActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;


                            case 2:
                                Intent intentReportList = new Intent(MyAccountActivity.this, ReportListActivity.class);
                                startActivity(intentReportList);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;


                            case 3:
                                Intent intentGraph = new Intent(MyAccountActivity.this, GraphActivity.class);
                                startActivity(intentGraph);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;

                            case 4:
                                Intent intentNotification = new Intent(MyAccountActivity.this, NotificationActivity.class);
                                startActivity(intentNotification);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;

                            case 5:

                                break;


                            case 6:
                                Intent intentAbout = new Intent(MyAccountActivity.this, AboutActivity.class);
                                startActivity(intentAbout);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                break;

                            case 7:
                                new MaterialDialog.Builder(MyAccountActivity.this)
                                        .title("Provide Feedback")
                                        .content("If you want to give feedback, then please click SEND FEEDBACK")
                                        .positiveText("SEND FEEDBACK")
                                        .neutralText("SUPPORT")
                                        .negativeText("CLOSE")
                                        .typeface(SetTypeFace.getTypeface(MyAccountActivity.this), SetTypeFace.getTypeface(MyAccountActivity.this))
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                        @Override
                                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                            final MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(MyAccountActivity.this)
                                                                    .content("Please provide the feedback")
                                                                    .contentColor(getResources().getColor(R.color.app_text_color_dark))
                                                                    .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                                                                    .typeface(SetTypeFace.getTypeface(MyAccountActivity.this), SetTypeFace.getTypeface(MyAccountActivity.this))
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
                            //Intent intentHelpSupport =  new Intent(MyAccountActivity.this, )

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
                .typeface(SetTypeFace.getTypeface(MyAccountActivity.this), SetTypeFace.getTypeface(MyAccountActivity.this))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        userDetailsPref.putIntPref(MyAccountActivity.this, UserDetailsPref.USER_ID, 0);
                        userDetailsPref.putStringPref(MyAccountActivity.this, UserDetailsPref.USER_EMAIL, "");
                        Intent intent = new Intent(MyAccountActivity.this, LoginActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }).build();
        dialog.show();
    }

    private void sendAppFeedback(final String feedback) {
        if (NetworkConnection.isNetworkAvailable(MyAccountActivity.this)) {
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
                                        Utils.showSnackBar(MyAccountActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    } else {
                                        Utils.showSnackBar(MyAccountActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar(MyAccountActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar(MyAccountActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
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
                            Utils.showSnackBar(MyAccountActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
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
                    params.put(AppConfigTags.HEADER_USER_LOGIN_KEY, userDetailsPref.getStringPref(MyAccountActivity.this, AppConfigTags.USER_LOGIN_KEY));
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {
            Utils.showToast(MyAccountActivity.this, "API ERROR", true);
        }
    }
}




