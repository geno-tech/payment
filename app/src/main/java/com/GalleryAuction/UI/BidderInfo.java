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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.bill_folder.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static com.GalleryAuction.Client.TagInfoClient.toHexString;
import static com.GalleryAuction.Item.HttpClientItem.ArtInfo;
import static com.GalleryAuction.Item.HttpClientItem.BiddingInfoBest;

public class BidderInfo extends Activity implements View.OnClickListener {
    String imgUrl = "http://221.156.54.210:8989/NFCTEST/art_images/";
    Bitmap bmImg;
    Button btn1, btn2;
    ImageView iv;
    EditText et ;
    TextView tv;
    long bidding, min_bidding;
    String artimmage, auckey, bidprice;
    NfcAdapter  nfcAdapter;
    PendingIntent pendingIntent;
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
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);
        iv = (ImageView)findViewById(R.id.auctionartimage_img);
        Intent intent = getIntent();
        artimmage = intent.getStringExtra("artimage");
        auckey = intent.getStringExtra("auckey");
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
                        intent.putExtra("min_bidding", min_bidding);
                        intent.putExtra("auckey", auckey);
                        intent.putExtra("userID", userID);
                        Log.d("min_biddddd", ""+min_bidding);
                        startActivity(intent);
                        finish();
                }}

                break;
            case R.id.bidding_x_btn :
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
