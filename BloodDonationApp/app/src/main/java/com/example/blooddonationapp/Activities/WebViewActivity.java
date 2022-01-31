package com.example.blooddonationapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.blooddonationapp.R;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        webView = findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient()); // to load site in app

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient()); // to play youtube videos

        link = "https://www.google.com/";

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            link = extras.getString("link");
        }

        webView.loadUrl(link);
    }
}