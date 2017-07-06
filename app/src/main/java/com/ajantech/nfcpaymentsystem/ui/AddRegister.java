package com.ajantech.nfcpaymentsystem.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.regex.Pattern;

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
import android.text.InputFilter;
import android.text.Spanned;
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
import android.widget.TextView;

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

public class AddRegister extends Activity implements OnNFCServiceCallBack {
	private final String TAG = "AddRegisterActivity";
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
	private TextView textview3;
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
						Log.d(TAG, "??????? ???? ????!!");
						break;
					case DefaultRequestStruct.REQUEST_COMMAND_LOGIN_PWD:
						Log.d(TAG, "????? ????!!");
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
							Log.d(TAG, "??????? ???? ????  - token ???!!");
						} else {
							Log.d(TAG, "??????? ???? ????  - token ???? ????!!");
						}
						break;
					case DefaultRequestStruct.REQUEST_COMMAND_LOGIN_PWD:
						Log.d(TAG, "????? ???? - token ???!!");
						break;
					}

					showDialog(DLG_ADD_SUCCESS);

				}
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.registration);

		// first!!!
		mConfingData = ShareData.newInstance(this);

		CommunicationService.getInstance(this).registerNFCCallback(this);

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

		RadioButton radio_short = (RadioButton) findViewById(R.id.reg_type_client);
		radio_short.setChecked(true);

		mNameEditText = (EditText) findViewById(R.id.reg_name_edittext);
		mNameEditText.setText("");
		mFirstIdentificationEditText = (EditText) findViewById(R.id.reg_identification_first_edittext);
		mFirstIdentificationEditText
				.setNextFocusDownId(R.id.reg_identification_second_edittext);
		mFirstIdentificationEditText.setText("881119");
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
		mSecondIdentificationEditText.setText("1996270");
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
		mIdEditText.setFilters(new InputFilter[]{filterAlphaNum});

		mPasswordEditText = (EditText) findViewById(R.id.reg_password_edittext);
		mPasswordEditText.setText("");
		mPhoneNumEditText = (EditText) findViewById(R.id.reg_phone_edittext);
		mPhoneNumEditText.setText("");
		mAccountListSpinner = (Spinner) findViewById(R.id.reg_bank_spinner);
		mAccountNumEditText = (EditText) findViewById(R.id.reg_account_edittext);
		InputFilter[] FilerArray = new InputFilter[1];
		FilerArray[0] = new InputFilter.LengthFilter(14);
		mAccountNumEditText.setFilters(FilerArray);
		mAccountNumEditText.setText("4152654-2012598");
		mAccountPasswordEditText = (EditText) findViewById(R.id.reg_account_password_edittext);
		mAccountPasswordEditText.setText("1234");
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
				 * Server???? ????
				 */
				/*if (checkIdentification(mFirstIdentificationEditText.getText()
						.toString(), mSecondIdentificationEditText.getText()
						.toString())) {
					showDialog(DLG_CONN_SERVER);
					sendInformation();

				} else {
					showDialog(DLG_WRONG_IDENDIFICATION);
				}
				*/
				String id = mIdEditText.getText().toString();
				if (mIdEditText.getText().toString().equals("")) {
					showDialog(DLG_EMPTY_ID);
				} else if (mNameEditText.getText().toString().equals("")) {
					showDialog(21);
				} else if (mPasswordEditText.getText().toString().equals("")) {
					showDialog(DLG_EMPTY_PASSWORD);
				} else if (mPhoneNumEditText.getText().toString().equals("")) {
					showDialog(DLG_EMPTY_PHONE);
				} else {
					String Confirm = Confirm(mIdEditText.getText().toString());
					if (Confirm.equals("")) {
						sendInformation();
					} else {
						showDialog(DLG_WRONG_ID);
						textview3 = (TextView) findViewById(R.id.textView3);
						//textview3.setText(Confirm);
					}

				}
			}
		});
		mCancelBtn = (Button) findViewById(R.id.reg_cancel_btn);
		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				Intent intent = new Intent(AddRegister.this, PwCommon.class);
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
											new AlertDialog.Builder(AddRegister.this,
													AlertDialog.THEME_HOLO_LIGHT)
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
		case DLG_EMPTY_ID: {
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
			builder.setTitle(R.string.register_title);
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
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
			builder.setTitle(R.string.register_title);
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
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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
			mProgressDialog = new ProgressDialog(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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

			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
			builder.setTitle(R.string.register_title);
			builder.setMessage(R.string.register_add_complete_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

							finish();
							Intent intent = new Intent(AddRegister.this, PwCommon.class);
							startActivity(intent);
						}

					});
			dialog = builder.create();
			break;
		}
		case DLG_ADD_FAIL: {
			AlertDialog.Builder builder = new Builder(AddRegister.this,
					AlertDialog.THEME_HOLO_LIGHT);
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
										case 21: {
											AlertDialog.Builder builder = new AlertDialog.Builder(AddRegister.this,
													AlertDialog.THEME_HOLO_LIGHT);
											builder.setTitle(R.string.register_title);
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
		 * ????? ?????????? 13??? ?????? ???? ?? ??????? ??? mulValue?? ??? ?? ?? ??????. ?? ??????? 11??
		 * ??????. ??????? ??????? 11???? ????????.
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
			// ?????? //???? //?????? //??????? ??????? : 0, ??????? : 1 //?????????? //?????? //???????
			// //?????? //??????
			if (D) {
				String msg = "?????? : "
						+ mIdEditText.getText().toString()
						+ "???? : "
						+ mNameEditText.getText().toString()
						+ "?????? : "
						+ mPasswordEditText.getText().toString()
						+ "??????? : "
						+ mUserTypeStr
						+ uuid
						+ mPhoneNumEditText.getText().toString()
						// + "UUID : " + "ABCDEF"
						+ "??????? ?????? : "
						+ mFirstIdentificationEditText.getText().toString()
						+ "-" + "??????? ?????? : "
						+ mSecondIdentificationEditText.getText().toString()
						+ "?????? : "
						+ mAccountListSpinner.getSelectedItem().toString()
						+ "??????? : " + mAccountNumEditText.getText().toString();
				Log.d(TAG, "SEND REGISTER MESSAGE : " + msg);
			}
			// String newID = mIdEditText.getText().toString();
			// result = sendRegister(newID, "kims", "kims", "1",
			// "DC0000111100001111", "010-0000-0000", "720101-15000000",
			// "????????", "000-0000-00000");

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
			// "1", "AAAAAAAA", "010-0000-0000", "100000-2000000", "????????",
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
	 * ???? device?? android ID??? ?????? UUID?? ???. ????? ?????? ???? ?? ????.
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
		Intent intent = new Intent(AddRegister.this, PwCommon.class);
		startActivity(intent);
	}
	private String Confirm(String msg) {
		if(msg == null)
			msg = "";

		String URL = "http://221.156.54.210:9999/Confirm.jsp";

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
	public InputFilter filterAlphaNum = new InputFilter() {
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
			if (!ps.matcher(source).matches()) {
				return "";
			}
			return null;
		}
	};


}
