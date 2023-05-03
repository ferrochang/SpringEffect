package com.example.sample.sample.myapplication

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.example.lib.effect.effect.widget.SpringRelativeLayout
import com.example.lib.effect.effect.widget.SpringWebView
import com.example.sample.myapplication.R

class WebViewActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.spring_webview)
        val springRelativeLayout = findViewById<View>(R.id.spring_layout) as SpringRelativeLayout
        springRelativeLayout.addSpringView(R.id.web_view)
        val webview = findViewById<View>(R.id.web_view) as SpringWebView
        val url = "file:///android_asset/copyright_en.htm"
        webview.settings.builtInZoomControls = false
        webview.loadUrl(url)

        //webview.getSettings().setJavaScriptEnabled(true);
        //webView.setWebViewClient(new WebViewClient()); //not Android System WebView
        //webview.loadUrl("https://www.amazon.com/");
        webview.setEdgeEffectFactory(springRelativeLayout.createViewEdgeEffectFactory())
    }
}