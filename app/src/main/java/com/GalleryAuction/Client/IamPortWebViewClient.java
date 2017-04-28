package com.GalleryAuction.Client;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;

/**
 * Created by GOD on 2017-04-17.
 */

public class IamPortWebViewClient extends WebViewClient {
    private Activity activity;

    public IamPortWebViewClient(Activity activity) {
        this.activity = activity;
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
            Intent intent = null;

            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); //IntentURI처리
                Uri uri = Uri.parse(intent.getDataString());

                activity.startActivity(new Intent(Intent.ACTION_VIEW, uri)); //해당되는 Activity 실행
                return true;
            } catch (URISyntaxException ex) {
                return false;
            } catch (ActivityNotFoundException e) {
                if ( intent == null )   return false;

                String packageName = intent.getPackage();
                if (packageName != null) { //packageName이 있는 경우에는 Google Play에서 검색을 기본
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    return true;
                }

                return false;
            }
        }

        return false;
    }
}
