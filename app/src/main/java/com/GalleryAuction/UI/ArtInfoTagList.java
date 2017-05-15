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
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.GalleryAuction.Adapter.ArtInfoAdapter;
import com.geno.bill_folder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import it.neokree.materialtabs.MaterialTabHost;

import static com.GalleryAuction.Client.ImageViewItem.getImageBitmap;
import static com.GalleryAuction.Client.TagInfoClient.toHexString;
import static com.GalleryAuction.Item.HttpClientItem.ArtAlbumSelect;
import static com.GalleryAuction.Item.HttpClientItem.ArtInfo;

public class ArtInfoTagList extends Activity {
    //    DBHelper dbHelper;
    Bitmap bmImg;
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";
    String userId;
    private ListView listView;
    private ArtInfoAdapter adapter;
    Button refresh_btn, taglist_ext_btn;
    String artkey, arttitle, image, nowtime, nfcid, auckey, bidkey, albumkey, auctime_end, auction, bid, auctime_start;
    NfcAdapter  nfcAdapter;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartinfotaglist);
        final Intent intent = getIntent();
        taglist_ext_btn = (Button)findViewById(R.id.taglist_ext_btn);
        userId = intent.getStringExtra("userID");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);
//        SharedPreferences preferences = getSharedPreferences("KEY", 0);
//        String userkey = preferences.getString("key", null);
//        Toast.makeText(this, Artinfo(key), Toast.LENGTH_SHORT).show();
        refresh_btn = (Button)findViewById(R.id.refresh_btn);
//        dbHelper = new DBHelper(getApplicationContext(), "MoneyBook.db", null, 1);
        adapter = new ArtInfoAdapter();
        listView = (ListView) findViewById(R.id.artinfo_listview);
        listView.setAdapter(adapter);
        taglist_ext_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        try {

                String albumlist = ArtAlbumSelect(userId);
                JSONArray ja = new JSONArray(albumlist);
                Log.d("auctime", albumlist);

                for (int i = 0; i < ja.length(); i++) {

                    JSONObject job = (JSONObject) ja.get(i);
                    artkey = job.get("art_seq").toString();
                    arttitle = job.get("art_title").toString();
                    nfcid = job.get("nfc_id").toString();
                    image = job.get("art_image").toString();
                    nowtime = job.get("art_con_time").toString();
                    auckey = job.get("auc_seq").toString();
                    bidkey = job.get("bid_seq").toString();
                    bid = job.get("bid_status").toString();
                    albumkey = job.get("album_seq").toString();
                    auctime_end = job.get("auc_end").toString();
                    auction = job.get("auc_status").toString();
                    Log.d("aauser", "" + auckey + ", " + arttitle + ", " + bidkey);
                    //new DownloadImageTask().execute(imgUrl+image);
                    adapter.addItem(arttitle, nowtime, auction, bidkey, bid , getImageBitmap(imgUrl+image));
                    Log.d("aaa", imgUrl + image);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                        String albumlist = ArtAlbumSelect(userId);
                        JSONArray ja = new JSONArray(albumlist);
                        JSONObject job = (JSONObject) ja.get(position);
                        artkey = job.get("art_seq").toString();
                        arttitle = job.get("art_title").toString();
                        nfcid = job.get("nfc_id").toString();
                        image = job.get("art_image").toString();
                        nowtime = job.get("art_date_e").toString();
                        bidkey = job.get("bid_seq").toString();
                        bid = job.get("bid_status").toString();
                        albumkey = job.get("album_seq").toString();
                        auckey = job.get("auc_seq").toString();
                        auction = job.get("auc_status").toString();
                        auctime_end = job.get("auc_end").toString();
                        auctime_start = job.get("auc_start").toString();
                        Log.d("aauser", "" + auction);
                        Log.d("aaa", imgUrl + image);
                    new ArtInfoAdapter().addItem(arttitle, nowtime, auction, bidkey, bid , getImageBitmap(imgUrl+image));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if ((auction.equals("0"))||(auction.equals("1"))||(auction.equals("1") && !bidkey.equals("0")) || (auction.equals("3") && bidkey.equals("0")) || (auction.equals("2"))) {
                    Log.d("bb", bidkey + "그림 정보화면");
                    Log.d("cc", auction +"???"+bidkey + "그림정보화면");

                    Intent intent1 = new Intent(ArtInfoTagList.this, ArtInformation2.class);
                    intent1.putExtra("tagid", nfcid);
                    intent1.putExtra("userID", userId);
                    intent1.putExtra("auction", auction);

                    intent1.putExtra("bidkey", bidkey);
                    intent1.putExtra("aucend", auctime_end);
                    intent1.putExtra("aucstart", auctime_start);

                    startActivity(intent1);
                    finish();
                } else if (auction == "3" &&bidkey != "0") {
                    Log.d("cc", auction +"???"+bidkey + "재입찰화면");
                    Intent intent1 = new Intent(ArtInfoTagList.this, ReBidding.class);
                    intent1.putExtra("auckey", auckey);
                    intent1.putExtra("userID", userId);
                    intent1.putExtra("bidkey", bidkey);
                    intent1.putExtra("aucend", auctime_end);
                    intent1.putExtra("image", image);
                    startActivity(intent1);
                    finish();
                } else if (auction.equals("5")) {
                    Intent intent1 = new Intent(ArtInfoTagList.this, WinningBidListActivity.class);
                    intent1.putExtra("auckey", auckey);
                    intent1.putExtra("userID", userId);
                    intent1.putExtra("bid", bid);
                    intent1.putExtra("arttitle", arttitle);
                    intent1.putExtra("nfcid", nfcid);
                    intent1.putExtra("image", image);
                    intent1.putExtra("nowtime", nowtime);
                    intent1.putExtra("auction", auction);
                    intent1.putExtra("albumkey", albumkey);
                    intent1.putExtra("auckey", auckey);



                    startActivity(intent1);
                    finish();
                } else if (auction.equals("4")) {
                    Toast.makeText(ArtInfoTagList.this, "아티스트의 낙찰을 기다리는 중 입니다.", Toast.LENGTH_SHORT).show();
                } else if (auction.equals("6")) {
                    Toast.makeText(ArtInfoTagList.this, "아티스트의 동의를 기다리는 중 입니다.", Toast.LENGTH_SHORT).show();

                } else if (auction.equals("7")) {
                    Toast.makeText(ArtInfoTagList.this, "경매완료", Toast.LENGTH_SHORT).show();

                }

            }
        });
