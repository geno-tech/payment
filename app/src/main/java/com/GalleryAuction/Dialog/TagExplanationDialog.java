package com.GalleryAuction.Dialog;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.geno.payment.R;

public class TagExplanationDialog extends Activity {
    Button ext;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gallery_tagexplanation_dialog);
        getWindow().setGravity(Gravity.RIGHT);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent2 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent2, 0);
        ext = (Button)findViewById(R.id.tagexplanation_dialog_extbtn);
        ext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            Toast.makeText(this, "창을 닫아주세요", Toast.LENGTH_SHORT).show();
        }
        Log.d("TAGTEST : ", ""+ tag);
    }
}
