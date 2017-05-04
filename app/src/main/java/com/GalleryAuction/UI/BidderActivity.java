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

import com.geno.bill_folder.R;

public class BidderActivity extends Activity implements View.OnClickListener {
    Button btn1, btn2, btn3;
    String userID;
    NfcAdapter  nfcAdapter;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallerybidderactivity);
        btn1 = (Button)findViewById(R.id.artinfor_list_btn);
        btn2 = (Button)findViewById(R.id.tag_btn);
//        btn3 = (Button)findViewById(R.id.winningbidlist_btn);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.artinfor_list_btn:

                Intent intent = new Intent(BidderActivity.this, ArtInfoTagList.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
                break;
            case R.id.tag_btn:
                Intent intent2 = new Intent(BidderActivity.this, TagInfo.class);
                intent2.putExtra("userID", userID);
                startActivity(intent2);
                break;
//            case R.id.winningbidlist_btn:
//                Intent intent3 = new Intent(BidderActivity.this, TagInfo.class);
//                intent3.putExtra("userID", userID);
//                startActivity(intent3);
//                finish();
//                break;
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
}
