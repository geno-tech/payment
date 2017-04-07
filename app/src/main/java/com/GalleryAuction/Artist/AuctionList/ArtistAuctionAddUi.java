package com.GalleryAuction.Artist.AuctionList;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.geno.bill_folder.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class ArtistAuctionAddUi extends Activity implements AdapterView.OnItemClickListener {
    int year , month , day , hour , minute , second;
    int cnt;
    String[] itemYear, itemMonth, itemDay, itemHour, itemMinute, itemSecond;
    Spinner spin1, spin2, spin3, spin4, spin5, spin6, spin1_e, spin2_e, spin3_e, spin4_e, spin5_e, spin6_e;
    ArrayAdapter adapter, adapter2, adapter3, adapter4, adapter5, adapter6, adapter_e, adapter2_e, adapter3_e, adapter4_e, adapter5_e, adapter6_e;
    Calendar cal;
    TextView test;
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

        itemYear = new String[2];
        cnt = 0 ;
        for(int i = year ; i <= year+1; i++){
            itemYear[cnt++] = String.valueOf(i);
        }

        itemMonth = new String[12];
        cnt = 0 ;
        for(int i = 1 ; i <= cal.get(Calendar.MONTH)+1; i++){
            itemMonth[cnt++] = String.valueOf(i);
        }
        itemDay = new String[31];
        cnt = 0 ;
        for(int i = 1 ; i <= cal.getMaximum(Calendar.DAY_OF_MONTH); i++){
            itemDay[cnt++] = String.valueOf(i);
        }

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

        itemSecond = new String[61];
        cnt = 0 ;
        for(int i = 0 ; i <= 60; i++){
            itemSecond[cnt++] = String.valueOf(i);
        }
        adapter = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemYear);
        adapter2 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemMonth);
        adapter3 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemDay);
        adapter4 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemHour);
        adapter5 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemMinute);
        adapter6 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemSecond);

        adapter_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemYear);
        adapter2_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemMonth);
        adapter3_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemDay);
        adapter4_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemHour);
        adapter5_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemMinute);
        adapter6_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemSecond);
        // city 와 gu 를 담을 두개의 Spinner 객체
        spin1.setAdapter(adapter);
        spin2.setAdapter(adapter2);
        spin3.setAdapter(adapter3);
        spin4.setAdapter(adapter4);
        spin5.setAdapter(adapter5);
        spin6.setAdapter(adapter6);

        spin1_e.setAdapter(adapter_e);
        spin2_e.setAdapter(adapter2_e);
        spin3_e.setAdapter(adapter3_e);
        spin4_e.setAdapter(adapter4_e);
        spin5_e.setAdapter(adapter5_e);
        spin6_e.setAdapter(adapter6_e);

        //  년도 에 대한 Spinner
        spin1.setOnItemClickListener(this);
        spin2.setOnItemClickListener(this);
        spin3.setOnItemClickListener(this);
        spin4.setOnItemClickListener(this);
        spin5.setOnItemClickListener(this);
        spin6.setOnItemClickListener(this);

        spin1_e.setOnItemClickListener(this);
        spin2_e.setOnItemClickListener(this);
        spin3_e.setOnItemClickListener(this);
        spin4_e.setOnItemClickListener(this);
        spin5_e.setOnItemClickListener(this);
        spin6_e.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()){
            case R.id.spinner_yy :
//                test.setText();
                break;
            case R.id.spinner_MM :
                break;
            case R.id.spinner_dd :
                break;
            case R.id.spinner_HH :
                break;
            case R.id.spinner_mm :
                break;
            case R.id.spinner_ss :
                break;
            case R.id.spinner_yy_e :
                break;
            case R.id.spinner_MM_e :
                break;
            case R.id.spinner_dd_e :
                break;
            case R.id.spinner_HH_e :
                break;
            case R.id.spinner_mm_e :
                break;
            case R.id.spinner_ss_e :
                break;
        }
    }
}
