package com.GalleryAuction.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.GalleryAuction.Adapter.BidderGoingAdapter;
import com.GalleryAuction.Item.BidderGoingItem;
import com.geno.bill_folder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.GalleryAuction.Client.ImageViewItem.getImageBitmap;
import static com.GalleryAuction.Item.HttpClientItem.AuctionProgress;

public class BidderMainActivity extends AppCompatActivity {
    ListView listView;
    BidderGoingAdapter bidderGoingAdapter;
    String name, title, auc_end;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_biddermain_activity);

        listView = (ListView)findViewById(R.id.bidder_going_listview);
        bidderGoingAdapter = new BidderGoingAdapter();
        listView.setAdapter(bidderGoingAdapter);

        try {
            JSONArray ja = new JSONArray(AuctionProgress("", "art"));

            for (int i = 0; i < ja.length(); i++) {

                JSONObject job = (JSONObject) ja.get(i);
                title = job.get("art_title").toString();
                name = job.get("USER_NAME").toString();
                auc_end = job.get("auc_end").toString();
                bidderGoingAdapter.addItem(name, title, "ll");
                Log.d("@@@@", title + " " +name + " " +auc_end);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
