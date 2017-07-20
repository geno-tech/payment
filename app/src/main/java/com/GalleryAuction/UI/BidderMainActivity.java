package com.GalleryAuction.UI;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.GalleryAuction.Adapter.BidderGoingAdapter;
import com.GalleryAuction.Adapter.GridAdapter;
import com.GalleryAuction.Dialog.TagExplanationDialog;
import com.GalleryAuction.Item.BidderGoingItem;
import com.GalleryAuction.Item.GridViewItem;
import com.geno.bill_folder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.GalleryAuction.Client.ImageViewItem.getImageBitmap;
import static com.GalleryAuction.Client.TagInfoClient.toHexString;
import static com.GalleryAuction.Item.HttpClientItem.ArtAlbumSelect;
import static com.GalleryAuction.Item.HttpClientItem.ArtInfo;
import static com.GalleryAuction.Item.HttpClientItem.AuctionProgress;

public class BidderMainActivity extends AppCompatActivity implements View.OnClickListener {
    ListView listView;
    BidderGoingAdapter bidderGoingAdapter;
    LinearLayout SeeLayout;
    Button SeeBtn;
    String name, title, auc_end, auc_start, art_content, userID, image;
    String[] start, end, start_hhmm, end_hhmm;
    GridViewItem gridView;
    GridAdapter gridAdapter;
    String imgUrl = "http://221.156.54.210:8989/NFCTEST/art_images/";
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_biddermain_activity);
        SeeBtn = (Button)findViewById(R.id.Bidder_Main_See_List_btn);
        SeeLayout = (LinearLayout)findViewById(R.id.Bidder_Main_See_List_layout);
        listView = (ListView)findViewById(R.id.bidder_going_listview);
        gridView = (GridViewItem)findViewById(R.id.Bidder_Main_See_List_gridview);
        gridView.setEnabled(false);
        gridAdapter = new GridAdapter();
        bidderGoingAdapter = new BidderGoingAdapter();
        listView.setAdapter(bidderGoingAdapter);
        SeeBtn.setOnClickListener(this);
        Intent intent = new Intent(BidderMainActivity.this, TagExplanationDialog.class);
        startActivity(intent);
        Intent intent2 = getIntent();
        userID = intent2.getStringExtra("userID");
        try {
            JSONArray ja = new JSONArray(AuctionProgress("", "art"));

            for (int i = 0; i < ja.length(); i++) {

                JSONObject job = (JSONObject) ja.get(i);
                title = job.get("art_title").toString();
                name = job.get("USER_NAME").toString();
                auc_start = job.get("auc_start").toString();
                auc_end = job.get("auc_end").toString();
                art_content = job.get("ART_CONTENT").toString();
                start = auc_start.split(" ");
                start_hhmm = start[1].split(":");

                end = auc_end.split(" ");
                end_hhmm = end[1].split(":");

                bidderGoingAdapter.addItem(title, name, "진행시간 : " +  start_hhmm[0] + "시" + start_hhmm[1] + "분" + " - " + end_hhmm[0] + "시" + end_hhmm[1] + "분");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            JSONArray ja = new JSONArray(ArtAlbumSelect(userID));

            for (int i = 0; i < ja.length(); i++) {

                JSONObject job = (JSONObject) ja.get(i);
                title = job.get("art_title").toString();
                image = job.get("art_image").toString();

                gridAdapter.addItem(title , getImageBitmap(imgUrl+image));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gridView.setAdapter(gridAdapter);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent3 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent3, 0);
    }
    @Override
    protected void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }
    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            final byte[] tagId = tag.getId();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intent0 = getIntent();
                    String userID = intent0.getStringExtra("userID");
                    Intent intent = new Intent(BidderMainActivity.this, ArtInformation.class);
                    intent.putExtra("artinfo",ArtInfo(toHexString(tagId)));
                    intent.putExtra("userID",userID);
                    intent.putExtra("name",name);


                    //Log.d("tag",ArtInfo(toHexString(tagId)));
                    startActivity(intent);
                    finish();
                }
            }).start();

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Bidder_Main_See_List_btn :
                if (SeeLayout.getVisibility() == View.VISIBLE) {
                    SeeLayout.setVisibility(View.GONE);
                } else {
                    SeeLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
}
