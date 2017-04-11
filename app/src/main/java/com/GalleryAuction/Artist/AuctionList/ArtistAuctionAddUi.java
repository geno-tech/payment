package com.GalleryAuction.Artist.AuctionList;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.bill_folder.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class ArtistAuctionAddUi extends Activity {
    int year , month , day , hour , minute , second;
    int cnt;
    String[] itemYear, itemMonth, itemDay, itemHour, itemMinute, itemSecond;
    String s ,e, artkey;
    Spinner spin1, spin2, spin3, spin4, spin5, spin6, spin1_e, spin2_e, spin3_e, spin4_e, spin5_e, spin6_e;
    ArrayAdapter adapter, adapter2, adapter3, adapter4, adapter5, adapter6, adapter_e, adapter2_e, adapter3_e, adapter4_e, adapter5_e, adapter6_e;
    Calendar cal;
    TextView test, test2, test3, test4, test5, test6;
    Button btn1, btn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_artistauction_add);
        spin1 = (Spinner)findViewById(R.id.spinner_yy);
        spin2 = (Spinner)findViewById(R.id.spinner_MM);
        spin3 = (Spinner)findViewById(R.id.spinner_dd);
        spin4 = (Spinner)findViewById(R.id.spinner_HH);
        spin5 = (Spinner)findViewById(R.id.spinner_mm);
        spin6 = (Spinner)findViewById(R.id.spinner_ss);
        spin1_e = (Spinner)findViewById(R.id.spinner_yy_e);
        spin2_e = (Spinner)findViewById(R.id.spinner_MM_e);
        spin3_e = (Spinner)findViewById(R.id.spinner_dd_e);
        spin4_e = (Spinner)findViewById(R.id.spinner_HH_e);
        spin5_e = (Spinner)findViewById(R.id.spinner_mm_e);
        spin6_e = (Spinner)findViewById(R.id.spinner_ss_e);
        cal = Calendar.getInstance();
        test = (TextView)findViewById(R.id.test);
        test2 = (TextView)findViewById(R.id.test2);
        test3 = (TextView)findViewById(R.id.test3);
        test4 = (TextView)findViewById(R.id.test4);
        test5 = (TextView)findViewById(R.id.test5);
        test6 = (TextView)findViewById(R.id.test6);
        btn1 = (Button)findViewById(R.id.artistauction_detail_btn);
        btn2 = (Button)findViewById(R.id.artistauction_detail_btn_exit);
        Intent intent = getIntent();
        artkey = intent.getStringExtra("artkey");
        long currentTimeMillis = System.currentTimeMillis();
        Date date = new Date(currentTimeMillis);
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH:mm:ss");
        String resultDay = CurDayFormat.format(date);
        String resultHour = CurHourFormat.format(date);
        StringTokenizer st = new StringTokenizer(resultDay, "-");
        StringTokenizer st2 = new StringTokenizer(resultHour, ":");
        int[] CurDay = new int[3];
        int[] CurHour = new int[3];
        cnt = 0;
        while(st.hasMoreTokens()){
            CurDay[cnt++] = Integer.parseInt(st.nextToken());
        }
        cnt = 0;
        while(st2.hasMoreTokens()){
            CurHour[cnt++] = Integer.parseInt(st2.nextToken());
        }
        year = CurDay[0];
        month = CurDay[1];
        day = CurDay[2];
        hour = CurHour[0];
        minute = CurHour[1];
        second = CurHour[2];


//        cal.set(2017, 3, 1);
        Log.d("CurDay : ",CurDay[0]+"-"+CurDay[1]+"-"+CurDay[2]);
        Log.d("CurHour : ",CurHour[0]+":"+CurHour[1]+":"+CurHour[2]);
        itemYear = new String[2];
        cnt = 0 ;
        for(int i = year ; i <= year+1; i++){
            itemYear[cnt++] = String.valueOf(i);

        }

        itemMonth = new String[12];
        cnt = 0 ;
        for(int i = 1 ; i <= 12; i++){
            itemMonth[cnt++] = String.valueOf(i);
        }

