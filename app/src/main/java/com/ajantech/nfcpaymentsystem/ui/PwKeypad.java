package com.ajantech.nfcpaymentsystem.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ajantech.nfc_network.CommunicationService;
import com.ajantech.nfc_network.CommunicationService.OnNFCServiceCallBack;
import com.ajantech.nfc_network.ShareData;
import com.ajantech.nfc_network.request.DefaultRequestStruct;
import com.geno.payment.R;

public class PwKeypad extends Activity implements OnNFCServiceCallBack {
	private final String TAG = "PwKeypad";
	private final boolean D = true;

	private final int DLG_EXIT = 0;
	private final int DLG_INPUT_AGAIN_PASSWORD = DLG_EXIT + 1;
	private final int DLG_DEFFERENT_PASSWORD = DLG_INPUT_AGAIN_PASSWORD + 1;
	private final int DLG_SAVE_PASSWORD = DLG_DEFFERENT_PASSWORD + 1;
	private final int DLG_CHECK_FAIL_PASSWORD = DLG_SAVE_PASSWORD + 1;

	private final String SPREF_NAME = "Nfc_payment_system";
	private final String SPREF_PASSWORD = "password";
	
	private EditText mPwEditText;
	private ImageButton mNum1Btn;
	private ImageButton mNum2Btn;
	private ImageButton mNum3Btn;
	private ImageButton mNum4Btn;
	private ImageButton mNum5Btn;
	private ImageButton mNum6Btn;
	private ImageButton mNum7Btn;
	private ImageButton mNum8Btn;
	private ImageButton mNum9Btn;
	private ImageButton mNum0Btn;
	private ImageButton mBackBtn;
	private ImageButton mClearBtn;
	private Button mOkBtn;
	private Button mCancelBtn;
	private boolean mSavePassword = false;
	private String mPassword = "";
	private String mFirstPassword = "";
	private boolean mLogSuccess = false;

