package com.GalleryAuction.Artist.AuctionList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.GalleryAuction.Artist.ArtList.ArtistArtInformation;
import com.GalleryAuction.Bidder.ArtList.ArtInfoTagList;
import com.geno.bill_folder.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ArtistAuctionInformation extends Activity {
    ArtistAuctionAdapter artistAuctionAdapter = new ArtistAuctionAdapter();
    String artistID,arttitle, nowbidding, end, auction, auckey,artkey, title, image, userID;
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";
    ListView listView;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_artist_auctioninformation);
        final Intent intent = getIntent();
        artistID = intent.getStringExtra("artistID");
        listView = (ListView)findViewById(R.id.auctioninfo_listview);
        btn = (Button) findViewById(R.id.auctionexit_btn);
        try {
            JSONArray ja = new JSONArray(AuctionInfo(artistID));

            for (int i = 0 ; i  < ja.length(); i++){

                JSONObject job = (JSONObject) ja.get(i);
                nowbidding = job.get("bid_price").toString();
                end = job.get("art_date_e").toString();
                auction = job.get("auc_status").toString();
                auckey = job.get("auc_seq").toString();
                artkey = job.get("art_seq").toString();

                title = job.get("art_title").toString();
                image = job.get("art_image").toString();
                userID = job.get("user_id").toString();

                artistAuctionAdapter.addItem(title, nowbidding, end, auction, auckey , getImageBitmap(imgUrl+image));
                Log.d("aaaa" , auckey);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listView.setAdapter(artistAuctionAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONArray ja = new JSONArray(AuctionInfo(artistID));

                        JSONObject job = (JSONObject) ja.get(position);
                        title = job.get("art_title").toString();
                        nowbidding = job.get("bid_price").toString();
                        end = job.get("art_date_e").toString();
                        auction = job.get("auc_status").toString();
                        auckey = job.get("auc_seq").toString();
                        artkey = job.get("art_seq").toString();
                        image = job.get("art_image").toString();
                        userID = job.get("user_id").toString();

                    new ArtistAuctionAdapter().addItem(title, nowbidding, end, auction, auckey , getImageBitmap(imgUrl+image));

                        Log.d("key", auckey);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (auction.equals("1") || auction.equals("0"))  {
                    Intent intent0 = new Intent(ArtistAuctionInformation.this, ArtistAuctionAddUi.class);
                    intent0.putExtra("artkey", artkey);
                    startActivity(intent0);
                    finish();
                } else if (auction.equals("4")) {
                    Intent intent1 = new Intent(ArtistAuctionInformation.this, ArtistAuctionCompleteUi.class);
                    intent1.putExtra("auckey", auckey);
                    intent1.putExtra("title", title);
                    intent1.putExtra("image", image);
                    startActivity(intent1);
                    finish();
                }else if (auction.equals("3")) {
                    Intent intent2 = new Intent(ArtistAuctionInformation.this, ArtistAuctionDetailInfoUi.class);
                    intent2.putExtra("auckey", auckey);
                    startActivity(intent2);
                    finish();
                } else if (auction.equals("2")) {

                    Toast.makeText(ArtistAuctionInformation.this, "아직 경매가 시작되지 않았습니다.", Toast.LENGTH_SHORT).show();

                } else if (auction.equals("5")){
                    Toast.makeText(ArtistAuctionInformation.this, "유저의 동의가 필요합니다", Toast.LENGTH_SHORT).show();
                } else if (auction.equals("6")) {
                    Intent intent3 = new Intent(ArtistAuctionInformation.this, ArtistAuctionUserWinningBidUi.class);
                    intent3.putExtra("auckey", auckey);
                    intent3.putExtra("userID", userID);
                    intent3.putExtra("artistID", artistID);
                    startActivity(intent3);
                    finish();
                } else if (auction.equals("7")) {
                    Toast.makeText(ArtistAuctionInformation.this, "낙찰상태" , Toast.LENGTH_SHORT).show();

//                    Intent intent1 = new Intent(ArtistAuctionInformation.this, ArtistAuctionUserWinningBidUi.class);
//                    startActivity(intent1);
                }
            }

        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String AuctionInfo(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/artist_auc_info.jsp";

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

    public Bitmap getImageBitmap(final String imageURL){
        final Bitmap[] bitmapImage = new Bitmap[1];
        Thread mThread = new Thread(){
            @Override
            public void run() {

                try {
                    URL url = new URL(imageURL); // URL 주소를 이용해서 URL 객체 생성

                    //  아래 코드는 웹에서 이미지를 가져온 뒤
                    //  이미지 뷰에 지정할 Bitmap을 생성하는 과정

                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    bitmapImage[0] = BitmapFactory.decodeStream(is);

                } catch(IOException ex) {
                    ex.getMessage();
                }
            }

        };
        mThread.start();
        try {
            mThread.join();

        }
        catch (InterruptedException e){
            e.getMessage();
        }
        return bitmapImage[0];
    }
}
