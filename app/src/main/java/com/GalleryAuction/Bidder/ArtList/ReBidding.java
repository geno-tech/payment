package com.GalleryAuction.Bidder.ArtList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.GalleryAuction.Artist.AuctionList.ArtistAuctionAdapter;
import com.GalleryAuction.Artist.AuctionList.ArtistAuctionDetailAdapter;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReBidding extends Activity implements View.OnClickListener {
    Button btn_ok, btn_x;
    String bidprice, userID, image, auckey, aucend;
    TextView tv1,tv2, tv3;
    Bitmap bmImg;
    ImageView imView;
    EditText et;
    Thread thread;
    SimpleDateFormat sdf;
    long now, end, ne, dd, nd, HH, nH, mm, ss;
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";
    Date date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_rebidding);
        btn_ok = (Button)findViewById(R.id.rebidding);
        btn_x = (Button)findViewById(R.id.rebidding_x);
        tv1 = (TextView) findViewById(R.id.nowbidding_tv2);
        tv2 = (TextView) findViewById(R.id.bestbidding_tv2);
        tv3 = (TextView)findViewById(R.id.rebiddingcountingtime_tv);
        imView = (ImageView)findViewById(R.id.auctionartimage2_img);
        et = (EditText)findViewById(R.id.rebidding_edt);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Log.d("now", ""+now);
        back task = new back();
        btn_ok.setOnClickListener(this);
        btn_x.setOnClickListener(this);
        Intent intent = getIntent();
        auckey = intent.getStringExtra("auckey");
        userID = intent.getStringExtra("userID");
        image = intent.getStringExtra("image");
        aucend = intent.getStringExtra("aucend");
        now = System.currentTimeMillis();
        try {
            date = sdf.parse(aucend);
            end = date.getTime();
            Log.d("end", "" + end);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        task.execute(imgUrl + image);
        Log.d("bidkey", userID);

        Log.d("time", ""+ne);
        Log.d("time", ""+ dd+"일" + HH+"시" +mm+"분"+ ss +"초");


        try {
            JSONObject job = new JSONObject(BiddingInfo(userID, auckey));
            //Log.d("SS", ""+job);

            bidprice = job.get("bid_price").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArtistAuctionDetailAdapter artistAuctionAdapter = new ArtistAuctionDetailAdapter();
        tv1.setText(artistAuctionAdapter.currentpoint(bidprice) + "원");
        Log.d("bidprice", bidprice);

        if (dd ==0 && HH == 0 && mm == 0 && ss ==0 ) {
            handler.sendEmptyMessage(1);
        }
        thread = new Thread();
        thread.start();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rebidding :
                int bidprice_int = Integer.parseInt(bidprice);
                int rebiddinStr = Integer.parseInt(et.getText().toString());
                Log.w("안녕", bidprice_int + ", " + rebiddinStr);
                if (rebiddinStr <= bidprice_int) {
                Toast.makeText(ReBidding.this, "금액이 적습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Bidding_Insert(auckey, userID, et.getText().toString());
                    Intent intent = new Intent(ReBidding.this, ReBiddingConfirm.class);
                    intent.putExtra("rebidding", et.getText().toString());
                    intent.putExtra("image", image);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.rebidding_x :
                finish();
        }
    }
//UserID auckey
    private String BiddingInfo(String msg, String msg2) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/bidding_info.jsp";

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

    // auckey, 유저아이디, 입찰가
    private void Bidding_Insert(String msg, String msg2, String msg3) {
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
    //auckey
    private String BiddingInfoBest(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/bidding_info_best.jsp";

            DefaultHttpClient client = new DefaultHttpClient();
            try {

                HttpPost post = new HttpPost(URL + "?msg=" + msg);
                HttpParams params = client.getParams();
                HttpConnectionParams.setConnectionTimeout(params, 3000);
                HttpConnectionParams.setSoTimeout(params, 3000);
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
                        tv3.setText(dd+"일" + HH+"시" +mm+"분"+ ss +"초" +" 지났습니다.");

                    }
                    else{
                        ne = en/1000; dd = ne/86400; nd = ne%86400; HH = nd/3600; nH = nd%3600; mm = nH/60; ss = nH%60;
                        tv3.setText(dd+"일" + HH+"시" +mm+"분"+ ss +"초" + " 남았습니다.");

                    }

                    break;

                case 1:
                   //thread.stopThread();
                   tv3.setText("Thread 가 중지됨.");
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