package com.GalleryAuction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.bill_folder.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ArtInformation extends Activity implements View.OnClickListener{
    Button btn1, btn2;
    String username, test2, image, key;
    TextView tv1, tv2;
    ImageView imView;
    String imgUrl = "http://59.3.109.220:9998/NFCTEST/art_images/";
    Bitmap bmImg;
    back task;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartinformation);
        btn1 = (Button)findViewById(R.id.auctionok_btn);
        btn2 = (Button)findViewById(R.id.auctionX_btn);
        tv1 = (TextView)findViewById(R.id.artname_txt);
        tv2 = (TextView)findViewById(R.id.artcontents_txt);
        task = new back();
        imView = (ImageView) findViewById(R.id.imageView);
        dbHelper = new DBHelper(getApplicationContext(), "MoneyBook.db", null, 1);

        Intent intent = getIntent();
        String tagid = intent.getStringExtra("tagid").toString();
        JSONObject job = null;
        try {
            job = new JSONObject(tagid);
            username = job.get("user_name").toString();
            test2 = job.get("user_join_time").toString();
            image = job.get("image").toString();
            key = job.get("test_seq").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        tv1.setText(username);
        tv2.setText(test2);
        task.execute(imgUrl+image);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.auctionok_btn:
                Intent intent = new Intent(ArtInformation.this, BidderInfo.class);

                startActivity(intent);
                finish();
                break;
            case R.id.auctionX_btn:
//                SharedPreferences preferences = getSharedPreferences("KEY", 0);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putString("key", key);
//                if (editor.commit()){
//                    Toast.makeText(ArtInformation.this, key, Toast.LENGTH_SHORT).show();
//                }
                JSONObject job = null;
                try {
                    job = new JSONObject(Artinfo(key));
                    username = job.get("user_name").toString();
                    test2 = job.get("user_join_time").toString();
                    image = job.get("image").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dbHelper.insert(test2,image,username);
//                Log.d("aa",dbHelper.getResult());

                finish();
                break;
        }
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
    private String Artinfo(String msg) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:9998/NFCTEST/NewFile.jsp";

        DefaultHttpClient client = new DefaultHttpClient();
        try {

            HttpPost post = new HttpPost(URL + "?msg=" + msg);
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 3000);
            HttpResponse response = client.execute(post);
            BufferedReader bufreader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(),
                            "utf-8"));

            String line = null;
            String result = "";

            while ((line = bufreader.readLine()) != null) {
                result += line;

            }
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}