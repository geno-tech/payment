package com.ajantech.nfcpaymentsystem.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

import com.ajantech.nfc_network.CommunicationService;
import com.ajantech.nfc_network.CommunicationService.OnNFCServiceCallBack;
import com.ajantech.nfc_network.ShareData;
import com.ajantech.nfc_network.request.AuthTokenRequestStruct;
import com.ajantech.nfc_network.request.DefaultRequestStruct;
import com.ajantech.nfc_network.request.ErrorRequestStruct;
import com.geno.bill_folder.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class ModifyInformation extends Activity implements OnNFCServiceCallBack {
	private final String TAG = "ModifyInformation";
	private final boolean D = true;

	private final int DLG_EXIT = 0;
	private final int DLG_EMPTY_ID = DLG_EXIT + 1;
	private final int DLG_WRONG_ID = DLG_EMPTY_ID + 1;
	private final int DLG_EMPTY_PASSWORD = DLG_WRONG_ID + 1;
	private final int DLG_WRONG_PASSWORD = DLG_EMPTY_PASSWORD + 1;
	private final int DLG_EMPTY_FRONT_IDENDIFICATION = DLG_WRONG_PASSWORD + 1;
	private final int DLG_EMPTY_BACK_IDENDIFICATION = DLG_EMPTY_FRONT_IDENDIFICATION + 1;
	private final int DLG_WRONG_IDENDIFICATION = DLG_EMPTY_BACK_IDENDIFICATION + 1;
	private final int DLG_EMPTY_PHONE = DLG_WRONG_IDENDIFICATION + 1;
	private final int DLG_WRONG_PHONE = DLG_EMPTY_PHONE + 1;
	private final int DLG_EMPTY_ACCOUNT_NUMBER = DLG_WRONG_PHONE + 1;
	private final int DLG_WRONG_ACCOUNT_NUMBER = DLG_EMPTY_ACCOUNT_NUMBER + 1;
	private final int DLG_EMPTY_ACCOUNT_PASSWORD = DLG_WRONG_ACCOUNT_NUMBER + 1;
	private final int DLG_WRONG_ACCOUNT_PASSWORD = DLG_EMPTY_ACCOUNT_PASSWORD + 1;
	private final int DLG_CONN_SERVER = DLG_WRONG_ACCOUNT_PASSWORD + 1;
	private final int DLG_CONN_FAIL_SERVER = DLG_CONN_SERVER + 1;
	private final int DLG_ADD_SUCCESS = DLG_CONN_FAIL_SERVER + 1;
	private final int DLG_ADD_FAIL = DLG_ADD_SUCCESS + 1;

	private final int HM_DEFAULT = 0;
	private final int HM_GET_MSG = HM_DEFAULT + 1;
	private final int HM_GET_REQUEST = HM_GET_MSG + 1;

	private RadioGroup mTypeRadioGroup;
	private EditText mNameEditText;
	private EditText mFirstIdentificationEditText;
	private EditText mSecondIdentificationEditText;
	private EditText mIdEditText;
	private EditText mPasswordEditText;
	private String mUserTypeStr;
	private EditText mPhoneNumEditText;
	private Spinner mAccountListSpinner;
	private EditText mAccountNumEditText;
	private EditText mAccountPasswordEditText;
	private Button mOkBtn;
	private Button mCancelBtn;
	private ProgressDialog mProgressDialog;
	// private DbHelper mDbHelper;
	// private SQLiteDatabase mDb;

	private String mBeforeFirstIdentStr = "";
	private String mBeforeSecondIdentStr = "";
	private String mBeforeAccPwIdentStr = "";

	ShareData mConfingData = null;

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == HM_GET_MSG) {
				if (D) {
					Log.d(TAG, "GET MESSAGE = " + msg.obj);
				}
			} else if (msg.what == HM_GET_REQUEST) {
				DefaultRequestStruct request = (DefaultRequestStruct) msg.obj;

				if (mProgressDialog != null)
					mProgressDialog.dismiss();

				switch (request.type) {
				case DefaultRequestStruct.REQUEST_TYPE_ERROR: {
					if (D) {
						Log.d(TAG, "REQUEST_TYPE_ERROR");
					}

					ErrorRequestStruct errorRequest = (ErrorRequestStruct) request;

					Log.d(TAG, "runRequestCommand : "
							+ errorRequest.runRequestCommand);
					Log.d(TAG, "errorCode : " + errorRequest.errorCode);
					Log.d(TAG, "errorMessage : " + errorRequest.errorMessage);
					switch (errorRequest.runRequestCommand) {
					case DefaultRequestStruct.REQUEST_COMMAND_NEW_USER:
						Log.d(TAG, "신규유저 등록 실패!!");
						break;
					case DefaultRequestStruct.REQUEST_COMMAND_LOGIN_PWD:
						Log.d(TAG, "로그인 실패!!");
						break;
					}
					break;
				}
				case DefaultRequestStruct.REQUEST_TYPE_AUTH_TOKEN: {
					Log.d(TAG, "REQUEST_TYPE_AUTH_TOKEN");

					AuthTokenRequestStruct newUserRequestStruct = (AuthTokenRequestStruct) request;

					switch (newUserRequestStruct.runRequestCommand) {
					case DefaultRequestStruct.REQUEST_COMMAND_NEW_USER:
						if (insertDb(newUserRequestStruct)) {
							Log.d(TAG, "신규유저 등록 성공  - token 갱신!!");
						} else {
							Log.d(TAG, "신규유저 등록 성공  - token 저장 실패!!");
						}
						break;
					case DefaultRequestStruct.REQUEST_COMMAND_LOGIN_PWD:
						Log.d(TAG, "로그인 성공 - token 갱신!!");
						break;
					}

					Intent intent = new Intent(ModifyInformation.this,
							Main.class);
					startActivity(intent);
				}
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.modify_information);

		// first!!!
		mConfingData = ShareData.newInstance(this);

		CommunicationService.getInstance(this).registerNFCCallback(this);

		mUserTypeStr = mConfingData.getLastLoginUserType();

		viewInit();
		buttonInit();

		// mDbHelper = DbHelper.getInstance(AddRegister.this);
		// mDb = mDbHelper.getDatabase();

	}

	/**
	 * 
	 */
	private void viewInit() {
		mTypeRadioGroup = (RadioGroup) findViewById(R.id.reg_type_group);
		mTypeRadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
							case R.id.reg_type_client: {
								mUserTypeStr = "1";

								break;
							}
							case R.id.reg_type_retailer: {
								mUserTypeStr = "0";

								break;
							}
						}
					}
				});


		if(mUserTypeStr.equals("1")) {
			RadioButton radio_short = (RadioButton) findViewById(R.id.reg_type_client);
			radio_short.setChecked(true);
		}
		else
		{
			RadioButton radio_short = (RadioButton) findViewById(R.id.reg_type_retailer);
			radio_short.setChecked(true);
		}
		mNameEditText = (EditText) findViewById(R.id.reg_name_edittext);
		String load_name = load_name(mConfingData.getLastID());
		mNameEditText.setText(load_name);
		mFirstIdentificationEditText = (EditText) findViewById(R.id.reg_identification_first_edittext);
		mFirstIdentificationEditText
				.setNextFocusDownId(R.id.reg_identification_second_edittext);
		mFirstIdentificationEditText.setText("");
		mFirstIdentificationEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() > 6) {
					mFirstIdentificationEditText.setText(mBeforeFirstIdentStr);
					mFirstIdentificationEditText
							.setSelection(mFirstIdentificationEditText
									.getText().toString().length());
				} else if (s.toString().length() == 6) {
					mSecondIdentificationEditText.requestFocus();
				} else {
					mBeforeFirstIdentStr = s.toString();
				}
			}
		});
		mSecondIdentificationEditText = (EditText) findViewById(R.id.reg_identification_second_edittext);
		mSecondIdentificationEditText.setText("");
		mSecondIdentificationEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() > 7) {
					mSecondIdentificationEditText
							.setText(mBeforeSecondIdentStr);
					mSecondIdentificationEditText
							.setSelection(mSecondIdentificationEditText
									.getText().toString().length());
				} else {
					mBeforeSecondIdentStr = s.toString();
				}
			}
		});
		mIdEditText = (EditText) findViewById(R.id.reg_id_edittext);
		mIdEditText.setText(mConfingData.getLastID());
		mPasswordEditText = (EditText) findViewById(R.id.reg_password_edittext);
		String load_pass = load_pass(mConfingData.getLastID());
		mPasswordEditText.setText(load_pass);
		mPhoneNumEditText = (EditText) findViewById(R.id.reg_phone_edittext);
		String load_hp = load_hp(mConfingData.getLastID());
		mPhoneNumEditText.setText(load_hp);
		mAccountListSpinner = (Spinner) findViewById(R.id.reg_bank_spinner);
		mAccountNumEditText = (EditText) findViewById(R.id.reg_account_edittext);
		mAccountNumEditText.setText("");
		mAccountPasswordEditText = (EditText) findViewById(R.id.reg_account_password_edittext);
		mAccountPasswordEditText.setText("");
		mAccountPasswordEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.toString().length() > 4) {
					mAccountPasswordEditText.setText(mBeforeAccPwIdentStr);
					mAccountPasswordEditText
							.setSelection(mAccountPasswordEditText.getText()
									.toString().length());
				} else {
					mBeforeAccPwIdentStr = s.toString();
				}
			}
		});
	}

	/**
	 * 
	 */
	private void buttonInit() {
		mOkBtn = (Button) findViewById(R.id.reg_ok_btn);
		mOkBtn.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				/**
				 * Server와의 연동
				 */
				/*if (checkIdentification(mFirstIdentificationEditText.getText()
						.toString(), mSecondIdentificationEditText.getText()
						.toString())) {
					showDialog(DLG_CONN_SERVER);
					sendInformation();

				} else {
					showDialog(DLG_WRONG_IDENDIFICATION);
				}*/
				String id = mIdEditText.getText().toString();
				if (!id.equals(mConfingData.getLastID())) {
					showDialog(DLG_WRONG_ID);
				} else if (mNameEditText.getText().toString().equals("")) {
					showDialog(21);
				} else if (mPasswordEditText.getText().toString().equals("")) {
					showDialog(DLG_EMPTY_PASSWORD);
				} else if(mPhoneNumEditText.getText().toString().equals(""))
				{
					showDialog(DLG_EMPTY_PHONE);
				}
				else {
					String un = mNameEditText.getText().toString();
					String up = mPasswordEditText.getText().toString();
					String ut = mUserTypeStr;
					String hp = mPhoneNumEditText.getText().toString();
					String change = change(id + "_" + un + "_" + up + "_" + ut + "_" + hp);
					showDialog(20);
				}
				//String change =change("qqqq_b_c_d_e");
			}
		});
		mCancelBtn = (Button) findViewById(R.id.reg_cancel_btn);
		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (D) {
					Log.d(TAG, "CLICK EXIT BUTTON");
				}
				//android.os.Process.killProcess(android.os.Process.myPid());
				finish();
				Intent intent = new Intent(ModifyInformation.this,
						PwCommon.class);
				startActivity(intent);
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
			new AlertDialog.Builder(ModifyInformation.this,
					AlertDialog.THEME_HOLO_LIGHT)
					.setTitle(getString(R.string.exit_title))
					.setMessage(getString(R.string.exit_message))
					.setPositiveButton(getString(R.string.ok_str),
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
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
		case DLG_EMPTY_ID: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_empty_id_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
		case DLG_WRONG_ID: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_wrong_id_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();
			break;
		}
		case DLG_EMPTY_PASSWORD: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.mod_title);
			builder.setMessage(R.string.register_empty_password_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
		case DLG_WRONG_PASSWORD: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_wrong_password_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
		case DLG_EMPTY_FRONT_IDENDIFICATION: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_empty_identification_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
		case DLG_EMPTY_BACK_IDENDIFICATION: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_empty_identification_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
		case DLG_WRONG_IDENDIFICATION: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_wrong_identification_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
		case DLG_EMPTY_PHONE: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.mod_title);
			builder.setMessage(R.string.register_empty_phone_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
		case DLG_WRONG_PHONE: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_wrong_phone_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
		case DLG_EMPTY_ACCOUNT_NUMBER: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_empty_account_number_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
		case DLG_WRONG_ACCOUNT_NUMBER: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_wrong_account_number_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
		case DLG_EMPTY_ACCOUNT_PASSWORD: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_empty_account_password_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
		case DLG_WRONG_ACCOUNT_PASSWORD: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_wrong_account_password_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
		case DLG_CONN_SERVER: {
			mProgressDialog = new ProgressDialog(ModifyInformation.this);
			mProgressDialog.setTitle(R.string.register_title);
			mProgressDialog.setMessage(getResources().getText(
					R.string.register_connecting_server_msg));
			mProgressDialog.setButton(
					getResources().getText(R.string.cancel_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}
					});
			return mProgressDialog;
		}
		case DLG_CONN_FAIL_SERVER: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_connecting_fail_server_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
		case DLG_ADD_SUCCESS: {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}

			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_add_complete_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();
			break;
		}
		case DLG_ADD_FAIL: {
			AlertDialog.Builder builder = new Builder(ModifyInformation.this);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_add_fail_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();

			break;
		}
			case 20: {
				AlertDialog.Builder builder = new AlertDialog.Builder(ModifyInformation.this,
						AlertDialog.THEME_HOLO_LIGHT);
				builder.setTitle(R.string.mod_title);
				builder.setMessage(R.string.change_complete);
				builder.setPositiveButton(getResources().getText(R.string.ok_str),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int paramInt) {
								finish();
								Intent intent = new Intent(ModifyInformation.this,
										PwCommon.class);
								startActivity(intent);
							}

						});
				dialog = builder.create();
				break;
			}
			case 21: {
				AlertDialog.Builder builder = new AlertDialog.Builder(ModifyInformation.this,
						AlertDialog.THEME_HOLO_LIGHT);
				builder.setTitle(R.string.mod_title);
				builder.setMessage(R.string.register_empty_name_msg);
				builder.setPositiveButton(getResources().getText(R.string.ok_str),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int paramInt) {

							}

						});
				dialog = builder.create();
				break;
			}
		}

		return dialog;
	}

	/**
	 * 
	 * @param msg
	 */
	@Override
	public void OnNFCServiceMessage(DefaultRequestStruct request) {
		mHandler.obtainMessage(HM_GET_REQUEST, request).sendToTarget();
	}

	/**
	 * 
	 * @return
	 */
	private boolean checkIdentification(String aFirstIden, String aSecondIden) {
		int sum = 0;
		int[] mulValue = { 2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5 };

		if ((aFirstIden.length() + aSecondIden.length()) != 13) {
			return false;
		}

		int identification[] = strToint(aFirstIden + aSecondIden);

		/**
		 * 계산법 주민등록번호 13번째 자리를 제외 한 나머지를 각각 mulValue와 곱한 뒤 다 더한다. 그 더한값에 11로
		 * 나눈다. 나눈값의 나머지를 11에서 제외한다.
		 */
		for (int cnt = 0; cnt < 12; cnt++) {
			sum += identification[cnt] * mulValue[cnt];
		}
		return ((11 - (sum % 11)) % 10 == identification[12]) ? true : false;
	}

	/**
	 * 
	 * @param aArg
	 * @return
	 */
	private int[] strToint(String aArg) {
		int[] returnInt = new int[13];

		for (int cnt = 0; cnt < 13; cnt++) {
			try {
				returnInt[cnt] = Integer.parseInt(aArg.substring(cnt, cnt + 1));
			} catch (NumberFormatException e) {
				e.printStackTrace();

				return returnInt;
			}
		}
		return returnInt;
	}

	/**
	 * 
	 * @return
	 */
	private int sendInformation() {
		String uuid = makeUUID();
		String result = "";
		if (D) {
			Log.d(TAG, "DEVICE UUID = " + uuid);
		}
		try {
			// 아이디 //이름 //비밀번호 //회원구분 판매회원 : 0, 구매회원 : 1 //디바이스코드 //폰넘버 //주민번호
			// //은행명 //계좌번호
			if (D) {
				String msg = "아이디 : "
						+ mIdEditText.getText().toString()
						+ "성명 : "
						+ mNameEditText.getText().toString()
						+ "비밀번호 : "
						+ mPasswordEditText.getText().toString()
						+ "유저구분 : "
						+ mUserTypeStr
						+ uuid
						+ mPhoneNumEditText.getText().toString()
						// + "UUID : " + "ABCDEF"
						+ "주민등록 앞자리 : "
						+ mFirstIdentificationEditText.getText().toString()
						+ "-" + "주민번호 뒷자리 : "
						+ mSecondIdentificationEditText.getText().toString()
						+ "은행명 : "
						+ mAccountListSpinner.getSelectedItem().toString()
						+ "은행계좌 : " + mAccountNumEditText.getText().toString();
				Log.d(TAG, "SEND REGISTER MESSAGE : " + msg);
			}
			// String newID = mIdEditText.getText().toString();
			// result = sendRegister(newID, "kims", "kims", "1",
			// "DC0000111100001111", "010-0000-0000", "720101-15000000",
			// "국자은행", "000-0000-00000");

			String id = mIdEditText.getText().toString();
			String name = mNameEditText.getText().toString();
			String pwd = mPasswordEditText.getText().toString();
			String user_type = mUserTypeStr;
			String hp_number = mPhoneNumEditText.getText().toString();
			String user_number = String.format("%s-%s",
					mFirstIdentificationEditText.getText().toString(),
					mSecondIdentificationEditText.getText().toString());
			String account_name = mAccountListSpinner.getSelectedItem()
					.toString();
			String account_number = mAccountNumEditText.getText().toString();

			// #################################################
			uuid = "DC0000-11100001111";
			// #################################################

			result = CommunicationService.getInstance().sendRegister(id, name,
					pwd, user_type, uuid, hp_number, user_number, account_name,
					account_number);

			// result = mService.sendRegister("test444", "name", "password",
			// "1", "AAAAAAAA", "010-0000-0000", "100000-2000000", "援��옄���뻾",
			// "000000-000000");

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (D) {
			Log.d(TAG, "SEND REGISTER RESULT = " + result);
		}
		return 0;
	}

	/**
	 * 현재 device의 android ID만을 가지고 UUID를 리턴. 추후에 내용이 바뀔 수 있음.
	 *
	 * @return UUID
	 */
	private String makeUUID() {
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		String serial = tm.getSimSerialNumber();
		String androidId = Secure.getString(getContentResolver(),
				android.provider.Settings.Secure.ANDROID_ID);

		if (D) {
			Log.d(TAG, "DEVICE ID = " + deviceId);
			Log.d(TAG, "DEVICE SERIAL = " + serial);
			Log.d(TAG, "DEVICE ANDROID_ID = " + androidId);
		}
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tm.hashCode() << 32) | tm.hashCode());

		return deviceUuid.toString();
	}

	private boolean insertDb(AuthTokenRequestStruct aAtrs) {

		String newID = mIdEditText.getText().toString();
		String user_pw = mPasswordEditText.getText().toString();
		String user_type = mUserTypeStr;
		String user_app_pw = "";
		String token = aAtrs.token;
		String mdc = aAtrs.mdc;
		String mds = aAtrs.mds;

		if (!aAtrs.token.isEmpty() && !aAtrs.mdc.isEmpty()
				&& !aAtrs.mds.isEmpty() && !user_type.isEmpty()) {
			// ShareData.newInstance().saveLastLoginToken(newID, "", token, mdc,
			// mds, user_type);
			mConfingData.saveLastLoginToken(newID, "", token, mdc, mds,
					user_type);
			com.ajantech.nfc_network.ShareData.Settings st = mConfingData
					.getSettings();
			st.app_pwd = user_app_pw;
			mConfingData.setSettings(st);
		} else
			return false;

		return true;

		/*
		 * 
		 * try { ContentValues values = new ContentValues();
		 * values.put("user_id", mIdEditText.getText().toString());
		 * values.put("user_pw", mPasswordEditText.getText().toString());
		 * values.put("user_type", mUserTypeStr); values.put("user_app_pw", "");
		 * values.put("token", aAtrs.token);
		 * 
		 * Cursor cursor = mDb.rawQuery("select * from " + DbHelper.TABLE_NAME,
		 * null);
		 * 
		 * int count = 0; if(cursor != null) count = cursor.getCount();
		 * 
		 * cursor.close();
		 * 
		 * if(cursor.getCount() == 0) mDb.insert(DbHelper.TABLE_NAME, null,
		 * values); else { //String query = String.format(
		 * "UPDATE %s SET user_id=%d,app_end=%d WHERE app_intent='%s';",
		 * DbHelper.TABLE_NAME); //mDb.execSQL(query);
		 * mDb.update(DbHelper.TABLE_NAME, values, null, null);
		 * //mDb.insert(DbHelper.TABLE_NAME, null, values); }
		 * 
		 * return true; } catch (Exception e) { e.printStackTrace();
		 * 
		 * return false; }
		 */
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {
		this.finish();
		Intent intent = new Intent(ModifyInformation.this, PwCommon.class);
		startActivity(intent);
	}

	private String change(String msg) {
		if(msg == null)
			msg = "";

		String URL = "http://59.3.109.220:9999/change.jsp";

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			/* ???? id?? pwd?? ?????? ???? */

			HttpPost post = new HttpPost(URL+"?msg="+msg);

			/* ??????? ??? 5?? */
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			/* ?????? ???? ?? ???????? ??????? ?????? ???? */
			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(),
							"utf-8"));

			String line = null;
			String result = "";

			while ((line = bufreader.readLine()) != null) {
				result += line;
			}

			return result;
		} catch (Exception e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();	// ???? ???? ????
			return "";
		}

	}
	private String load_name(String msg) {
		if(msg == null)
			msg = "";

		String URL = "http://59.3.109.220:9999/load_name.jsp";

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			/* ???? id?? pwd?? ?????? ???? */

			HttpPost post = new HttpPost(URL+"?msg="+msg);

			/* ??????? ??? 5?? */
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			/* ?????? ???? ?? ???????? ??????? ?????? ???? */
			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(),
							"euc-kr"));

			String line = null;
			String result = "";

			while ((line = bufreader.readLine()) != null) {
				result += line;
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();	// ???? ???? ????
			return "";
		}

	}
	private String load_pass(String msg) {
		if(msg == null)
			msg = "";

		String URL = "http://59.3.109.220:9999/load_pass.jsp";

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			/* ???? id?? pwd?? ?????? ???? */

			HttpPost post = new HttpPost(URL+"?msg="+msg);

			/* ??????? ??? 5?? */
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			/* ?????? ???? ?? ???????? ??????? ?????? ???? */
			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(),
							"euc-kr"));

			String line = null;
			String result = "";

			while ((line = bufreader.readLine()) != null) {
				result += line;
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();	// ???? ???? ????
			return "";
		}

	}
	private String load_hp(String msg) {
		if(msg == null)
			msg = "";

		String URL = "http://59.3.109.220:9999/load_hp.jsp";

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			/* ???? id?? pwd?? ?????? ???? */

			HttpPost post = new HttpPost(URL+"?msg="+msg);

			/* ??????? ??? 5?? */
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 3000);
			HttpConnectionParams.setSoTimeout(params, 3000);

			/* ?????? ???? ?? ???????? ??????? ?????? ???? */
			HttpResponse response = client.execute(post);
			BufferedReader bufreader = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent(),
							"euc-kr"));

			String line = null;
			String result = "";

			while ((line = bufreader.readLine()) != null) {
				result += line;
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			client.getConnectionManager().shutdown();	// ???? ???? ????
			return "";
		}

	}
}
