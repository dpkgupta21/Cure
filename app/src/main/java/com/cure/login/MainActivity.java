package com.cure.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cure.CureApplication;
import com.cure.R;
import com.cure.dashboard.DashboardActivity;
import com.cure.model.UserDTO;
import com.cure.preferences.CurePreferences;
import com.cure.signup.AdminSignUp;
import com.cure.signup.SignUpActivity;
import com.cure.signup.SplashScreen;
import com.cure.utility.BaseActivity;
import com.cure.utility.Constants;
import com.cure.utility.CustomAlert;
import com.cure.utility.Utils;
import com.cure.volley.CustomJsonRequest;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private Context mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = MainActivity.this;
        init();
    }

    private void init() {

        setClick(R.id.btn_login);
        setClick(R.id.txt_sign_up);
        setHeader("SIGN IN");
        setLeftClick();

    }


    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.txt_sign_up:


                new CustomAlert(mActivity, mActivity)
                        .doubleButtonAlertDialog(
                                getString(R.string.are_you),
                                getString(R.string.txt_doctor),
                                getString(R.string.txt_user), "dblBtnCallbackResponse", 1000);

                break;

            case R.id.btn_login:
                doLogin();
                break;

            case R.id.back_btn:
                startActivity(new Intent(mActivity, SplashScreen.class));
                break;

        }
    }


    private void doLogin() {

        Utils.hideKeyboard((Activity) mActivity);
        if (Utils.isOnline(mActivity)) {
            if (validateForm()) {
                Map<String, String> params = new HashMap<>();
                params.put("email", getEditTextText(R.id.et_user_name));
                params.put("password", getEditTextText(R.id.et_password));
                params.put("device","Android");
                params.put("device_id",CurePreferences.getPushRegistrationId(mActivity));

                final ProgressDialog pdialog = Utils.createProgressDialog(mActivity, null, false);
                CustomJsonRequest postReq = new CustomJsonRequest(Request.Method.POST, Constants.SERVICE_URL + Constants.LOGIN_METHOD, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Utils.ShowLog(Constants.TAG, "Response -> " + response.toString());
                                pdialog.dismiss();
                                try {
                                    if (Utils.getWebServiceStatus(response)) {

                                        UserDTO userDTO = new Gson().fromJson(response.getJSONObject("loginData").toString(), UserDTO.class);
                                        CurePreferences.putObjectIntoPref(mActivity, userDTO, Constants.USER_INFO);
                                        Intent intent = new Intent(mActivity, DashboardActivity.class);
                                        intent.putExtra("redirectURL", Utils.getWebServiceURL(response));
                                        startActivity(intent);
                                        finish();
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
            Utils.showDialog(this, "Message", "Please enter emailid");
            return false;
        } else if (getEditTextText(R.id.et_password).equals("")) {
            Utils.showDialog(this, "Message", "Please enter password");
            return false;
        }
        return true;
    }


    AuthCallback authCallback = new AuthCallback() {
        @Override
        public void success(DigitsSession session, String phoneNumber) {
            if (phoneNumber != null) {

                Intent intent = new Intent(mActivity, SignUpActivity.class);
                intent.putExtra("phoneNum", phoneNumber);
                startActivity(intent);
            }
        }

        @Override
        public void failure(DigitsException exception) {

        }
    };


    public void dblBtnCallbackResponse(Boolean flag, int code) {
        if (flag) {

            startActivity(new Intent(mActivity, AdminSignUp.class));
        } else {
            Digits.getSessionManager().clearActiveSession();
            Digits.authenticate(authCallback, "");
        }
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(mActivity, SplashScreen.class));
    }
}
