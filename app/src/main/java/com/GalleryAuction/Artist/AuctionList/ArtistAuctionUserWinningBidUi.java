package com.GalleryAuction.Artist.AuctionList;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.geno.bill_folder.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ArtistAuctionUserWinningBidUi extends Activity {
    TextView tv1;
    Button btn1, btn2;
    String userID, artistID, auckey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartistauction_userwinningbid);
        tv1 = (TextView) findViewById(R.id.artistcontract_txt);
        btn1 = (Button)findViewById(R.id.artistcontract_agree_btn);
        btn2 = (Button)findViewById(R.id.artistcontract_x_btn);
        tv1.setText(getString(R.string.agreement_msg));
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        artistID = intent.getStringExtra("artistID");
        auckey = intent.getStringExtra("auckey");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BiddingWinArtistAgree(auckey);
                finish();
            }
        });
    }

    private String BiddingWinArtistAgree(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/bidding_win_artistAgree.jsp";

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
