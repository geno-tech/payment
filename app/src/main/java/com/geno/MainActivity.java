package com.geno; // 패키지 정의

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.GalleryAuction.UI.ArtistMainActivity;
import com.GalleryAuction.UI.BidderMainActivity;
import com.ajantech.nfc_network.ShareData;
import com.ajantech.nfcpaymentsystem.ClearEditText;
import com.ajantech.nfcpaymentsystem.ui.Main;
import com.geno.payment.R;

public class MainActivity extends Activity { //액티비티 정의

	private final int DLG_EXIT = 0;
	ShareData mConfingData = null;
	NfcAdapter nfcAdapter;
	PendingIntent pendingIntent;
	int mType = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {//액티비티 생성시에 호출되는 메서드
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);//액티비티 화면을 R.layout.activity_main으로 설정
		ClearEditText.class.getClass();

		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (tag != null) {
			Toast.makeText(this, "태그창에서 태그하시오", Toast.LENGTH_SHORT).show();
		}
		Log.d("TAGTEST : ", ""+ tag);
	}

	public void mOnClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.Button1:

			finish();// 현재 액티비티를 종료함
			intent = new Intent(MainActivity.this, Main.class);
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
				mConfingData = ShareData.newInstance(this);
				mType = Integer.parseInt(mConfingData.getLastLoginUserType());
				Log.d("aa", "" + mType);
				Intent intent0 = getIntent();
				String ID = intent0.getStringExtra("userID");
				// 입찰자
				if (mType == 1) {


					Intent intent1 = new Intent(MainActivity.this, BidderMainActivity.class);
					intent1.putExtra("userID", ID);
					startActivity(intent1);
					//아티스트
				} else if (mType == 0) {
					Intent intent2 = new Intent(MainActivity.this, ArtistMainActivity.class);
					intent2.putExtra("artistID", ID);
					startActivity(intent2);
				}
				break;
			case R.id.Button6 :
				AlertDialog.Builder alert2 = new AlertDialog.Builder(MainActivity.this);
				alert2.setMessage("사업자명 : 제노테크(주) \n 대표 : 차병래 \n  주소 : 61005 광주 광역시 북구 첨단과기로 123 광주과학기술원 창업기술사업화센터 B동 306호 \n 전화 : 070-8269-0591 \n 팩스 : 070-8269-0591 \n 이메일 : genotech2012@daum.net \n 사업자등록번호 : 409-86-29358").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {


					}
				});
				alert2.setCancelable(false);
				alert2.show();
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
