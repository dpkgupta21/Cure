package com.cure.dashboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cure.R;
import com.cure.customviews.CustomProgressDialog;
import com.cure.login.MainActivity;
import com.cure.utility.BaseActivity;
import com.cure.utility.CustomAlert;
import com.cure.utility.Utils;

public class DashboardActivity extends BaseActivity {

    private Activity mActivity;
    //private ProgressDialog pdialog;
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        init();
    }


    private void init() {

        mActivity = DashboardActivity.this;
        webView = (WebView) findViewById(R.id.web_view);
        String url = getIntent().getStringExtra("redirectURL");
        CustomProgressDialog.showProgDialog(mActivity, null);
        loadWebView(url);
    }


    private void loadWebView(String url) {


//        webView.setWebChromeClient(new WebChromeClient()
//
//        {
//            @Override
//            public void onProgressChanged(WebView view, int progress) {
//                if (pdialog == null) {
//                    pdialog = Utils.createProgressDialog(mActivity, null, false);
//                    pdialog.show();
//                }
//                if (progress == 100) {
//                    pdialog.dismiss();
//                    pdialog = null;
//                }
//            }
//        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                CustomProgressDialog.hideProgressDialog();
                super.onPageFinished(view, url);
            }

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);
                return true;
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            //callLogout();
        }
    }


    private void callLogout() {
        new CustomAlert(mActivity, mActivity)
                .doubleButtonAlertDialog(
                        getString(R.string.you_logout),
                        getString(R.string.ok_button),
                        getString(R.string.canceled), "dblBtnCallbackResponse", 1000);
    }


    public void dblBtnCallbackResponse(Boolean flag, int code) {
        if (flag) {
            startActivity(new Intent(mActivity, MainActivity.class));
        }
    }
}


