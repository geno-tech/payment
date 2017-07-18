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
import com.GalleryAuction.Item.GridItem;
import com.geno.bill_folder.R;

import java.util.ArrayList;

/**
 * Created by GOD on 2017-04-03.
 */

public class GridAdapter  extends BaseAdapter{
    private ArrayList<GridItem> gridItemArrayList = new ArrayList<GridItem>();
    TextView tv;
    ImageView imageView;
    @Override
    public int getCount() {
        return gridItemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return gridItemArrayList.get(position);
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
            convertView = inflater.inflate(R.layout.gallery_gridadapter_item, parent, false);
        }
        tv = (TextView) convertView.findViewById(R.id.grid_txt_item);
        imageView = (ImageView)convertView.findViewById(R.id.grid_img_item);
        GridItem gridItem = gridItemArrayList.get(position);
        tv.setText(gridItem.getTitle());
        imageView.setImageBitmap(gridItem.getImage());
        return convertView;
    }

    public void addItem(String time , Bitmap image) {
        GridItem item = new GridItem();
        item.setTitle(time);
        item.setImage(image);
        gridItemArrayList.add(item);
    }
}
