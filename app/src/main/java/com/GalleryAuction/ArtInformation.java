package com.GalleryAuction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geno.bill_folder.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ArtInformation extends Activity implements View.OnClickListener{
    Button btn1, btn2;
    String username, test2, image, key;
    TextView tv1, tv2;
    ImageView imView;
    String imgUrl = "http://59.3.109.220:9998/NFCTEST/art_images/";
    Bitmap bmImg;
    back task;

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

        Intent intent = getIntent();
        String tagid = intent.getStringExtra("tagid").toString();
        JSONObject job = null;
        try {
            job = new JSONObject(tagid);
            username = job.get("user_name").toString();
            test2 = job.get("user_join_time").toString();
            image = job.get("image").toString();
            key = job.get("test_seq").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv1.setText(username);
        tv2.setText(test2);
        task.execute(imgUrl+image);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.auctionok_btn:
                Intent intent = new Intent(ArtInformation.this, BidderInfo.class);

                startActivity(intent);
                finish();
                break;
            case R.id.auctionX_btn:
                Intent intent2 = new Intent(ArtInformation.this, ArtInfoTagList.class);
                intent2.putExtra("key", key);
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


}