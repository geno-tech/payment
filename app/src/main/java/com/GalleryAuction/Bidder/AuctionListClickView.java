package com.GalleryAuction.Bidder;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.geno.bill_folder.R;

public class AuctionListClickView extends Activity implements View.OnClickListener {
    Button btn_ok, btn_x;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_auctionlist_clickview);
        btn_ok = (Button)findViewById(R.id.rebidding);
        btn_x = (Button)findViewById(R.id.rebidding_x);
        btn_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rebidding :
                Intent intent = new Intent(AuctionListClickView.this, AuctionListClickConfirm.class);
                startActivity(intent);
                break;
            case R.id.rebidding_x :
                finish();
        }
    }

}
