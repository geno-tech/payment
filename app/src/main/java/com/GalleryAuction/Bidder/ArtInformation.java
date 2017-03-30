package com.GalleryAuction.Bidder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.bill_folder.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ArtInformation extends Activity implements View.OnClickListener{
    Button btn1, btn2;
    String arttitle, arttext, image, artkey, nfcid, artistid, auctionkey, artdate_s, artadte_e, artdate;
    TextView tv1, tv2;
    ImageView imView;
    String imgUrl = "http://59.3.109.220:9998/NFCTEST/art_images/";
    Bitmap bmImg;
    back task;
    DBHelper dbHelper;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartinformation);
        btn1 = (Button)findViewById(R.id.auctionok_btn);
        btn2 = (Button)findViewById(R.id.auctionX_btn);
        tv1 = (TextView)findViewById(R.id.artname_txt);
        tv2 = (TextView)findViewById(R.id.artcontents_txt);
        task = new back();
        imView = (ImageView) findViewById(R.id.imageView);
//        dbHelper = new DBHelper(getApplicationContext(), "MoneyBook.db", null, 1);

        Intent intent = getIntent();
        String artinfo = intent.getStringExtra("artinfo");
        userId = intent.getStringExtra("userID");
        JSONObject job = null;
        try {
            job = new JSONObject(artinfo);
            artkey = job.get("art_seq").toString();
            nfcid = job.get("nfc_id").toString();
            artistid = job.get("artist_id").toString();
            arttitle = job.get("art_title").toString();
            arttext = job.get("art_content").toString();
            image = job.get("art_image").toString();
            auctionkey = job.get("art_seq").toString();
            artdate_s = job.get("art_seq").toString();
            artadte_e = job.get("art_seq").toString();
            artdate = job.get("art_seq").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv1.setText(arttitle);
        tv2.setText(arttext);
        task.execute(imgUrl+image);
        Log.d("ffffff", image);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.auctionok_btn:
                Intent intent = new Intent(ArtInformation.this, BidderInfo.class);
                intent.putExtra("artimage", image);
                startActivity(intent);
                finish();
                break;
            case R.id.auctionX_btn:
                ArtAlbumList(userId, artkey);
//                JSONObject job = null;
//                try {
//                    job = new JSONObject(Artinfo(key));
//                    arttitle = job.get("user_name").toString();
//                    arttext = job.get("user_join_time").toString();
//                    image = job.get("image").toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                dbHelper.insert(arttext,image, arttitle);
//                Log.d("aa",dbHelper.getResult());

                finish();
                break;
        }
    }

    private class back extends AsyncTask<String, Integer,Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try{
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);

            }catch(IOException e){
                e.printStackTrace();
            }
            return bmImg;
        }
        protected void onPostExecute(Bitmap img){
            imView.setImageBitmap(bmImg);
        }
    }
    private void ArtAlbumList(String msg , String msg2) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:9998/NFCTEST/artalbum_insert.jsp";

        DefaultHttpClient client = new DefaultHttpClient();
        try {

            HttpPost post = new HttpPost(URL + "?msg=" + msg + "&msg2=" + msg2);
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 3000);
            HttpResponse response = client.execute(post);
            BufferedReader bufreader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(),
                            "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}