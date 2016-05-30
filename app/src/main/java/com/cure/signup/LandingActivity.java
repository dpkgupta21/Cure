package com.cure.signup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cure.CureApplication;
import com.cure.R;
import com.cure.dashboard.DashboardActivity;
import com.cure.model.CheckLoginDTO;
import com.cure.model.UserDTO;
import com.cure.preferences.CurePreferences;
import com.cure.utility.BaseActivity;
import com.cure.utility.Constants;
import com.cure.utility.Utils;
import com.cure.volley.CustomJsonRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LandingActivity extends BaseActivity {

    private Context mActivity;
    private long splashDelay = 3000;
    private Timer timer;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        mActivity = LandingActivity.this;
        checkLogin();
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        };
//        timer = new Timer();
//        timer.schedule(task, splashDelay);
    }

    private void checkLogin() {

        Utils.hideKeyboard((Activity) mActivity);
        if (Utils.isOnline(mActivity)) {
            Map<String, String> params = new HashMap<>();
            params.put("deviceId", CurePreferences.getPushRegistrationId(mActivity));

            final ProgressDialog pdialog = Utils.createProgressDialog(mActivity, null, false);
            CustomJsonRequest postReq = new CustomJsonRequest(Request.Method.POST, Constants.SERVICE_URL +
                    Constants.CHECK_LOGIN_METHOD, params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Utils.ShowLog(Constants.TAG, "Response -> " + response.toString());
                            pdialog.dismiss();
                            try {

                                CheckLoginDTO userDTO = new Gson().fromJson(response.toString(), CheckLoginDTO.class);
                                if(userDTO.isStatus()) {
                                    Intent intent = new Intent(mActivity, DashboardActivity.class);
                                    intent.putExtra("redirectURL", userDTO.getRedirectTo());
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Intent i = new Intent(mActivity, SplashScreen.class);
                                    startActivity(i);
                                    finish();
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
                    Intent i = new Intent(mActivity, SplashScreen.class);
                    startActivity(i);
                    finish();
                    //Utils.showExceptionDialog(mActivity);
                }
            });
            pdialog.show();
            CureApplication.getInstance().getRequestQueue().add(postReq);
            postReq.setRetryPolicy(new DefaultRetryPolicy(
                    30000, 0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        } else {
            Utils.showNoNetworkDialog(mActivity);
        }


    }

}
