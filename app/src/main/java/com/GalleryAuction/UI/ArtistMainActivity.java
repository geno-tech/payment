package com.GalleryAuction.UI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.GalleryAuction.Adapter.GridAdapter;
import com.GalleryAuction.Dialog.GridCustomDialog;
import com.GalleryAuction.Item.GridViewItem;
import com.ajantech.nfc_network.service.Login_tonek_WorkPart;
import com.geno.payment.R;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.GalleryAuction.Client.ImageViewItem.getImageBitmap;
import static com.GalleryAuction.Item.HttpClientItem.ArtAdd;
import static com.GalleryAuction.Item.HttpClientItem.ArtistArtInfo;
import static com.GalleryAuction.Item.HttpClientItem.Artist_auc_cancle;
import static com.GalleryAuction.Item.HttpClientItem.AuctionInfo;
import static com.GalleryAuction.Item.HttpClientItem.AuctionProgress;
import static com.GalleryAuction.Item.HttpClientItem.BiddingWin;

public class ArtistMainActivity extends AppCompatActivity implements View.OnClickListener, com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {
    Button add1, add2, add3, add4, pro1, add5, detail1, complete1;
    LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout_detail, linearLayout_detail2, linearLayout_complete1;
    String image, artistID, title, inTime, inDate, artkey, auckey, auc_end, auc_start, bidding, auc_status, min_bidding, artistname, artisthp, buyername, buyerhp, buyerID;
    com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd, tpd2;
    TextView data1, data2, count1;
    GridViewItem gridView, gridView_detail, gridView_complete;
    GridAdapter adapter, adapter_detail, adapter_complete;
    String imgUrl = "http://183.105.72.65:28989/NFCTEST/art_images/";
    long now, end, start, ne, HH, nH, mm, ss, es, se, HH_se, mm_se, ss_se, sH ;
    SimpleDateFormat sdf;
    Date date;
    Date date0;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    Handler handler2 = new Handler();
    int add = 0;
    int value;
    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryaction_artistmain);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));



        String data = "HHHHello";

        try {
            File path = Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_PICTURES);
            File f = new File(path, "external.txt"); // 경로, 파일명
            FileWriter write = new FileWriter(f, false);
            PrintWriter out = new PrintWriter(write);
            out.println(data);
            out.close();
            Log.d("@#@#@#", "저장완료  " + path);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // 파일을 서버로 보내는 부분
        try {
            HttpClient client = new DefaultHttpClient();
            String url = "http://183.105.72.65:28989/NFCTEST/MultipartEntity.jsp";
            HttpPost post = new HttpPost(url);
            File path = Environment.getExternalStoragePublicDirectory
                    (Environment.DIRECTORY_PICTURES);
            // FileBody 객체를 이용해서 파일을 받아옴
            File file = new File(path, "test.txt");
            FileBody bin = new FileBody(file);

            MultipartEntity multipart = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipart.addPart("images", bin);
            post.setEntity(multipart);
            client.execute(post);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        detail1 = (Button)findViewById(R.id.Artist_Main_Art_List_btn);
        linearLayout_detail = (LinearLayout)findViewById(R.id.Artist_Main_Art_List_layout);
        linearLayout_detail2 = (LinearLayout)findViewById(R.id.Artist_Main_Art_List_layout2);
        gridView_detail = (GridViewItem)findViewById(R.id.Artist_Main_Art_List_GridView);
        gridView_detail.setExpanded(true);
        adapter_detail = new GridAdapter();
        complete1 = (Button)findViewById(R.id.Artist_Main_Auction_Complete_btn);
        gridView_complete = (GridViewItem)findViewById(R.id.Artist_Main_Complete_GridView);
        gridView_complete.setExpanded(true);
        adapter_complete = new GridAdapter();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);
        add1 = (Button)findViewById(R.id.Artist_Main_Auction_Add_btn);
        add2 = (Button)findViewById(R.id.Artist_Main_Auction_Art_Add_btn);
        add3 = (Button)findViewById(R.id.Artist_Main_AuctionAdd_Start_btn);
        add4 = (Button)findViewById(R.id.Artist_Main_AuctionAdd_End_btn);
        add5 = (Button)findViewById(R.id.Artist_Main_AuctionAdd_Confirm_btn);

        pro1 = (Button)findViewById(R.id.Artist_Main_Auction_Time_btn);
        Context context = null;

        data1 = (TextView)findViewById(R.id.Artist_Main_Start_txt_secret);
        data2 = (TextView)findViewById(R.id.Artist_Main_End_txt_secret);
        count1 = (TextView)findViewById(R.id.Artist_main_Auction_Time_count_txt);
        linearLayout1 = (LinearLayout)findViewById(R.id.Artist_Main_Auction_Add_layout);
        linearLayout2 = (LinearLayout)findViewById(R.id.Artist_Main_Auction_Time_layout);
        linearLayout3 = (LinearLayout)findViewById(R.id.Artist_Main_Auction_Time_ArtList_layout);
        linearLayout_complete1 = (LinearLayout)findViewById(R.id.Artist_Main_Auction_Complete_layout2);
        gridView = (GridViewItem) findViewById(R.id.Artist_Main_Going_GridView);
        gridView.setExpanded(true);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final Intent intent = getIntent();
        adapter = new GridAdapter();
        artistID = intent.getStringExtra("artistID");
        try {
            JSONArray ja = new JSONArray(ArtistArtInfo(artistID));

            for (int i = 0 ; i  < ja.length(); i++){

                JSONObject job = (JSONObject) ja.get(i);
                title = job.get("art_title").toString();
                image = job.get("art_image").toString();
                artkey = job.get("art_seq").toString();
                adapter_detail.addItem(title, getImageBitmap(imgUrl+image));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gridView_detail.setAdapter(adapter_detail);

        try {
            JSONArray ja = new JSONArray(AuctionProgress(artistID, "complete"));

            for (int i = 0; i < ja.length(); i++) {

                JSONObject job = (JSONObject) ja.get(i);
                title = job.get("art_title").toString();
                image = job.get("art_image").toString();
                artkey = job.get("art_seq").toString();
                bidding = job.get("bid_price").toString();
                artistname = job.get("artist_name").toString();
                artisthp = job.get("artist_hp").toString();
                buyername = job.get("buyer_name").toString();
                buyerhp = job.get("user_hp").toString();
                buyerID = job.get("user_id").toString();
                min_bidding = bidding == "0"  ? "판매되지 않음" : "구매가 : " +bidding+ "원";

                adapter_complete.addItem(min_bidding, getImageBitmap(imgUrl + image));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gridView_complete.setAdapter(adapter_complete);
        gridView_complete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONArray ja = new JSONArray(AuctionProgress(artistID, "complete"));

                    JSONObject job = (JSONObject) ja.get(i);
                    title = job.get("art_title").toString();
                    auckey = job.get("auc_seq").toString();
                    auc_status = job.get("auc_status").toString();
                    bidding = job.get("bid_price").toString();
                    image = job.get("art_image").toString();
                    min_bidding = bidding == "0"  ? "판매되지 않음" : "구매가 : " +bidding+ "원";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (min_bidding.equals("판매되지 않음")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(ArtistMainActivity.this);
                    alert.setMessage(min_bidding + "입니다. 취소하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Artist_auc_cancle(auckey);
                            Intent intent = new Intent(ArtistMainActivity.this, ArtistMainActivity.class);
                            intent.putExtra("artistID", artistID);
                            startActivity(intent);
                            finish();
                        }
                    }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertdialog = alert.create();
                    alertdialog.show();
                } else {
                    if (auc_status.equals("4")) {
                        Intent intent1 = new Intent(ArtistMainActivity.this, ArtistAuctionCompleteUi.class);
                        intent1.putExtra("title", title);
                        intent1.putExtra("image", image);
                        intent1.putExtra("auckey", auckey);
                        intent1.putExtra("artistID", artistID);
                        intent1.putExtra("bidding", bidding);
                        intent1.putExtra("artistname", artistname);
                        intent1.putExtra("artisthp", artisthp);
                        intent1.putExtra("buyername", buyername);
                        intent1.putExtra("buyerhp", buyerhp);
                        intent1.putExtra("buyerID", buyerID);
                        startActivity(intent1);
                        finish();
                    } else if (auc_status.equals("5") || auc_status.equals("6")) {
                        Toast.makeText(ArtistMainActivity.this, "구매자의 입금을 기다리는 중 입니다.", Toast.LENGTH_SHORT).show();
                    } else if (auc_status.equals("7")) {
                        Intent intent1 = new Intent(ArtistMainActivity.this ,ArtistAddress.class);
                        intent1.putExtra("title", title);
                        intent1.putExtra("image", image);
                        intent1.putExtra("artistID", artistID);
                        intent1.putExtra("bidding", bidding);
                        intent1.putExtra("auckey", auckey);
                        Log.d("@@@@@@@@@@@@@@@@@@@@@@", title + image + artistID + bidding + auckey);
                        startActivity(intent1);

                    }
                }
            }
        });
        try {
            JSONArray ja = new JSONArray(AuctionProgress(artistID, "active"));

            for (int i = 0; i < ja.length(); i++) {

                JSONObject job = (JSONObject) ja.get(i);
                title = job.get("art_title").toString();
                image = job.get("art_image").toString();
                artkey = job.get("art_seq").toString();
                adapter.addItem(title, getImageBitmap(imgUrl + image));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    JSONArray ja = new JSONArray(AuctionProgress(artistID, "active"));

                    JSONObject job = (JSONObject) ja.get(i);
                    auc_start = job.get("auc_start").toString();
                    auc_end = job.get("auc_end").toString();
                    artkey = job.get("art_seq").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    date0 = sdf.parse(auc_start);
                    start = date0.getTime();
                    date = sdf.parse(auc_end);
                    end = date.getTime();

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                thread = new Thread();
                thread.start();

                final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);

                java.lang.Thread t = new java.lang.Thread(new Runnable() {
                    @Override
                    public void run() { // Thread 로 작업할 내용을 구현
                        es = end - start;
                        se = es/1000;  HH_se = se/3600; sH = se%3600; mm_se = sH/60; ss_se = sH%60;
                        HH_se = HH_se * 60;
                        now = System.currentTimeMillis();
                        long en = end-now;
                        ne = en/1000;  HH = ne/3600; nH = ne%3600; mm = nH/60; ss = nH%60;
                        HH = HH * 60;
                        value = (int) (HH_se + mm_se);
                        add = (int) (value -(HH + mm));
                        Log.d("!!!!!!", es +"," +en);
                        while(true) {
                            add += 1;
                            Log.d("@@@@@", add + ",,," + value);

                            handler.post(new Runnable() {
                                @Override
                                public void run() { // 화면에 변경하는 작업을 구현
                                    pb.setMax(value);
                                    pb.setProgress(add);
                                }
                            });

                            try {
                                java.lang.Thread.sleep(60000); // 시간지연
                            } catch (InterruptedException e) {    }
                        } // end of while
                    }
                });
                t.start(); // 쓰레드 시작

            }
        });

        detail1.setOnClickListener(this);
        add1.setOnClickListener(this);
        add2.setOnClickListener(this);
        add3.setOnClickListener(this);
        add4.setOnClickListener(this);
        pro1.setOnClickListener(this);
        add5.setOnClickListener(this);
        complete1.setOnClickListener(this);
        inDate   = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        title = intent.getStringExtra("title");
        artkey = intent.getStringExtra("artkey");
        auckey = intent.getStringExtra("auckey");
        if (title != null && (auckey.equals("0") ||auckey != null)) {
            add3.setEnabled(true);
            add4.setEnabled(true);
            linearLayout1.setVisibility(View.VISIBLE);
            add2.setText(title);
        } else {
            add3.setEnabled(false);
            add4.setEnabled(false);
            linearLayout1.setVisibility(View.GONE);
            add2.setText("그림 선택");
        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.Artist_Main_Art_List_btn :
                if (linearLayout_detail2.getVisibility() == View.VISIBLE) {
                    linearLayout_detail2.setVisibility(View.GONE);
                } else {
                    linearLayout_detail2.setVisibility(View.VISIBLE);
                }
                break;
            //Auction시작시간 설정하기 클릭
            case R.id.Artist_Main_Auction_Add_btn :
                if (linearLayout1.getVisibility() == View.VISIBLE) {
                    add3.setEnabled(false);
                    add4.setEnabled(false);
                    add2.setText("그림 선택");
                    linearLayout1.setVisibility(View.GONE);
                } else {
                    linearLayout1.setVisibility(View.VISIBLE);
                }
                break;

            //그림 선택 클릭
            case R.id.Artist_Main_Auction_Art_Add_btn :

                Intent intent = new Intent(ArtistMainActivity.this, GridCustomDialog.class);
                intent.putExtra("artistID", artistID);
                startActivity(intent);
                finish();
                break;
            //그림 Start 시간 클릭
            case R.id.Artist_Main_AuctionAdd_Start_btn :
                    Calendar now = Calendar.getInstance();

                tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                        ArtistMainActivity.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                );
                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d("TimePicker", "Dialog was cancelled");
                    }
                });
                tpd.show(getFragmentManager(), "Timepickerdialog");
                break;
            //그림 End 시간 클릭
            case R.id.Artist_Main_AuctionAdd_End_btn :
                Calendar now2 = Calendar.getInstance();
                tpd2 = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                        ArtistMainActivity.this,
                        now2.get(Calendar.HOUR_OF_DAY),
                        now2.get(Calendar.MINUTE),
                        false
                );
                tpd2.setAccentColor(Color.parseColor("#9C27B0"));

                tpd2.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d("TimePicker", "Dialog was cancelled");
                    }
                });
                tpd2.show(getFragmentManager(), "Timepickerdialog");
                break;
            //현재 진행중인 Auction 보는 창
            case R.id.Artist_Main_Auction_Time_btn :
                if (linearLayout2.getVisibility() == View.VISIBLE) {
                    linearLayout2.setVisibility(View.GONE);
                } else {
                    linearLayout2.setVisibility(View.VISIBLE);
                }
                break;
            //그림 확인 클릭
            case R.id.Artist_Main_AuctionAdd_Confirm_btn :

               if (title != null && !add3.getText().equals("start") && !add4.getText().equals("end")) {
                    inTime   = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
                    String[] Now = inTime.split(":");
                    int Now_HH = Integer.parseInt(Now[0]) * 60;
                    int Now_mm = Integer.parseInt(Now[1]);
                    String[] Start = String.valueOf(data1.getText()).split(":");
                    int Start_HH = Integer.parseInt(Start[0]) * 60;
                    int Start_mm = Integer.parseInt(Start[1]);
                    String[] End = String.valueOf(data2.getText()).split(":");
                    int End_HH = Integer.parseInt(End[0]) * 60;
                    int End_mm= Integer.parseInt(End[1]);
                    int Now_sum = Now_HH + Now_mm;
                    int Start_sum = Start_HH + Start_mm;
                    int End_sum = End_HH + End_mm;
                   Log.d("@@@@@@@@@@@", inTime + "," + data1.getText() + "," +data2.getText());
                    if (Now_sum >= Start_sum  || Now_sum >= End_sum){
                        Log.d("N@N@@@@@", Now_sum + ", " + Start_sum + ", " + End_sum );
                        Toast.makeText(ArtistMainActivity.this, "현재 시간에서 1분 이후로 등록할 수 있습니다.", Toast.LENGTH_SHORT).show();

                    } else if (Start_sum >= End_sum -2) {
                        Toast.makeText(ArtistMainActivity.this, "경매시간은 최소 3분 이상으로 설정해야 합니다.", Toast.LENGTH_SHORT).show();

                    }else {
                        AlertDialog.Builder alert2 = new AlertDialog.Builder(ArtistMainActivity.this);
                        alert2.setMessage(add3.getText() + " ~ " + add4.getText() + "으로 Auction을 진행하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                inTime   = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
                                String[] Now = inTime.split(":");
                                int Now_HH = Integer.parseInt(Now[0]) * 60;
                                int Now_mm = Integer.parseInt(Now[1]);
                                String[] Start = String.valueOf(data1.getText()).split(":");
                                int Start_HH = Integer.parseInt(Start[0]) * 60;
                                int Start_mm = Integer.parseInt(Start[1]);
                                String[] End = String.valueOf(data2.getText()).split(":");
                                int End_HH = Integer.parseInt(End[0]) * 60;
                                int End_mm= Integer.parseInt(End[1]);
                                int Now_sum = Now_HH + Now_mm;
                                int Start_sum = Start_HH + Start_mm;
                                int End_sum = End_HH + End_mm;
                                if (Now_sum >= Start_sum  || Now_sum >= End_sum){
                                    Toast.makeText(ArtistMainActivity.this, "현재 시간에서 1분 이후로 등록할 수 있습니다.", Toast.LENGTH_SHORT).show();

                                } else {
                                    ArtAdd(artkey, inDate + "%20" + data1.getText(), inDate  +"%20" + data2.getText());
                                    Intent intent = new Intent(ArtistMainActivity.this, ArtistMainActivity.class);
                                    intent.putExtra("artistID", artistID);
                                    intent.putExtra("title", title);
                                    intent.putExtra("artkey", artkey);
                                    intent.putExtra("auckey", auckey);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alert2.setCancelable(false);
                        alert2.show();
                    }
                } else {
                    Toast.makeText(ArtistMainActivity.this, "그림선택과 시간을 정해주세요", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.Artist_Main_Auction_Complete_btn :
                if (linearLayout_complete1.getVisibility() == View.VISIBLE) {
                    linearLayout_complete1.setVisibility(View.GONE);
                } else {
                    linearLayout_complete1.setVisibility(View.VISIBLE);
                }
                break;
        }
    }
//    private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
//        @Override
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//
//            add3.setText(hourOfDay + "시 " + minute + "분");
//            add3.setTextSize(11);
//            data1.setText(hourOfDay + ":" + minute);
//            add3.setTypeface(null, Typeface.BOLD);
//        }
//    };
//    private TimePickerDialog.OnTimeSetListener listener2 = new TimePickerDialog.OnTimeSetListener() {
//        @Override
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//
//            add4.setText(hourOfDay + "시 " + minute + "분");
//            add4.setTextSize(11);
//            data2.setText(hourOfDay + ":" +minute);
//            add4.setTypeface(null, Typeface.BOLD);
//
//        }
//    };

    final Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 0:
                    now = System.currentTimeMillis();
                    long en = end-now;
                    ne = en/1000;  HH = ne/3600; nH = ne%3600; mm = nH/60; ss = nH%60;

                    if(en < 0){
                        count1.setText("마감되었습니다.");
                        thread.interrupt();

                    }
                    else{
                        Log.d("@@@@@@@@@@@@@", ne + ", "  +HH + ", " +nH + ", " +mm + ", " + ss);
                        count1.setText("남은시간 : " + HH+"시간 " +mm+"분 " + ss +"초" + " 남았습니다.");

                    }
                    break;

                case 1:
                    //thread.stopThread();
                    break;

                default:
                    break;
            }


        }
    };

    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        String minuteString = minute < 10 ? "0"+minute : ""+minute;
        String time = hourString+"시 "+minuteString+"분";
        if (view == tpd) {
            add3.setText(time);
            add3.setTextSize(11);
            data1.setText(hourOfDay + ":" + minute);
            add3.setTypeface(null, Typeface.BOLD);
        } else if (view == tpd2) {
            add4.setText(time);
            add4.setTextSize(11);
            data2.setText(hourOfDay + ":" +minute);
            add4.setTypeface(null, Typeface.BOLD);
        } else {

        }
    }

    class Thread extends java.lang.Thread {

        boolean stopped = false;
        int i = 0;

        public Thread(){
            stopped = false;
        }

        public void stopThread() {
            stopped = true;
        }

        @Override
        public void run() {
            super.run();

            while(stopped == false) {
                i++;

                // 메시지 얻어오기
                Message message = handler.obtainMessage();

                // 메시지 ID 설정
                message.what = 0;

                // 메시지 내용 설정 (int)
                message.arg1 = i;

                // 메시지 내용 설정 (Object)
                String information = new String("초 째 Thread 동작 중입니다.");
                message.obj = information;

                // 메시지 전
                handler.sendMessage(message);

                try {
                    // 1초 씩 딜레이 부여
                    sleep(1000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            Toast.makeText(this, "구매자 ID가 아닙니다.", Toast.LENGTH_SHORT).show();
        }
        Log.d("TAGTEST : ", ""+ tag);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
