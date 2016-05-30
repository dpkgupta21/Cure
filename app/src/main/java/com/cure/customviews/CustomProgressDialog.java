package com.cure.customviews;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by deepak.gupta on 29-01-2016.
 */
public class CustomProgressDialog {


    private static ProgressDialog progressDialog = null;

    public static void showProgDialog(Activity mActivity, String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setCanceledOnTouchOutside(false);
            if (message == null)
                progressDialog.setMessage("Loading....");
            else
                progressDialog.setMessage(message);
            progressDialog.show();
        }
    }



    public static void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

    }


}