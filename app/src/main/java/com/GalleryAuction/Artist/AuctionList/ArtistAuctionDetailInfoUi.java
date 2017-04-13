package com.GalleryAuction.Artist.AuctionList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import java.io.InputStreamReader;

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
    private String AuctionDetailInfo(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/artist_bid_info.jsp";

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
    private String AuctionRemove(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/artist_auc_cancle.jsp";

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