//        itemDay = new String[31];
//        cnt = 0 ;
//        for(int i = 1 ; i <= 31; i++){
//            itemDay[cnt++] = String.valueOf(i);
//        }

        itemHour = new String[24];
        cnt = 0 ;
        for(int i = 1 ; i <= 24; i++){
            itemHour[cnt++] = String.valueOf(i);
        }

        itemMinute = new String[61];
        cnt = 0 ;
        for(int i = 0 ; i <= 60; i++){
            itemMinute[cnt++] = String.valueOf(i);
        }
        cal.set(year, 0, 1);

        itemSecond = new String[61];
        cnt = 0 ;
        for(int i = 0 ; i <= 60; i++){
            itemSecond[cnt++] = String.valueOf(i);
        }
        itemDay = new String[cal.getMaximum(Calendar.DAY_OF_MONTH)];
        cnt = 0 ;
        for (int j = 1; j <= cal.getMaximum(Calendar.DAY_OF_MONTH); j++){
            itemDay[cnt++] = String.valueOf(j);
            Log.d("jjjjjjj", ""+j);
        }

        if (spin1.isSelected() && spin2.isSelected()) {
            adapter3 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin, itemDay);
            adapter3_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin, itemDay);
            spin3.setAdapter(adapter3);
            spin3_e.setAdapter(adapter3_e);
        }
        adapter = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemYear);
        adapter2 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemMonth);
        adapter3 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin, itemDay);
        adapter4 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemHour);
        adapter5 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemMinute);
        adapter6 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemSecond);

        adapter_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemYear);
        adapter2_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemMonth);
        adapter3_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin, itemDay);
        adapter4_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemHour);
        adapter5_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemMinute);
        adapter6_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemSecond);
        // city 와 gu 를 담을 두개의 Spinner 객체
        spin1.setAdapter(adapter);
        spin3.setAdapter(adapter3);
        spin2.setAdapter(adapter2);
        spin4.setAdapter(adapter4);
        spin5.setAdapter(adapter5);
        spin6.setAdapter(adapter6);
        spin1_e.setAdapter(adapter_e);
        spin2_e.setAdapter(adapter2_e);
        spin3_e.setAdapter(adapter3_e);
        spin4_e.setAdapter(adapter4_e);
        spin5_e.setAdapter(adapter5_e);
        spin6_e.setAdapter(adapter6_e);
        s = null;
        e = null;
//        String s = a + "-" +b + "-" + c + " " + d + ":" + e + ":" + f;
//        final String e = spin1_e.getSelectedItem() + "-" + spin2_e.getSelectedItem() + "-" + spin3_e.getSelectedItem() + " " + spin4_e.getSelectedItem() + ":" + spin5_e.getSelectedItem() + ":" + spin6_e.getSelectedItem();

        Spinner();
