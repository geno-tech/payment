package com.GalleryAuction.Bidder;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.geno.bill_folder.R;

public class ArtInfoTagList extends Activity {
    String username, test2, image;
    DBHelper dbHelper;
    private ListView listView;
    private ArtInfoAdapter adapter;
    Button remove_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galleryartinfotaglist);
//        SharedPreferences preferences = getSharedPreferences("KEY", 0);
//        String key = preferences.getString("key", null);
//        Toast.makeText(this, Artinfo(key), Toast.LENGTH_SHORT).show();
        remove_btn = (Button)findViewById(R.id.artinforemove_btn);
        dbHelper = new DBHelper(getApplicationContext(), "MoneyBook.db", null, 1);
        adapter = new ArtInfoAdapter();

        listView = (ListView) findViewById(R.id.artinfo_listview);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            adapter.
            }
        });
//        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, LIST_MENU) ;
//        ListView listView = (ListView)findViewById(R.id.artinfo_listview);
//        listView.setAdapter(adapter);
    getname();
    remove_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dbHelper.deleteAll();
            adapter.notifyDataSetChanged();
        }
    });
    }

    public void getname() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM MONEYBOOK", null);
        while (cursor.moveToNext()) {
            adapter.addItem(cursor.getString(0),cursor.getString(1),cursor.getString(3));
        }

    }
}
