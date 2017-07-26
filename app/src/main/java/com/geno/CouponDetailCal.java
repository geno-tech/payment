package com.geno;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.geno.payment.R;

import java.util.ArrayList;

public class CouponDetailCal extends Activity {
	private final int DLG_EXIT = 0;
	Intent intent;
	String dbPosition;
	DBHelper dbHelper;
	ImageView imgView;
	final static String dbName = "payment_coupon.db";
	final static int dbVersion = 1;
	Cursor cursor;
	String voucher_no, used_value, final_seller, issuer, used_date, used_time;
	TextView valueText, issuerText, usedText;
	ArrayList<String> payDbList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.detail_coupon_cal);
		intent = getIntent();
		dbPosition = intent.getStringExtra("dbPosition");
		payDbList = new ArrayList<String>();

		imgView = (ImageView) findViewById(R.id.img1);
		valueText = (TextView) findViewById(R.id.tv2);
		issuerText = (TextView) findViewById(R.id.tv4);
		usedText = (TextView) findViewById(R.id.tv6);
		searchDB();
		viewInit();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void viewInit() {
		valueText.setText(payDbList.get(2));
		issuerText.setText(payDbList.get(1));
		usedText.setText(payDbList.get(4) + "," + payDbList.get(5));
		Resources res = getResources();
		int couponNum;
		if(payDbList.get(1).equals("롯데백화점"))
		{
			couponNum = res.getIdentifier("coupon_1", "drawable", "com.geno.payment");
		} else if(payDbList.get(1).equals("신세계백화점"))
		{
			couponNum = res.getIdentifier("coupon_2", "drawable", "com.geno.payment");
		}
		else if(payDbList.get(1).equals("NC"))
		{
			couponNum = res.getIdentifier("coupon_3", "drawable", "com.geno.payment");
		}
		else if(payDbList.get(1).equals("현대백화점"))
		{
			couponNum = res.getIdentifier("coupon_4", "drawable", "com.geno.payment");
		}
		else if(payDbList.get(1).equals("온누리"))
		{
			couponNum = res.getIdentifier("coupon_7", "drawable", "com.geno.payment");
		}
		else
		{
			couponNum = res.getIdentifier("coupon_5", "drawable", "com.geno.payment");
		}
		imgView.setBackgroundResource(couponNum);
	}

	public void mOnClick(View v) {
		switch (v.getId()) {
		case R.id.prevbutton:
			finish();
			intent = new Intent(this, coupon_list.class);
			startActivity(intent);
			break;
		case R.id.titlebutton:
			finish();
			intent = new Intent(this, coupon_list.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DLG_EXIT:
			new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
					.setTitle(getString(R.string.exit_title))
					.setMessage(getString(R.string.exit_message))
					.setPositiveButton(getString(R.string.ok_str),
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
									finish();
									android.os.Process
											.killProcess(android.os.Process
													.myPid());
								}
							})
					.setNegativeButton(getString(R.string.cancel_str),
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
								}
							}).show();
			break;
		}
		return null;
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		showDialog(DLG_EXIT);
	}

	public void searchDB() {
		SQLiteDatabase db;
		String sql;

		dbHelper = new DBHelper(this, dbName, null, dbVersion);
		db = dbHelper.getReadableDatabase();

		int j = Integer.parseInt(dbPosition);
		sql = "SELECT * FROM payment_coupon;";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				if (cursor.getPosition() == j) {
					for (int i = 1; i < cursor.getColumnCount(); i++) {
						payDbList.add(cursor.getString(i));
					}
				}
			}

		}
		cursor.close();

	}

	class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL("CREATE TABLE payment_coupon (id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "voucher_no VARCHAR(16), used_value varchar(10), issuer varchar(10), final_seller VARCHAR(10), sell_date varchar(10), sell_time varchar(8));");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXITS user_info");
			onCreate(db);
		}

	}

}
