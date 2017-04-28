package com.GalleryAuction.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ListView;

import com.GalleryAuction.Adapter.ArtistArtInfoAdapter;
import com.geno.bill_folder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.GalleryAuction.Client.ImageViewItem.getImageBitmap;
import static com.GalleryAuction.Item.HttpClientItem.ArtistArtInfo;

public class ArtistArtInformation extends Activity {
    ListView listView;
    ArtistArtInfoAdapter adapter;
    String artistID, title, nowtime , image;
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_artist_artinformation);
        Intent intent = getIntent();
        artistID = intent.getStringExtra("artistID");

        adapter = new ArtistArtInfoAdapter();
        listView = (ListView)findViewById(R.id.artistartlist_listview);
        listView.setAdapter(adapter);
        try {
            JSONArray ja = new JSONArray(ArtistArtInfo(artistID));

            for (int i = 0 ; i  < ja.length(); i++){

                JSONObject job = (JSONObject) ja.get(i);
                title = job.get("art_title").toString();
                nowtime = job.get("art_regit_date").toString();
                image = job.get("art_image").toString();
                adapter.addItem(title, nowtime , getImageBitmap(imgUrl+image));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
