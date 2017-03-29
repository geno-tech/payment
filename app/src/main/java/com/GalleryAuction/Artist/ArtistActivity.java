package com.GalleryAuction.Artist;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.geno.bill_folder.R;

public class ArtistActivity extends Activity implements View.OnClickListener {
    Button a1,a2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartistactivity);
        a1 = (Button)findViewById(R.id.artinfo_btn_artist);
        a2 = (Button)findViewById(R.id.auctionlist_btn_artist);
        a1.setOnClickListener(this);
        a2.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.artinfo_btn_artist:
                Intent intent1 = new Intent(ArtistActivity.this, ArtistArtInformation.class);
                startActivity(intent1);
                break;
            case R.id.auctionlist_btn_artist:
                Intent intent2 = new Intent(ArtistActivity.this, ArtistAuctionInformation.class);
                startActivity(intent2);
                break;

        }
    }
}
