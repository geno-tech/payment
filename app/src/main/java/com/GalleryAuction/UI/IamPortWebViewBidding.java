package com.GalleryAuction.UI;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.GalleryAuction.Client.IamPortWebViewClient;
import com.geno.bill_folder.R;

import static com.GalleryAuction.Item.HttpClientItem.BiddingInfoInsert;

public class IamPortWebViewBidding extends Activity {
    private WebView mainWebView;
    private final String APP_SCHEME = "iamportkakao://";
    private TextView txt;
    private String auckey, userID, nowbidding, artimg, aucend, min_bidding, bidding, image, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_iamport_webview);

        Intent intent1 = getIntent();
        bidding = intent1.getStringExtra("bidding");
        intent1.getStringExtra("artimg");
        intent1.getStringExtra("aucend");
        intent1.getStringExtra("min_bidding");
        auckey = intent1.getStringExtra("auckey");
        userID = intent1.getStringExtra("userID");


        mainWebView = (WebView) findViewById(R.id.mainWebView);
        mainWebView.setWebViewClient(new IamPortWebViewClient(this));
        WebSettings settings = mainWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mainWebView.loadUrl("http://59.3.109.220:8989/NFCTEST/iamport.jsp");
        mainWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(final WebView view,
                                     final String url, final String message,
                                     JsResult result) {
                if (message.contains("완료")) {
                    BiddingInfoInsert(auckey, userID, bidding);
                    Toast.makeText(IamPortWebViewBidding.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                } else if (message.contains("실패")) {
                    Log.d("aa", "onJsAlert(!" + view + ", " + url + ", "
                            + message + ", " + result + ")");
                    finish();
                    Toast.makeText(IamPortWebViewBidding.this, message, Toast.LENGTH_SHORT).show();
                }
                result.confirm();
                return true; // I handled it
            }
        });

        if (mainWebView.canGoBack()) {
            finish();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if ( intent != null ) {
            Uri intentData = intent.getData();

            if ( intentData != null ) {
                //카카오페이 인증 후 복귀했을 때 결제 후속조치
                String url = intentData.toString();

                if ( url.startsWith(APP_SCHEME) ) {
                    String path = url.substring(APP_SCHEME.length());
                    if ( "process".equalsIgnoreCase(path) ) {
                        mainWebView.loadUrl("javascript:IMP.communicate({result:'process'})");
                    } else {
                        mainWebView.loadUrl("javascript:IMP.communicate({result:'cancel'})");
                    }
                }
            }
        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
//                if (mainWebView.canGoBack()){
//                    mainWebView.goBack();
//                } else {
//
//                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
