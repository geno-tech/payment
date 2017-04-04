package com.GalleryAuction.Artist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.geno.bill_folder.R;

import java.util.ArrayList;

/**
 * Created by GOD on 2017-04-03.
 */

public class ArtistArtInfoAdapter  extends BaseAdapter{
    private ArrayList<ArtistArtInfoItem> itemArrayList = new ArrayList<ArtistArtInfoItem>();
    TextView tv1, tv2;
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
            convertView = inflater.inflate(R.layout.galleryartinfo_listview_item, parent, false);
        }
        tv1 = (TextView)convertView.findViewById(R.id.artinfoitem1_txt);
        tv2 = (TextView)convertView.findViewById(R.id.artinfoitem2_txt);
        ArtistArtInfoItem artistArtInfoItem = itemArrayList.get(position);
        tv1.setText(artistArtInfoItem.getTitle());
        tv2.setText(artistArtInfoItem.getTime());
        return convertView;
    }

    public void addItem(String title, String time) {
        ArtistArtInfoItem item = new ArtistArtInfoItem();

        item.setTitle(title);
        item.setTime(time);
        itemArrayList.add(item);
    }
}
