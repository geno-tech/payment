package com.ajantech.nfcpaymentsystem.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.ajantech.nfc_network.ShareData;
import com.ajantech.nfcpaymentsystem.DbHelper;
import com.geno.payment.R;

public class Title extends Activity {
	private final int HM_SHOW_TYPE_DIALOG = 0;
	private Timer mTimer;
	private int mChoiceIndex = 0;

	private DbHelper mDbHelper;
	private SQLiteDatabase mDb;

	ShareData mConfingData = null;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (msg.what == HM_SHOW_TYPE_DIALOG) {
				String token = mConfingData.getLastLoginToken();

				if (token == null || token.equals("")) {

					finish();
					Intent intent = new Intent(Title.this, Start.class);
					startActivity(intent);
					// 이부분에 메인 메뉴, 등록, 로그인 화면 넣으면 됨.
					// DialogSelectOption();
				} else {
					finish();
					// 기존 로그인 정보가 있을 경우 바로 앱 인증 비밀번호 화면으로 이동
					Intent intent = new Intent(Title.this, PwKeypad.class);
					startActivity(intent);
				}
			}
		}
	};

	class MainTimer extends TimerTask {

		@Override
		public void run() {
			mHandler.obtainMessage(HM_SHOW_TYPE_DIALOG).sendToTarget();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.title_layout);

		// first!!!
		mConfingData = ShareData.newInstance(this);

		mDbHelper = DbHelper.getInstance(Title.this);
		mDb = mDbHelper.getDatabase();

		mTimer = new Timer();
		MainTimer mTim = new MainTimer();
		mTimer.schedule(mTim, 1000);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {
		showDialog(0);
	}

	@Deprecated
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);

		AlertDialog dialog = null;

		switch (id) {
		case 0:
			new AlertDialog.Builder(Title.this, AlertDialog.THEME_HOLO_LIGHT)
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
		return dialog;
	}

	private void DialogSelectOption() {
		final String items[] = { getString(R.string.signup_str),
				getString(R.string.login_str) };
		AlertDialog.Builder ab = new AlertDialog.Builder(Title.this,
				AlertDialog.THEME_HOLO_LIGHT);
		ab.setTitle("선택하세요.");
		ab.setSingleChoiceItems(items, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mChoiceIndex = whichButton;
					}
				})
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (mChoiceIndex == 0) {
							finish();
							Intent intent = new Intent(Title.this,
									GeneralConditions.class);
							startActivity(intent);
						} else {
							/**
							 * 1. 토큰검사 2. 있으면 PwKeypad로 3. 없으면 PwCommon으로
							 */
							com.ajantech.nfc_network.ShareData.Settings st = mConfingData
									.getSettings();
							String token = mConfingData.getLastLoginToken();
							if (token == null || token.equals("")) {
								finish();
								Intent intent = new Intent(Title.this,
										PwCommon.class);
								// Intent intent = new Intent(Title.this,
								// PwKeypad.class);
								startActivity(intent);
							} else {
								finish();
								Intent intent = new Intent(Title.this,
										PwKeypad.class);
								startActivity(intent);
							}

							/*
							 * Cursor cursor = mDb.rawQuery("select * from " +
							 * DbHelper.TABLE_NAME, null);
							 * 
							 * if(cursor == null || cursor.getCount() == 0) {
							 * cursor.close(); finish(); Intent intent = new
							 * Intent(Title.this, PwCommon.class); // Intent
							 * intent = new Intent(Title.this, PwKeypad.class);
							 * startActivity(intent); } else {
							 * 
							 * cursor.moveToFirst(); do {
							 * 
							 * String token = cursor.getString(DbHelper.TOKEN);
							 * 
							 * Log.i("token","token : " + token); }
							 * while(cursor.moveToNext());
							 * 
							 * 
							 * String token = cursor.getString(DbHelper.TOKEN);
							 * if(token == null || token.equals("")) {
							 * cursor.close(); finish(); Intent intent = new
							 * Intent(Title.this, PwCommon.class); // Intent
							 * intent = new Intent(Title.this, PwKeypad.class);
							 * startActivity(intent); } else { cursor.close();
							 * finish(); Intent intent = new Intent(Title.this,
							 * PwKeypad.class); startActivity(intent); } }
							 */
						}
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								mTimer.cancel();
								finish();
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}
						}).setCancelable(false);
		ab.show();
	}

}
