package com.geno;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.geno.payment.R;

public class Agreement extends Activity {
	private SharedPreferences mSprefs;
	private final String SPREF_NAME = "agree";
	private final String SPREF_CONFIRM = "gc_confirm";
	private final int DLG_EXIT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.agreement);

		mSprefs = getSharedPreferences(SPREF_NAME, MODE_PRIVATE);
		if (mSprefs.getBoolean(SPREF_CONFIRM, false)) {//저장된 값이 불려지면 바로 join으로 넘어감
			//Toast.makeText(getApplicationContext(), "if", Toast.LENGTH_SHORT).show();
			finish();
			Intent intent = new Intent(Agreement.this, Join.class);
			startActivity(intent);
		} else {
			//Toast.makeText(getApplicationContext(), "else", Toast.LENGTH_SHORT).show();
		}
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
		Intent intent;
		switch (v.getId()) {
		case R.id.imagebutton1:
			SharedPreferences.Editor editor = mSprefs.edit();
			editor.putBoolean(SPREF_CONFIRM, true);//현재 값을 저장함
			editor.commit();
			finish();

			intent = new Intent(Agreement.this, Join.class);
			startActivity(intent);
			break;
		case R.id.imagebutton2:
			showDialog(DLG_EXIT);
			break;
		case R.id.titlebutton:
			finish();
			intent = new Intent(this, MainActivity.class);
			startActivity(intent);
			break;
		}
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
	public void onBackPressed() {
		// super.onBackPressed();

		showDialog(DLG_EXIT);
	}
}
