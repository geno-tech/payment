package com.GalleryAuction.UI;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.GalleryAuction.Adapter.GridAdapter;
import com.GalleryAuction.Dialog.GridCustomDialog;
import com.GalleryAuction.Item.GridViewItem;
import com.geno.bill_folder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.GalleryAuction.Client.ImageViewItem.getImageBitmap;
import static com.GalleryAuction.Item.HttpClientItem.ArtAdd;
import static com.GalleryAuction.Item.HttpClientItem.ArtistArtInfo;
import static com.GalleryAuction.Item.HttpClientItem.AuctionInfo;
import static com.GalleryAuction.Item.HttpClientItem.AuctionProgress;

public class ArtistMainActivity extends AppCompatActivity implements View.OnClickListener {
    Button add1,add2,add3,add4, pro1, add5, detail1;
    LinearLayout linearLayout1, linearLayout2, linearLayout3, linearLayout_detail, linearLayout_detail2;
    String  image, artistID, title, inTime, inDate, artkey, auckey, auc_end, auc_start;
    TextView data1, data2, count1;
    GridViewItem gridView, gridView_detail;
    GridAdapter adapter, adapter_detail;
    ScrollView scrollView;
    String imgUrl = "http://221.156.54.210:8989/NFCTEST/art_images/";
    long now, end, ne, dd, nd, HH, nH, mm, ss, min_bidding;
    SimpleDateFormat sdf;
    Date date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryaction_artistmain);

        detail1 = (Button)findViewById(R.id.Artist_Main_Art_List_btn);
        linearLayout_detail = (LinearLayout)findViewById(R.id.Artist_Main_Art_List_layout);
        linearLayout_detail2 = (LinearLayout)findViewById(R.id.Artist_Main_Art_List_layout2);
        gridView_detail = (GridViewItem)findViewById(R.id.Artist_Main_Art_List_GridView);
        gridView_detail.setExpanded(true);
        adapter_detail = new GridAdapter();
        add1 = (Button)findViewById(R.id.Artist_Main_Auction_Add_btn);
        add2 = (Button)findViewById(R.id.Artist_Main_Auction_Art_Add_btn);
        add3 = (Button)findViewById(R.id.Artist_Main_AuctionAdd_Start_btn);
        add4 = (Button)findViewById(R.id.Artist_Main_AuctionAdd_End_btn);
        add5 = (Button)findViewById(R.id.Artist_Main_AuctionAdd_Confirm_btn);

        pro1 = (Button)findViewById(R.id.Artist_Main_Auction_Time_btn);

        data1 = (TextView)findViewById(R.id.Artist_Main_Start_txt_secret);
        data2 = (TextView)findViewById(R.id.Artist_Main_End_txt_secret);
        count1 = (TextView)findViewById(R.id.Artist_main_Auction_Time_count_txt);
        linearLayout1 = (LinearLayout)findViewById(R.id.Artist_Main_Auction_Add_layout);
        linearLayout2 = (LinearLayout)findViewById(R.id.Artist_Main_Auction_Time_layout);
        linearLayout3 = (LinearLayout)findViewById(R.id.Artist_Main_Auction_Time_ArtList_layout);
//        progressBar = (ProgressBar)findViewById(R.id.progressBar1);
        gridView = (GridViewItem) findViewById(R.id.Artist_Main_Going_GridView);
        gridView.setExpanded(true);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Intent intent = getIntent();
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
                    JSONArray ja = new JSONArray(AuctionInfo(artistID));

                    JSONObject job = (JSONObject) ja.get(i);
                    auc_start = job.get("auc_start").toString();
                    auc_end = job.get("auc_end").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    date = sdf.parse(auc_end);
                    end = date.getTime();
                    Log.d("end", "" + end);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Thread thread = new Thread();
                thread.start();
            }
        });

        detail1.setOnClickListener(this);
        add1.setOnClickListener(this);
        add2.setOnClickListener(this);
        add3.setOnClickListener(this);
        add4.setOnClickListener(this);
        pro1.setOnClickListener(this);
        add5.setOnClickListener(this);
        inDate   = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        title = intent.getStringExtra("title");
        artkey = intent.getStringExtra("artkey");
        auckey = intent.getStringExtra("auckey");
        Log.d("@@@@", title + auckey);
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
                    TimePickerDialog dialog = new TimePickerDialog(this, listener, 15, 24, false);

                    dialog.show();
                break;
            //그림 End 시간 클릭
            case R.id.Artist_Main_AuctionAdd_End_btn :
                    TimePickerDialog dialog2 = new TimePickerDialog(this, listener2, 15, 24, false);
                    dialog2.show();

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
                    if (Now_sum >= Start_sum -3 || Now_sum >= End_sum -3){
                        Toast.makeText(ArtistMainActivity.this, "현재 시간에서 3분 이후로 등록할 수 있습니다.", Toast.LENGTH_SHORT).show();

                    } else if (Start_sum >= End_sum -30) {
                        Toast.makeText(ArtistMainActivity.this, "경매시간은 최소 30분 이상으로 설정해야 합니다.", Toast.LENGTH_SHORT).show();

                    }else {
                        android.app.AlertDialog.Builder alert2 = new android.app.AlertDialog.Builder(ArtistMainActivity.this);
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
                                if (Now_sum >= Start_sum -3 || Now_sum >= End_sum -3){
                                    Toast.makeText(ArtistMainActivity.this, "현재 시간에서 3분 이후로 등록할 수 있습니다.", Toast.LENGTH_SHORT).show();

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
        }
    }
    private TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            add3.setText(hourOfDay + "시 " + minute + "분");
            add3.setTextSize(11);
            data1.setText(hourOfDay + ":" + minute);
            add3.setTypeface(null, Typeface.BOLD);
            Toast.makeText(getApplicationContext(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();
        }
    };
    private TimePickerDialog.OnTimeSetListener listener2 = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            add4.setText(hourOfDay + "시 " + minute + "분");
            add4.setTextSize(11);
            data2.setText(hourOfDay + ":" +minute);
            add4.setTypeface(null, Typeface.BOLD);
            Toast.makeText(getApplicationContext(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();

        }
    };
    final Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg){
            switch (msg.what) {
                case 0:
                    now = System.currentTimeMillis();
                    long en = end-now;
                    if(en < 0){
                        count1.setText("마감되었습니다.");

                    }
                    else{
                        ne = en/1000; dd = ne/86400; nd = ne%86400; HH = nd/3600; nH = nd%3600; mm = nH/60; ss = nH%60;
                        count1.setText("남은시간 : " + dd+"일" + HH+"시" +mm+"분"+ ss +"초" + " 남았습니다.");

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



}
