package com.GalleryAuction.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.GalleryAuction.Item.ArtistAuctionItem;
import com.geno.bill_folder.R;

import java.util.ArrayList;

/**
 * Created by GOD on 2017-04-03.
 */

public class ArtistAuctionAdapter  extends BaseAdapter{
    private ArrayList<ArtistAuctionItem> itemArrayList = new ArrayList<ArtistAuctionItem>();
    ArtistAuctionDetailAdapter artistAuctionAdapter = new ArtistAuctionDetailAdapter();

    TextView tv1, tv2, tv3;
    ImageView imageView;
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
        imageView = (ImageView)convertView.findViewById(R.id.auctionitem_image);
        ArtistAuctionItem artistAuctionItem = itemArrayList.get(position);
        imageView.setImageBitmap(artistAuctionItem.getImage());
        if (artistAuctionItem.getAuction().equals("0")) {
            tv1.setText(artistAuctionItem.getTitle());
            tv2.setText("경매 미등록상태");
            tv2.setTextSize(13);
            tv2.setTextColor(Color.BLACK);
            tv3.setText(artistAuctionItem.getTime());


        } else if (artistAuctionItem.getAuction().equals("1")){
            tv1.setText(artistAuctionItem.getTitle());
            tv2.setText("경매 취소상태");
            tv2.setTextSize(13);
            tv2.setTextColor(Color.BLACK);
            tv3.setText(artistAuctionItem.getTime());
        }else if (artistAuctionItem.getAuction().equals("2")){
            tv1.setText(artistAuctionItem.getTitle());
            tv2.setText("경매 대기상태");
            tv2.setTextSize(13);
            tv2.setTextColor(Color.BLACK);
            tv3.setText(artistAuctionItem.getTime());
        }else if (artistAuctionItem.getAuction().equals("3")){
            if (artistAuctionItem.getNowbidding().equals("0")){
                tv1.setText(artistAuctionItem.getTitle());
                tv2.setText("입찰금액 없음");
                tv2.setTextSize(13);
                tv2.setTextColor(Color.BLUE);
                tv3.setText(artistAuctionItem.getTime());

            } else{
                tv1.setText(artistAuctionItem.getTitle());
                tv2.setText(artistAuctionAdapter.currentpoint(artistAuctionItem.getNowbidding()) + "원");
                //tv2.setText("원");
                tv2.setTextSize(20);
                tv2.setTextColor(Color.GREEN);

                tv3.setText(artistAuctionItem.getTime());
            }
        } else if (artistAuctionItem.getAuction().equals("4")) {
            tv1.setText(artistAuctionItem.getTitle());
            tv2.setTextColor(Color.BLACK);
            tv2.setText("경매마감상태");
            tv2.setTextSize(13);
            tv3.setText(artistAuctionItem.getTime());

        } else if (artistAuctionItem.getAuction().equals("5")) {
            tv1.setText(artistAuctionItem.getTitle());
            tv2.setText("낙찰유저대기상태");
            tv2.setTextSize(13);
            tv3.setText(artistAuctionItem.getTime());
        } else if (artistAuctionItem.getAuction().equals("6")) {
            tv1.setText(artistAuctionItem.getTitle());
            tv2.setText("낙찰유저동의상태");
            tv2.setTextSize(13);
            tv3.setText(artistAuctionItem.getTime());
        }else if (artistAuctionItem.getAuction().equals("7")) {
            tv1.setText(artistAuctionItem.getTitle());
            tv2.setText("낙찰완료상태");
            tv2.setTextSize(13);
            tv3.setText(artistAuctionItem.getTime());
        }
        return convertView;
    }

    public void addItem(String title, String nowbidding, String time, String auction, String auckey , Bitmap image) {
        ArtistAuctionItem item = new ArtistAuctionItem();

        item.setTitle(title);
        item.setNowbidding(nowbidding);
        item.setTime(time);
        item.setAuction(auction);
        item.setAuckey(auckey);
        item.setImage(image);
        itemArrayList.add(item);
    }

    public  void clear(int position) {
        itemArrayList.remove(position);
    }

}
