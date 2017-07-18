package com.GalleryAuction.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.GalleryAuction.Item.ArtistArtInfoItem;
import com.GalleryAuction.Item.BidderGoingItem;
import com.geno.bill_folder.R;

import java.util.ArrayList;

/**
 * Created by GOD on 2017-04-03.
 */

public class BidderGoingAdapter  extends BaseAdapter{
    private ArrayList<BidderGoingItem> itemArrayList = new ArrayList<BidderGoingItem>();
    TextView name, title, time;

    @Override
    public int getCount() {
        return itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int pos = position;
        Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_bidder_auctiongoing_item, parent, false);
        }
        name = (TextView)convertView.findViewById(R.id.bidder_auctiongoing_item_name);
        title = (TextView)convertView.findViewById(R.id.bidder_auctiongoing_item_arttitle);
        time = (TextView)convertView.findViewById(R.id.bidder_auctiongoing_item_time);
        BidderGoingItem bidderGoingItem = itemArrayList.get(position);
        name.setText(bidderGoingItem.getName());
        title.setText(bidderGoingItem.getTitle());
        time.setText(bidderGoingItem.getTime());
        return convertView;
    }

    public void addItem(String name, String title , String time) {
        BidderGoingItem item = new BidderGoingItem();

        item.setName(name);
        item.setTitle(title);
        item.setTime(time);
    }
}
