package com.cure.signup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cure.R;
import com.cure.WakeLocker;
import com.cure.login.MainActivity;
import com.cure.preferences.CurePreferences;
import com.cure.utility.BaseActivity;
import com.cure.utility.CustomAlert;
import com.cure.utility.Utils;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.android.gcm.GCMRegistrar;


import static com.cure.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.cure.CommonUtilities.EXTRA_MESSAGE;
import static com.cure.CommonUtilities.SENDER_ID;
public class SplashScreen extends BaseActivity {

    private Context mActivity;

    private AsyncTask<Void, Void, Void> mRegisterTask;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init();
    }


    private void init() {

        mActivity = SplashScreen.this;
        setClick(R.id.txt_sign_in);
        setClick(R.id.txt_register);

        String pushRegistrationId = CurePreferences.getPushRegistrationId(mActivity);
        if (pushRegistrationId == null || pushRegistrationId.equalsIgnoreCase("")) {
            registrationPushNotification();
        }

    }


    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.txt_sign_in:
                startActivity(new Intent(mActivity, MainActivity.class));
                break;

            case R.id.txt_register:

                new CustomAlert(mActivity, mActivity)
                        .doubleButtonAlertDialog(
                                getString(R.string.are_you),
                                getString(R.string.txt_doctor),
                                getString(R.string.txt_user), "dblBtnCallbackResponse", 1000);

                break;

        }

    }

    public void dblBtnCallbackResponse(Boolean flag, int code) {
        if (flag) {

            startActivity(new Intent(mActivity, AdminSignUp.class));
        } else {
            Digits.getSessionManager().clearActiveSession();
            Digits.authenticate(authCallback, "");
        }
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



    // For Push notification
    private void registrationPushNotification() {
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(mActivity);

        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(mActivity);

        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));

        // Get GCM registration id
        final String regId = GCMRegistrar
                .getRegistrationId(mActivity);

        CurePreferences.setPushRegistrationId(mActivity, regId);
        Log.i("info", "RegId :" + regId);
        // Check if regid already presents
        if (regId.equals("")) {
            Log.i("info", "RegId :" + regId);
            // Registration is not present, register now with GCM
            GCMRegistrar.register(mActivity, SENDER_ID);
        } else {
            // Device is already registered on GCM
            if (GCMRegistrar
                    .isRegisteredOnServer(mActivity)) {
                // Skips registration.
                Log.i("info", "Already registered with GCM");
            } else {
                Log.i("info", "Not registered with GCM");
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
                mRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                mRegisterTask.execute(null, null, null);
            }
        }
    }

    /**
     * Receiving push messages
     */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
            // Waking up mobile if it is sleeping
            WakeLocker.acquire(getApplicationContext());

            /**
             * Take appropriate action on this message depending upon your app
             * requirement For now i am just displaying it on the screen
             * */

            // Showing received message

            // Releasing wake lock
            WakeLocker.release();
        }
    };


    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        try {
            unregisterReceiver(mHandleMessageReceiver);
            GCMRegistrar.onDestroy(mActivity);
        } catch (Exception e) {
            Utils.ShowLog("Cure Application", "UnRegister Receiver Error " + e.getMessage());
        }
        super.onDestroy();
    }


}

