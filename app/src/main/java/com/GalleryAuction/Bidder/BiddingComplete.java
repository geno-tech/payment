package com.GalleryAuction.Bidder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geno.bill_folder.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BiddingComplete extends Activity {
    String nowbidding;
    TextView nowbid_txt;
    Button btn;
    String imgUrl = "http://59.3.109.220:9998/NFCTEST/art_images/";
    Bitmap bmImg;
    ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_biddingcomplete);
        btn = (Button)findViewById(R.id.biddingcomplate_ok_btn);
        iv = (ImageView)findViewById(R.id.auctionartimage2_img);
        nowbid_txt = (TextView)findViewById(R.id.nowbidding_tv);
        Intent intent = getIntent();
        nowbidding = intent.getStringExtra("nowbidding");
        nowbid_txt.setText(nowbidding + "원");
        back task = new back();
        Intent intent2 = getIntent();
        String artimg = intent2.getStringExtra("artimg");
        task.execute(imgUrl+artimg);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
            iv.setImageBitmap(bmImg);
        }
    }
}
