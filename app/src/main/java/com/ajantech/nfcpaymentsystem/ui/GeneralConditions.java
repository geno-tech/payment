package com.ajantech.nfcpaymentsystem.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.geno.bill_folder.R;

public class GeneralConditions extends Activity {
	private final String TAG = "GeneralConditions";
	private final boolean D = true;

	private final int DLG_EXIT = 0;
	private final int DLG_INFO_GENERAL_CONDITIONS = DLG_EXIT + 1;

	private final String SPREF_NAME = "Nfc_payment_system";
	private final String SPREF_CONFIRM = "gc_confirm";

	private Button mOkBtn;
	private Button mCancelBtn;
	private SharedPreferences mSprefs;
	NfcAdapter nfcAdapter;
	PendingIntent pendingIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.general_conditions);
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);
		buttonInit();

		mSprefs = getSharedPreferences(SPREF_NAME, MODE_PRIVATE);
		if (mSprefs.getBoolean(SPREF_CONFIRM, false)) {
			finish();
			Intent intent = new Intent(GeneralConditions.this,
					AddRegister.class);
			startActivity(intent);
		} else {
			showDialog(DLG_INFO_GENERAL_CONDITIONS);
		}
	}

	private void buttonInit() {
		mOkBtn = (Button) findViewById(R.id.gc_ok_btn);
		mOkBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 동의를 받은것을 기록 후 Server와의 연동 가입이 안되어 있을 경우, 회원가입 페이지. 가입이 되어 있는
				 * 경우, 로그인 페이지.
				 */
				SharedPreferences.Editor editor = mSprefs.edit();
				editor.putBoolean(SPREF_CONFIRM, true);
				editor.commit();
				finish();
				Intent intent = new Intent(GeneralConditions.this,
						AddRegister.class);
				startActivity(intent);
			}
		});
		mCancelBtn = (Button) findViewById(R.id.gc_cancel_btn);
		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (D) {
					Log.d(TAG, "CLICK EXIT BUTTON");
				}
				showDialog(DLG_EXIT);
			}
		});
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		this.finish();
		Intent intent = new Intent(GeneralConditions.this, PwCommon.class);
		startActivity(intent);
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);

		AlertDialog dialog = null;

		switch (id) {
		case DLG_EXIT: {
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
		/*
		 * case DLG_INFO_GENERAL_CONDITIONS: { AlertDialog.Builder bilder = new
		 * Builder(GeneralConditions.this, AlertDialog.THEME_HOLO_LIGHT);
		 * bilder.setTitle(R.string.general_conditions_title);
		 * bilder.setMessage(R.string.general_condition_info);
		 * bilder.setPositiveButton(getResources().getText(R.string.ok_str),new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * 
		 * }
		 * 
		 * }); dialog = bilder.create(); break; }
		 */
		}

		return dialog;
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
		}
	}
	@Override
	protected void onNewIntent(Intent intent0) {
		super.onNewIntent(intent0);
		Tag tag = intent0.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (tag != null) {
			Toast.makeText(this, "아직 로그인되지 않았습니다.", Toast.LENGTH_SHORT).show();
		}
		Log.d("TAGTEST : ", ""+ tag);
	}
}
