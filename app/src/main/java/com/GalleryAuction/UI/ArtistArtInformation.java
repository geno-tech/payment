package com.GalleryAuction.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ListView;

import com.GalleryAuction.Adapter.ArtistArtInfoAdapter;
import com.geno.bill_folder.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArtistArtInformation extends Activity {
    ListView listView;
    ArtistArtInfoAdapter adapter;
    String artistID, title, nowtime , image;
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_artist_artinformation);
        Intent intent = getIntent();
        artistID = intent.getStringExtra("artistID");

        adapter = new ArtistArtInfoAdapter();
        listView = (ListView)findViewById(R.id.artistartlist_listview);
        listView.setAdapter(adapter);
        try {
            JSONArray ja = new JSONArray(ArtistArtInfo(artistID));

            for (int i = 0 ; i  < ja.length(); i++){

                JSONObject job = (JSONObject) ja.get(i);
                title = job.get("art_title").toString();
                nowtime = job.get("art_regit_date").toString();
                image = job.get("art_image").toString();
                adapter.addItem(title, nowtime , getImageBitmap(imgUrl+image));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String ArtistArtInfo(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/art_info_artistid_list.jsp";

        DefaultHttpClient client = new DefaultHttpClient();
        try {

            HttpPost post = new HttpPost(URL + "?msg=" + msg);
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 30000);
            HttpConnectionParams.setSoTimeout(params, 30000);
            HttpResponse response = client.execute(post);
            BufferedReader bufreader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(),
                            "utf-8"));

            String line = null;
            String result = "";

            while ((line = bufreader.readLine()) != null) {
                result += line;

            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Bitmap getImageBitmap(final String imageURL){
        final Bitmap[] bitmapImage = new Bitmap[1];
        Thread mThread = new Thread(){
            @Override
            public void run() {

                try {
                    URL url = new URL(imageURL); // URL 주소를 이용해서 URL 객체 생성

                    //  아래 코드는 웹에서 이미지를 가져온 뒤
                    //  이미지 뷰에 지정할 Bitmap을 생성하는 과정

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmapImage[0] = BitmapFactory.decodeStream(is);

                } catch(IOException ex) {
                    ex.getMessage();
                }
            }

        };
        mThread.start();
        try {
            mThread.join();

        }
        catch (InterruptedException e){
            e.getMessage();
        }
        return bitmapImage[0];
    }
}
