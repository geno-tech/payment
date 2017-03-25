package com.GalleryAuction;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URL;

/**
 * Created by GOD on 2017-03-21.
 */

public class HttpPingAsyncTask extends AsyncTask<URL, Void, Void> {

    public void reqGET(URL url) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(String.valueOf(url));
            HttpResponse resGET = client.execute(get);
            HttpEntity resEntityGET = resGET.getEntity();

            if(resEntityGET != null) {
                Log.i("httpRequest", EntityUtils.toString(resEntityGET));
            }

        } catch(Exception e){
            e.printStackTrace();
        }
        Log.i("httpRequest", "conn");
    }

    @Override
    protected Void doInBackground(URL ... params) {
        reqGET(params[0]);
        return null;
    }
}