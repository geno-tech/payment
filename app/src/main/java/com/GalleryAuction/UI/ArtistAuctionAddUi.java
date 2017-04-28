package com.GalleryAuction.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.bill_folder.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class ArtistAuctionAddUi extends Activity {
    int year , month , day , hour , minute , second;
    int cnt;
    long currentTimeMillis, startTimeConf, selectTime, selectTime_end;
    String[] itemYear, itemMonth, itemDay, itemHour, itemMinute, itemSecond;
    String s ,e, artkey, ss, ee, image, title;
    Spinner spin1, spin2, spin3, spin4, spin5, spin6, spin1_e, spin2_e, spin3_e, spin4_e, spin5_e, spin6_e;
    ArrayAdapter adapter, adapter2, adapter3, adapter4, adapter5, adapter6, adapter_e, adapter2_e, adapter3_e, adapter4_e, adapter5_e, adapter6_e;
    Calendar cal;
    Button btn1, btn2;
    TextView title_txt;
    ImageView title_image;
    Bitmap bmImg;
    String imgUrl = "http://59.3.109.220:8989/NFCTEST/art_images/";

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
        back task;

        task = new back();

        btn1 = (Button)findViewById(R.id.artistauction_detail_btn);
        btn2 = (Button)findViewById(R.id.artistauction_detail_btn_exit);
        title_txt = (TextView)findViewById(R.id.addarttitle_txt);
        title_image = (ImageView)findViewById(R.id.artistauctionaddimage) ;
        Intent intent = getIntent();
        artkey = intent.getStringExtra("artkey");
        title = intent.getStringExtra("title");
        image = intent.getStringExtra("image");
        currentTimeMillis = System.currentTimeMillis();
        title_txt.setText(title);
        title_txt.setTextSize(20);

        Date date = new Date(currentTimeMillis);
//        Log.d("date",""+ date);
        SimpleDateFormat CurDayFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH:mm:ss");
        String resultDay = CurDayFormat.format(date);
        String resultHour = CurHourFormat.format(date);
//        Log.d("date",""+ resultDay);
//        Log.d("date",""+ resultHour);
        task.execute(imgUrl + image);

        StringTokenizer st = new StringTokenizer(resultDay, "-");
        StringTokenizer st2 = new StringTokenizer(resultHour, ":");
        int[] CurDay = new int[3];
        int[] CurHour = new int[3];
        cnt = 0;
        startTimeConf = currentTimeMillis + 300000;
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
        for(int i = 0 ; i < 24; i++){
            itemHour[cnt++] = String.valueOf(i);
        }

        itemMinute = new String[60];
        cnt = 0 ;
        for(int i = 0 ; i < 60; i++){
            itemMinute[cnt++] = String.valueOf(i);
        }
        cal.set(year, 0, 1);

        itemSecond = new String[60];
        cnt = 0 ;
        for(int i = 0 ; i < 60; i++){
            itemSecond[cnt++] = String.valueOf(i);
        }
        itemDay = new String[cal.getMaximum(Calendar.DAY_OF_MONTH)];
        cnt = 0 ;
        for (int j = 1; j <= cal.getMaximum(Calendar.DAY_OF_MONTH); j++){
            itemDay[cnt++] = String.valueOf(j);
            Log.d("jjjjjjj", ""+j);
        }

//        if (spin1.isSelected() && spin2.isSelected()) {
//            adapter3 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin, itemDay);
//            adapter3_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin, itemDay);
//            spin3.setAdapter(adapter3);
//            spin3_e.setAdapter(adapter3_e);
//        }
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
        Log.d("DADADAY", ""+day);
        spin3.setSelection(day -1);

        spin2.setAdapter(adapter2);
        spin2.setSelection(month -1);

        spin4.setAdapter(adapter4);
        spin4.setSelection(hour);

        spin5.setAdapter(adapter5);
        spin5.setSelection(minute);

        spin6.setAdapter(adapter6);
        spin6.setSelection(second);

        spin1_e.setAdapter(adapter_e);

        spin2_e.setAdapter(adapter2_e);
        spin2_e.setSelection(month -1);

        spin3_e.setAdapter(adapter3_e);
        spin3_e.setSelection(day -1);

        spin4_e.setAdapter(adapter4_e);
        spin4_e.setSelection(hour);

        spin5_e.setAdapter(adapter5_e);
        spin5_e.setSelection((minute +5));

        spin6_e.setAdapter(adapter6_e);
        spin6_e.setSelection(second);

        s = null;
        e = null;

        Spinner();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String splitArray[] = {};
                String splitArray2[] = {};
                splitArray = s.split("%20");
                splitArray2 = e.split("%20");
                ss = splitArray[0] + " " + splitArray[1];
                ee = splitArray2[0] + " " + splitArray2[1];

                Log.d("splitArray[0]",splitArray[0]);
                Log.d("splitArray[1]",splitArray[1]);
                Log.d("splitArray2[0]",splitArray2[0]);
                Log.d("splitArray2[1]",splitArray2[1]);
                SimpleDateFormat FormatStringtoDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date formatdate = FormatStringtoDate.parse(ss);
                    Date formatdate2 = FormatStringtoDate.parse(ee);

                    selectTime = formatdate.getTime();
                    selectTime_end = formatdate2.getTime();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                if (startTimeConf > selectTime) {
                    Toast.makeText(ArtistAuctionAddUi.this, "현재시간보다 5분 이상의 시간이여야 합니다", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (selectTime >= selectTime_end) {
                        Toast.makeText(ArtistAuctionAddUi.this, "마감시간이 시작시간보다 빠르거나 시간이 같습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        ArtAdd(artkey, s, e);
                        finish();
                    }
                }
                Log.d("시작합 " , ss);
                Log.d("끝 합", ee);
                Log.d("현재시간 밀리초 " , String.valueOf(currentTimeMillis));
                Log.d("현재시간 밀리초 " , String.valueOf(selectTime));
                Log.d("현재시간+300000 밀리초 " , String.valueOf(startTimeConf) );
                Log.d("현재시간합 " , String.valueOf(selectTime-startTimeConf) );
                //currentTimeMillis
                //startTimeConf
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
                spin1(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cal.set(Integer.parseInt(spin1.getSelectedItem().toString()) , spin2.getSelectedItemPosition(), 1);
                itemDay = new String[cal.getActualMaximum(Calendar.DATE)];
                cnt = 0 ;

                for (int j = 1; j <= cal.getActualMaximum(Calendar.DATE); j++){
                    itemDay[cnt++] = String.valueOf(j);
//                    Log.d("jjjjjjj", ""+j);

                }
//                Log.d("year", ""+year);
//                Log.d("year", ""+year);
//                Log.d("spin1item", ""+Integer.parseInt(spin1.getSelectedItem().toString()) );
//
//                Log.d("MM", String.valueOf(cal.getActualMaximum(Calendar.DATE)));

                adapter3 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin, itemDay) {
                    @Override
                    public boolean isEnabled(int position) {
                        if(Integer.parseInt(spin1.getSelectedItem().toString()) == year && Integer.parseInt(spin2.getSelectedItem().toString()) == month ) {
                            if (position >= day - 1)
                                return true;
                            else
                                return false;
                        } else
                        return true;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(Integer.parseInt(spin1.getSelectedItem().toString()) == year && Integer.parseInt(spin2.getSelectedItem().toString()) == month ) {
                            if (position >= day - 1) {
                                // Set the disable item text color
                                tv.setTextColor(Color.BLACK);

                            } else {
                                tv.setTextColor(Color.GRAY);
                            }
                        }
                        return view;
                    }
                };
                spin3.setAdapter(adapter3);
                spin3.setSelection(day -1);

                adapter2_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemMonth) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (position >= spin2.getSelectedItemPosition())
                            return true;
                        else
                            return false;
                    }
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position >= spin2.getSelectedItemPosition() ) {
                            // Set the disable item text color
                            tv.setTextColor(Color.BLACK);

                        }
                        else {
                            tv.setTextColor(Color.GRAY);
                        }
                        return view;
                    }
                };
                spin2_e.setAdapter(adapter2_e);
                spin2_e.setSelection(spin2.getSelectedItemPosition());

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
                adapter4 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemHour) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (Integer.parseInt(spin1.getSelectedItem().toString()) == year && Integer.parseInt(spin2.getSelectedItem().toString()) == month && Integer.parseInt(spin3.getSelectedItem().toString()) == day) {
                            if (position >= hour)
                                return true;
                            else
                                return false;
                        } else
                            return true;
                    }
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (Integer.parseInt(spin1.getSelectedItem().toString()) == year && Integer.parseInt(spin2.getSelectedItem().toString()) == month  &&Integer.parseInt(spin3.getSelectedItem().toString()) == day) {
                            if (position >= hour) {
                                // Set the disable item text color
                                tv.setTextColor(Color.BLACK);

                            } else {
                                tv.setTextColor(Color.GRAY);
                            }
                        } else
                            tv.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                spin4.setAdapter(adapter4);
                spin4.setSelection(hour);

                adapter3_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemDay) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (Integer.parseInt(spin1_e.getSelectedItem().toString()) == Integer.parseInt(spin1.getSelectedItem().toString()) && Integer.parseInt(spin2_e.getSelectedItem().toString()) == Integer.parseInt(spin2.getSelectedItem().toString())) {

                            if (position >= spin3.getSelectedItemPosition())
                                return true;
                            else
                                return false;
                        } else
                            return true;
                    }
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(Integer.parseInt(spin1_e.getSelectedItem().toString()) == Integer.parseInt(spin1.getSelectedItem().toString()) && Integer.parseInt(spin2_e.getSelectedItem().toString()) == Integer.parseInt(spin2.getSelectedItem().toString()) ) {
                            if (position >= spin3.getSelectedItemPosition()) {
                                // Set the disable item text color
                                tv.setTextColor(Color.BLACK);

                            } else {
                                tv.setTextColor(Color.GRAY);
                            }
                        } else
                            tv.setTextColor(Color.BLACK);

                        return view;
                    }
                };
                spin3_e.setAdapter(adapter3_e);
                spin3_e.setSelection(spin3.getSelectedItemPosition());
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
                adapter5 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemMinute) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (Integer.parseInt(spin1.getSelectedItem().toString()) == year && Integer.parseInt(spin2.getSelectedItem().toString()) == month  &&Integer.parseInt(spin3.getSelectedItem().toString()) == day && Integer.parseInt(spin4.getSelectedItem().toString()) == hour) {
                            if (position >= minute+5)
                                return true;
                            else
                                return false;
                        } else
                            return true;
                    }
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (Integer.parseInt(spin1.getSelectedItem().toString()) == year && Integer.parseInt(spin2.getSelectedItem().toString()) == month  &&Integer.parseInt(spin3.getSelectedItem().toString()) == day && Integer.parseInt(spin4.getSelectedItem().toString()) == hour) {
                            if (position >= minute+5) {
                                // Set the disable item text color
                                tv.setTextColor(Color.BLACK);

                            } else {
                                tv.setTextColor(Color.GRAY);
                            }
                        } else
                            tv.setTextColor(Color.BLACK);
                        return view;
                        }

                };
                spin5.setAdapter(adapter5);
                spin5.setSelection(minute+5);

                adapter4_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemHour) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (Integer.parseInt(spin1_e.getSelectedItem().toString()) == Integer.parseInt(spin1.getSelectedItem().toString()) && Integer.parseInt(spin2_e.getSelectedItem().toString()) == Integer.parseInt(spin2.getSelectedItem().toString()) && Integer.parseInt(spin3_e.getSelectedItem().toString()) == Integer.parseInt(spin3.getSelectedItem().toString())) {

                            if (position >= spin4.getSelectedItemPosition())
                                return true;
                            else
                                return false;
                        } else
                            return true;
                    }
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (Integer.parseInt(spin1_e.getSelectedItem().toString()) == Integer.parseInt(spin1.getSelectedItem().toString()) && Integer.parseInt(spin2_e.getSelectedItem().toString()) == Integer.parseInt(spin2.getSelectedItem().toString()) && Integer.parseInt(spin3_e.getSelectedItem().toString()) == Integer.parseInt(spin3.getSelectedItem().toString())) {

                            if (position >= spin4.getSelectedItemPosition()) {
                                // Set the disable item text color
                                tv.setTextColor(Color.BLACK);

                            } else {
                                tv.setTextColor(Color.GRAY);
                            }
                        } else
                            tv.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                spin4_e.setAdapter(adapter4_e);
                spin4_e.setSelection(spin4.getSelectedItemPosition());
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
                adapter6 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemSecond) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (Integer.parseInt(spin1.getSelectedItem().toString()) == year && Integer.parseInt(spin2.getSelectedItem().toString()) == month  &&Integer.parseInt(spin3.getSelectedItem().toString()) == day && Integer.parseInt(spin4.getSelectedItem().toString()) == hour &&Integer.parseInt(spin5.getSelectedItem().toString()) == minute) {
                            if (position >= second)
                                return true;
                            else
                                return false;
                        } else
                            return true;
                    }
                    @Override
                            public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                TextView tv = (TextView) view;
                                if (Integer.parseInt(spin1.getSelectedItem().toString()) == year && Integer.parseInt(spin2.getSelectedItem().toString()) == month  &&Integer.parseInt(spin3.getSelectedItem().toString()) == day && Integer.parseInt(spin4.getSelectedItem().toString()) == hour && Integer.parseInt(spin5.getSelectedItem().toString()) == minute) {
                                    if (position >= second) {
                                        // Set the disable item text color
                                        tv.setTextColor(Color.BLACK);

                            } else {
                                tv.setTextColor(Color.GRAY);
                            }
                        } else
                        tv.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                spin6.setAdapter(adapter6);
                spin6.setSelection(second);

                adapter5_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemMinute) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (Integer.parseInt(spin1_e.getSelectedItem().toString()) == Integer.parseInt(spin1.getSelectedItem().toString()) && Integer.parseInt(spin2_e.getSelectedItem().toString()) == Integer.parseInt(spin2.getSelectedItem().toString()) && Integer.parseInt(spin3_e.getSelectedItem().toString()) == Integer.parseInt(spin3.getSelectedItem().toString()) && Integer.parseInt(spin4_e.getSelectedItem().toString()) == Integer.parseInt(spin4.getSelectedItem().toString())) {
                            if (position >= spin5.getSelectedItemPosition())
                                return true;
                            else
                                return false;
                        } else
                            return true;
                    }
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (Integer.parseInt(spin1_e.getSelectedItem().toString()) == Integer.parseInt(spin1.getSelectedItem().toString()) && Integer.parseInt(spin2_e.getSelectedItem().toString()) == Integer.parseInt(spin2.getSelectedItem().toString()) && Integer.parseInt(spin3_e.getSelectedItem().toString()) == Integer.parseInt(spin3.getSelectedItem().toString()) && Integer.parseInt(spin4_e.getSelectedItem().toString()) == Integer.parseInt(spin4.getSelectedItem().toString())) {

                            if (position >= spin5.getSelectedItemPosition()) {
                                // Set the disable item text color
                                tv.setTextColor(Color.BLACK);

                            } else {
                                tv.setTextColor(Color.GRAY);
                            }
                        } else
                            tv.setTextColor(Color.BLACK);
                        return view;
                    }
                };
                spin5_e.setAdapter(adapter5_e);
                spin5_e.setSelection(spin5.getSelectedItemPosition());
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
                adapter6_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemSecond) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (Integer.parseInt(spin1_e.getSelectedItem().toString()) == Integer.parseInt(spin1.getSelectedItem().toString()) && Integer.parseInt(spin2_e.getSelectedItem().toString()) == Integer.parseInt(spin2.getSelectedItem().toString()) && Integer.parseInt(spin3_e.getSelectedItem().toString()) == Integer.parseInt(spin3.getSelectedItem().toString()) && Integer.parseInt(spin4_e.getSelectedItem().toString()) == Integer.parseInt(spin4.getSelectedItem().toString()) && Integer.parseInt(spin5_e.getSelectedItem().toString()) == Integer.parseInt(spin5.getSelectedItem().toString())) {

                            if (position >= spin6.getSelectedItemPosition())
                                return true;
                            else
                                return false;
                        } else {
                            return true;
                        }
                    }
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (Integer.parseInt(spin1_e.getSelectedItem().toString()) == Integer.parseInt(spin1.getSelectedItem().toString()) && Integer.parseInt(spin2_e.getSelectedItem().toString()) == Integer.parseInt(spin2.getSelectedItem().toString()) && Integer.parseInt(spin3_e.getSelectedItem().toString()) == Integer.parseInt(spin3.getSelectedItem().toString()) && Integer.parseInt(spin4_e.getSelectedItem().toString()) == Integer.parseInt(spin4.getSelectedItem().toString()) && Integer.parseInt(spin5_e.getSelectedItem().toString()) == Integer.parseInt(spin5.getSelectedItem().toString())) {

                            if (position >= spin6.getSelectedItemPosition()) {
                                // Set the disable item text color
                                tv.setTextColor(Color.BLACK);

                            } else {
                                tv.setTextColor(Color.GRAY);
                            }
                        } else
                            tv.setTextColor(Color.BLACK);

                        return view;
                    }
                };
                spin6_e.setAdapter(adapter6_e);
                spin6_e.setSelection(spin6.getSelectedItemPosition());
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
                cal.set(Integer.parseInt(spin1_e.getSelectedItem().toString()) , spin2_e.getSelectedItemPosition(), 1);
                itemDay = new String[cal.getActualMaximum(Calendar.DATE)];
                cnt = 0 ;

                for (int j = 1; j <= cal.getActualMaximum(Calendar.DATE); j++){
                    itemDay[cnt++] = String.valueOf(j);
                    Log.d("jjjjjjj", ""+j);

                }

                adapter3_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin, itemDay) {
                    @Override
                    public boolean isEnabled(int position) {
                        if(Integer.parseInt(spin1_e.getSelectedItem().toString()) == Integer.parseInt(spin1.getSelectedItem().toString()) && Integer.parseInt(spin2_e.getSelectedItem().toString()) == Integer.parseInt(spin2.getSelectedItem().toString()) ) {
                            if (position >= day - 1)
                                return true;
                            else
                                return false;
                        } else
                            return true;
                    }

                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if(Integer.parseInt(spin1_e.getSelectedItem().toString()) == Integer.parseInt(spin1.getSelectedItem().toString()) && Integer.parseInt(spin2_e.getSelectedItem().toString()) == Integer.parseInt(spin2.getSelectedItem().toString()) ) {
                            if (position >= day - 1) {
                                // Set the disable item text color
                                tv.setTextColor(Color.BLACK);

                            } else {
                                tv.setTextColor(Color.GRAY);
                            }
                        }else
                            tv.setTextColor(Color.BLACK);

                        return view;
                    }
                };
                spin3_e.setAdapter(adapter3_e);
                spin3_e.setSelection(day -1);

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
            HttpConnectionParams.setConnectionTimeout(params, 30000);
            HttpConnectionParams.setSoTimeout(params, 30000);
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

    private void spin1(int position) {
        cal.set(spin1.getSelectedItemPosition(), spin2.getSelectedItemPosition(), 1);
        s = getDateresult(itemYear[position]  , spin2.getSelectedItem() , spin3.getSelectedItem() , spin4.getSelectedItem() , spin5.getSelectedItem()  , spin6.getSelectedItem());
        adapter2 = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemMonth) {
            @Override
            public boolean isEnabled(int position) {
                if (Integer.parseInt(spin1.getSelectedItem().toString()) == year) {
                    if (position >= month - 1)
                        return true;
                    else
                        return false;
                } else
                    return true;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position >= month -1) {
                    // Set the disable item text color
                    tv.setTextColor(Color.BLACK);

                }
                else {
                    tv.setTextColor(Color.GRAY);
                }
                return view;
            }
        };
        spin2.setAdapter(adapter2);
        spin2.setSelection(month -1);

        adapter_e = new ArrayAdapter(getApplicationContext(), R.layout.gallery_spin ,itemYear) {
            @Override
            public boolean isEnabled(int position) {
                if (position >= spin1.getSelectedItemPosition())
                    return true;
                else
                    return false;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position >= spin1.getSelectedItemPosition() ) {
                    // Set the disable item text color
                    tv.setTextColor(Color.BLACK);

                }
                else {
                    tv.setTextColor(Color.GRAY);
                }
                return view;
            }
        };
        spin1_e.setAdapter(adapter_e);
        spin1_e.setSelection(spin1.getSelectedItemPosition());
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
            title_image.setImageBitmap(bmImg);
        }
    }

}
