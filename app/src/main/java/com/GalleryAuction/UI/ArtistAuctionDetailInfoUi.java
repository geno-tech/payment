package com.GalleryAuction.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.GalleryAuction.Adapter.ArtistAuctionDetailAdapter;
import com.geno.bill_folder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.GalleryAuction.Item.HttpClientItem.AuctionDetailInfo;
import static com.GalleryAuction.Item.HttpClientItem.AuctionRemove;

public class ArtistAuctionDetailInfoUi extends Activity implements View.OnClickListener {
    String nowbidding, userid, end, time, auckey;
    ListView listView;
    Button btn1, btn2;
    ArtistAuctionDetailAdapter adapter = new ArtistAuctionDetailAdapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_artistauction_detail_info);
        listView = (ListView)findViewById(R.id.DetailInfo_listview) ;
        btn1 = (Button)findViewById(R.id.artistauction_detail_btn);
        btn2 = (Button)findViewById(R.id.artistauction_detail_btn_exit);
        Intent intent = getIntent();
        auckey = intent.getStringExtra("auckey");
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        try {
            JSONArray ja = new JSONArray(AuctionDetailInfo(auckey));

            for (int i = 0 ; i  < ja.length(); i++){

                JSONObject job = (JSONObject) ja.get(i);
                userid = job.get("user_id").toString();
                nowbidding = job.get("bid_price").toString();
                time = job.get("bid_con_time").toString();
                adapter.addItem(nowbidding, userid, time);
                Log.d("aaaa" , nowbidding);
                Log.d("aaaa" , userid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.artistauction_detail_btn:
                AlertDialog.Builder alert = new AlertDialog.Builder(ArtistAuctionDetailInfoUi.this);
                alert.setMessage("취소하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    AuctionRemove(auckey);
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


                break;
            case R.id.artistauction_detail_btn_exit :
                finish();
                break;
        }
    }



}
