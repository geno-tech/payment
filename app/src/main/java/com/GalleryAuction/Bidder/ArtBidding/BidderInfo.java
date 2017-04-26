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
import android.widget.TextView;
import android.widget.Toast;

import com.GalleryAuction.Bidder.WinningBidWhether.IamPortWebViewBidding;
import com.GalleryAuction.Bidder.WinningBidWhether.IamPortWebViewRebidding;
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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class BidderInfo extends Activity implements View.OnClickListener {
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";
    Bitmap bmImg;
    Button btn1, btn2;
    ImageView iv;
    EditText et ;
    TextView tv;
    long bidding, min_bidding;
    String artimmage, auckey, bidprice, aucend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallerybidderinfo);
        back task;
        btn1 = (Button)findViewById(R.id.bidding_btn);
        btn2 = (Button)findViewById(R.id.bidding_x_btn);
        et = (EditText)findViewById(R.id.bidding_edt);
        tv = (TextView)findViewById(R.id.bestbidding_txt_bidding);
        task = new back();

        iv = (ImageView)findViewById(R.id.auctionartimage_img);
        Intent intent = getIntent();
        artimmage = intent.getStringExtra("artimage");
        auckey = intent.getStringExtra("auckey");
        aucend = intent.getStringExtra("aucend");
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
                    } else  {
                    Intent intent0 = getIntent();
                    String userID = intent0.getStringExtra("userId");
                    String nowbidding = et.getText().toString();
                    Log.d("aff", userID+"와"+nowbidding);
//                    BiddingInfo(auckey ,userID, nowbidding);

//                    Intent intent = new Intent(BidderInfo.this, BiddingComplete.class);
//                    intent.putExtra("nowbidding" , nowbidding);
//                    intent.putExtra("artimg", artimmage);
//                    intent.putExtra("aucend", aucend);
//                    intent.putExtra("min_bidding", min_bidding);
//                        startActivity(intent);
//                    finish();
                        Intent intent = new Intent(BidderInfo.this, IamPortWebViewBidding.class);
                        intent.putExtra("bidding" , nowbidding);
                        intent.putExtra("artimg", artimmage);
                        intent.putExtra("aucend", aucend);
                        intent.putExtra("min_bidding", min_bidding);
                        intent.putExtra("auckey", auckey);
                        intent.putExtra("userID", userID);
                        startActivity(intent);
                        finish();
                }}

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
    private String BiddingInfoBest(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/bidding_info_best.jsp";

        DefaultHttpClient client = new DefaultHttpClient();
        try {

            HttpPost post = new HttpPost(URL + "?msg=" + msg);
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 30000);
            HttpConnectionParams.setSoTimeout(params, 30000);
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
}
