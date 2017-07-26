package com.GalleryAuction.Adapter;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.GalleryAuction.Item.ArtInfoItem;
import com.geno.payment.R;

public class ArtInfoAdapter extends BaseAdapter {
    // 문자열 보관 ArrayList
    private ArrayList<ArtInfoItem> listViewItemList = new ArrayList<ArtInfoItem>() ;
    TextView tv1, tv2, tv3;
    ImageView imageView;
    public ArtInfoAdapter() {

    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.galleryartinfo_listview_item, parent, false);
        }
        tv1 = (TextView) convertView.findViewById(R.id.artinfoitem1_txt);
        tv2 = (TextView) convertView.findViewById(R.id.artinfoitem2_txt);
        tv3 = (TextView) convertView.findViewById(R.id.artinfoitem_add_txt);
        imageView = (ImageView) convertView.findViewById(R.id.artinfoitem_image);
        ArtInfoItem artInfoItem = listViewItemList.get(position);
        tv1.setText(artInfoItem.getIcon());
        tv2.setText(artInfoItem.getTitle());
        tv3.setText("");
        imageView.setImageBitmap(artInfoItem.getImage());
        //경매 X
        if (artInfoItem.getAuckey() == "1") {
            tv1.setTextColor(Color.BLACK);
            tv2.setTextColor(Color.BLACK);
            tv3.setTextColor(Color.BLACK);
            tv3.setText(Html.fromHtml("<u>경매중이 아닙니다</u>"));

        }
        else if (artInfoItem.getAuckey() == "1" && artInfoItem.getBidkey() != "0"){
            //경매취소 (한번이라도 경매했을때 )
            tv1.setTextColor(Color.BLACK);
            tv2.setTextColor(Color.BLACK);
            tv3.setTextColor(Color.BLACK);
            tv3.setText(Html.fromHtml("<u>경매중이 아닙니다</u>"));

        }
        else if (artInfoItem.getAuckey() == "3" &&artInfoItem.getBidkey() == "0"){
            //경매중일때
            tv1.setTextColor(Color.BLUE);
            tv2.setTextColor(Color.BLUE);
            tv3.setTextColor(Color.BLUE);
            tv3.setText(Html.fromHtml("<u>경매중(입찰하지않은 물품)</u>"));

        } else if (artInfoItem.getAuckey() == "2") {
            tv1.setTextColor(Color.GREEN);
            tv2.setTextColor(Color.GREEN);
            tv3.setTextColor(Color.GREEN);
            tv3.setText(Html.fromHtml("<u>경매 대기중</u>"));

        } else if (artInfoItem.getAuckey() == "3" && artInfoItem.getBidkey() != "0") {
                //입찰했을때
            tv1.setTextColor(Color.rgb(255,228,0));
            tv2.setTextColor(Color.rgb(255,228,0));
            tv3.setTextColor(Color.rgb(255,228,0));
            tv3.setText(Html.fromHtml("<u>경매중(입찰한 물품)</u>"));

        }
        else if (artInfoItem.getAuckey().equals("4")) {
            //마감됐을때
                tv1.setTextColor(Color.CYAN);
            tv2.setTextColor(Color.CYAN);
            tv3.setText(Html.fromHtml("<u>경매마감대기중</u>"));

        }
        else if (artInfoItem.getAuckey().equals("5")) {
            //마감됐을때
            if(artInfoItem.getBid().equals("2")){
                tv1.setTextColor(Color.rgb(134,42,0));
                tv2.setTextColor(Color.rgb(134,42,0));
                tv3.setTextColor(Color.rgb(134,42,0));
                tv3.setText(Html.fromHtml("<u>낙찰되지 않았습니다</u>"));

            }
            else if(artInfoItem.getBid().equals("3")){
                tv1.setTextColor(Color.rgb(255,187,0));
                tv2.setTextColor(Color.rgb(255,187,0));
                tv3.setTextColor(Color.rgb(255,187,0));
                tv3.setText(Html.fromHtml("<u>낙찰되었습니다</u>"));

            }
            else{
                tv1.setTextColor(Color.rgb(241,95,95));
                tv2.setTextColor(Color.rgb(241,95,95));
                tv3.setTextColor(Color.rgb(241,95,95));
                tv3.setText(Html.fromHtml("<u>입찰하지 않았습니다</u>"));

            }
        }
        else if (artInfoItem.getAuckey().equals("6")) {
            //마감됐을때
            if(artInfoItem.getBid().equals("2")){
                tv1.setTextColor(Color.rgb(134,42,0));
                tv2.setTextColor(Color.rgb(134,42,0));
                tv3.setTextColor(Color.rgb(134,42,0));
                tv3.setText(Html.fromHtml("<u>낙찰되지 않았습니다</u>"));
            }
            else if(artInfoItem.getBid().equals("3")){
                tv1.setTextColor(Color.rgb(255,187,0));
                tv2.setTextColor(Color.rgb(255,187,0));
                tv3.setTextColor(Color.rgb(255,187,0));
                tv3.setText(Html.fromHtml("<u>낙찰되었습니다</u>"));
            }
            else{
                tv1.setTextColor(Color.rgb(241,95,95));
                tv2.setTextColor(Color.rgb(241,95,95));
                tv3.setTextColor(Color.rgb(241,95,95));
                tv3.setText(Html.fromHtml("<u>입찰하지 않았습니다</u>"));
            }
            tv3.setText(Html.fromHtml(tv3.getText() + " <u>유저동의</u>"));


        } else if (artInfoItem.getAuckey().equals("7")) {
            //마감됐을때
            if(artInfoItem.getBid().equals("2")){
                tv1.setTextColor(Color.rgb(134,42,0));
                tv2.setTextColor(Color.rgb(134,42,0));
                tv3.setTextColor(Color.rgb(134,42,0));
                tv3.setText(Html.fromHtml("<u>낙찰되지 않았습니다</u>"));

            }
            else if(artInfoItem.getBid().equals("3")){
                tv1.setTextColor(Color.rgb(255,187,0));
                tv2.setTextColor(Color.rgb(255,187,0));
                tv3.setTextColor(Color.rgb(255,187,0));
                tv3.setText(Html.fromHtml("<u>낙찰되었습니다</u>"));

            }
            else{
                tv1.setTextColor(Color.rgb(241,95,95));
                tv2.setTextColor(Color.rgb(241,95,95));
                tv3.setTextColor(Color.rgb(241,95,95));
                tv3.setText(Html.fromHtml("<u>입찰하지 않았습니다</u>"));

            }
            tv3.setText(Html.fromHtml(tv3.getText() + " <u>경매완료</u>"));
        }
        else {
            tv1.setTextColor(Color.BLACK);
            tv2.setTextColor(Color.BLACK);
            tv3.setTextColor(Color.BLACK);
            tv3.setText(Html.fromHtml("<u>경매중이 아닙니다</u>"));

        }







        return convertView;
    }

    public void addItem(String icon, String title, String auckey, String bidkey, String bid , Bitmap image) {
        ArtInfoItem item = new ArtInfoItem();

        item.setIcon(icon);
        item.setTitle(title);
        item.setAuckey(auckey);
        item.setBidkey(bidkey);
        item.setBid(bid);
        item.setImage(image);

        listViewItemList.add(item);
    }



    public void removeitem(String icon, String title, String auckey) {
        ArtInfoItem item = new ArtInfoItem();
        item.setIcon(icon);
        item.setTitle(title);
        item.setAuckey(auckey);
        listViewItemList.remove(item);
    }

    public void refreshAdapter(ArrayList<ArtInfoItem> items) {

        this.listViewItemList.clear();
        this.listViewItemList.addAll(items);
        notifyDataSetChanged();

    }
}
