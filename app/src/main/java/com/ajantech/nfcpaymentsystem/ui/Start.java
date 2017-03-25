package com.ajantech.nfcpaymentsystem.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import com.geno.bill_folder.R;

public class Start extends Activity {
	
	ImageButton regBut, loginBut;
	Intent intent1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.start);
		
		regBut = (ImageButton) findViewById(R.id.start_reg);
		loginBut = (ImageButton) findViewById(R.id.start_login);
		
		regBut.setOnClickListener(new ButtonClick());
		loginBut.setOnClickListener(new ButtonClick());


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
				new AlertDialog.Builder(Start.this, AlertDialog.THEME_HOLO_LIGHT)
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
				AlertDialog.Builder builder = new AlertDialog.Builder(Start.this,
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
		}
		return dialog;
	}
	class ButtonClick implements View.OnClickListener {
		public void onClick(View v) {
			int sel = v.getId();
			switch (sel) {
			case R.id.start_reg:
				finish();
    			intent1 = new Intent(Start.this, GeneralConditions.class);
    			startActivity(intent1);
				break;
			case R.id.start_login:
				finish();
				intent1 = new Intent(Start.this, PwCommon.class);
				startActivity(intent1);
				break;
			}
		}
	}

}
