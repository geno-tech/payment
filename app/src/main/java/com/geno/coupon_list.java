package com.geno;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.geno.bill_folder.R;

import java.util.ArrayList;

public class coupon_list extends Activity {
	Intent intent;
	private NfcAdapter mNfcAdapter;
	DBHelper dbHelper, dbHelper2, dbHelper3;

	private PendingIntent mPendingIntent;
	private IntentFilter[] mIntentFilters;
	private final int DLG_EXIT = 0;
	final static String dbName = "basic_coupon.db";
	final static String dbName2 = "payment_coupon.db";
	final static String dbName3 = "user_info.db";
	final static int dbVersion = 1;
	Cursor cursor;
	String voucher_no, issuer, issue_date, issue_time, expired_date,
			expired_time, purchaser, current_owner, available_value;
	String voucher_no1, issuer1, issue_date1, issue_time1, expired_date1,
			expired_time1, purchaser1, current_owner1, available_value1,
			transfer_date1, transfer_time1, transferor1;
	String userName;
	int _id;
	ArrayList<ArrayList<String>> basicList = null;
	ArrayList<String> basicDbList = null;

	ArrayList<ArrayList<String>> payList = null;
	ArrayList<String> payDbList = null;
	ToggleButton tog;
	DataListView list;
	LinearLayout linear1;
	IconTextListAdapter adapter_use;
	IconTextListAdapter adapter_used;
	Resources res;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.use_coupon_layout);

		basicList = new ArrayList<ArrayList<String>>();
		basicDbList = new ArrayList<String>();

		payList = new ArrayList<ArrayList<String>>();
		payDbList = new ArrayList<String>();
		searchDB(); // DB 로드

		list = new DataListView(this);
		linear1 = (LinearLayout) findViewById(R.id.linear1);
		// 어댑터 객체 생성
		//
		adapter_use = new IconTextListAdapter(this);
		adapter_used = new IconTextListAdapter(this);
		// 아이템 데이터 만들기
		res = getResources();

		tog = (ToggleButton) findViewById(R.id.toggle);
		viewInit(); // 리스트뷰 객체 생성

		linear1.addView(list);

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter != null) {
			/*
			 * Toast.makeText(getApplicationContext(),
			 * "Tap to beam to another NFC device", Toast.LENGTH_SHORT) .show();
			 */
		} else {
			Toast.makeText(getApplicationContext(),
					"This phone is not NFC enabled.", Toast.LENGTH_SHORT)
					.show();
		}

		intent = new Intent(getApplicationContext(), coupon_list.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
				intent, 0);

		// set an intent filter for all MIME data
		IntentFilter ndefIntent = new IntentFilter(
				NfcAdapter.ACTION_NDEF_DISCOVERED);
		try {
			ndefIntent.addDataType("*/*");
			mIntentFilters = new IntentFilter[] { ndefIntent };
		} catch (Exception e) {
			Log.e("TagDispatch", e.toString());
		}

	}

	public void viewInit() {
		int couponNum;
		adapter_use = new IconTextListAdapter(this);
		adapter_used = new IconTextListAdapter(this);

		for (int k = 0; k < basicList.size(); k++) {
			if (basicList.get(k).get(1).equals("롯데백화점")) {
				couponNum = res.getIdentifier("coupon_1", "drawable",
						"com.geno.bill_folder");
			} else if (basicList.get(k).get(1).equals("신세계백화점")) {
				couponNum = res.getIdentifier("coupon_2", "drawable",
						"com.geno.bill_folder");
			} else if (basicList.get(k).get(1).equals("NC")) {
				couponNum = res.getIdentifier("coupon_3", "drawable",
						"com.geno.bill_folder");
			} else if (basicList.get(k).get(1).equals("현대백화점")) {
				couponNum = res.getIdentifier("coupon_4", "drawable",
						"com.geno.bill_folder");
			} else if (basicList.get(k).get(1).equals("온누리")) {
				couponNum = res.getIdentifier("coupon_7", "drawable",
						"com.geno.bill_folder");
			} else {
				couponNum = res.getIdentifier("coupon_5", "drawable",
						"com.geno.bill_folder");
			}
			adapter_use.addItem(new IconTextItem(res.getDrawable(couponNum),
					basicList.get(k).get(1), basicList.get(k).get(8) + " 원",
					basicList.get(k).get(4) + "," + basicList.get(k).get(5)));
		}
		for (int k = 0; k < payList.size(); k++) {
			if (payList.get(k).get(1).equals("롯데백화점")) {
				couponNum = res.getIdentifier("coupon_1", "drawable",
						"com.geno.bill_folder");
			} else if (payList.get(k).get(1).equals("신세계백화점")) {
				couponNum = res.getIdentifier("coupon_2", "drawable",
						"com.geno.bill_folder");
			} else if (payList.get(k).get(1).equals("NC")) {
				couponNum = res.getIdentifier("coupon_3", "drawable",
						"com.geno.bill_folder");
			} else if (payList.get(k).get(1).equals("현대백화점")) {
				couponNum = res.getIdentifier("coupon_4", "drawable",
						"com.geno.bill_folder");
			} else if (payList.get(k).get(1).equals("온누리")) {
				couponNum = res.getIdentifier("coupon_7", "drawable",
						"com.geno.bill_folder");
			} else {
				couponNum = res.getIdentifier("coupon_5", "drawable",
						"com.geno.bill_folder");
			}
			adapter_used.addItem(new IconTextItem(res.getDrawable(couponNum),
					payList.get(k).get(1), payList.get(k).get(2) + " 원",
					payList.get(k).get(4) + "," + payList.get(k).get(5)));
		}
		list.setAdapter(adapter_use);
		// 리스트뷰에 어댑터 설정
		// 새로 정의한 리스너로 객체를 만들어 설정

		list.setOnDataSelectionListener(new OnDataSelectionListener() {
			public void onDataSelected(AdapterView parent, View v,
					int position, long id) {
				// 커스텀 리스트뷰에 적힌 텍스트를 읽어옴, 우리는 내부 DB에서 데이터를 가져오면 되므로 position 만
				// 필요함
				/*
				 * IconTextItem curItem = (IconTextItem) adapter_use
				 * .getItem(position); String[] curData = curItem.getData();
				 */
				finish();
				intent = new Intent(coupon_list.this, CouponDetailPaid.class);
				intent.putExtra("dbPosition", "" + position);
				startActivity(intent);
			}
		});
		tog.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Perform action on clicks
				if (tog.isChecked()) { // 토클버튼이 ON인 상태인 경우
					list.setAdapter(adapter_used);
					list.setOnDataSelectionListener(new OnDataSelectionListener() {
						public void onDataSelected(AdapterView parent, View v,
								int position, long id) {
							// 커스텀 리스트뷰에 적힌 텍스트를 읽어옴, 우리는 내부 DB에서 데이터를 가져오면 되므로
							// position 만 필요함
							/*
							 * IconTextItem curItem = (IconTextItem) adapter_use
							 * .getItem(position); String[] curData =
							 * curItem.getData();
							 */
							finish();
							intent = new Intent(coupon_list.this,
									CouponDetailCal.class);
							intent.putExtra("dbPosition", "" + position);
							startActivity(intent);
						}
					});
					// Toast.makeText(coupon_list.this, "ON",
					// Toast.LENGTH_SHORT)
					// .show();
				} else { // 토글버튼이 OFF인 경우
					list.setAdapter(adapter_use);
					list.setOnDataSelectionListener(new OnDataSelectionListener() {
						public void onDataSelected(AdapterView parent, View v,
								int position, long id) {
							// 커스텀 리스트뷰에 적힌 텍스트를 읽어옴, 우리는 내부 DB에서 데이터를 가져오면 되므로
							// position 만 필요함
							/*
							 * IconTextItem curItem = (IconTextItem) adapter_use
							 * .getItem(position); String[] curData =
							 * curItem.getData();
							 */
							finish();
							intent = new Intent(coupon_list.this,
									CouponDetailPaid.class);
							intent.putExtra("dbPosition", "" + position);
							startActivity(intent);
						}
					});
					// Toast.makeText(coupon_list.this, "OFF",
					// Toast.LENGTH_SHORT)
					// .show();
				}
			}
		});
	}

	public void searchDB() {
		SQLiteDatabase db, db2, db3;
		String sql, sql2, sql3;

		dbHelper = new DBHelper(this, dbName, null, dbVersion);
		dbHelper2 = new DBHelper(this, dbName2, null, dbVersion);
		dbHelper3 = new DBHelper(this, dbName3, null, dbVersion);
		db = dbHelper.getReadableDatabase();
		db2 = dbHelper2.getReadableDatabase();
		db3 = dbHelper3.getReadableDatabase();

		sql = "SELECT * FROM basic_coupon;";
		sql2 = "SELECT * FROM payment_coupon;";
		sql3 = "SELECT * FROM user_info;";
		basicDbList.clear();
		basicList.clear();
		payDbList.clear();
		payList.clear();
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			int j = 0;
			while (cursor.moveToNext()) {

				// 0번은 고유 아이디 값(Integer)이므로 1부터 시작하며, 컬럼의 수만큼 반복하면서 dbList에 값을
				// 삽입
				for (int i = 1; i < cursor.getColumnCount(); i++) {
					basicDbList.add(cursor.getString(i));
				}
				basicList.add(basicDbList);
				basicDbList = new ArrayList<String>();
				// Toast.makeText(coupon_list.this, basicList.get(j) + "",
				// Toast.LENGTH_SHORT).show();
				j = j + 1;
			}
			cursor.close();
		}
		cursor = db2.rawQuery(sql2, null);
		if (cursor.getCount() > 0) {
			int j = 0;
			while (cursor.moveToNext()) {

				// 0번은 고유 아이디 값(Integer)이므로 1부터 시작하며, 컬럼의 수만큼 반복하면서 dbList에 값을
				// 삽입
				for (int i = 1; i < cursor.getColumnCount(); i++) {
					payDbList.add(cursor.getString(i));
				}
				payList.add(payDbList);
				payDbList = new ArrayList<String>();
				// Toast.makeText(coupon_list.this, payList.get(j) + "",
				// Toast.LENGTH_SHORT).show();
				j = j + 1;
			}
			cursor.close();
		}
		cursor = db3.rawQuery(sql3, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				userName = cursor.getString(1);
			}

		}
		cursor.close();
	}

	@Override
	public void onResume() {
		super.onResume();
		mNfcAdapter.enableForegroundDispatch(this, mPendingIntent,
				mIntentFilters, null);
		Log.d("onResume", "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		mNfcAdapter.disableForegroundDispatch(this);
	}

	class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE user_info (id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "name VARCHAR(5), tell VARCHAR(11), email varchar(30), password varchar(12));");
			db.execSQL("CREATE TABLE basic_coupon (id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "voucher_no VARCHAR(16), issuer VARCHAR(10), issue_date varchar(10), issue_time varchar(8),"
					+ "expired_date varchar(10), expired_time varchar(8), purchaser varchar(5),"
					+ "current_owner varchar(5), available_value varchar(10), transfer_date varchar(10),"
					+ "transfer_time varchar(8), transferor varchar(10), transferee varchar(10));");
			db.execSQL("CREATE TABLE payment_coupon (id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "voucher_no VARCHAR(16), used_value varchar(10), issuer varchar(10), final_seller VARCHAR(10), sell_date varchar(10), sell_time varchar(8));");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXITS user_info");
			onCreate(db);
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

	public void mOnClick(View v) {
		switch (v.getId()) {
		case R.id.issuebutton:
			finish();
			intent = new Intent(this, IssueCoupon.class);
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
	public void onNewIntent(Intent intent) {
		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];
		// record 0 contains the MIME type, record 1 is the AAR, if present
		String strSite = new String(msg.getRecords()[0].getPayload());
		if (strSite != null && !strSite.equals("")) {
			voucher_no1 = strSite.substring(strSite.indexOf("vou") + 4,//basicDbList.get(0)값 vou: 다음값부터
					strSite.indexOf("\n")); // \n의 인덱스값 까지의 문자열을 반환
			issuer1 = strSite.substring(strSite.indexOf("iss") + 4,//basicDbList.get(1)값 \niss: 다음값부터
					strSite.indexOf("\n", strSite.indexOf("iss") + 4));//iss에서 시작해서 다음 \n의 인덱스 값 까지의 문자열을 반환
			issue_date1 = strSite.substring(strSite.indexOf("issd") + 5,
					strSite.indexOf("\n", strSite.indexOf("issd") + 5));
			issue_time1 = strSite.substring(strSite.indexOf("isst") + 5,
					strSite.indexOf("\n", strSite.indexOf("isst") + 5));
			expired_date1 = strSite.substring(strSite.indexOf("expd") + 5,
					strSite.indexOf("\n", strSite.indexOf("expd") + 5));
			expired_time1 = strSite.substring(strSite.indexOf("expt") + 5,
					strSite.indexOf("\n", strSite.indexOf("expt") + 5));
			purchaser1 = strSite.substring(strSite.indexOf("pur") + 4,
					strSite.indexOf("\n", strSite.indexOf("pur") + 4));
			current_owner1 = strSite.substring(strSite.indexOf("cur") + 4,
					strSite.indexOf("\n", strSite.indexOf("cur") + 4));
			available_value1 = strSite.substring(strSite.indexOf("ava") + 4,
					strSite.indexOf("\n", strSite.indexOf("ava") + 4));
			transfer_date1 = strSite.substring(strSite.indexOf("trad") + 5,
					strSite.indexOf("\n", strSite.indexOf("trad") + 5));
			transfer_time1 = strSite.substring(strSite.indexOf("trat") + 5,
					strSite.indexOf("\n", strSite.indexOf("trat") + 5));
			transferor1 = strSite.substring(strSite.indexOf("traf") + 5,
					strSite.length());

			// Issuer = strSite.substring(strSite.indexOf("Issuer") + 7,
			// strSite.length());
		}
		/*
		 * Toast.makeText(getApplicationContext(), "vou ="+voucher_no1 +
		 * "\nissuer ="+issuer1 + "\nissue_date ="+issue_date1 +
		 * "\nissue_time ="+issue_time1 + "\nexpired_date ="+expired_date1 +
		 * "\nexpired_time ="+expired_time1 + "\npurchaser ="+purchaser1 +
		 * "\ncurrent_owner ="+current_owner1 +
		 * "\navailable_value ="+available_value1 +
		 * "\ntransfer_date ="+transfer_date1+
		 * "\ntransfer_time ="+transfer_time1+ "\ntransferor ="+transferor1 +
		 * "\ntransferee ="+userName, Toast.LENGTH_SHORT).show();
		 */
		SQLiteDatabase db;
		String sql;
		db = dbHelper.getWritableDatabase();//데이터를 입력
		sql = String.format(
				"INSERT INTO basic_coupon VALUES (NULL, '%s', '%s', '%s', '%s',"
						+ " '%s', '%s', '%s', '%s', '%s',"
						+ " '%s','%s','%s','%s');", voucher_no1, issuer1,
				issue_date1, issue_time1, expired_date1, expired_time1,
				purchaser1, userName, available_value1, transfer_date1,
				transfer_time1, transferor1, userName);
		db.execSQL(sql);
		Log.d("onNewIntent", "onNewIntent");
		searchDB();
		viewInit();
		setIntent(intent);
		return;
	}
}
