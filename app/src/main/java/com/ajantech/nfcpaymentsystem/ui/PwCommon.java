package com.ajantech.nfcpaymentsystem.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.ajantech.nfc_network.CommunicationService;
import com.ajantech.nfc_network.CommunicationService.OnNFCServiceCallBack;
import com.ajantech.nfc_network.ShareData;
import com.ajantech.nfc_network.request.AuthTokenRequestStruct;
import com.ajantech.nfc_network.request.DefaultRequestStruct;
import com.ajantech.nfc_network.request.ErrorRequestStruct;
import com.ajantech.nfcpaymentsystem.ClearEditText;
import com.geno.MainActivity;
import com.geno.bill_folder.R;

public class PwCommon extends Activity implements OnClickListener, OnNFCServiceCallBack{
	private final String TAG = "PwCommon";
	private final boolean D = true;

	private Button pw_common_ok_btn;
	private Button pw_common_cancel_btn;
	private EditText reg_id_edittext;
	private EditText reg_password_edittext;
	Drawable drawable;
	private ProgressDialog mProgressDialog;

    ShareData mConfingData = null;
    
	String mNewID = "";
	Intent intent;
	@Override
	protected void onPause() {
		loginProgressDialgDisable();
		
		super.onPause();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.pw_common);

		//first!!!
	    mConfingData = ShareData.newInstance(this);

	    CommunicationService.getInstance(this).registerNFCCallback(this);
	    
		pw_common_ok_btn = (Button) findViewById(R.id.pw_common_ok_btn);
		pw_common_ok_btn.setOnClickListener(this);

		pw_common_cancel_btn = (Button) findViewById(R.id.pw_common_cancel_btn);
		pw_common_cancel_btn.setOnClickListener(this);

		reg_id_edittext = (EditText) findViewById(R.id.reg_id_edittext);
		
