package com.geno;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.geno.bill_folder.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IssueCoupon extends Activity {
	DBHelper dbHelper, dbHelper2;
	LayoutInflater inflater1;
	final static String dbName = "user_info.db";
	final static String dbName2 = "basic_coupon.db";
	final static int dbVersion = 1;
	Cursor cursor;
	ImageButton imgbut1;
	LayoutInflater inflater;

	private final int DLG_EXIT = 0;
	final int DIALOG_1 = 1;
	final int DIALOG_2 = 2;
	Intent intent, intent2;
	String bankName, storeName, userName;
	String voucher_no, nowday, nowtime, expiredday, expiredtime;
	EditText bankAccount, passWd, faceValue;
	Spinner spinner1;
	Spinner spinner2;

	String returnValue;
	String[] parseData;
	private ProgressDialog dialog;
	protected ArrayList<Map<String, String>> dataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.issue_coupon);
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		bankAccount = (EditText) findViewById(R.id.et2);
		passWd = (EditText) findViewById(R.id.et3);
		faceValue = (EditText) findViewById(R.id.et4);

		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner2 = (Spinner) findViewById(R.id.spinner2);

		viewInit();
		dbHelper = new DBHelper(this, dbName, null, dbVersion);
		dbHelper2 = new DBHelper(this, dbName2, null, dbVersion);
		SQLiteDatabase db;
		String sql;
		db = dbHelper.getReadableDatabase();
		sql = "SELECT * FROM user_info;";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				userName = cursor.getString(1);
			}
		}
		intent2 = new Intent(IssueCoupon.this, coupon_list.class);
		// Toast.makeText(getApplicationContext(),userName,Toast.LENGTH_SHORT).show();

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

	private void viewInit() {
		String[] bank_list = getResources().getStringArray(R.array.bank_list);
		String[] seller_list = getResources().getStringArray(
				R.array.seller_list);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, bank_list);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, seller_list);

		spinner1.setAdapter(adapter1);
		spinner2.setAdapter(adapter2); 
		spinner1.setOnItemSelectedListener(new SpinnerSelect1());
		spinner2.setOnItemSelectedListener(new SpinnerSelect2());

		bankAccount.setText("012345678912");
		passWd.setText("1234");
		faceValue.setText("100000");
	}

	public class SpinnerSelect1 implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub

			bankName = (String) parent.getItemAtPosition(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}

	public class SpinnerSelect2 implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub

			storeName = (String) parent.getItemAtPosition(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}

	public void mOnClick(View v) {
		switch (v.getId()) {
		case R.id.issuebutton:
			if (bankName.equals(null)) {
				Toast.makeText(getApplicationContext(), "은행을 선택하세요.",
						Toast.LENGTH_SHORT).show();
			} else if (bankAccount.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(), "은행계좌번호를 입력하세요.",
						Toast.LENGTH_SHORT).show();
			} else if (passWd.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.",
						Toast.LENGTH_SHORT).show();
			} else if (faceValue.getText().toString().equals("")) {
				Toast.makeText(getApplicationContext(), "상품권 금액을 입력하세요.",
						Toast.LENGTH_SHORT).show();
			} else if (storeName.equals(null)) {
				Toast.makeText(getApplicationContext(), "가맹점을 선택하세요.",
						Toast.LENGTH_SHORT).show();
			} else {
				/*
				 * Toast.makeText(getApplicationContext(), "은행:"+bankName+
				 * "\n계좌번호:"+bankAccount.getText()+ "\n비밀번호:"+passWd.getText()+
				 * "\n상품권가격:"+faceValue.getText()+ "\n가맹점:"+storeName,
				 * Toast.LENGTH_SHORT).show();
				 */
				IssueCoupon();

			}
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
		case DIALOG_1:
			final LinearLayout linear = (LinearLayout) inflater.inflate(
					R.layout.issue_confirm, null);
			return new AlertDialog.Builder(IssueCoupon.this)
			// .setTitle("쿠폰 발급")
			// .setIcon(R.drawable.ic_launcher)
					.setView(linear)
					/*
					 * .setPositiveButton("종료", new
					 * DialogInterface.OnClickListener() {
					 * 
					 * @Override public void onClick(DialogInterface dialog, int
					 * which) { finish(); } }).setNegativeButton("발급", null)
					 */.create();
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

	class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE basic_coupon (id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "voucher_no VARCHAR(16), issuer VARCHAR(10), issue_date varchar(10), issue_time varchar(8),"
					+ "expired_date varchar(10), expired_time varchar(8), purchaser varchar(5),"
					+ "current_owner varchar(5), available_value varchar(10), transfer_date varchar(10),"
					+ "transfer_time varchar(8), transferor varchar(10), transferee varchar(10));");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXITS user_info");
			onCreate(db);
		}

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		showDialog(DLG_EXIT);
	}

	public void IssueCoupon() {
		dialog = new ProgressDialog(this);
		dialog.setTitle("Loading...");
		dialog.setMessage("쿠폰 발급");
		dialog.setCancelable(false);
		dialog.setMax(3);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.show();
		mTask.execute();
	}

	private AsyncTask<Void, Integer, Void> mTask = new AsyncTask<Void, Integer, Void>() {
		protected Void doInBackground(Void... p) {
			// 새롭게 데이터를 가져오는 부분(기존 List는 삭제)

			dataList = new ArrayList<Map<String, String>>();

			try {
				publishProgress(1); // 접속 중
				HttpClient httpClient = new DefaultHttpClient();
				// replace with your url
				HttpPost httpPost = new HttpPost(
						"http://118.40.113.241:9999/db_connect.jsp");

				// Post Data
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(
						2);
				nameValuePair.add(new BasicNameValuePair("bankName", bankName));
				nameValuePair.add(new BasicNameValuePair("bankAccount", bankAccount.getText().toString()));
				nameValuePair.add(new BasicNameValuePair("passWd", passWd.getText().toString()));
				nameValuePair.add(new BasicNameValuePair("faceValue", faceValue.getText().toString()));
				nameValuePair.add(new BasicNameValuePair("storeName", storeName));
				nameValuePair.add(new BasicNameValuePair("userName", userName));

				publishProgress(2); // 데이터 수신 중
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair,"euc-kr"));
				HttpResponse response = httpClient.execute(httpPost);

				publishProgress(3); // 데이터 분석중
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					int l;
					byte[] tmp = new byte[4096];
					while ((l = instream.read(tmp)) != -1) {
						returnValue = new String(tmp, 0, tmp.length);
					}
				}

				parseData = returnValue.split("::"); //"::"를 경계로 문자열을 잘라서 단어로 만듬
			} catch (Exception e) {
				publishProgress(-1);
				Log.e("net", "게시판 오류", e);
			}

			return null;
		}

		protected void onProgressUpdate(Integer[] values) {
			switch (values[0]) {
			case -1:
				Toast.makeText(getApplicationContext(), "통신 오류", 0).show();
				break;
			case 1:
				dialog.setMessage("연결 중...");
				break;
			case 2:
				dialog.setMessage("발급 중...");
				break;
			case 3:
				dialog.setMessage("발급 중...");
				break;
			}

			dialog.setProgress(values[0]);
		}

		protected void onPostExecute(Void result) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(), "발급완료", 0).show();
			voucher_no = parseData[1];
			nowday = parseData[2];
			nowtime = parseData[3];
			expiredday = parseData[4];
			expiredtime = parseData[5];
			SQLiteDatabase db;
			String sql;
			db = dbHelper2.getWritableDatabase();
			sql = String
					.format("INSERT INTO basic_coupon VALUES (NULL, '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s',null,null,null,null);",
							voucher_no, storeName, nowday, nowtime, expiredday, expiredtime, userName, userName, faceValue.getText().toString());			
			db.execSQL(sql);
			finish();
			startAcitivity(intent2);
		}
	};

	protected void startAcitivity(Intent intent) {
		// TODO Auto-generated method stub
		startActivity(intent);
	}
}
