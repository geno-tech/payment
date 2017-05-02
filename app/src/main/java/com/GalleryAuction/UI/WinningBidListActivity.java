package com.GalleryAuction.UI;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.GalleryAuction.Adapter.WinningBidInfoAdapter;
import com.GalleryAuction.Item.HttpClientItem;
import com.geno.bill_folder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import static com.GalleryAuction.Item.HttpClientItem.BiddingInfo;
import static com.GalleryAuction.Item.HttpClientItem.BiddingInfoBest;
import static com.GalleryAuction.Item.HttpClientItem.BiddingList;
import static com.GalleryAuction.Item.HttpClientItem.BiddingWinUserAgree;
import static com.GalleryAuction.Item.HttpClientItem.BiddingWinUserCancel;

public class WinningBidListActivity extends Activity {
    TextView tv1, tv2, tv3;
    Button btn1, btn2, btn3;
    String auckey, userID, mybest, best, bid, bidkey, bidprice, bidcontime;
    long min_bidding, min_bidding2;
    private ListView listView;
    private WinningBidInfoAdapter adapter;

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_winningbidlist);
        tv2 = (TextView)findViewById(R.id.winningbidlistbest_txt);
        tv3 = (TextView)findViewById(R.id.winningbidlistmybest_txt);
        btn1 = (Button)findViewById(R.id.winningbidlist_btnagree);
        btn2 = (Button)findViewById(R.id.winningbidlist_btnX);
        btn3 = (Button)findViewById(R.id.winningbidlist_btnExit);
        listView = (ListView)findViewById(R.id.winningbidlist_list);
        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        final Intent intent = getIntent();
        auckey = intent.getStringExtra("auckey");
        userID = intent.getStringExtra("userID");
        bid = intent.getStringExtra("bid");
        bidkey = intent.getStringExtra("bidkey");
        adapter = new WinningBidInfoAdapter();
        try {
            JSONObject job2 = new JSONObject(BiddingInfoBest(auckey));
            best = job2.get("bid_price").toString();
            Log.d("SSAAAA", ""+job2);

            JSONObject job = new JSONObject(BiddingInfo(userID, auckey));
            mybest = job.get("bid_price").toString();
            Log.d("SSAAAA", ""+job);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        min_bidding = mybest==null?0:Long.parseLong(mybest);
        min_bidding2 = best==null?0:Long.parseLong(best);

        tv2.setText("낙찰가는 " + ReBidding.currentpoint(String.valueOf(min_bidding2)) + "원 입니다");
        tv3.setText("당신의 입찰가는 " + ReBidding.currentpoint(String.valueOf(min_bidding)) + "원 입니다");
        if (bid.equals("0")|| bid.equals("2")) {
            btn1.setVisibility(View.GONE);
            btn2.setVisibility(View.GONE);
            btn3.setVisibility(View.VISIBLE);
            btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder alert = new AlertDialog.Builder(WinningBidListActivity.this);
                    alert.setMessage(getString(R.string.agreement_msg)).setCancelable(false).setPositiveButton("거부", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tts.stop();

                        }
                    }).setNeutralButton("동의", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tts.stop();
                            tts.shutdown();
                            BiddingWinUserAgree(auckey);

                            Toast.makeText(WinningBidListActivity.this, "거래완료", Toast.LENGTH_SHORT).show();

//                            adapter.notifyDataSetChanged();
                            Intent intent1 = new Intent(WinningBidListActivity.this, ArtInfoTagList.class) ;
                            intent1.putExtra("userID", userID);
                            startActivity(intent1);
                            finish();
                        }
                    }).setNegativeButton("계약서 읽기", null);
                    final AlertDialog alertdialog = alert.create();
                    alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button b = alertdialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                            b.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        ttsGreater21(getString(R.string.agreement_msg).toString());
                                    } else {
                                        ttsUnder20(getString(R.string.agreement_msg).toString());
                                    }
                                }
                            });
                        }
                    });
                    alertdialog.setCancelable(false);
                    alertdialog.show();
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert2 = new AlertDialog.Builder(WinningBidListActivity.this);
                    alert2.setMessage("거부하시면 가계약금을 받을 수 없습니다.\n그래도 거부하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BiddingWinUserCancel(auckey);
                            Toast.makeText(WinningBidListActivity.this, "낙찰을 취소하였습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert2.setCancelable(false);
                    alert2.show();
                }
            });
        }
        try {

            String albumlist = BiddingList(userID, auckey);
            JSONArray ja = new JSONArray(albumlist);
            Log.d("auctime", albumlist);

            for (int i = 0; i < ja.length(); i++) {

                JSONObject job = (JSONObject) ja.get(i);
                bidprice = job.get("bid_price").toString();
                bidcontime = job.get("bid_con_time").toString();
                adapter.addItem(bidprice, bidcontime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listView.setAdapter(adapter);

    }



    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    }