		reg_password_edittext = (EditText) findViewById(R.id.reg_password_edittext);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {

			if(msg.what == 1026)
			{
				DefaultRequestStruct request = (DefaultRequestStruct)msg.obj;
				
				Log.d(TAG, "recv runRequestType : " + request.type);
				Log.d(TAG, "recv runRequestCommand : " + request.runRequestCommand);

				switch(request.type)
				{
				case DefaultRequestStruct.REQUEST_TYPE_ERROR:
					Log.d(TAG, "REQUEST_TYPE_ERROR");
					
					loginProgressDialgDisable();

					ErrorRequestStruct errorRequest = (ErrorRequestStruct) request;

					Log.d(TAG, "runRequestCommand : " + errorRequest.runRequestCommand);
					Log.d(TAG, "errorCode : " + errorRequest.errorCode);
					Log.d(TAG, "errorMessage : " + errorRequest.errorMessage);
					switch(errorRequest.runRequestCommand)
					{
					case DefaultRequestStruct.REQUEST_COMMAND_NEW_USER:
						Log.d(TAG, "신규유저 등록 실패!!");
						showDialog(2);
						break;
					case DefaultRequestStruct.REQUEST_COMMAND_LOGIN_PWD:
						Log.d(TAG, "로그인 실패!!");
						showDialog(2);
						break;
					}
					break;
				case DefaultRequestStruct.REQUEST_TYPE_AUTH_TOKEN:
					Log.d(TAG, "REQUEST_TYPE_AUTH_TOKEN");
					
					loginProgressDialgDisable();
					
					AuthTokenRequestStruct newUserRequestStruct = (AuthTokenRequestStruct) request;

					switch (newUserRequestStruct.runRequestCommand) {
					case DefaultRequestStruct.REQUEST_COMMAND_NEW_USER:
					case DefaultRequestStruct.REQUEST_COMMAND_LOGIN_PWD:
						if (insertDb(newUserRequestStruct)) {
							Log.d(TAG, "신규유저 등록 성공  - token 갱신!!");
						} else {
							Log.d(TAG, "신규유저 등록 성공  - token 저장 실패!!");
						}
						break;
					}


					finish();

					Intent intent = new Intent(PwCommon.this, MainActivity.class);
					startActivity(intent);
					

					return;
					//break;
				}
			}
			
			super.handleMessage(msg);
		}
		
	};

	@Override
	public void onClick(View arg0) {

		switch(arg0.getId())
		{		
		case R.id.pw_common_ok_btn:
			{


				//로그인 시도
				mNewID = reg_id_edittext.getText().toString();
				String pwd = reg_password_edittext.getText().toString().trim();
				if(mNewID.isEmpty() || pwd.isEmpty())
				{
					showDialog(1);
				}
				else
				{
					loginProgressDialg();
					String result = CommunicationService.getInstance().sendLogin(mNewID, pwd, "");
				}
			}
			break;

		case R.id.pw_common_cancel_btn:
			this.finish();
			intent = new Intent(PwCommon.this, Start.class);
			startActivity(intent);
			break;

		}
		
	}
	
	public void OnNFCServiceMessage(DefaultRequestStruct request)
	{
		mHandler.obtainMessage(1026, request).sendToTarget();
	}
	
	private boolean insertDb(AuthTokenRequestStruct aAtrs) {		
		String newID = mNewID;
		String user_pw = "";
		String user_type = aAtrs.user_type;
		String user_app_pw = "";
		String token = aAtrs.token;
		String mdc = aAtrs.mdc;
		String mds = aAtrs.mds;

		if(!aAtrs.token.isEmpty() /*&& !aAtrs.mdc.isEmpty() && !aAtrs.mds.isEmpty()*/ && !user_type.isEmpty())
		{
			//ShareData.newInstance().saveLastLoginToken(newID, "", token, mdc, mds, user_type);
			mConfingData.saveLastLoginToken(newID, "", token, mdc, mds, user_type);
			com.ajantech.nfc_network.ShareData.Settings st = mConfingData.getSettings();
			st.app_pwd = user_app_pw;
			mConfingData.setSettings(st);
		}
		else
			return false;
		
		return true;
		
		/*
		
		try {
			ContentValues values = new ContentValues();
			values.put("user_id", mIdEditText.getText().toString());
			values.put("user_pw", mPasswordEditText.getText().toString());
			values.put("user_type", mUserTypeStr);
			values.put("user_app_pw", "");
			values.put("token", aAtrs.token);

			Cursor cursor = mDb.rawQuery("select * from " + DbHelper.TABLE_NAME, null);

			int count = 0;
			if(cursor != null)
				count = cursor.getCount();
				
			cursor.close();
			
        	if(cursor.getCount() == 0)
    			mDb.insert(DbHelper.TABLE_NAME, null, values);
        	else {
        		//String query = String.format("UPDATE %s SET user_id=%d,app_end=%d WHERE app_intent='%s';", DbHelper.TABLE_NAME);
        		//mDb.execSQL(query);
        		mDb.update(DbHelper.TABLE_NAME, values, null, null);
        		//mDb.insert(DbHelper.TABLE_NAME, null, values);
        	}
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}
		*/
	}
	
	public void loginProgressDialg()
	{
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle(R.string.register_title);
		mProgressDialog.setMessage(getResources().getText(
				R.string.register_connecting_server_msg));
		
		mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
				getResources().getText(R.string.cancel_str),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int paramInt) {
						mProgressDialog.dismiss();
						mProgressDialog = null;
					}
				});

		mProgressDialog.show();

	}
	
	public void loginProgressDialgDisable()
	{
		if(mProgressDialog != null)
		{
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {
		this.finish();
		intent = new Intent(PwCommon.this, Start.class);
		startActivity(intent);
	}
	
	
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);

		AlertDialog dialog = null;

		switch (id) {
			case 0: 
				new AlertDialog.Builder(PwCommon.this, AlertDialog.THEME_HOLO_LIGHT)
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
			case 1: {
				AlertDialog.Builder builder = new AlertDialog.Builder(PwCommon.this,
						AlertDialog.THEME_HOLO_LIGHT);
				builder.setTitle(R.string.login_title);
				builder.setMessage(R.string.rog_error);
				builder.setPositiveButton(getResources().getText(R.string.ok_str),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int paramInt) {

							}

						});
				dialog = builder.create();

				break;
			}
			case 2: {
				AlertDialog.Builder builder = new AlertDialog.Builder(PwCommon.this,
						AlertDialog.THEME_HOLO_LIGHT);
				builder.setTitle(R.string.login_title);
				builder.setMessage(R.string.rog_error2);
				builder.setPositiveButton(getResources().getText(R.string.ok_str),
						new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface paramDialogInterface,
							int paramInt) {

					}
				});


				dialog = builder.create();

				break;
			}
		}
		return dialog;
	}


}
