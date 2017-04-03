package com.GalleryAuction.Bidder;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geno.bill_folder.R;

public class ArtInfoAdapter extends BaseAdapter {
    // 문자열 보관 ArrayList
    private ArrayList<ArtInfoItem> listViewItemList = new ArrayList<ArtInfoItem>() ;
    TextView tv2;
    WebView iv1;
    String imgUrl = "http://59.3.109.220:9998/NFCTEST/art_images/";
    Bitmap bmImg;
//    back task;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.galleryartinfo_listview_item, parent, false);
        }
//        task = new back();
        iv1 = (WebView) convertView.findViewById(R.id.artinfoitem1_img);
        tv2 = (TextView) convertView.findViewById(R.id.artinfoitem2_txt);
        ArtInfoItem artInfoItem = listViewItemList.get(position);
        iv1.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

//        task.execute(artInfoItem.getIcon());
        iv1.setBackgroundColor(0);
        iv1.setVerticalScrollBarEnabled(false);
        iv1.setHorizontalScrollBarEnabled(false);
        iv1.loadUrl(artInfoItem.getIcon());
        tv2.setText(artInfoItem.getTitle());
        return convertView;
    }

    public void addItem(String icon, String title) {
        ArtInfoItem item = new ArtInfoItem();
        item.setIcon(icon);
        item.setTitle(title);

        listViewItemList.add(item);
    }
//    private class back extends AsyncTask<String, Integer,Bitmap> {
//        @Override
//        protected Bitmap doInBackground(String... urls) {
//            // TODO Auto-generated method stub
//            try{
//                URL myFileUrl = new URL(urls[0]);
//                HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
//                conn.setDoInput(true);
//                conn.connect();
//
//                InputStream is = conn.getInputStream();
//                bmImg = BitmapFactory.decodeStream(is);
//
//            }catch(IOException e){
//                e.printStackTrace();
//            }
//            return bmImg;
//        }
//        protected void onPostExecute(Bitmap img){
//            iv1.setImageBitmap(bmImg);
//        }
//    }
    public void removeitem() {
        listViewItemList.clear();
    }
}
