package com.GalleryAuction.UI;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geno.payment.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static com.GalleryAuction.Client.ImageViewItem.getImageBitmap;
import static com.GalleryAuction.Item.HttpClientItem.AuctionProgress;
import static com.GalleryAuction.Item.HttpClientItem.AuctionProgress_Buyer;

public class ArtistAddress extends AppCompatActivity {
    TextView tv_id, tv_name, tv_title, tv_price, tv_phone , tv_address;
    String userID, name, title, price, phone, address, image, artistID, auckey;
    Bitmap bmImg;
    Button btn;
    back task;
    String imgUrl = "http://221.156.54.210:8989/NFCTEST/art_images/";

    ImageView imView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_artist_address);
        btn = (Button)findViewById(R.id.artist_address_exitbtn);
        imView = (ImageView)findViewById(R.id.artist_address_img);
        tv_id = (TextView)findViewById(R.id.artist_address_id);
        tv_name = (TextView)findViewById(R.id.artist_address_name);
        tv_title = (TextView)findViewById(R.id.artist_address_imgtitle);
        tv_price = (TextView)findViewById(R.id.artist_address_price);
        tv_phone = (TextView)findViewById(R.id.artist_address_phone);
        tv_address = (TextView)findViewById(R.id.artist_address_address);


        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        image = intent.getStringExtra("image");
        artistID = intent.getStringExtra("artistID");
        price = intent.getStringExtra("bidding");
        auckey = intent.getStringExtra("auckey");
        task = new back();
        task.execute(imgUrl+image);
        try {
            JSONArray ja = new JSONArray(AuctionProgress_Buyer(auckey));

            for (int i = 0; i < ja.length(); i++) {

                JSONObject job = (JSONObject) ja.get(i);
                name = job.get("user_name").toString();
                userID = job.get("user_id").toString();
                phone = job.get("hp_number").toString();
                address = job.get("address").toString();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("@@@@@@@@@@@", name + ","+ userID+"," + phone+ ","+ address + "," + auckey);
        tv_id.setText(userID);
        tv_name.setText(name);
        tv_title.setText(title);
        tv_price.setText(currentpoint(price) + "원");
        tv_phone.setText(phone);
        tv_address.setText(address);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    public static String currentpoint(String result) {

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator(',');

        DecimalFormat df = new DecimalFormat("###,###,###,###");
        df.setDecimalFormatSymbols(dfs);

        try {
            double inputNum = Double.parseDouble(result);
            result = df.format(inputNum);
        } catch (NumberFormatException e) {
            // TODO: handle exception
        }

        return result;
    }
    private class back extends AsyncTask<String, Integer,Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            // TODO Auto-generated method stub
            try{
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection)myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);

            }catch(IOException e){
                e.printStackTrace();
            }
            return bmImg;
        }
        protected void onPostExecute(Bitmap img){
            imView.setImageBitmap(bmImg);
        }
    }
}
