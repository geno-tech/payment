package com.GalleryAuction.Artist.AuctionList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.geno.bill_folder.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

/**
 * Created by GOD on 2017-04-03.
 */

public class ArtistAuctionAdapter  extends BaseAdapter{
    private ArrayList<ArtistAuctionItem> itemArrayList = new ArrayList<ArtistAuctionItem>();
    ArtistAuctionDetailAdapter artistAuctionAdapter = new ArtistAuctionDetailAdapter();

    TextView tv1, tv2, tv3;
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
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.gallery_auction_listview_item, parent, false);
        }
        tv1 = (TextView)convertView.findViewById(R.id.auctionitem1_txt);
        tv2 = (TextView)convertView.findViewById(R.id.auctionitem2_txt);
        tv3 = (TextView)convertView.findViewById(R.id.auctionitem3_txt);
        ArtistAuctionItem artistAuctionItem = itemArrayList.get(position);

        if (artistAuctionItem.getAuckey() == "0") {
            tv1.setText(artistAuctionItem.getTitle());
            tv2.setText("경매 진행중이 아닙니다.");
            tv2.setTextSize(13);
            tv3.setText(artistAuctionItem.getTime());

        } else {
            if (artistAuctionItem.getNowbidding() == "0"){
                tv1.setText(artistAuctionItem.getTitle());
                tv2.setText("입찰된 금액이 없습니다.");
                tv2.setTextSize(13);
                tv3.setText(artistAuctionItem.getTime());
            } else{
                tv1.setText(artistAuctionItem.getTitle());
                tv2.setText(artistAuctionAdapter.currentpoint(artistAuctionItem.getNowbidding()) + "원");
                tv2.setTextSize(20);
                tv3.setText(artistAuctionItem.getTime());
            }
        }
        return convertView;
    }

    public void addItem(String title, String nowbidding,String time, String auction, String auckey) {
        ArtistAuctionItem item = new ArtistAuctionItem();

        item.setTitle(title);
        item.setNowbidding(nowbidding);
        item.setTime(time);
        item.setAuction(auction);
        item.setAuckey(auckey);
        itemArrayList.add(item);
    }

}