//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                try {
//                    final String albumlist2 = ArtAlbumlist(userId);
//
//                    JSONArray ja = new JSONArray(albumlist2);
//                    JSONObject job = (JSONObject) ja.get(position);
//                    artkey = job.get("art_seq").toString();
//                    arttitle = job.get("art_title").toString();
//                    nfcid = job.get("nfc_id").toString();
//                    image = job.get("art_image").toString();
//                    nowtime = job.get("art_date_e").toString();
//                    bidkey = job.get("bid_seq").toString();
//                    albumkey = job.get("album_seq").toString();
//                    auckey = job.get("auc_seq").toString();
//                    auctime_end = job.get("auc_end").toString();
//                    Log.d("auctime", auctime_end);
//                    Log.d("aauser", "" +artkey);
//                    Log.d("aaa", imgUrl+image);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                AlertDialog.Builder alert = new AlertDialog.Builder(ArtInfoTagList.this);
//                alert.setMessage("삭제하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        Toast.makeText(ArtInfoTagList.this, artkey, Toast.LENGTH_SHORT).show();
//                    }
//                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                });
//                AlertDialog alertdialog = alert.create();
//                alertdialog.show();
//                return false;
//            }
//        });
//    getname();
        refresh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent1 = new Intent(ArtInfoTagList.this, ArtInfoTagList.class);
                intent1.putExtra("userID", userId);
                startActivity(intent1);
                overridePendingTransition(R.anim.scale, R.anim.scale);

                finish();

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
            final byte[] tagId = tag.getId();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intent0 = getIntent();
                    String userID = intent0.getStringExtra("userID");
                    Intent intent = new Intent(ArtInfoTagList.this, ArtInformation.class);
                    intent.putExtra("artinfo",ArtInfo(toHexString(tagId)));
                    intent.putExtra("userID",userID);
                    //Log.d("tag",ArtInfo(toHexString(tagId)));
                    startActivity(intent);
                    finish();
                }
            }).start();

        }
    }


}

