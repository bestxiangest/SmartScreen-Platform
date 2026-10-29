package com.example.campusscreen;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {
    
    private WebView webView;
    private TextView titleTextView;
    private ImageView backButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        
        initViews();
        setupWebView();
        loadUrl();
    }
    
    private void initViews() {
        webView = findViewById(R.id.webview);
        titleTextView = findViewById(R.id.tv_webview_title);
        backButton = findViewById(R.id.btn_back);
        
        // 设置返回按钮点击事件
        backButton.setOnClickListener(v -> {
            finish(); // 关闭当前Activity，返回到基地动态界面
        });
    }
    
    private void setupWebView() {
        // 启用JavaScript
        webView.getSettings().setJavaScriptEnabled(true);
        
        // 设置WebView客户端
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        
        // 启用缩放功能
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        
        // 设置用户代理
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36");
    }
    
    private void loadUrl() {
        // 从Intent获取URL和标题
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        
        if (url != null) {
            webView.loadUrl(url);
        }
        
        if (title != null && titleTextView != null) {
            titleTextView.setText(title);
        }
    }
    
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}