package com.GalleryAuction.Bidder.ArtBidding;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geno.bill_folder.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.GalleryAuction.Bidder.ArtBidding.BidderInfo.currentpoint;

public class BiddingComplete extends Activity {
    String nowbidding, aucend, artimg;
    TextView nowbid_txt, tv2, tv3;
    Button btn;
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";
    Bitmap bmImg;
    ImageView iv;
    long now, end, ne, dd, nd, HH, nH, mm, ss, min_bidding;
    SimpleDateFormat sdf;
    Date date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_biddingcomplete);
        btn = (Button)findViewById(R.id.biddingcomplate_ok_btn);
        iv = (ImageView)findViewById(R.id.auctionartimage2_img);
        nowbid_txt = (TextView)findViewById(R.id.nowbidding_tv);
        tv2 = (TextView)findViewById(R.id.biddingtime_txt);
        tv3 = (TextView)findViewById(R.id.bestbidding_tv);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Intent intent = getIntent();
        nowbidding = intent.getStringExtra("nowbidding");
        artimg = intent.getStringExtra("artimg");
        aucend = intent.getStringExtra("aucend");
        min_bidding = intent.getLongExtra("min_bidding", min_bidding);
        Log.d("aucend", aucend);
        nowbid_txt.setText(nowbidding + "원");
        back task = new back();
        task.execute(imgUrl+artimg);
        now = System.currentTimeMillis();
        try {
            date = sdf.parse(aucend);
            end = date.getTime();
            Log.d("end", "" + end);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv3.setText("최고 입찰가 : " + currentpoint(String.valueOf(min_bidding)) +"원");
        Thread thread = new Thread();
        thread.start();
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
    final Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 0:
                    now = System.currentTimeMillis();
                    long en = end-now;
                    if(en < 0){
                        ne = -(en)/1000; dd = ne/86400; nd = ne%86400; HH = nd/3600; nH = nd%3600; mm = nH/60; ss = nH%60;
                        tv2.setText("남은시간 : " + dd+"일" + HH+"시" +mm+"분"+ ss +"초" +" 지났습니다.");

                    }
                    else{
                        ne = en/1000; dd = ne/86400; nd = ne%86400; HH = nd/3600; nH = nd%3600; mm = nH/60; ss = nH%60;
                        tv2.setText("남은시간 : " + dd+"일" + HH+"시" +mm+"분"+ ss +"초" + " 남았습니다.");

                    }

                    break;

                case 1:
                    //thread.stopThread();
                    tv2.setText("Thread 가 중지됨.");
                    break;

                default:
                    break;
            }


        }
    };
    class Thread extends java.lang.Thread {

        boolean stopped = false;
        int i = 0;

        public Thread(){
            stopped = false;
        }

        public void stopThread() {
            stopped = true;
        }

        @Override
        public void run() {
            super.run();

            while(stopped == false) {
                i++;

                // 메시지 얻어오기
                Message message = handler.obtainMessage();

                // 메시지 ID 설정
                message.what = 0;

                // 메시지 내용 설정 (int)
                message.arg1 = i;

                // 메시지 내용 설정 (Object)
                String information = new String("초 째 Thread 동작 중입니다.");
                message.obj = information;

                // 메시지 전
                handler.sendMessage(message);

                try {
                    // 1초 씩 딜레이 부여
                    sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }
}
