package com.GalleryAuction.UI;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.GalleryAuction.Service.MyService;
import com.geno.payment.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.GalleryAuction.Client.TagInfoClient.toHexString;
import static com.GalleryAuction.Item.HttpClientItem.ArtInfo;
import static com.GalleryAuction.Item.HttpClientItem.AuctionProgress;
import static com.GalleryAuction.Item.HttpClientItem.AuctionProgress_Rank;
import static com.GalleryAuction.Item.HttpClientItem.BiddingCount;
import static com.GalleryAuction.Item.HttpClientItem.BiddingInfoBest;
import static com.GalleryAuction.Item.HttpClientItem.BiddingInfoInsert;

public class BidderInfo extends AppCompatActivity implements View.OnClickListener {
    String imgUrl = "http://183.105.72.65:28989/NFCTEST/art_images/";
    Bitmap bmImg;
    Button btn1, btn2, btn_5, btn_10, btn_50, btn_100;
    ImageView iv;
    EditText et ;
    TextView tv, titlename, titletxt, content_txt, time, rownumtxt;
    long bidding, min_bidding;
    String artimmage, auckey, bidprice, title, name, content, auc_end, bid_count, userID, rownum, bidpriceresult;
    NfcAdapter  nfcAdapter;
    PendingIntent pendingIntent;

