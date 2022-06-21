package com.brandshaastra.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.brandshaastra.R;
import com.brandshaastra.databinding.ActivityPrivacyBinding;
import com.brandshaastra.interfaces.Consts;
import com.brandshaastra.network.NetworkManager;

public class PrivacyActivity extends AppCompatActivity {

    ActivityPrivacyBinding binding;
    String WEBVIEW_TYPE ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(PrivacyActivity.this,R.layout.activity_privacy);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        WEBVIEW_TYPE = getIntent().getStringExtra(Consts.WEBVIEW_TYPE);
        binding.rlclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.mWebView.setWebViewClient(new MyBrowser());
        binding.mWebView.getSettings().setLoadsImagesAutomatically(true);
        binding.mWebView.getSettings().setJavaScriptEnabled(true);

        if (WEBVIEW_TYPE.equalsIgnoreCase("privacy")){

            if (NetworkManager.isConnectToInternet(this)) {

                binding.mWebView.loadUrl(Consts.BASE_URL+Consts.PRIVACY);
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
            }

        }else {
            if (NetworkManager.isConnectToInternet(this)) {

                binding.mWebView.loadUrl(Consts.BASE_URL+Consts.TERMS_URL);
            } else {
                Toast.makeText(this, getResources().getString(R.string.internet_concation), Toast.LENGTH_SHORT).show();
            }

        }

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}