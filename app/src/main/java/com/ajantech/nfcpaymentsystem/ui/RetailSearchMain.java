package com.ajantech.nfcpaymentsystem.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;

import com.geno.bill_folder.R;

@SuppressWarnings("deprecation")
public class RetailSearchMain extends TabActivity {
	private final String TAG = "RetailSearchMain";
	private final boolean D = true;
	public static Context context;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_main);

		context = this;
		Resources res = this.getResources();
		TabHost tabhost = this.getTabHost();
		TabHost.TabSpec spec;
		Intent intent = null;

		intent = new Intent().setClass(this, RetailSearchProduct.class);
		spec = tabhost.newTabSpec("searchitem");
		View view = LayoutInflater.from(this).inflate(R.layout.tabwidget_tabs,
				tabhost.getTabWidget(), false);
		spec.setIndicator(view);
		spec.setContent(intent);
		tabhost.addTab(spec);

		intent = new Intent().setClass(this, RetailSearchDate.class);
		spec = tabhost.newTabSpec("searchdate");
		View view2 = LayoutInflater.from(this).inflate(
				R.layout.tabwidget_tabs2, tabhost.getTabWidget(), false);
		spec.setIndicator(view2);
		spec.setContent(intent);
		tabhost.addTab(spec);
		tabhost.setCurrentTab(0);
	}

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
			new AlertDialog.Builder(RetailSearchMain.this,
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
		return dialog;
	}
}