    long now, end, ne, dd, nd, HH, nH, mm, ss;
    SimpleDateFormat sdf;
    Date date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallerybidderinfo);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        back task;
        btn1 = (Button)findViewById(R.id.bidding_btn);
        btn2 = (Button)findViewById(R.id.bidding_x_btn);
        btn_5 = (Button)findViewById(R.id.bidding_5btn);
        btn_10 = (Button)findViewById(R.id.bidding_10btn);
        btn_50 = (Button)findViewById(R.id.bidding_50btn);
        btn_100 = (Button)findViewById(R.id.bidding_100btn);
        et = (EditText)findViewById(R.id.bidding_edt);
        tv = (TextView)findViewById(R.id.bestbidding_txt_bidding);
        rownumtxt = (TextView)findViewById(R.id.bidderinfo_lownum_txt);
        titlename = (TextView)findViewById(R.id.bidderinfo_title_name);
        titletxt = (TextView)findViewById(R.id.bidderinfo_title_txt);
        content_txt = (TextView)findViewById(R.id.bidderinfo_content);
        time = (TextView)findViewById(R.id.bidderinfo_time_txt);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        task = new back();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);
        iv = (ImageView)findViewById(R.id.auctionartimage_img);
        Intent intent = getIntent();
        artimmage = intent.getStringExtra("artimage");
        auckey = intent.getStringExtra("auckey");
        title = intent.getStringExtra("title");
        name = intent.getStringExtra("name");
        content = intent.getStringExtra("content");
        auc_end = intent.getStringExtra("auc_end");
        userID = intent.getStringExtra("userId");
        bidpriceresult = intent.getStringExtra("bidpriceresult");
        try {
            date = sdf.parse(auc_end);
            end = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        titlename.setText(title + " - " + name);
        titletxt.setText(title);
        content_txt.setText(content);
        Thread2 thread = new Thread2();
        thread.start();
        try {
            JSONArray ja = new JSONArray(BiddingCount(auckey,"count"));
            for (int i = 0; i < ja.length(); i++) {
                JSONObject job = (JSONObject) ja.get(i);
                bid_count = job.get("bid_count").toString();
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray ja = new JSONArray(AuctionProgress_Rank(userID, auckey));

            for (int i = 0; i < ja.length(); i++) {
                JSONObject job = (JSONObject) ja.get(i);
                rownum = job.get("ROWNUM").toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        rownum = rownum == null ? "응찰하지 않았습니다." : "당신은 현재" + rownum + "번째의 최고가 응찰자입니다.";
                rownumtxt.setText(bid_count+"명/" + rownum);
        try {
            JSONObject job = new JSONObject(BiddingInfoBest(auckey));
            Log.d("bidprice", ""+job);

            bidprice = job.get("bid_price").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        task.execute(imgUrl+artimmage);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_10.setOnClickListener(this);
        btn_50.setOnClickListener(this);
        btn_100.setOnClickListener(this);

        min_bidding = bidprice==null?0:Long.parseLong(bidprice);


        tv.setText("최고 입찰가 : " + currentpoint(String.valueOf(min_bidding)) +"원");
    }

    public static String currentpoint(String result) {

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator(',');

        DecimalFormat df = new DecimalFormat("###,###,###,###");
        df.setDecimalFormatSymbols(dfs);

        try {
            double inputNum = Double.parseDouble(result);
            result = df.format(inputNum).toString();
        } catch (NumberFormatException e) {
            // TODO: handle exception
        }

        return result;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bidding_btn :
                if ( et.getText().toString().length() == 0) {
                    Toast.makeText(this, "입찰할 금액을 입력하시오", Toast.LENGTH_SHORT).show();
                } else {
                    bidding = Long.parseLong(et.getText().toString());

                    long  min_bidding = bidprice==null?0:Long.parseLong(bidprice);

                    if (bidding <= min_bidding) {
                        Toast.makeText(this, "금액이 적습니다", Toast.LENGTH_SHORT).show();
                    } else  if (time.getText() == "마감되었습니다.") {
                        Toast.makeText(this, "Auction이 마감되었습니다.", Toast.LENGTH_SHORT).show();

                    } else {
                    Intent intent0 = getIntent();
                    String userID = intent0.getStringExtra("userId");
                    String nowbidding = et.getText().toString();
                    Log.d("aff", userID+"와"+nowbidding);
//                    BiddingInfo(auckey ,userID, nowbidding);
                        try {
                            JSONObject job = new JSONObject(BiddingInfoInsert(auckey, userID, nowbidding));
                            bidpriceresult = job.get("result").toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("@@@@@@@@@@@@@@@", auckey+ ","+userID + ","+nowbidding + ","+bidpriceresult);
                        if (bidpriceresult.equals("0")) {
                            Toast.makeText(this, "최고구매가를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                        }
                        Intent intent2 = new Intent(this, MyService.class);

                        //서비스 시작시키기
                        startService(intent2);
                    Intent intent = new Intent(BidderInfo.this, BidderInfo.class);
                        intent.putExtra("artimage", artimmage);
                        intent.putExtra("userId", userID);
                        intent.putExtra("auckey", auckey);
                        intent.putExtra("title",  title);
                        intent.putExtra("name", name);
                        intent.putExtra("content", content);
                        intent.putExtra("auc_end", auc_end);
                        startActivity(intent);
                    finish();
//                        Intent intent = new Intent(BidderInfo.this, IamPortWebViewBidding.class);
//                        intent.putExtra("bidding" , nowbidding);
//                        intent.putExtra("artimg", artimmage);
//                        intent.putExtra("min_bidding", min_bidding);
//                        intent.putExtra("auckey", auckey);
//                        intent.putExtra("userID", userID);
//                        Log.d("min_biddddd", ""+min_bidding);
//                        startActivity(intent);
//                        finish();
                }}

                break;
            case R.id.bidding_x_btn :
                finish();
                break;
            case R.id.bidding_5btn :
                if (et.getText().toString().length() == 0) {
                    et.setText(String.valueOf(50000));
                } else {
                    et.setText(String.valueOf((Integer.parseInt(et.getText().toString()) + 50000)));
                }
                break;
            case R.id.bidding_10btn :
                if (et.getText().toString().length() == 0) {
                    et.setText(String.valueOf(100000));
                } else {
                    et.setText(String.valueOf((Integer.parseInt(et.getText().toString()) + 100000)));
                }
                break;
            case R.id.bidding_50btn :
                if (et.getText().toString().length() == 0) {
                    et.setText(String.valueOf(500000));
                } else {
                    et.setText(String.valueOf((Integer.parseInt(et.getText().toString()) + 500000)));
                }
                break;
            case R.id.bidding_100btn :
                if (et.getText().toString().length() == 0) {
                    et.setText(String.valueOf(1000000));
                } else {
                    et.setText(String.valueOf((Integer.parseInt(et.getText().toString()) + 1000000)));
                }
                break;

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
                        time.setText("마감되었습니다.");

                    }
                    else{
                        ne = en/1000; dd = ne/86400; nd = ne%86400; HH = nd/3600; nH = nd%3600; mm = nH/60; ss = nH%60;
                        time.setText("남은시간 : " + HH+"시간" +mm+"분"+ ss +"초" + " 남았습니다.");

                    }
                    break;

                case 1:
                    //thread.stopThread();
                    break;

                default:
                    break;
            }


        }
    };



    class Thread2 extends java.lang.Thread {

        boolean stopped = false;
        int i = 0;

        public Thread2(){
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
            final byte[] tagId = tag.getId();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intent0 = getIntent();
                    String userID = intent0.getStringExtra("userID");
                    Intent intent = new Intent(BidderInfo.this, ArtInformation.class);
                    intent.putExtra("artinfo",ArtInfo(toHexString(tagId)));
                    intent.putExtra("userID",userID);
                    //Log.d("tag",ArtInfo(toHexString(tagId)));
                    startActivity(intent);
                    finish();
                }
            }).start();

        }
    }

}
