package com.GalleryAuction.Bidder.ArtTag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.GalleryAuction.Bidder.ArtBidding.BidderInfo;
import com.GalleryAuction.Bidder.DBHelper;
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
    String arttitle, arttext, image, artkey, nfcid, artistid, auckey ;
    TextView tv1, tv2;
    ImageView imView;
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";

    Bitmap bmImg;
    back task;
    String userId, artinfo;
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
        artinfo = intent.getStringExtra("artinfo");
        //Log.d("HH", artinfo);
        userId = intent.getStringExtra("userID");

                  JSONObject job = null;

            try {
                job = new JSONObject(artinfo);
                Log.d("SS", ""+job);

                artkey = job.get("art_seq").toString();
                nfcid = job.get("nfc_id").toString();
                artistid = job.get("artist_id").toString();
                arttitle = job.get("art_title").toString();
                Log.d("art_content : ",""+job.get("art_content"));
                if(job.get("art_content") != null && job.get("art_content").toString().equals("")) {
                    arttext = job.get("art_content").toString();
                }
                else {
                    arttext = "값없음";
                }

                image = job.get("art_image").toString();
                auckey = job.get("auc_seq").toString();


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException n) {
                n.printStackTrace();
                Toast.makeText(ArtInformation.this, "다시시도하세요", Toast.LENGTH_SHORT).show();
            }
        tv1.setText(arttitle);
        tv2.setText(arttext);
        task.execute(imgUrl+image);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.auctionok_btn:
                Log.d("auckey", auckey);
                if (auckey == "null" || auckey.equals(null)) {
                    Toast.makeText(ArtInformation.this, "경매하고 있지 않습니다.", Toast.LENGTH_SHORT).show();
                    ArtAlbumList(userId, artkey);
                } else {
                    Intent intent = new Intent(ArtInformation.this, BidderInfo.class);
                    intent.putExtra("artimage", image);
                    intent.putExtra("userId", userId);
                    intent.putExtra("auckey", auckey);
                    startActivity(intent);
                    ArtAlbumList(userId, artkey);
                    finish();
                }
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

        String URL = "http://59.3.109.220:8989/NFCTEST/artalbum_insert.jsp";

        DefaultHttpClient client = new DefaultHttpClient();
        try {

            HttpPost post = new HttpPost(URL + "?msg=" + msg + "&msg2=" + msg2);
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 30000);
            HttpConnectionParams.setSoTimeout(params, 30000);
            HttpResponse response = client.execute(post);
            BufferedReader bufreader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(),
                            "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
