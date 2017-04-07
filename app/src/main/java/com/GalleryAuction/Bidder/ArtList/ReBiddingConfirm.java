package com.GalleryAuction.Bidder.ArtList;

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

import com.GalleryAuction.Artist.AuctionList.ArtistAuctionDetailAdapter;
import com.geno.bill_folder.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReBiddingConfirm extends Activity {
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";
    Bitmap bmImg;
    ImageView imView;
    ArtistAuctionDetailAdapter artistAuctionAdapter = new ArtistAuctionDetailAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_rebidding_confirm);
        TextView tv = (TextView)findViewById(R.id.rebiddingconfirm_txt);
        Button btn = (Button)findViewById(R.id.auctionview_exit_btn);
        imView = (ImageView)findViewById(R.id.clickconfirm_img);
        Intent intent = getIntent();
        String rebidding = intent.getStringExtra("rebidding");
        String image = intent.getStringExtra("image");
        tv.setText(artistAuctionAdapter.currentpoint(rebidding) + "원을 재입찰하였습니다.");
        back task = new back();
        task.execute(imgUrl + image);
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
            imView.setImageBitmap(bmImg);
        }
    }
}
