package com.GalleryAuction.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.GalleryAuction.Client.ImageViewItem;
import com.GalleryAuction.Dialog.WebViewAddressDialog;
import com.geno.bill_folder.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import static com.GalleryAuction.Item.HttpClientItem.BidBuy;
import static com.GalleryAuction.Item.HttpClientItem.BiddingWinArtistAgree;

public class BidderAddress extends Activity {
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    Button payment_btn, OK_btn, Exit_btn;
    String title,image,auckey,aucstatus,artistname,bidprice,userID,bidkey;
    TextView title_name_txt, price_txt;
    ImageView imView;
    String imgUrl = "http://221.156.54.210:8989/NFCTEST/art_images/";
    Bitmap bmImg;
    back task;
    private EditText et_address, et_address_detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_bidderaddress);
        OK_btn = (Button)findViewById(R.id.bidderaddress_Okbtn);
        Exit_btn = (Button)findViewById(R.id.bidderaddress_Exitbtn);
        payment_btn = (Button)findViewById(R.id.bidderaddress_winningbid_payment_btn);
        et_address = (EditText)findViewById(R.id.et_address);
        et_address_detail = (EditText)findViewById(R.id.et_address_detail);
        title_name_txt = (TextView)findViewById(R.id.bidderinfo_title_name);
        price_txt = (TextView)findViewById(R.id.bidderaddress_winningbid_txt);
        imView = (ImageView)findViewById(R.id.bidderaddress_img);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        image = intent.getStringExtra("image");
        auckey = intent.getStringExtra("auckey");
        aucstatus = intent.getStringExtra("aucstatus");
        artistname = intent.getStringExtra("artistname");
        bidprice = intent.getStringExtra("bidprice");
        userID = intent.getStringExtra("userID");
        bidkey = intent.getStringExtra("bidkey");
        task = new back();
        task.execute(imgUrl+image);
        title_name_txt.setText(title + " - " + artistname);
        price_txt.setText(currentpoint(bidprice) + "원");
        et_address.setClickable(false);
        et_address.setFocusable(false);
        if (aucstatus.equals("7")) {
            payment_btn.setEnabled(false);
            payment_btn.setText("입금 완료");
            payment_btn.setTextColor(Color.WHITE);
            payment_btn.setBackgroundColor(Color.DKGRAY);

        }
        OK_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!payment_btn.getText().equals("입금 완료")) {
                    Toast.makeText(BidderAddress.this, "최종으로 입금 된 금액을 결제 해 주시길 바랍니다.", Toast.LENGTH_SHORT).show();
                } else if (et_address.getText().toString().length() == 0 || et_address_detail.getText().toString().length() == 0) {
                    Toast.makeText(BidderAddress.this, "주소를 입력해 주시길 바랍니다.", Toast.LENGTH_SHORT).show();
                } else if (payment_btn.getText().equals("입금 완료") && (et_address.getText().toString().length() != 0 || et_address_detail.getText().toString().length() != 0)){
                    BiddingWinArtistAgree(auckey);
                    //String address = et_address.getText().toString() + et_address_detail.getText().toString();
                    String address = et_address.getText().toString() + " " + et_address_detail.getText().toString();
                    Log.d("@@@@@@@", bidkey + auckey +bidprice+userID+address);
                    BidBuy(bidkey ,auckey ,bidprice ,userID , address);
                    Intent intent = new Intent(BidderAddress.this, BidderMainActivity.class);
                    intent.putExtra("userID", userID);

                    startActivity(intent);
                    finish();
                    Log.d("@@@@@@@",bidkey+ "," +auckey + "," +bidprice + "," +userID + "," +address);
                }
            }
        });
        Exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aucstatus = "6";
                Intent intent = new Intent(BidderAddress.this, BidderMainActivity.class);
                intent.putExtra("userID", userID);

                startActivity(intent);
                finish();
            }
        });
        payment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BidderAddress.this, IamPortWebViewBidding.class);
                intent.putExtra("title", title);
                intent.putExtra("image", image);
                intent.putExtra("auckey", auckey);
                intent.putExtra("aucstatus", aucstatus);
                intent.putExtra("artistname", artistname);
                intent.putExtra("bidprice", bidprice);
                intent.putExtra("userID", userID);
                intent.putExtra("bidkey",bidkey);
                startActivity(intent);
                finish();
            }
        });
        Button btn_search = (Button)findViewById(R.id.button);

        if (btn_search != null)
            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(BidderAddress.this, WebViewAddressDialog.class);
                    startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
                }
            });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode){

            case SEARCH_ADDRESS_ACTIVITY:

                if(resultCode == RESULT_OK){

                    String data = intent.getExtras().getString("data");
                    if (data != null)
                        et_address.setText(data);

                }
                break;

        }

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
