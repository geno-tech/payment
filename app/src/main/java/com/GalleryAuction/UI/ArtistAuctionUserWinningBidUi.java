package com.GalleryAuction.UI;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.bill_folder.R;

import static com.GalleryAuction.Item.HttpClientItem.BiddingWinArtistAgree;

public class ArtistAuctionUserWinningBidUi extends Activity {
    TextView tv1;
    Button btn1, btn2;
    String userID, artistID, auckey;
    NfcAdapter  nfcAdapter;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartistauction_userwinningbid);
        tv1 = (TextView) findViewById(R.id.artistcontract_txt);
        btn1 = (Button)findViewById(R.id.artistcontract_agree_btn);
        btn2 = (Button)findViewById(R.id.artistcontract_x_btn);

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
