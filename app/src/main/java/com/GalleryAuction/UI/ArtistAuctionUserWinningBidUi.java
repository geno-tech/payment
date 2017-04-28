package com.GalleryAuction.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.geno.bill_folder.R;

import static com.GalleryAuction.Item.HttpClientItem.BiddingWinArtistAgree;

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


}
