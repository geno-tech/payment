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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.payment.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.GalleryAuction.Client.TagInfoClient.toHexString;
import static com.GalleryAuction.Item.HttpClientItem.ArtAlbumList;
import static com.GalleryAuction.Item.HttpClientItem.ArtInfo;


public class ArtInformation2 extends Activity implements View.OnClickListener{
    Button btn1, btn2;
    String arttitle, arttext, image, artkey, nfcid, artistid, auckey, auction, bidkey, aucend , aucstart;
    TextView tv1, tv2;
    ImageView imView;
    String imgUrl = "http://221.156.54.210:8989/NFCTEST/art_images/";
    long now, start, ne, dd, nd, HH, nH, mm, ss;

    Bitmap bmImg;
    back task;
    String userId;
    Date date;
    SimpleDateFormat sdf;
    NfcAdapter  nfcAdapter;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartinformation2);
        btn1 = (Button)findViewById(R.id.auctionok_btn2);
        btn2 = (Button)findViewById(R.id.auctionX_btn2);
        tv1 = (TextView)findViewById(R.id.artname_txt2);
        tv2 = (TextView)findViewById(R.id.artcontents_txt2);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        task = new back();
        imView = (ImageView) findViewById(R.id.imageView2);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);
//        dbHelper = new DBHelper(getApplicationContext(), "MoneyBook.db", null, 1);
        Intent intent2 = getIntent();
        String tagid = intent2.getStringExtra("tagid");
        Log.d("tagID TEST : ",tagid);
        auction = intent2.getStringExtra("auction");
        bidkey = intent2.getStringExtra("bidkey");
        aucend = intent2.getStringExtra("aucend");
        aucstart = intent2.getStringExtra("aucstart");
        Log.e("aucend : ", aucend);
        Intent intent = getIntent();
        String artinfo = intent.getStringExtra("artinfo");
        userId = intent.getStringExtra("userID");
        Log.d("assss" , userId);
        try {
            date = sdf.parse(aucstart);
            start = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            Log.d("tagID TEST : ",tagid);
            JSONObject job = new JSONObject(ArtInfo(tagid));

            artkey = job.get("art_seq").toString();
            nfcid = job.get("nfc_id").toString();
            artistid = job.get("artist_id").toString();
            arttitle = job.get("art_title").toString();
            arttext = job.get("ART_CONTENT").toString();
            image = job.get("art_image").toString();
            auckey = job.get("auc_seq").toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv1.setText(arttitle);
        tv2.setText(arttext);
        tv2.setMovementMethod(new ScrollingMovementMethod());

        task.execute(imgUrl+image);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.auctionok_btn2:
                if ((auction.equals("1") || (auction.equals("1")) && !bidkey.equals("0")) ||auction.equals("0")) {
                    Log.d("auction", auction + " bidkey : " + bidkey);
                    Toast.makeText(ArtInformation2.this, "경매하고 있지 않습니다.", Toast.LENGTH_SHORT).show();

                }
                else if (auction.equals("2") ){
                    now = System.currentTimeMillis();
                    //경매취소 ,마감 (한번이라도 경매했을때 )
                    long en = start-now;
                    if(en < 0){
                        ne = -(en)/1000; dd = ne/86400; nd = ne%86400; HH = nd/3600; nH = nd%3600; mm = nH/60; ss = nH%60;
                        Toast.makeText(ArtInformation2.this, "경매시작까지" + dd+"일" + HH+"시" +mm+"분"+ ss +"초" +" 지났습니다.", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        ne = en/1000; dd = ne/86400; nd = ne%86400; HH = nd/3600; nH = nd%3600; mm = nH/60; ss = nH%60;
                        Toast.makeText(ArtInformation2.this,"경매시작까지" + dd+"일" + HH+"시" +mm+"분"+ ss +"초" + " 남았습니다.", Toast.LENGTH_SHORT).show();

                    }
//                    Toast.makeText(ArtInformation2.this, "곧 경매가 시작됩니다. 확인해 주세요.", Toast.LENGTH_SHORT).show();
                }
                else if (auction.equals("3") && bidkey.equals("0")) {
                    Intent intent = new Intent(ArtInformation2.this, BidderInfo.class);
                    intent.putExtra("artimage", image);
                    intent.putExtra("userId", userId);
                    intent.putExtra("auckey", auckey);
                    intent.putExtra("aucend", aucend);
                    startActivity(intent);
                    finish();
                } else if(auction.equals("5")||auction.equals("4") || auction.equals("6")){
                    Toast.makeText(ArtInformation2.this, "구입한 다른 유저와 경매진행중입니다..", Toast.LENGTH_SHORT).show();

                } else if(auction.equals("7") ) {
                    Toast.makeText(ArtInformation2.this, "경매가 완료되었습니다.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ArtInformation2.this, "말도안돼", Toast.LENGTH_SHORT).show();
                    Log.d("auction", auction + " bidkey : " + bidkey);

                }
                break;
            case R.id.auctionX_btn2:
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
                Intent intent = new Intent(ArtInformation2.this, ArtInfoTagList.class);
                intent.putExtra("userID", userId);
                startActivity(intent);
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
            final byte[] tagId = tag.getId();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArtAlbumList(userId, artkey);
                    Intent intent0 = getIntent();
                    String userID = intent0.getStringExtra("userID");
                    Intent intent = new Intent(ArtInformation2.this, ArtInformation.class);
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
