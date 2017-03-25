package com.geno;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.geno.bill_folder.R;

public class Join extends Activity {
	DBHelper dbHelper, dbHelper2, dbHelper3, dbHelper4; //디비
	EditText nameEdit, tellEdit, emailEdit, passEdit, passEdit2; // 텍스트값
	LayoutInflater inflater1, inflater2;
	final static String dbName = "user_info.db";
	final static String dbName2 = "basic_coupon.db";
	final static String dbName3 = "payment_coupon.db";
	final static int dbVersion = 1;
	private final int DLG_EXIT = 0;
	final int DIALOG_1 = 1;
	final int DIALOG_2 = 2;
	final int DIALOG_3 = 3;
	String pwd;
	Cursor cursor;

	Button button1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.join);
		inflater1 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		nameEdit = (EditText) findViewById(R.id.et1);
		tellEdit = (EditText) findViewById(R.id.et2);
		emailEdit = (EditText) findViewById(R.id.et3);
		passEdit = (EditText) findViewById(R.id.et4);
		button1 = (Button) findViewById(R.id.joinconfirm);
		
		dbHelper = new DBHelper(this, dbName, null, dbVersion);
		dbHelper2 = new DBHelper(this, dbName2, null, dbVersion);
		dbHelper3 = new DBHelper(this, dbName3, null, dbVersion);

		SQLiteDatabase db;
		String sql;
		db = dbHelper.getReadableDatabase();//디비 읽음
		sql = "SELECT * FROM user_info;";//명령어
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				pwd = cursor.getString(4);
			}
			nameEdit.setEnabled(false);
			tellEdit.setEnabled(false);
			emailEdit.setEnabled(false);			
			passEdit.setEnabled(false);
			button1.setEnabled(false);
			
			showDialog(DIALOG_1);
		} else {

		}

		cursor.close();
		/*
		 * finish(); Intent intent = new Intent(this, IssueCoupon.class);
		 * startActivity(intent);
		 */
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

	public void mOnClick(View v) {
		SQLiteDatabase db;
		String sql;
		Intent intent;
		switch (v.getId()) {
		case R.id.joinconfirm:
			String name = nameEdit.getText().toString();
			String age = tellEdit.getText().toString();
			String email = emailEdit.getText().toString();
			String pass = passEdit.getText().toString();

			db = dbHelper.getWritableDatabase();
			sql = String
					.format("INSERT INTO user_info VALUES (NULL,  '%s', '%s', '%s', '%s');",
							name, age, email, pass);
			db.execSQL(sql);

			db = dbHelper2.getReadableDatabase();
			sql = "SELECT * FROM basic_coupon;";
			Cursor cursor = db.rawQuery(sql, null);
			if (cursor.getCount() > 0) {
				// Toast.makeText(getApplicationContext(), "쿠폰 존재.",
				// Toast.LENGTH_SHORT).show();
				finish();
				intent = new Intent(this, coupon_list.class);
				startActivity(intent);

			} else {
				// Toast.makeText(getApplicationContext(), "쿠폰 없음.",
				// Toast.LENGTH_SHORT).show();
				showDialog(DIALOG_2);
			}

			break;
		case R.id.joincancel:
			finish();
			break;
		case R.id.issueconfirm:
			finish();
			intent = new Intent(this, IssueCoupon.class);
			startActivity(intent);
			break;
		case R.id.issuecancel:
			finish();
			intent = new Intent(this, coupon_list.class);
			startActivity(intent);
			break;
		case R.id.titlebutton:
			finish();
			intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
		case R.id.pw_confirm:

			if (pwd.equals((passEdit2.getText().toString()))) {//pwd : db에 저장된 비번값 = passEdit2 : 내가 입력한 비번값 이어야지 로그인됨
				dbHelper = new DBHelper(this, dbName, null, dbVersion);

				db = dbHelper2.getReadableDatabase();
				sql = "SELECT * FROM basic_coupon;";
				cursor = db.rawQuery(sql, null);
				if (cursor.getCount() > 0) {
					// Toast.makeText(getApplicationContext(), "쿠폰 존재.",
					// Toast.LENGTH_SHORT).show();
					finish();
					intent = new Intent(this, coupon_list.class);
					startActivity(intent);

				} else {
					// Toast.makeText(getApplicationContext(), "쿠폰 없음.",
					// Toast.LENGTH_SHORT).show();
					dismissDialog(DIALOG_1);
					showDialog(DIALOG_2);
				}
			} else {
				Toast.makeText(getApplicationContext(), "비밀번호가 틀렸습니다.",
						Toast.LENGTH_SHORT).show();
			}

		}
		dbHelper.close();

	}

	class DBHelper extends SQLiteOpenHelper {//SQLiteOpenHelper를 상속받아서 dbHelper클래스를 작성한다
		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}
		//매개변수 context에 데이터베이스를 생성하는 액티비티 전달 name- DB파일이름

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE user_info (id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "name VARCHAR(5), tell VARCHAR(11), email varchar(30), password varchar(12));");//user_info 테이블 생성. 생성된거 변경하려면 다른 sql문 넣는거 말고는 방법이 없는듯 허다.
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
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);

		AlertDialog dialog = null;

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

		case DIALOG_1:
			final LinearLayout linear1 = (LinearLayout) inflater1.inflate(
					R.layout.password, null);
			passEdit2 = (EditText) linear1.findViewById(R.id.pw_et);
			return new AlertDialog.Builder(Join.this).setView(linear1).create();
		case DIALOG_2:
			final LinearLayout linear2 = (LinearLayout) inflater2.inflate(
					R.layout.issue_confirm, null);
			return new AlertDialog.Builder(Join.this)
			// .setTitle("쿠폰 발급")
			// .setIcon(R.drawable.ic_launcher)
					.setView(linear2)
					/*
					 * .setPositiveButton("종료", new
					 * DialogInterface.OnClickListener() {
					 * 
					 * @Override public void onClick(DialogInterface dialog, int
					 * which) { finish(); } }).setNegativeButton("발급", null)
					 */.create();
		case DIALOG_3:
			this.dismissDialog(DIALOG_2);
			break;
		}

		return dialog;
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		showDialog(DLG_EXIT);
	}

	private void viewInit() {
		nameEdit.setText("문종민");
		tellEdit.setText("01096563295");
		emailEdit.setText("whdals0@naver.com");
		passEdit.setText("1234");
	}
}
