package com.geno; // 패키지 정의

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.GalleryAuction.GalleryActivity;
import com.ajantech.nfcpaymentsystem.ClearEditText;
import com.ajantech.nfcpaymentsystem.ui.Start;
import com.geno.bill_folder.R;

public class MainActivity extends Activity { //액티비티 정의

	private final int DLG_EXIT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {//액티비티 생성시에 호출되는 메서드
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);//액티비티 화면을 R.layout.activity_main으로 설정
		ClearEditText.class.getClass();
	}

	public void mOnClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.Button1:

			finish();// 현재 액티비티를 종료함
			intent = new Intent(MainActivity.this, Start.class);
			startActivity(intent);//start클래스를 실행함
			break;
		case R.id.Button2:

			finish();// 현재 액티비티를 종료함
			intent = new Intent(MainActivity.this, Agreement.class);
			startActivity(intent);//agreement.class를 실행함
			break;
		case R.id.Button3:
			Toast.makeText(this, "준비중입니다.", Toast.LENGTH_SHORT).show();//짧게 준비중입니다 텍스쳐를 띄움
			break;
		case R.id.Button4:
			Toast.makeText(this, "준비중입니다.", Toast.LENGTH_SHORT).show();
			break;
			case R.id.Button5:
				intent = new Intent(MainActivity.this, GalleryActivity.class);
				startActivity(intent);
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
