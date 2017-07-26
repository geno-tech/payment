package com.GalleryAuction.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.GalleryAuction.Item.ArtInfoItem;
import com.GalleryAuction.Item.WinningBidInfoItem;
import com.geno.payment.R;

import java.util.ArrayList;

public class WinningBidInfoAdapter extends BaseAdapter {
    // 문자열 보관 ArrayList
    private ArrayList<WinningBidInfoItem> arrayList = new ArrayList<WinningBidInfoItem>() ;
    TextView tv1, tv2, tv3;
    ImageView imageView;
    public WinningBidInfoAdapter() {

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.galleryartinfo_listview_item, parent, false);
        }
        imageView = (ImageView) convertView.findViewById(R.id.artinfoitem_image);
        tv1 = (TextView) convertView.findViewById(R.id.artinfoitem1_txt);
        tv2 = (TextView) convertView.findViewById(R.id.artinfoitem2_txt);
        WinningBidInfoItem item = arrayList.get(position);
        tv1.setText(item.getMybest() + "원");
        tv2.setText(item.getTime());
        imageView.setVisibility(View.GONE);






        return convertView;
    }

    public void addItem(String mybest, String time) {
        WinningBidInfoItem item = new WinningBidInfoItem();

        item.setMybest(mybest);
        item.setTime(time);
        arrayList.add(item);
    }


    public void removeitem(String icon, String title, String auckey) {
        ArtInfoItem item = new ArtInfoItem();
        item.setIcon(icon);
        item.setTitle(title);
        item.setAuckey(auckey);
        arrayList.remove(item);
    }

    public void refreshAdapter(ArrayList<WinningBidInfoItem> items) {

        this.arrayList.clear();
        this.arrayList.addAll(items);
        notifyDataSetChanged();

    }
}
