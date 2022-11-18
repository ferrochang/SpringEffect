package com.example.sample.sample.myapplication;

import android.app.Activity;
import android.os.Bundle;

import com.example.lib.effect.effect.widget.SpringRelativeLayout;
import com.example.lib.effect.effect.widget.SpringWebView;
import com.example.sample.myapplication.R;


public class WebViewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spring_webview);

        SpringRelativeLayout springRelativeLayout = (SpringRelativeLayout) findViewById(R.id.spring_layout);
        springRelativeLayout.addSpringView(R.id.web_view);
        SpringWebView webview = (SpringWebView) findViewById(R.id.web_view);
        String url = "file:///android_asset/copyright_en.htm";
        webview.getSettings().setBuiltInZoomControls(false);
        webview.loadUrl(url);

        //webview.getSettings().setJavaScriptEnabled(true);
        //webView.setWebViewClient(new WebViewClient()); //不調用系統瀏覽器
        //webview.loadUrl("https://www.qq.com/");

        webview.setEdgeEffectFactory(springRelativeLayout.createViewEdgeEffectFactory());
    }
}
