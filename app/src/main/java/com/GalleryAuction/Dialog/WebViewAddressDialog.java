package com.GalleryAuction.Dialog;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.geno.payment.R;

public class WebViewAddressDialog extends Activity {

    private WebView browser;

    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data) {

            Bundle extra = new Bundle();
            Intent intent = new Intent();
            extra.putString("data", data);
            intent.putExtras(extra);
            setResult(RESULT_OK, intent);
            finish();


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gallery_webview_address_dialog);

        browser = (WebView) findViewById(R.id.webView);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "Android");
        WebSettings webSettings = browser.getSettings();
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {

                browser.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        //browser.loadUrl("file:///android_asset/daum.html");
        //browser.loadUrl("http://www.daddyface.com/public/daum.html");
        browser.loadUrl("http://221.156.54.210:8989/NFCTEST/Sample.html");








    }
}
