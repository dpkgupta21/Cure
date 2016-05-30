package com.cure.signup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cure.R;
import com.cure.utility.BaseActivity;
import com.cure.utility.Utils;

public class AdminSignUp extends BaseActivity {


    private WebView webView;
    private Context mActivity;
    private ProgressDialog pdialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_up);
        init();
    }


    private void init() {
        mActivity = AdminSignUp.this;
        webView = (WebView) findViewById(R.id.web_view_admin_sign_up);

        loadWebView("http://cureadviser.com/doctor/signup");
    }


    private void loadWebView(String url) {


        webView.setWebChromeClient(new WebChromeClient()

        {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (pdialog == null) {
                    pdialog = Utils.createProgressDialog(mActivity, null, false);
                    pdialog.show();
                }
                if (progress == 100) {
                    pdialog.dismiss();
                    pdialog = null;
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
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
            startActivity(new Intent(mActivity, SplashScreen.class));
        }
    }


}
