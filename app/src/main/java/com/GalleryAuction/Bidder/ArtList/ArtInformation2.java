package com.GalleryAuction.Bidder.ArtList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.GalleryAuction.Bidder.ArtBidding.BidderInfo;
import com.GalleryAuction.Bidder.ArtTag.ArtInformation;
import com.GalleryAuction.Bidder.DBHelper;
import com.GalleryAuction.Bidder.WinningBidWhether.WinningBidUi;
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


public class ArtInformation2 extends Activity implements View.OnClickListener{
    Button btn1, btn2;
    String arttitle, arttext, image, artkey, nfcid, artistid, auckey, auction, bidkey, aucend ;
    TextView tv1, tv2;
    ImageView imView;
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";

    Bitmap bmImg;
    back task;
    DBHelper dbHelper;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartinformation2);
        btn1 = (Button)findViewById(R.id.auctionok_btn2);
        btn2 = (Button)findViewById(R.id.auctionX_btn2);
        tv1 = (TextView)findViewById(R.id.artname_txt2);
        tv2 = (TextView)findViewById(R.id.artcontents_txt2);
        task = new back();
        imView = (ImageView) findViewById(R.id.imageView2);
//        dbHelper = new DBHelper(getApplicationContext(), "MoneyBook.db", null, 1);
        Intent intent2 = getIntent();
        String tagid = intent2.getStringExtra("tagid");
        auction = intent2.getStringExtra("auction");
        bidkey = intent2.getStringExtra("bidkey");
        aucend = intent2.getStringExtra("aucend");
        Log.e("aucend : ", aucend);
        Intent intent = getIntent();
        String artinfo = intent.getStringExtra("artinfo");
        userId = intent.getStringExtra("userID");
        Log.d("assss" , userId);

        try {
            JSONObject job = new JSONObject(ArtInfo(tagid));
            artkey = job.get("art_seq").toString();
            nfcid = job.get("nfc_id").toString();
            artistid = job.get("artist_id").toString();
            arttitle = job.get("art_title").toString();
            arttext = job.get("art_content").toString();
            image = job.get("art_image").toString();
            auckey = job.get("auc_seq").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv1.setText(arttitle);
        tv2.setText(arttext);
        task.execute(imgUrl+image);
        Log.d("ffffff", image);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.auctionok_btn2:
                if (auction.equals("1") || (auction.equals("1") && !bidkey.equals("0")) ||auction.equals("0")) {
                    Log.d("auction", auction + " bidkey : " + bidkey);
                    Toast.makeText(ArtInformation2.this, "경매하고 있지 않습니다.", Toast.LENGTH_SHORT).show();

                }
                else if (auction.equals("2") ){
                    //경매취소 ,마감 (한번이라도 경매했을때 )
                    Toast.makeText(ArtInformation2.this, "곧 경매가 시작됩니다. 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (auction.equals("3") && bidkey.equals("0")) {
                    Intent intent = new Intent(ArtInformation2.this, BidderInfo.class);
                    intent.putExtra("artimage", image);
                    intent.putExtra("userId", userId);
                    intent.putExtra("auckey", auckey);
                    intent.putExtra("aucend", aucend);
                    startActivity(intent);
                    finish();
                } else if(auction.equals("5")||auction.equals("4")){
                    Toast.makeText(ArtInformation2.this, "마감되었습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ArtInformation2.this, "말도안돼", Toast.LENGTH_SHORT).show();
                    Log.d("auction", auction + " bidkey : " + bidkey);

                }
                break;
            case R.id.auctionX_btn2:
                ArtAlbumList(userId, artkey);
//                JSONObject job = null;
//                try {
//                    job = new JSONObject(Artinfo(key));
//                    arttitle = job.get("user_name").toString();
//                    arttext = job.get("user_join_time").toString();
//                    image = job.get("image").toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                dbHelper.insert(arttext,image, arttitle);
//                Log.d("aa",dbHelper.getResult());

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
    //유저아이디와 그림Key 전달
       private void ArtAlbumList(String msg , String msg2) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/artalbum_insert.jsp";

        DefaultHttpClient client = new DefaultHttpClient();
        try {

            HttpPost post = new HttpPost(URL + "?msg=" + msg + "&msg2=" + msg2);
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

    private String ArtInfo(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/art_info.jsp";

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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
