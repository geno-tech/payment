package com.GalleryAuction.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.GalleryAuction.Item.ArtistAuctionItem;
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
    ImageView iv;
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
            convertView = inflater.inflate(R.layout.gallery_artistdetail_item, parent, false);
        }
        iv = (ImageView) convertView.findViewById(R.id.bidderfaceimg);
        tv1 = (TextView)convertView.findViewById(R.id.detailitem1_txt);
        tv2 = (TextView)convertView.findViewById(R.id.detailitem2_txt);
        tv3 = (TextView)convertView.findViewById(R.id.detailitem3_txt);
        ArtistAuctionItem artistAuctionItem = itemArrayList.get(position);
        tv3.setText(artistAuctionItem.getTime());

                tv2.setText(currentpoint(artistAuctionItem.getTitle())+"원");
                tv1.setText(artistAuctionItem.getNowbidding());
        Log.d("abcde", artistAuctionItem.getTitle());
        Log.d("abcde", artistAuctionItem.getNowbidding());
        iv.setImageBitmap(artistAuctionItem.getImage());


        return convertView;
    }

    public void addItem( String nowbidding, String title, String time, Bitmap image) {
        ArtistAuctionItem item = new ArtistAuctionItem();

        item.setTitle(nowbidding);
        item.setNowbidding(title);
        item.setTime(time);
        item.setImage(image);
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
