package com.GalleryAuction.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import static com.GalleryAuction.Client.TagInfoClient.toHexString;
import static com.GalleryAuction.Item.HttpClientItem.ArtInfo;
import static com.GalleryAuction.Item.HttpClientItem.Artist_auc_cancle;
import static com.GalleryAuction.Item.HttpClientItem.BiddingInfoBest;
import static com.GalleryAuction.Item.HttpClientItem.BiddingWin;

public class ArtistAuctionCompleteUi extends Activity {
    ImageView iv, iv2;
    TextView tv1, tv2, tv3, tv0;
    Button btn1, btn2;
    String auckey, bidprice, userid, title, image;
    long min_bidding;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    Bitmap bmImg;
    Bitmap bmImg2;
    back task;
    back2 task2;
    String imgUrl = "http://221.156.54.210:8989/NFCTEST/art_images/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_artistauction_complete);
        iv = (ImageView) findViewById(R.id.artimageconfirm_img);
        iv2 = (ImageView) findViewById(R.id.userimage_complete);
        //그림 이름
        tv1 = (TextView) findViewById(R.id.artinfoconfirm_txt);
        tv0 = (TextView) findViewById(R.id.winningbiderconfirm_txt2);
        // 그림 최고입찰가
        tv2 = (TextView) findViewById(R.id.winningbidconfirm_txt);
        // 그림 최고입찰가 정한 user 아이디
        tv3 = (TextView) findViewById(R.id.winningbiderconfirm_txt);
        btn1 = (Button) findViewById(R.id.artistwinningbid_btn);
        btn2 = (Button) findViewById(R.id.artistwinningbid_x_btn);
        task = new back();
        task2 = new back2();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);
        Intent intent = getIntent();
        auckey = intent.getStringExtra("auckey");
        title = intent.getStringExtra("title");
        image = intent.getStringExtra("image");
        task.execute(imgUrl + image);

        try {
            JSONObject job = new JSONObject(BiddingInfoBest(auckey));
            Log.d("bidprice", "" + job);

            bidprice = job.get("bid_price").toString();
            userid = job.get("user_id").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        min_bidding = bidprice == null ? 0 : Long.parseLong(bidprice);
        //()
        if ((bidprice == null || bidprice.equals("")) && (userid == null || userid.equals(""))) {
            tv0.setText("입찰자 : ");
            tv1.setText(title);
            tv2.setText("최고입찰가 : " + min_bidding + "원으로 입찰가가 없습니다.");
            tv3.setText("없습니다");
            btn1.setVisibility(View.GONE);
        } else {
            tv1.setText(title);
            tv2.setText("최고입찰가 : " + min_bidding + "원");
            tv0.setText("입찰자 : ");
            tv3.setText(userid);

            task2.execute(imgUrl + "bidder.png");

        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ArtistAuctionCompleteUi.this);
                alert.setMessage("최고 입찰가는" + min_bidding + "원 입니다.\n낙찰하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BiddingWin(auckey);
                        finish();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertdialog = alert.create();
                alertdialog.show();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Artist_auc_cancle(auckey);
                finish();
            }
        });
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

    private class back extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmImg;
        }
        protected void onPostExecute(Bitmap img){
            iv.setImageBitmap(bmImg);
        }
    }
    private class back2 extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                bmImg2 = BitmapFactory.decodeStream(is);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmImg2;
        }
        protected void onPostExecute(Bitmap img){
            iv2.setImageBitmap(bmImg2);
        }
    }
}
