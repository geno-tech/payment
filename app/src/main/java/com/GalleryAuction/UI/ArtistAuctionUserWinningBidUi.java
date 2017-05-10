package com.GalleryAuction.UI;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.bill_folder.R;

import java.util.HashMap;
import java.util.Locale;

import static com.GalleryAuction.Item.HttpClientItem.BiddingWinArtistAgree;
import static com.GalleryAuction.Item.HttpClientItem.BiddingWinArtistCancel;
import static com.GalleryAuction.Item.HttpClientItem.BiddingWinUserCancel;

public class ArtistAuctionUserWinningBidUi extends Activity {
    TextView tv1;
    Button btn1, btn2;
    String userID, artistID, auckey;
    NfcAdapter  nfcAdapter;
    PendingIntent pendingIntent;
    TextToSpeech tts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartistauction_userwinningbid);
        tv1 = (TextView) findViewById(R.id.artistcontract_txt);
        btn1 = (Button)findViewById(R.id.artistcontract_agree_btn);
        btn2 = (Button)findViewById(R.id.artistcontract_x_btn);
        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        tv1.setText(getString(R.string.agreement_msg));
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        artistID = intent.getStringExtra("artistID");
        auckey = intent.getStringExtra("auckey");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BiddingWinArtistAgree(auckey);
                finish();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert2 = new AlertDialog.Builder(ArtistAuctionUserWinningBidUi.this);
                alert2.setMessage("거부하시면 가계약금을 받은 가계약금이 빠져나갑니다. \n그래도 거부하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BiddingWinArtistCancel(auckey);
                        Toast.makeText(ArtistAuctionUserWinningBidUi.this, "낙찰을 취소하였습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert2.setCancelable(false);
                alert2.show();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(getString(R.string.agreement_msg).toString());
        } else {
            ttsUnder20(getString(R.string.agreement_msg).toString());
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
            Toast.makeText(this, "[갤러리옥션 - 태그하기]에서 태그하시오", Toast.LENGTH_SHORT).show();
        }
        Log.d("TAGTEST : ", ""+ tag);
    }
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
