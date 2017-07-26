package com.GalleryAuction.UI;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.GalleryAuction.Adapter.ArtistArtInfoAdapter;
import com.geno.payment.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.GalleryAuction.Client.ImageViewItem.getImageBitmap;
import static com.GalleryAuction.Client.TagInfoClient.toHexString;
import static com.GalleryAuction.Item.HttpClientItem.ArtInfo;
import static com.GalleryAuction.Item.HttpClientItem.ArtistArtInfo;

public class ArtistArtInformation extends Activity {
    ListView listView;
    ArtistArtInfoAdapter adapter;
    String artistID, title, nowtime , image;
    String imgUrl = "http://221.156.54.210:8989/NFCTEST/art_images/";
    NfcAdapter  nfcAdapter;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_artist_artinformation);
        Intent intent = getIntent();
        artistID = intent.getStringExtra("artistID");

        adapter = new ArtistArtInfoAdapter();
        listView = (ListView)findViewById(R.id.artistartlist_listview);
        listView.setAdapter(adapter);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            Toast.makeText(this, "구매자 ID가 아닙니다.", Toast.LENGTH_SHORT).show();
        }
    }

}
