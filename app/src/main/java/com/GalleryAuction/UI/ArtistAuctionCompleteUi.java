package com.GalleryAuction.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geno.bill_folder.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ArtistAuctionCompleteUi extends Activity {
    ImageView iv;
    TextView tv1, tv2, tv3;
    Button btn1, btn2;
    String auckey, bidprice,userid, title, image;
    long  min_bidding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_artistauction_complete);
        iv = (ImageView)findViewById(R.id.artimageconfirm_img);
        //그림 이름
        tv1 = (TextView)findViewById(R.id.artinfoconfirm_txt);
        // 그림 최고입찰가
        tv2 = (TextView)findViewById(R.id.winningbidconfirm_txt);
        // 그림 최고입찰가 정한 user 아이디
        tv3 = (TextView)findViewById(R.id.winningbiderconfirm_txt);
        btn1 = (Button)findViewById(R.id.artistwinningbid_btn);
        btn2 = (Button)findViewById(R.id.artistwinningbid_x_btn);
        Intent intent = getIntent();
        auckey = intent.getStringExtra("auckey");
        title = intent.getStringExtra("title");
        image = intent.getStringExtra("image");
        try {
            JSONObject job = new JSONObject(BiddingInfoBest(auckey));
            Log.d("bidprice", ""+job);

            bidprice = job.get("bid_price").toString();
            userid = job.get("user_id").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        min_bidding = bidprice==null?0:Long.parseLong(bidprice);
        //()
        if ((bidprice == null || bidprice.equals("")) && (userid == null || userid.equals(""))) {
            tv1.setText(title);
            tv2.setText("최고입찰가 : " + min_bidding + "원으로 입찰가가 없습니다.");
            tv3.setText("입찰 한 아이디 : 없습니다");
            btn1.setVisibility(View.GONE);
        } else {
            tv1.setText(title);
            tv2.setText("최고입찰가 : " + min_bidding + "원");
            tv3.setText("입찰 한 아이디 : " + userid);

        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ArtistAuctionCompleteUi.this);
                alert.setMessage("최고 입찰가는" + min_bidding + "원 입니다.\n낙찰하시겠습니까?" ).setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BiddingWin(auckey);
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
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Artist_auc_cancle(auckey);
                finish();
            }
        });
    }

    private String BiddingWin(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/bidding_win.jsp";

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

    private String Artist_auc_cancle(String msg) {
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
