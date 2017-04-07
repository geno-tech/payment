package com.GalleryAuction.Bidder.ArtBidding;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.geno.bill_folder.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BidderInfo extends Activity implements View.OnClickListener {
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";
    Bitmap bmImg;
    Button btn1, btn2;
    ImageView iv;
    EditText et ;
    String artimmage, auckey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallerybidderinfo);
        back task;
        btn1 = (Button)findViewById(R.id.bidding_btn);
        btn2 = (Button)findViewById(R.id.bidding_x_btn);
        et = (EditText)findViewById(R.id.bidding_edt);
        task = new back();



        iv = (ImageView)findViewById(R.id.auctionartimage_img);
        Intent intent = getIntent();
        artimmage = intent.getStringExtra("artimage");
        auckey = intent.getStringExtra("auckey");
        task.execute(imgUrl+artimmage);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bidding_btn :
                if ( et.getText().toString().length() == 0) {
                    Toast.makeText(this, "입찰할 금액을 입력하시오", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent0 = getIntent();
                    String userID = intent0.getStringExtra("userId");
                    String nowbidding = et.getText().toString();
                    Log.d("aff", userID+"와"+nowbidding);
                    BiddingInfo(auckey ,userID, nowbidding);

                    Intent intent = new Intent(BidderInfo.this, BiddingComplete.class);
                    intent.putExtra("nowbidding" , nowbidding);
                    intent.putExtra("artimg", artimmage);
                    startActivity(intent);
                    finish();
                }

                break;
            case R.id.bidding_x_btn :
                finish();
                break;
        }
    }
// auckey, 유저아이디, 입찰가
    private void BiddingInfo(String msg, String msg2, String msg3) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/biddinginfo_insert.jsp";

        DefaultHttpClient client = new DefaultHttpClient();
        try {

            HttpPost post = new HttpPost(URL + "?msg=" + msg + "&msg2=" + msg2 + "&msg3=" + msg3);
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
