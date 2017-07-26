package com.GalleryAuction.UI;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
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
import com.geno.payment.R;

import static com.GalleryAuction.Item.HttpClientItem.BiddingInfoInsert;
import static com.GalleryAuction.Item.HttpClientItem.BiddingWinArtistAgree;

public class IamPortWebViewBidding extends Activity {
    private WebView mainWebView;
    private final String APP_SCHEME = "iamportkakao://";
    private TextView txt;
    private String title, image, auckey, aucstatus, artistname, bidprice, userID, bidkey, biddername, bidderhp;
    NfcAdapter  nfcAdapter;
    PendingIntent pendingIntent;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_iamport_webview);

        intent = getIntent();
        title = intent.getStringExtra("title");
        image = intent.getStringExtra("image");
        auckey = intent.getStringExtra("auckey");
        artistname = intent.getStringExtra("artistname");
        bidprice = intent.getStringExtra("bidprice");
        userID = intent.getStringExtra("userID");
        bidkey = intent.getStringExtra("bidkey");
        biddername = intent.getStringExtra("biddername");
        bidderhp = intent.getStringExtra("bidderhp");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);

        mainWebView = (WebView) findViewById(R.id.mainWebView);
        mainWebView.setWebViewClient(new IamPortWebViewClient(this));
        WebSettings settings = mainWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mainWebView.loadUrl("http://221.156.54.210:8989/NFCTEST/iamport.jsp?name=" +biddername + "&art_name=" + title+ "&hp_num=" + bidderhp+ "&price=" + bidprice);

        mainWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(final WebView view,
                                     final String url, final String message,
                                     JsResult result) {
                if (message.contains("완료")) {
                    aucstatus ="7";
                    Intent intent1 = new Intent(IamPortWebViewBidding.this, BidderAddress.class);
                    intent1.putExtra("title", title);
                    intent1.putExtra("image", image);
                    intent1.putExtra("auckey", auckey);
                    intent1.putExtra("aucstatus", aucstatus);
                    intent1.putExtra("artistname", artistname);
                    intent1.putExtra("bidprice", bidprice);
                    intent1.putExtra("userID", userID);
                    intent1.putExtra("bidkey",bidkey);
                    intent1.putExtra("biddername",biddername);
                    intent1.putExtra("bidderhp",bidderhp);

                    startActivity(intent1);
                    Toast.makeText(IamPortWebViewBidding.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                } else if (message.contains("실패")) {
                    Log.d("aa", "onJsAlert(!" + view + ", " + url + ", "
                            + message + ", " + result + ")");
                    aucstatus = "6";
                    Intent intent1 = new Intent(IamPortWebViewBidding.this, BidderAddress.class);
                    intent1.putExtra("title", title);
                    intent1.putExtra("image", image);
                    intent1.putExtra("auckey", auckey);
                    intent1.putExtra("aucstatus", aucstatus);
                    intent1.putExtra("artistname", artistname);
                    intent1.putExtra("bidprice", bidprice);
                    intent1.putExtra("userID", userID);
                    intent1.putExtra("bidkey",bidkey);
                    intent1.putExtra("biddername",biddername);
                    intent1.putExtra("bidderhp",bidderhp);
                    startActivity(intent1);
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
        setIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            Toast.makeText(this, "[갤러리옥션 - 태그하기]에서 태그하시오", Toast.LENGTH_SHORT).show();
        }
        Log.d("TAGTEST : ", ""+ tag);
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
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
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
