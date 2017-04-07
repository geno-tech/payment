package com.GalleryAuction.Bidder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.GalleryAuction.Bidder.ArtList.ArtInfoTagList;
import com.GalleryAuction.Bidder.ArtTag.TagInfo;
import com.geno.bill_folder.R;

public class BidderActivity extends Activity implements View.OnClickListener {
    Button btn1, btn2, btn3;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallerybidderactivity);
        btn1 = (Button)findViewById(R.id.artinfor_list_btn);
        btn2 = (Button)findViewById(R.id.tag_btn);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        Intent intent0 = getIntent();
        userID = intent0.getStringExtra("userID");

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.artinfor_list_btn:

                Intent intent = new Intent(BidderActivity.this, ArtInfoTagList.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
                finish();
                break;
            case R.id.tag_btn:
                Intent intent2 = new Intent(BidderActivity.this, TagInfo.class);
                intent2.putExtra("userID", userID);
                startActivity(intent2);
                finish();
                break;
        }

    }
}
