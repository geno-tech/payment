package com.GalleryAuction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geno.bill_folder.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;


public class ArtInformation extends Activity implements View.OnClickListener{
    Button btn1, btn2;
    String username, test2;
    TextView tv1, tv2;
    ImageView imView;
    String imgUrl = "http://dnllab.incheon.ac.kr/appimg/";
    Bitmap bmImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartinformation);
        btn1 = (Button)findViewById(R.id.auctionok_btn);
        btn2 = (Button)findViewById(R.id.auctionX_btn);
        tv1 = (TextView)findViewById(R.id.artname_txt);
        tv2 = (TextView)findViewById(R.id.artcontents_txt);

        Intent intent = getIntent();
        String tagid = intent.getStringExtra("tagid").toString();
        JSONObject job = null;
        try {
            job = new JSONObject(tagid);
            username = job.get("user_name").toString();
            test2 = job.get("user_join_time").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv1.setText(username);
        tv2.setText(test2);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.auctionok_btn:
                Intent intent = new Intent(ArtInformation.this, BidderInfo.class);
                startActivity(intent);
                finish();
                break;
            case R.id.auctionX_btn:
                finish();
                break;
        }
    }


}