	// private DbHelper mDbHelper;
	// private SQLiteDatabase mDb;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 1024) {

			} else if (msg.what == 1025) {

			} else if (msg.what == 1026) {
				DefaultRequestStruct request = (DefaultRequestStruct) msg.obj;

				switch (request.type) {
				case DefaultRequestStruct.REQUEST_TYPE_ERROR: {
//					Toast.makeText(PwKeypad.this, "로그인이 실패하였습니다.",
					Toast.makeText(PwKeypad.this, "Fail to login.",
							Toast.LENGTH_SHORT).show();
					break;
				}
				case DefaultRequestStruct.REQUEST_TYPE_AUTH_TOKEN: {
					if(!mLogSuccess) {
						mLogSuccess = true;
						Toast.makeText(PwKeypad.this, "로그인이 성공하였습니다.",
								Toast.LENGTH_SHORT).show();
						finish();
						Intent intent = new Intent(PwKeypad.this, Main.class);
						startActivity(intent);
	//					showDialog(DLG_SAVE_PASSWORD);
					}
					break;
				}
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pw_keypad);

		CommunicationService.getInstance(this).registerNFCCallback(this);
		
		mPwEditText = (EditText) findViewById(R.id.pw_password_edittext);

		buttonInit();

		if (mPassword.equals("")) {
			mSavePassword = true;
		}

		// mDbHelper = DbHelper.getInstance(PwKeypad.this);
		// mDb = mDbHelper.getDatabase();

		/**
		 * password 가 있는지 check
		 */
		ShareData.newInstance(this);
		com.ajantech.nfc_network.ShareData.Settings st = ShareData.newInstance().getSettings();
		mPassword = st.app_pwd;	    

		if (mPassword.length() > 3) {
			mSavePassword = false;
		} else {
			mSavePassword = true;
		}

		/*
		 * Cursor cursor = mDb.rawQuery("select * from " + DbHelper.TABLE_NAME,
		 * null); if(cursor == null || cursor.getCount() == 0) { mSavePassword =
		 * true; } else { cursor.moveToFirst(); mPassword =
		 * cursor.getString(DbHelper.APP_PW); if(mPassword.length() > 3) {
		 * mSavePassword = false; } else { mSavePassword = true; }
		 * cursor.close(); }
		 */
	}

	private void buttonInit() {
		mNum1Btn = (ImageButton) findViewById(R.id.pw_number1_btn);
		mNum1Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPwEditText.setText(mPwEditText.getText().toString() + "1");
				mPwEditText.setSelection(mPwEditText.length());
			}
		});
		mNum2Btn = (ImageButton) findViewById(R.id.pw_number2_btn);
		mNum2Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPwEditText.setText(mPwEditText.getText().toString() + "2");
				mPwEditText.setSelection(mPwEditText.length());
			}
		});
		mNum3Btn = (ImageButton) findViewById(R.id.pw_number3_btn);
		mNum3Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPwEditText.setText(mPwEditText.getText().toString() + "3");
				mPwEditText.setSelection(mPwEditText.length());
			}
		});
		mNum4Btn = (ImageButton) findViewById(R.id.pw_number4_btn);
		mNum4Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPwEditText.setText(mPwEditText.getText().toString() + "4");
				mPwEditText.setSelection(mPwEditText.length());
			}
		});
		mNum5Btn = (ImageButton) findViewById(R.id.pw_number5_btn);
		mNum5Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPwEditText.setText(mPwEditText.getText().toString() + "5");
				mPwEditText.setSelection(mPwEditText.length());
			}
		});
		mNum6Btn = (ImageButton) findViewById(R.id.pw_number6_btn);
		mNum6Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPwEditText.setText(mPwEditText.getText().toString() + "6");
				mPwEditText.setSelection(mPwEditText.length());
			}
		});
		mNum7Btn = (ImageButton) findViewById(R.id.pw_number7_btn);
		mNum7Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPwEditText.setText(mPwEditText.getText().toString() + "7");
				mPwEditText.setSelection(mPwEditText.length());
			}
		});
		mNum8Btn = (ImageButton) findViewById(R.id.pw_number8_btn);
		mNum8Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPwEditText.setText(mPwEditText.getText().toString() + "8");
				mPwEditText.setSelection(mPwEditText.length());
			}
		});
		mNum9Btn = (ImageButton) findViewById(R.id.pw_number9_btn);
		mNum9Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPwEditText.setText(mPwEditText.getText().toString() + "9");
				mPwEditText.setSelection(mPwEditText.length());
			}
		});
		mNum0Btn = (ImageButton) findViewById(R.id.pw_number0_btn);
		mNum0Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPwEditText.setText(mPwEditText.getText().toString() + "0");
				mPwEditText.setSelection(mPwEditText.length());
			}
		});
		mBackBtn = (ImageButton) findViewById(R.id.pw_number_back_btn);
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mPwEditText.getText().toString().length() > 0) {
					mPwEditText.setText(mPwEditText.getText().toString().substring(0, mPwEditText.getText().toString().length() - 1));
					mPwEditText.setSelection(mPwEditText.length());
				} else {

				}
			}
		});
		mClearBtn = (ImageButton) findViewById(R.id.pw_number_clr_btn);
		mClearBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPwEditText.setText("");
			}
		});
		mOkBtn = (Button) findViewById(R.id.pw_ok_btn);
		mOkBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mSavePassword) {
					mFirstPassword = mPwEditText.getText().toString();
					mPwEditText.setText("");
					showDialog(DLG_INPUT_AGAIN_PASSWORD);
					mSavePassword = false;
				} else {
					if (!mFirstPassword.equals("")) {
						if (mFirstPassword.equals(mPwEditText.getText().toString())) {
							// ContentValues values = new ContentValues();
							// values.put("user_app_pw", mFirstPassword);
							// mDb.update(DbHelper.TABLE_NAME, values,
							// "user_app_pw = 1", null);
							savePassword(mFirstPassword);
//							showDialog(DLG_SAVE_PASSWORD);
							logInToServer();
						} else {
							mSavePassword = true;
							mFirstPassword = "";
							mPwEditText.setText("");
							showDialog(DLG_DEFFERENT_PASSWORD);
						}
					} else {
						if (mPassword.equals(mPwEditText.getText().toString())) {
							logInToServer();
						} else {
							showDialog(DLG_CHECK_FAIL_PASSWORD);
						}
					}
				}
			}
		});
		mCancelBtn = (Button) findViewById(R.id.pw_cancel_btn);
		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DLG_EXIT);
			}
		});

	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);

		AlertDialog dialog = null;

		switch (id) {
		case DLG_EXIT: {
			new AlertDialog.Builder(PwKeypad.this, AlertDialog.THEME_HOLO_LIGHT)
			.setTitle(getString(R.string.exit_title))
			.setMessage(getString(R.string.exit_message))
			.setPositiveButton(getString(R.string.ok_str),
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface paramDialogInterface,
								int paramInt) {
							finish();
							android.os.Process.killProcess(android.os.Process.myPid());
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
		case DLG_INPUT_AGAIN_PASSWORD: {
			AlertDialog.Builder bilder = new Builder(PwKeypad.this, AlertDialog.THEME_HOLO_LIGHT);
			bilder.setTitle(R.string.pw_register_title);
			bilder.setMessage(R.string.pw_again_msg);
			bilder.setPositiveButton(getResources().getText(R.string.ok_str),new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}

					});
			dialog = bilder.create();
			break;
		}
		case DLG_DEFFERENT_PASSWORD: {
			AlertDialog.Builder bilder = new Builder(PwKeypad.this);
			bilder.setTitle(R.string.pw_register_title);
			bilder.setMessage(R.string.pw_defferent_msg);
			bilder.setPositiveButton(getResources().getText(R.string.ok_str),new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}

					});
			dialog = bilder.create();
			break;
		}
		case DLG_SAVE_PASSWORD: {
			AlertDialog.Builder bilder = new Builder(PwKeypad.this);
			bilder.setTitle(R.string.pw_register_title);
			bilder.setMessage(R.string.pw_save_msg);
			bilder.setPositiveButton(getResources().getText(R.string.ok_str),new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
					Intent intent = new Intent(PwKeypad.this, Main.class);
					startActivity(intent);						
				}
				
			});
			dialog = bilder.create();
			break;
		}
		case DLG_CHECK_FAIL_PASSWORD: {
			AlertDialog.Builder bilder = new Builder(PwKeypad.this);
			bilder.setTitle(R.string.pw_check_title);
			bilder.setMessage(R.string.pw_check_fail_msg);
			bilder.setPositiveButton(getResources().getText(R.string.ok_str),new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}

					});
			dialog = bilder.create();
			break;
		}
		}

		return dialog;
	}

	private void savePassword(String pwd) {
		com.ajantech.nfc_network.ShareData.Settings st = ShareData
				.newInstance().getSettings();
		st.app_pwd = pwd;
		ShareData.newInstance().setSettings(st);

		logInToServer();
	}

	private void logInToServer() {
		CommunicationService.getInstance().sendLogin(ShareData.newInstance().getLastID(), null,
				ShareData.newInstance().getLastLoginToken());
	}

	@Override
	public void OnNFCServiceMessage(DefaultRequestStruct request) {
		mHandler.obtainMessage(1026, request).sendToTarget();
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {
		showDialog(0);
	}
}
