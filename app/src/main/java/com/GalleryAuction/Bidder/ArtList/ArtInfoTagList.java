package com.GalleryAuction.Bidder.ArtList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import java.io.InputStreamReader;

public class ArtInfoTagList extends Activity {
    //    DBHelper dbHelper;
    Bitmap bmImg;
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";
    String userId;
    private ListView listView;
    private ArtInfoAdapter adapter;
    Button remove_btn;
    String artkey, arttitle, image, nowtime, nfcid, auckey, bidkey, albumkey, auctime_end, auction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartinfotaglist);
        final Intent intent = getIntent();
        userId = intent.getStringExtra("userID");

//        SharedPreferences preferences = getSharedPreferences("KEY", 0);
//        String userkey = preferences.getString("key", null);
//        Toast.makeText(this, Artinfo(key), Toast.LENGTH_SHORT).show();
        remove_btn = (Button)findViewById(R.id.artinforemove_btn);
//        dbHelper = new DBHelper(getApplicationContext(), "MoneyBook.db", null, 1);
        adapter = new ArtInfoAdapter();

        listView = (ListView) findViewById(R.id.artinfo_listview);
        listView.setAdapter(adapter);




        try {
            String albumlist = ArtAlbumlist(userId);
            JSONArray ja = new JSONArray(albumlist);
            Log.d("auctime", albumlist);

            for (int i = 0 ; i  < ja.length(); i++){

                JSONObject job = (JSONObject) ja.get(i);
                artkey = job.get("art_seq").toString();
                arttitle = job.get("art_title").toString();
                nfcid = job.get("nfc_id").toString();
                image = job.get("art_image").toString();
                nowtime = job.get("art_con_time").toString();
                auckey = job.get("auc_seq").toString();
                bidkey = job.get("bid_seq").toString();
                albumkey = job.get("album_seq").toString();
                auctime_end = job.get("auc_end").toString();
                auction = job.get("auc_status").toString();
                Log.d("aauser", "" +auckey + ", " + arttitle + ", " + bidkey);
                adapter.addItem(arttitle, nowtime, auction, bidkey);
                Log.d("aaa", imgUrl+image);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String albumlist = ArtAlbumlist(userId);
                    JSONArray ja = new JSONArray(albumlist);
                        JSONObject job = (JSONObject) ja.get(position);
                        artkey = job.get("art_seq").toString();
                        arttitle = job.get("art_title").toString();
                        nfcid = job.get("nfc_id").toString();
                        image = job.get("art_image").toString();
                        nowtime = job.get("art_date_e").toString();
                        bidkey = job.get("bid_seq").toString();
                        albumkey = job.get("album_seq").toString();
                    auckey = job.get("auc_seq").toString();
                    auction = job.get("auc_status").toString();
                    auctime_end = job.get("auc_end").toString();
                    Log.d("aauser", "" +auction);
                        Log.d("aaa", imgUrl+image);
                    adapter.addItem(arttitle, nowtime, auction, bidkey);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if ((auction == "0")||(auction == "0" && bidkey != "0") || (auction == "2" && bidkey == "0") || (auction=="1")) {
                    Log.d("bb", bidkey + "그림 정보화면");
                    Intent intent1 = new Intent(ArtInfoTagList.this, ArtInformation2.class);
                    intent1.putExtra("tagid", nfcid);
                    intent1.putExtra("userID", userId);
                    intent1.putExtra("auction", auction);
                    intent1.putExtra("bidkey", bidkey);

                    startActivity(intent1);
                    finish();
                } else if (auction == "2" &&bidkey != "0") {
                    Log.d("cc", bidkey + "재입찰화면");
                    Intent intent1 = new Intent(ArtInfoTagList.this, ReBidding.class);
                    intent1.putExtra("auckey", auckey);
                    intent1.putExtra("userID", userId);
                    intent1.putExtra("bidkey", bidkey);
                    intent1.putExtra("aucend", auctime_end);
                    intent1.putExtra("image", image);
                    startActivity(intent1);
                    finish();
                }

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    final String albumlist2 = ArtAlbumlist(userId);

                    JSONArray ja = new JSONArray(albumlist2);
                    JSONObject job = (JSONObject) ja.get(position);
                    artkey = job.get("art_seq").toString();
                    arttitle = job.get("art_title").toString();
                    nfcid = job.get("nfc_id").toString();
                    image = job.get("art_image").toString();
                    nowtime = job.get("art_date_e").toString();
                    bidkey = job.get("bid_seq").toString();
                    albumkey = job.get("album_seq").toString();
                    auckey = job.get("auc_seq").toString();
                    auctime_end = job.get("auc_end").toString();
                    Log.d("auctime", auctime_end);
                    Log.d("aauser", "" +artkey);
                    Log.d("aaa", imgUrl+image);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AlertDialog.Builder alert = new AlertDialog.Builder(ArtInfoTagList.this);
                alert.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(ArtInfoTagList.this, artkey, Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog alertdialog = alert.create();
                alertdialog.show();
                return false;
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
    private String ArtAlbumRemote(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/artalbumremote.jsp";
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

    private String ArtAlbumlist(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/artalbum_select.jsp";
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