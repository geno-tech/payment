package com.GalleryAuction.Artist;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.geno.bill_folder.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ArtistArtInformation extends Activity {
    ListView listView;
    ArtistArtInfoAdapter adapter;
    String artistID, title, nowtime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_artist_artinformation);
        Intent intent = getIntent();
        artistID = intent.getStringExtra("artistID");

        adapter = new ArtistArtInfoAdapter();
        listView = (ListView)findViewById(R.id.artistartlist_listview);
        listView.setAdapter(adapter);
        JSONObject job = null;
        JSONArray ja = null;
        try {
            ja = new JSONArray(ArtistArtInfo(artistID));

            for (int i = 0 ; i  < ja.length(); i++){

                job = (JSONObject) ja.get(i);
                title = job.get("art_title").toString();
                nowtime = job.get("art_regit_date").toString();
                adapter.addItem(title, nowtime);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String ArtistArtInfo(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:9998/NFCTEST/art_info_artistid_list.jsp";

        DefaultHttpClient client = new DefaultHttpClient();
        try {

            HttpPost post = new HttpPost(URL + "?msg=" + msg);
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 3000);
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
