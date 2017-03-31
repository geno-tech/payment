package com.GalleryAuction.Bidder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ArtInfoTagList extends Activity {
//    DBHelper dbHelper;
    Bitmap bmImg;
    String imgUrl = "http://59.3.109.220:9998/NFCTEST/art_images/";

    private ListView listView;
    private ArtInfoAdapter adapter;
    Button remove_btn;
    String artkey, arttitle, image, time_e;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartinfotaglist);
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userID");

//        SharedPreferences preferences = getSharedPreferences("KEY", 0);
//        String userkey = preferences.getString("key", null);
//        Toast.makeText(this, Artinfo(key), Toast.LENGTH_SHORT).show();
        remove_btn = (Button)findViewById(R.id.artinforemove_btn);
//        dbHelper = new DBHelper(getApplicationContext(), "MoneyBook.db", null, 1);
        adapter = new ArtInfoAdapter();

        listView = (ListView) findViewById(R.id.artinfo_listview);
        listView.setAdapter(adapter);
        String albumlist = ArtAlbumlist(userId);


        JSONObject job = null;
        JSONArray  ja = null;
        try {
           ja = new JSONArray(albumlist);

            for (int i = 0 ; i  < ja.length(); i++){

                job = (JSONObject) ja.get(i);
            artkey = job.get("art_seq").toString();
            arttitle = job.get("art_title").toString();
            image = job.get("art_image").toString();
                    time_e = job.get("art_date_e").toString();
                Log.d("aauser", "" +artkey);
                adapter.addItem(imgUrl+image, arttitle, time_e);
                Log.d("aaa", imgUrl+image);
            }
            } catch (JSONException e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
            }
        });
//    getname();
    remove_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            dbHelper.deleteAll();
//            adapter.notifyDataSetChanged();
        }
    });
    }

//    public void getname() {
//        // 읽기가 가능하게 DB 열기
//        SQLiteDatabase db = dbHelper.getReadableDatabase();
//        String result = "";
//
//        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
//        Cursor cursor = db.rawQuery("SELECT * FROM MONEYBOOK", null);
//        while (cursor.moveToNext()) {
//            adapter.addItem(cursor.getString(0),cursor.getString(1),cursor.getString(3));
//        }
//
//    }
private String ArtAlbumlist(String msg) {
    if (msg == null) {
        msg = "";
    }

    String URL = "http://59.3.109.220:9998/NFCTEST/artalbum_select.jsp";
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
