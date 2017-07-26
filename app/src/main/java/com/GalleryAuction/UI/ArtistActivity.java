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
import android.widget.Toast;

import com.geno.payment.R;

public class ArtistActivity extends Activity implements View.OnClickListener {
    Button a1,a2;
    String artistID;
    NfcAdapter  nfcAdapter;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartistactivity);
//        a1 = (Button)findViewById(R.id.artinfo_btn_artist);
        a2 = (Button)findViewById(R.id.auctionlist_btn_artist);
//        a1.setOnClickListener(this);
        a2.setOnClickListener(this);
        Intent intent = getIntent();
        artistID = intent.getStringExtra("artistID");

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.artinfo_btn_artist:
//                Intent intent1 = new Intent(ArtistActivity.this, ArtistArtInformation.class);
//                intent1.putExtra("artistID", artistID);
//                startActivity(intent1);
//                break;
            case R.id.auctionlist_btn_artist:
                Intent intent2 = new Intent(ArtistActivity.this, ArtistAuctionInformation.class);
                intent2.putExtra("artistID", artistID);
                startActivity(intent2);
                break;

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
            Toast.makeText(this, "구매자 ID가 아닙니다.", Toast.LENGTH_SHORT).show();
        }
        Log.d("TAGTEST : ", ""+ tag);
    }
}