//        ArtAdd()
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArtAdd(artkey, s, e );
                finish();
                Log.d("시작합 " , s);
                Log.d("끝 합", e);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void Spinner() {
        spin1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cal.set(spin1.getSelectedItemPosition(), spin2.getSelectedItemPosition(), 1);
                s = getDateresult(itemYear[position]  , spin2.getSelectedItem() , spin3.getSelectedItem() , spin4.getSelectedItem() , spin5.getSelectedItem()  , spin6.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemDay = new String[cal.getMaximum(Calendar.DAY_OF_MONTH)];
                cnt = 0 ;

                cal.set(year, spin2.getSelectedItemPosition(), 1);
                for (int j = 1; j <= cal.getMaximum(Calendar.DAY_OF_MONTH); j++){
                    itemDay[cnt++] = String.valueOf(j);
                    Log.d("jjjjjjj", ""+j);
                    Log.d("MM", String.valueOf(cal.getMaximum(Calendar.DAY_OF_MONTH)));

                }
                adapter3 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin, itemDay);
                spin3.setAdapter(adapter3);
                String a = Integer.parseInt(itemMonth[position])>10?"":"0";
                Log.d("a", a + itemMinute[position]);
                s = getDateresult(spin1.getSelectedItem()  , itemMonth[position] , spin3.getSelectedItem() , spin4.getSelectedItem() , spin5.getSelectedItem()  , spin6.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spin3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = Integer.parseInt(itemDay[position])>10?"":"0";

                s = getDateresult(spin1.getSelectedItem()  , spin2.getSelectedItem() ,itemDay[position] , spin4.getSelectedItem() , spin5.getSelectedItem()  , spin6.getSelectedItem());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = Integer.parseInt(itemHour[position])>10?"":"0";
                s = getDateresult(spin1.getSelectedItem()  , spin2.getSelectedItem() , spin3.getSelectedItem() , itemHour[position] , spin5.getSelectedItem()  , spin6.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin5.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = Integer.parseInt(itemMinute[position])>10?"":"0";
                s = getDateresult(spin1.getSelectedItem()  , spin2.getSelectedItem() , spin3.getSelectedItem() , spin4.getSelectedItem() , itemMinute[position]  , spin6.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin6.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = Integer.parseInt(itemSecond[position])>10?"":"0";
                s = getDateresult(spin1.getSelectedItem()  , spin2.getSelectedItem() , spin3.getSelectedItem() , spin4.getSelectedItem() , spin5.getSelectedItem()  , itemSecond[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spin1_e.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                e = getDateresult(itemYear[position]  , spin2_e.getSelectedItem() , spin3_e.getSelectedItem() , spin4_e.getSelectedItem() , spin5_e.getSelectedItem()  , spin6_e.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin2_e.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = Integer.parseInt(itemMonth[position])>10?"":"0";
                e = getDateresult(spin1_e.getSelectedItem()  , itemMonth[position] , spin3_e.getSelectedItem() , spin4_e.getSelectedItem() , spin5_e.getSelectedItem()  , spin6_e.getSelectedItem()); }
                @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin3_e.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = Integer.parseInt(itemDay[position])>10?"":"0";
                e = getDateresult(spin1_e.getSelectedItem()  , spin2_e.getSelectedItem() , itemDay[position] , spin4_e.getSelectedItem() , spin5_e.getSelectedItem()  , spin6_e.getSelectedItem()); }
                @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin4_e.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = Integer.parseInt(itemHour[position])>10?"":"0";

                e = getDateresult(spin1_e.getSelectedItem()  , spin2_e.getSelectedItem() , spin3_e.getSelectedItem() , itemHour[position] , spin5_e.getSelectedItem()  , spin6_e.getSelectedItem()); }
                @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin5_e.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = Integer.parseInt(itemMinute[position])>10?"":"0";

                e = getDateresult(spin1_e.getSelectedItem()  , spin2_e.getSelectedItem() , spin3_e.getSelectedItem() , spin4_e.getSelectedItem() , itemMinute[position]  ,spin6_e.getSelectedItem()); }
                @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin6_e.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String a = Integer.parseInt(itemSecond[position])>10?"":"0";
                //e = spin1_e.getSelectedItem() + "-" + spin2_e.getSelectedItem() + "-" + spin3_e.getSelectedItem() + " " + spin4_e.getSelectedItem() + ":" + spin5_e.getSelectedItem() + ":" + a + itemSecond[position];            }
                e = getDateresult(spin1_e.getSelectedItem()  , spin2_e.getSelectedItem() , spin3_e.getSelectedItem() , spin4_e.getSelectedItem() , spin5_e.getSelectedItem()  , itemSecond[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
//아트키, 시작, 마감
    private void ArtAdd(String msg, String msg2, String msg3) {
        if (msg == null) {
            msg = "";
        }

        String URL = "http://59.3.109.220:8989/NFCTEST/artist_auc_insert.jsp";

        DefaultHttpClient client = new DefaultHttpClient();
        try {
            Log.d("debuging" , "msg : " +msg  + " msg2 : " + msg2 + " msg3 : " + msg3);
            HttpPost post = new HttpPost(URL + "?msg=" + msg + "&msg2=" + msg2 + "&msg3=" + msg3);
            HttpParams params = client.getParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 3000);
            HttpResponse response = client.execute(post);
            BufferedReader bufreader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(),
                            "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDateresult(Object ...item)
    {
        String result = "";
        for(int i = 0 ; i < item.length; i++) {


            if (Integer.parseInt(item[i].toString()) >= 10) {
                if (i == 0 )
                    result += item[i].toString() + "-";
                else if(i > 3 || i > item.length)
                {
                    result += ":" + item[i].toString();
                }
                else if(i == item.length) {
                    result += item[i].toString();
                }
                else{
                    if(i == 2 )
                        result += "-" + item[i].toString() + "%20";
                    else
                        result += item[i].toString();
                }
            }
            else {
                if (i == 0 )
                    result += item[i].toString() + "-";
                else if(i > 3 || i > item.length)
                {
                    result += ":" + "0" + item[i].toString();
                }
                else if(i == item.length) {
                    result +="0" + item[i].toString();
                }
                else{
                    if(i == 2 )
                        result += "-" + "0" + item[i].toString() + "%20";
                    else
                        result += "0" + item[i].toString();
                }
            }


        }
        return result;
    }



}
