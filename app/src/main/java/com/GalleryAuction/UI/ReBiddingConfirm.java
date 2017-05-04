package com.GalleryAuction.UI;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.GalleryAuction.Adapter.ArtistAuctionDetailAdapter;
import com.geno.bill_folder.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReBiddingConfirm extends Activity {
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";
    Bitmap bmImg;
    ImageView imView;
    TextView tv, tv2;
    String aucend;
    ArtistAuctionDetailAdapter artistAuctionAdapter = new ArtistAuctionDetailAdapter();
    long now, end, ne, dd, nd, HH, nH, mm, ss;
    Thread thread;
    Date date;
    SimpleDateFormat sdf;
    NfcAdapter  nfcAdapter;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_rebidding_confirm);
        tv = (TextView)findViewById(R.id.rebiddingconfirm_txt);
        tv2 = (TextView)findViewById(R.id.rebiddingconfirm_time_txt);
        Button btn = (Button)findViewById(R.id.auctionview_exit_btn);
        imView = (ImageView)findViewById(R.id.clickconfirm_img);
        Intent intent = getIntent();
        String rebidding = intent.getStringExtra("rebidding");
        String image = intent.getStringExtra("image");
        end = intent.getLongExtra("end", end);
        now = System.currentTimeMillis();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);
//        try {
//            date = sdf.parse(aucend);
//            end = date.getTime();
//
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        Log.d("end : ", ""+end);
        tv.setText(artistAuctionAdapter.currentpoint(rebidding) + "원을 재입찰하였습니다.");
        back task = new back();
        task.execute(imgUrl + image);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        thread = new Thread();
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
            imView.setImageBitmap(bmImg);
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
                        tv2.setText(dd+"일" + HH+"시" +mm+"분"+ ss +"초" +" 지났습니다.");

                    }
                    else{
                        ne = en/1000; dd = ne/86400; nd = ne%86400; HH = nd/3600; nH = nd%3600; mm = nH/60; ss = nH%60;
                        tv2.setText(dd+"일" + HH+"시" +mm+"분"+ ss +"초" + " 남았습니다.");

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

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            Toast.makeText(this, "[갤러리옥션 - 태그하기]에서 태그하시오", Toast.LENGTH_SHORT).show();
        }
        Log.d("TAGTEST : ", ""+ tag);
    }
}
