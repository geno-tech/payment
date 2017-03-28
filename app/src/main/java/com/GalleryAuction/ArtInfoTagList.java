package com.GalleryAuction;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.geno.bill_folder.R;

public class ArtInfoTagList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartinfotaglist);
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");
        Log.d("key", key);
    }
}
