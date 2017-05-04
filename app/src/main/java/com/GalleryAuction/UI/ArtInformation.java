package com.GalleryAuction.UI;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.bill_folder.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.GalleryAuction.Item.HttpClientItem.ArtAlbumList;


public class ArtInformation extends Activity implements View.OnClickListener{
    Button btn1, btn2;
    String arttitle, arttext, image, artkey, nfcid, artistid, auckey ,aucstatus ,bidkey, aucstart ;
    TextView tv1, tv2;
    ImageView imView;
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";
    Date date;
    SimpleDateFormat sdf;
    Bitmap bmImg;
    back task;
    String userId, artinfo;
    long now, start, ne, dd, nd, HH, nH, mm, ss;
    NfcAdapter  nfcAdapter;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartinformation);
        btn1 = (Button)findViewById(R.id.auctionok_btn);
        btn2 = (Button)findViewById(R.id.auctionX_btn);
        tv1 = (TextView)findViewById(R.id.artname_txt);
        tv2 = (TextView)findViewById(R.id.artcontents_txt);
        task = new back();
        imView = (ImageView) findViewById(R.id.imageView);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent2 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent2, 0);
//        dbHelper = new DBHelper(getApplicationContext(), "MoneyBook.db", null, 1);
        Intent intent = getIntent();
        artinfo = intent.getStringExtra("artinfo");
        //Log.d("HH", artinfo);
        userId = intent.getStringExtra("userID");

        JSONObject job = null;
        Log.d("artinfo", ""+artinfo);

        try {
            job = new JSONObject(artinfo);

            artkey = job.get("art_seq").toString();
            nfcid = job.get("nfc_id").toString();
            artistid = job.get("artist_id").toString();
            arttitle = job.get("art_title").toString();
            Log.d("art_content : ",""+job.get("ART_CONTENT"));
            arttext = (job.get("ART_CONTENT") == null && job.get("ART_CONTENT").toString().equals(""))?"값없음":job.get("ART_CONTENT").toString();
            image = job.get("art_image").toString();
            auckey = job.get("auc_seq").toString();
            aucstatus = job.get("auc_status").toString();
            bidkey = job.get("bid_seq").toString();
            aucstart = job.get("auc_start").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException n) {
            n.printStackTrace();
            Toast.makeText(ArtInformation.this, "다시시도하세요", Toast.LENGTH_SHORT).show();
        }
        try {
            date = sdf.parse(aucstart);
            start = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        tv1.setText(arttitle);
        tv2.setText(arttext);
        task.execute(imgUrl+image);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.auctionok_btn:
//                Log.d("auckey", auckey);
//                if (auckey == null || auckey.equals("null") || auckey.equals("0")) {
//                    Toast.makeText(ArtInformation.this, "경매하고 있지 않습니다다.", Toast.LENGTH_SHORT).show();
//
//                    ArtAlbumList(userId, artkey);
//                } else {
//                    if (aucstatus == null || aucstatus.equals("0") ||aucstatus.equals("null")) {
//                        Toast.makeText(ArtInformation.this, "경매하고 있지 않습니다.", Toast.LENGTH_SHORT).show();
//
//                    } else if (aucstatus.equals("1")) {
//                        Toast.makeText(ArtInformation.this, "경매가 취소되었습니다.", Toast.LENGTH_SHORT).show();
//
//                    } else if (aucstatus.equals("2")) {
//                        Toast.makeText(ArtInformation.this, "경매가 곧 시작됩니다. 기다리십시오", Toast.LENGTH_SHORT).show();
//
//                    }
//                    else {
//                        Intent intent = new Intent(ArtInformation.this, BidderInfo.class);
//                        intent.putExtra("artimage", image);
//                        intent.putExtra("userId", userId);
//                        intent.putExtra("auckey", auckey);
//                        startActivity(intent);
//                        ArtAlbumList(userId, artkey);
//                        finish();
//                    }
//                }
                if ((aucstatus.equals("1") || (aucstatus.equals("1")) && !bidkey.equals("0")) ||aucstatus.equals("0")) {
                    Toast.makeText(ArtInformation.this, "경매하고 있지 않습니다.", Toast.LENGTH_SHORT).show();

                }
                else if (aucstatus.equals("2") ){
                    now = System.currentTimeMillis();
                    //경매취소 ,마감 (한번이라도 경매했을때 )
                    long en = start-now;
                    if(en < 0){
                        ne = -(en)/1000; dd = ne/86400; nd = ne%86400; HH = nd/3600; nH = nd%3600; mm = nH/60; ss = nH%60;
                        Toast.makeText(ArtInformation.this, "경매시작까지" + dd+"일" + HH+"시" +mm+"분"+ ss +"초" +" 지났습니다.", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        ne = en/1000; dd = ne/86400; nd = ne%86400; HH = nd/3600; nH = nd%3600; mm = nH/60; ss = nH%60;
                        Toast.makeText(ArtInformation.this,"경매시작까지" + dd+"일" + HH+"시" +mm+"분"+ ss +"초" + " 남았습니다.", Toast.LENGTH_SHORT).show();

                    }
//                    Toast.makeText(ArtInformation2.this, "곧 경매가 시작됩니다. 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (aucstatus.equals("3") && bidkey.equals("0")) {
                    Intent intent = new Intent(ArtInformation.this, BidderInfo.class);
                    intent.putExtra("artimage", image);
                    intent.putExtra("userId", userId);
                    intent.putExtra("auckey", auckey);
                    startActivity(intent);
                    finish();
                } else if(aucstatus.equals("5")||aucstatus.equals("4")){
                    Toast.makeText(ArtInformation.this, "마감되었습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ArtInformation.this, "말도안돼", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.auctionX_btn:
                ArtAlbumList(userId, artkey);
//                JSONObject job = null;
//                try {
//                    job = new JSONObject(Artinfo(key));
//                    arttitle = job.get("user_name").toString();
//                    arttext = job.get("user_join_time").toString();
//                    image = job.get("image").toString();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                dbHelper.insert(arttext,image, arttitle);
//                Log.d("aa",dbHelper.getResult());

                finish();
                break;
        }
    }

    private class back extends AsyncTask<String, Integer,Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try{
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);

            }catch(IOException e){
                e.printStackTrace();
            }
            return bmImg;
        }
        protected void onPostExecute(Bitmap img){
            imView.setImageBitmap(bmImg);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
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
            Toast.makeText(this, "[갤러리옥션 - 태그하기]에서 태그하시오", Toast.LENGTH_SHORT).show();
        }
        Log.d("TAGTEST : ", ""+ tag);
    }
}