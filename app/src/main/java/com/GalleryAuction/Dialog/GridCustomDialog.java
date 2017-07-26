package com.GalleryAuction.Dialog;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;

import com.GalleryAuction.Adapter.GridAdapter;
import com.GalleryAuction.UI.ArtistMainActivity;
import com.geno.payment.R;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.GalleryAuction.Client.ImageViewItem.getImageBitmap;
import static com.GalleryAuction.Item.HttpClientItem.ArtistArtInfo;
import static com.GalleryAuction.Item.HttpClientItem.AuctionProgress;

public class GridCustomDialog extends Activity {

    private GridView gridView;
    GridAdapter adapter;
    private String artistID, image, title, artkey, auckey;
    String imgUrl = "http://221.156.54.210:8989/NFCTEST/art_images/";

    private View.OnClickListener mLeftClickListener;
    private View.OnClickListener mRightClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.gallery_gridcustom_dialog);
        Intent intent = getIntent();
        artistID = intent.getStringExtra("artistID");
        adapter = new GridAdapter();

        gridView = (GridView) findViewById(R.id.gridView1);

        // 제목과 내용을 생성자에서 셋팅한다.
        try {
            JSONArray ja = new JSONArray(AuctionProgress(artistID, "inactive"));

            for (int i = 0; i < ja.length(); i++) {

                JSONObject job = (JSONObject) ja.get(i);
                title = job.get("art_title").toString();
                image = job.get("art_image").toString();
                artkey = job.get("art_seq").toString();
                    adapter.addItem(title, getImageBitmap(imgUrl + image));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try {
                    JSONArray ja = new JSONArray(AuctionProgress(artistID, "inactive"));

                        JSONObject job = (JSONObject) ja.get(i);
                        title = job.get("art_title").toString();
                    artkey = job.get("art_seq").toString();
                    auckey = job.get("auc_seq").toString();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent2 = new Intent(GridCustomDialog.this, ArtistMainActivity.class);
                intent2.putExtra("artistID", artistID);
                intent2.putExtra("title", title);
                intent2.putExtra("artkey", artkey);
                intent2.putExtra("auckey", auckey);
                Log.d("@@@@@", artkey);
                startActivity(intent2);
                finish();
            }
        });

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent1 = new Intent(GridCustomDialog.this, ArtistMainActivity.class);
        title = null;
        intent1.putExtra("artistID", artistID);
        intent1.putExtra("title", title);
        intent1.putExtra("artkey", artkey);
        intent1.putExtra("auckey", auckey);
        startActivity(intent1);
        finish();
        return super.onKeyDown(keyCode, event);
    }

}

