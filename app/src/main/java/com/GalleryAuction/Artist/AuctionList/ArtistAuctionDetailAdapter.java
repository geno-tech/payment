package com.GalleryAuction.Artist.AuctionList;

import android.content.Context;
import android.util.Log;
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

public class ArtistAuctionDetailAdapter  extends BaseAdapter{
    private ArrayList<ArtistAuctionItem> itemArrayList = new ArrayList<ArtistAuctionItem>();
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

        tv3.setText(artistAuctionItem.getTime());

                tv2.setText(currentpoint(artistAuctionItem.getTitle())+"원");
                tv1.setText(artistAuctionItem.getNowbidding());
        Log.d("abcde", artistAuctionItem.getTitle());
        Log.d("abcde", artistAuctionItem.getNowbidding());


        return convertView;
    }

    public void addItem( String nowbidding, String title, String time) {
        ArtistAuctionItem item = new ArtistAuctionItem();

        item.setTitle(nowbidding);
        item.setNowbidding(title);
        item.setTime(time);
        itemArrayList.add(item);
    }

    public static String currentpoint(String result) {

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator(',');

        DecimalFormat df = new DecimalFormat("###,###,###,###");
        df.setDecimalFormatSymbols(dfs);

        try {
            double inputNum = Double.parseDouble(result);
            result = df.format(inputNum).toString();
        } catch (NumberFormatException e) {
            // TODO: handle exception
        }

        return result;
    }



}
