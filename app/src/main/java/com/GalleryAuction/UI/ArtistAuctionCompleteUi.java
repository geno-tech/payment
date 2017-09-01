package com.GalleryAuction.UI;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.hardware.fingerprint.FingerprintManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Rectangle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.GalleryAuction.Client.FingerprintAuthenticationDialogFragment5;
import com.GalleryAuction.Dialog.ContractDialog;
import com.geno.payment.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


import static com.GalleryAuction.Item.HttpClientItem.Artist_auc_cancle;
import static com.GalleryAuction.Item.HttpClientItem.BiddingInfoBest;
import static com.GalleryAuction.Item.HttpClientItem.BiddingWin;
import static com.GalleryAuction.Item.HttpClientItem.PDFTest;
import static com.GalleryAuction.Item.HttpClientItem.contractor;


public class ArtistAuctionCompleteUi extends AppCompatActivity {
    ImageView iv, iv2;
    TextView tv1, tv2, tv3, tv0;
    Button btn1, btn2, btn3;
    String auckey, bidprice, userid, title, image, artistID, bidding, buyername, buyerhp, artistname, artisthp, buyerID;
    long min_bidding;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    Bitmap bmImg;
    back task;
    String imgUrl = "http://183.105.72.65:28989/NFCTEST/art_images/";
    TextToSpeech tts;
    String contract = "";

    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    public static final String DEFAULT_KEY_NAME = "default_key";
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_artistauction_complete);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

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
        btn3 = (Button) findViewById(R.id.artistwinningbid_cancel_btn);
        task = new back();
        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);
        final Intent intent = getIntent();
        auckey = intent.getStringExtra("auckey");
        title = intent.getStringExtra("title");
        image = intent.getStringExtra("image");
        artistID = intent.getStringExtra("artistID");
        bidding = intent.getStringExtra("bidding");
        artistname = intent.getStringExtra("artistname");
        artisthp = intent.getStringExtra("artisthp");
        buyername = intent.getStringExtra("buyername");
        buyerhp = intent.getStringExtra("buyerhp");
        buyerID = intent.getStringExtra("buyerID");
        task.execute(imgUrl + image);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent3 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent3, 0);

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


        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date dt = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
                Log.d("@@@@@@@@@", buyerhp + buyername + artisthp + artistname);

                AlertDialog.Builder alert = new AlertDialog.Builder(ArtistAuctionCompleteUi.this);
                alert.setMessage("최고 구매가는" + min_bidding + "원 입니다.\n판매하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    PDFTest(buyerID, buyerhp, buyername, artistID, artisthp, artistname, title);
                    Intent intent1 = new Intent(ArtistAuctionCompleteUi.this, ContractDialog.class);
                    intent1.putExtra("artistID", artistID);
                    intent1.putExtra("buyerID", buyerID);
                    intent1.putExtra("buyerhp", buyerhp);
                    intent1.putExtra("buyername", buyername);
                    intent1.putExtra("artisthp", artisthp);
                    intent1.putExtra("artistname", artistname);
                    intent1.putExtra("title", title);
                    intent1.putExtra("auckey", auckey);
                    startActivity(intent1);
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
                Intent intent1 = new Intent(ArtistAuctionCompleteUi.this, ArtistMainActivity.class);
                intent1.putExtra("artistID", artistID);
                startActivity(intent1);
                finish();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ArtistAuctionCompleteUi.this);
                alert.setMessage("취소하시겠습니까?").setCancelable(false).setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Artist_auc_cancle(auckey);
                        Intent intent1 = new Intent(ArtistAuctionCompleteUi.this, ArtistMainActivity.class);
                        intent1.putExtra("artistID", artistID);
                        startActivity(intent1);
                        finish();
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertdialog = alert.create();
                alertdialog.show();
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
//    private class back2 extends AsyncTask<String, Integer, Bitmap> {
//        @Override
//        protected Bitmap doInBackground(String... urls) {
//            // TODO Auto-generated method stub
//            try {
//                URL myFileUrl = new URL(urls[0]);
//                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
//                conn.setDoInput(true);
//                conn.connect();
//
//                InputStream is = conn.getInputStream();
//                bmImg2 = BitmapFactory.decodeStream(is);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return bmImg2;
//        }
//        protected void onPostExecute(Bitmap img){
//            iv2.setImageBitmap(bmImg2);
//        }
//    }
private void ttsUnder20(String text) {
    HashMap<String, String> map = new HashMap<>();
    map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
    tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
}

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }



}
