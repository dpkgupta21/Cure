package com.cure.signup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cure.CureApplication;
import com.cure.R;
import com.cure.dashboard.DashboardActivity;
import com.cure.login.MainActivity;

import com.cure.model.UserDTO;
import com.cure.preferences.CurePreferences;
import com.cure.utility.BaseActivity;
import com.cure.utility.Constants;
import com.cure.utility.CustomAlert;
import com.cure.utility.Utils;
import com.cure.volley.CustomJsonRequest;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends BaseActivity {

    private DigitsAuthButton digitsButton;
    private Context mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
    }


    private void init() {
        setHeader("SIGN UP");
        setLeftClick();
        // DigitsAuthButton digitsButton = (DigitsAuthButton)findViewById(R.id.)
        // digitsButton.setAuthTheme(R.style.CustomDigitsTheme);
        mActivity = SignUpActivity.this;
        setClick(R.id.btn_sign_up);
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btn_sign_up:
                doSignUp(getIntent().getStringExtra("phoneNum"));
                break;

            case R.id.back_btn:
                startActivity(new Intent(mActivity, SplashScreen.class));
                break;
        }
    }


    private void doSignUp(String phoneNum) {
        Utils.hideKeyboard((Activity) mActivity);
        if (Utils.isOnline(mActivity)) {
            if (validateForm()) {
                Map<String, String> params = new HashMap<>();
                params.put("name", getEditTextText(R.id.et_name));
                params.put("email", getEditTextText(R.id.et_user_name));
                params.put("password", getEditTextText(R.id.et_password));
                params.put("role_id", "users");
                params.put("mobile", phoneNum);
                params.put("device", "Android");
                params.put("device_id", CurePreferences.getPushRegistrationId(mActivity));

                final ProgressDialog pdialog = Utils.createProgressDialog(mActivity, null, false);
                CustomJsonRequest postReq = new CustomJsonRequest(Request.Method.POST, Constants.SERVICE_URL + Constants.SIGN_UP_METHOD, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Utils.ShowLog(Constants.TAG, "Response -> " + response.toString());
                                pdialog.dismiss();
                                try {
                                    if (Utils.getWebServiceStatus(response)) {

//                                        Toast.makeText(mActivity, Utils.getWebServiceMessage(response), Toast.LENGTH_LONG).show();
//                                        Intent intent = new Intent(mActivity, MainActivity.class);
//                                        startActivity(intent);
//                                        finish();
                                        if (response.has("redirectTo")) {
                                            //UserDTO userDTO = new Gson().fromJson(response.getJSONObject("loginData").toString(), UserDTO.class);
                                            //CurePreferences.putObjectIntoPref(mActivity, userDTO, Constants.USER_INFO);
                                            Intent intent = new Intent(mActivity, DashboardActivity.class);
                                            intent.putExtra("redirectURL", Utils.getWebServiceURL(response));
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            new CustomAlert(mActivity, mActivity)
                                                    .singleButtonAlertDialog(
                                                            Utils.getWebServiceMessage(response),
                                                            getString(R.string.ok_button),
                                                            "singleBtnCallbackResponse", 1000);

                                        }
                                    } else {
                                        Utils.showDialog(mActivity, "Error", Utils.getWebServiceMessage(response));
                                    }

                                } catch (Exception e) {
                                    pdialog.dismiss();
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pdialog.dismiss();
                        Utils.showExceptionDialog(mActivity);
                    }
                });
                pdialog.show();
                CureApplication.getInstance().getRequestQueue().add(postReq);
                postReq.setRetryPolicy(new DefaultRetryPolicy(
                        30000, 0,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            }
        } else {
            Utils.showNoNetworkDialog(mActivity);
        }


    }


    public boolean validateForm() {

        if (getEditTextText(R.id.et_user_name).equals("")) {
            Utils.showDialog(mActivity, "Message", "Please enter email id");
            return false;
        } else if (getEditTextText(R.id.et_name).equals("")) {
            Utils.showDialog(mActivity, "Message", "Please enter name id");
            return false;
        } else if (getEditTextText(R.id.et_password).equals("")) {
            Utils.showDialog(mActivity, "Message", "Please enter password");
            return false;
        } else if (getEditTextText(R.id.et_confirm_password).equals("")) {
            Utils.showDialog(mActivity, "Message", "Please enter confirm password");
            return false;
        } else if (!getEditTextText(R.id.et_confirm_password).equalsIgnoreCase(getEditTextText(R.id.et_password))) {
            Utils.showDialog(mActivity, "Message", "Please enter valid password");
            return false;
        }

        return true;
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(mActivity, MainActivity.class));

    }

    public void singleBtnCallbackResponse(Boolean flag, int code) {
        if (flag) {
            startActivity(new Intent(mActivity, MainActivity.class));
        }
    }
}
