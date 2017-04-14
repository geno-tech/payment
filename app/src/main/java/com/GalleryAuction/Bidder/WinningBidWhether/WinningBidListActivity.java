package com.GalleryAuction.Bidder.WinningBidWhether;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.GalleryAuction.Artist.AuctionList.ArtistAuctionCompleteUi;
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

import static com.GalleryAuction.Bidder.ArtList.ReBidding.currentpoint;

public class WinningBidListActivity extends AppCompatActivity {
    TextView tv1, tv2, tv3;
    Button btn1, btn2;
    String auckey, userID, mybest, best;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_winningbidlist);
        tv2 = (TextView)findViewById(R.id.winningbidlistbest_txt);
        tv3 = (TextView)findViewById(R.id.winningbidlistmybest_txt);
        btn1 = (Button)findViewById(R.id.winningbidlist_btnagree);
        btn2 = (Button)findViewById(R.id.winningbidlist_btnX);
        Intent intent = getIntent();
        auckey = intent.getStringExtra("auckey");
        userID = intent.getStringExtra("userID");
        try {
            JSONObject job2 = new JSONObject(BiddingInfoBest(auckey));
            best = job2.get("bid_price").toString();
            Log.d("SSAAAA", ""+job2);

            JSONObject job = new JSONObject(BiddingInfo(userID, auckey));
            mybest = job.get("bid_price").toString();
            Log.d("SSAAAA", ""+job);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv2.setText("낙찰가는 " + currentpoint(best) + "원 입니다");
        tv3.setText("당신의 입찰가는 " + currentpoint(mybest) + "원 입니다");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(WinningBidListActivity.this);
                alert.setMessage(currentpoint(mybest) + "원에 낙찰되었습니다.\n수락하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1 = new Intent(WinningBidListActivity.this, WinningBidUi.class);

                        startActivity(intent1);
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
                AlertDialog.Builder alert2 = new AlertDialog.Builder(WinningBidListActivity.this);
                alert2.setMessage("거부하시면 가계약금을 받을 수 없습니다.\n그래도 거부하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(WinningBidListActivity.this, "낙찰을 취소하였습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }
    private String BiddingInfo(String msg, String msg2) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/bidding_info.jsp";

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
            HttpConnectionParams.setConnectionTimeout(params, 300000);
            HttpConnectionParams.setSoTimeout(params, 300000);
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
