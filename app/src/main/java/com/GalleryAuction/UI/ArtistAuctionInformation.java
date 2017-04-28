package com.GalleryAuction.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

import com.GalleryAuction.Adapter.ArtistAuctionAdapter;
import com.geno.bill_folder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.GalleryAuction.Client.ImageViewItem.getImageBitmap;
import static com.GalleryAuction.Item.HttpClientItem.Artist_auc_cancle;
import static com.GalleryAuction.Item.HttpClientItem.AuctionInfo;

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
                final int pos = position;
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
                    intent0.putExtra("image", image);
                    intent0.putExtra("title", title);
                    startActivity(intent0);
                    finish();
                } else if (auction.equals("4")) {



                        Intent intent1 = new Intent(ArtistAuctionInformation.this, ArtistAuctionCompleteUi.class);
                        intent1.putExtra("auckey", auckey);
                        intent1.putExtra("title", title);
                        intent1.putExtra("image", image);
                        startActivity(intent1);
                        finish();

                } else if (auction.equals("3")) {
                    Intent intent2 = new Intent(ArtistAuctionInformation.this, ArtistAuctionDetailInfoUi.class);
                    intent2.putExtra("auckey", auckey);
                    startActivity(intent2);
                    finish();
                } else if (auction.equals("2")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(ArtistAuctionInformation.this);
                    alert.setMessage("경매대기상태입니다. 취소하시겠습니까?" ).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Artist_auc_cancle(auckey);
                            finish();
                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertdialog = alert.create();
                    alertdialog.show();

//                    Toast.makeText(ArtistAuctionInformation.this, "아직 경매가 시작되지 않았습니다.", Toast.LENGTH_SHORT).show();

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


}
