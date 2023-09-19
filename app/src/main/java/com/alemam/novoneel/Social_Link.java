package com.alemam.novoneel;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class Social_Link extends AppCompatActivity {

    WebView webView;
    LottieAnimationView animationView;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_link);
        webView = findViewById(R.id.webView);
        animationView = findViewById(R.id.animationView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUserAgentString("Android");

        Bundle bundle = getIntent().getExtras();
        String UrlKey = bundle.getString("key");

        if(bundle!=null)
        {
            if (UrlKey.equals("github")) {
                 url = "https://www.github.com/alemam242/";
                animationView.setAnimation(R.raw.github);
            }


            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    setProgressBarVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    setProgressBarVisibility(View.GONE);
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    setProgressBarVisibility(View.GONE);
                }
            });

            webView.loadUrl(url);
        }
    }

    private void setProgressBarVisibility(int visibility) {
        if (animationView != null) {
            animationView.setVisibility(visibility);
        }
    }

    @Override
    public void onBackPressed() {

        if(webView.canGoBack())
        {
            webView.goBack();
        }
        else {
            super.onBackPressed();
        }
    }
}