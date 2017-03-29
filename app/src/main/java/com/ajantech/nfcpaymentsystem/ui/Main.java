package com.ajantech.nfcpaymentsystem.ui;

import java.nio.charset.Charset;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.ajantech.nfc_network.CommunicationService;
import com.ajantech.nfc_network.ShareData;
import com.ajantech.nfc_network.request.TransactionalInformationRequestStruct;
import com.geno.bill_folder.R;

@SuppressWarnings("deprecation")
public class Main extends TabActivity implements CreateNdefMessageCallback {
	private final String TAG = "Main";
	private final boolean D = true;

	public static Context context;
	private ImageButton mNFCPaymentBtn;
	private ImageButton mModifyRegisterBtn;
	private ImageButton mSearchBtn;
	NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	TextView textView;
	// private DbHelper mDbHelper;
	// private SQLiteDatabase mDb;
	private String mTrade_Code;
	private String mConfirmMsg;
	private String mClientId;
	private String mClientToken;
	private String mBuyProductName;
	private String mBuyPrice;
	private String mRetailerToken;
	private ProgressDialog mNfcPaymentProgressDialog;
	private final int DLG_EXIT = 0;
	private final int DLG_PAYMENT_CONFIRM = DLG_EXIT + 1;
	private final int DLG_PAYMENT_RETAIL_CONFIRM = DLG_PAYMENT_CONFIRM + 1;
	private final int DLG_NFC_CHECK = DLG_PAYMENT_RETAIL_CONFIRM + 1;
	private final int DLG_COMM_SERVER = DLG_NFC_CHECK + 1;
	private final int DLG_PAYMENT_CHECK = DLG_COMM_SERVER + 1;
	private final int DLG_PAYMENT_COMPLETE = DLG_PAYMENT_CHECK + 1;
	private final int DLG_PAYMENT_FAIL = DLG_PAYMENT_COMPLETE + 1;
	private int mType = 1;
	ShareData mConfingData = null;
	private String mTradeCode = "";
	private boolean mTranInfo = false;
	private ProgressDialog mNfcPaymentSrvProgDlg;
	private TransactionalInformationRequestStruct mTir;
	Intent intent;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("MainLog", "onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		Log.v("Mytag", "Logcat Test!!!!!verbose");
		Log.d("Mytag", "Logcat Test!!!!!debug");
		Log.i("Mytag", "Logcat Test!!!!!Infomation");
		Log.w("Mytag", "Logcat Test!!!!!waring");
		Log.e("Mytag", "Logcat Test!!!!!error");

		context = this;
		Resources res = this.getResources();
		TabHost tabhost = this.getTabHost();

		TabHost.TabSpec spec;
		Intent intent = null;

		mConfingData = ShareData.newInstance(this);
		mType = Integer.parseInt(mConfingData.getLastLoginUserType());
	Log.d("jjj", "" +mType);
		// Toast.makeText(getApplicationContext(),
		// Integer.toString(mType),Toast.LENGTH_SHORT).show();

		// 첫번째 탭 메뉴 추가
		if (mType == 1) {
			intent = new Intent().setClass(this, NFCPaymentClient.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		} else if (mType == 0) {
			intent = new Intent().setClass(this, NFCPaymentRetailer.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		spec = tabhost.newTabSpec("nfc");
		View view1 = LayoutInflater.from(this).inflate(R.layout.tabwidget_nfc,
				tabhost.getTabWidget(), false);
		spec.setIndicator(view1);
		spec.setContent(intent);

		tabhost.addTab(spec);

		// 두번째 탭 메뉴 추가
		intent = new Intent().setClass(this, RetailSearchMain.class);
		spec = tabhost.newTabSpec("search");
		View view2 = LayoutInflater.from(this).inflate(
				R.layout.tabwidget_search, tabhost.getTabWidget(), false);
		spec.setIndicator(view2);
		spec.setContent(intent);
		tabhost.addTab(spec);

		// 세번째 탭 메뉴 추가
		intent = new Intent().setClass(this, ModifyInformation.class);
		spec = tabhost.newTabSpec("info");
		View view3 = LayoutInflater.from(this).inflate(R.layout.tabwidget_info,
				tabhost.getTabWidget(), false);
		spec.setIndicator(view3);
		spec.setContent(intent);
		tabhost.addTab(spec);

		tabhost.setCurrentTab(0);

		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter == null) {
			Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG)
					.show();
			finish();
			return;
		}

		// 콜백 등록
		mNfcAdapter.setNdefPushMessageCallback(this, this);


	}

	public NdefMessage createNdefMessage(NfcEvent event) {
		Log.d("MainLog", "createNdefMessage");
		// Cursor cursor = mDb.rawQuery("select * from " + DbHelper.TABLE_NAME,
		// null);

		// if(cursor == null || cursor.getCount() == 0) {
		// cursor.close();
		// } else {
		// cursor.moveToFirst();
		String id = mConfingData.getLastID();
		String token = mConfingData.getLastLoginToken();

		String msgStr = "buyer_id:" + id + "\n" + "buyer_token:" + token + "\n"
				+ "product:" + NFCPaymentClient.ProductName + "\n" + "price:"
				+ NFCPaymentClient.ProductTotal;
		// cursor.close();
		byte[] testByte = msgStr.getBytes();
		NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord(
				"application/com.ajantech.nfcpaymentsystem", testByte) });
		return msg;
	}

	@Override
	public void onResume() {
		mType = Integer.parseInt(mConfingData.getLastLoginUserType());
		super.onResume();
		Log.d("MainLog", "onResume");
		// Toast.makeText(getApplicationContext(), getIntent().toString(),
		// Toast.LENGTH_SHORT).show();
		// 안드로이드 빔에 의해 시작된 것인지 체크
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())  && mType == 0) {
			processIntent(getIntent());
		}
		else
		{

		}
	}
	@Override
	public void onNewIntent(Intent intent) {
		// onResume은 이 메서드 이후에 호출되므로 이 인텐트를 얻어 처리한다.
		Log.d("MainLog", "onNewIntent");
		setIntent(intent);
	}

	private void processIntent(Intent intent) {
		Log.d("MainLog", "processIntent");
		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];

		String strSite = new String(msg.getRecords()[0].getPayload());
		if (strSite != null && !strSite.equals("")) {
			mClientId = strSite.substring(strSite.indexOf("buyer_id") + 9,
					strSite.indexOf("\n"));
			mClientToken = strSite.substring(
					strSite.indexOf("buyer_token") + 12,
					strSite.indexOf("\n", strSite.indexOf("buyer_token") + 12));
			mBuyProductName = strSite.substring(strSite.indexOf("product") + 8,
					strSite.indexOf("\n", strSite.indexOf("product") + 8));
			mBuyPrice = strSite.substring(strSite.indexOf("price") + 6,
					strSite.length());
			mConfirmMsg = getString(R.string.payment_product_name)
					+ mBuyProductName + "\n"
					+ getString(R.string.payment_product_price) + mBuyPrice;

			showDialog(DLG_PAYMENT_RETAIL_CONFIRM);
			mNfcAdapter.disableForegroundDispatch(this);
			return;
		}

		return;
	}

	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
		Log.d("MainLog", "createMimeRecord");
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				mimeBytes, new byte[0], payload);
		return mimeRecord;
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);

		AlertDialog dialog = null;

		switch (id) {

		case DLG_PAYMENT_RETAIL_CONFIRM: {
			// Toast.makeText(getApplicationContext(),"DLG_PAYMENT_RETAIL_CONFIRM",
			// Toast.LENGTH_SHORT).show();
			AlertDialog.Builder builder = new Builder(Main.context);
			builder.setCancelable(false);
			builder.setTitle(R.string.payment_title);
			builder.setMessage(mConfirmMsg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {
							dismissDialog(DLG_PAYMENT_RETAIL_CONFIRM);
							showDialog(DLG_COMM_SERVER);
							paymentToServer();

						}

					});
			builder.setNegativeButton(
					getResources().getText(R.string.cancel_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {
							finish();
							intent = new Intent(Main.this, Main.class);
							startActivity(intent);
						}

					});
			dialog = builder.create();
			break;
		}
		case DLG_COMM_SERVER: {
			// Toast.makeText(getApplicationContext(), "DLG_COMM_SERVER",
			// Toast.LENGTH_SHORT).show();
			mNfcPaymentSrvProgDlg = new ProgressDialog(Main.context);
			mNfcPaymentSrvProgDlg.setMessage("서버와 통신 중입니다.");
			mNfcPaymentSrvProgDlg.setButton(
					getResources().getString(R.string.cancel_str),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							dismissDialog(DLG_COMM_SERVER);
						}
					});

			return mNfcPaymentSrvProgDlg;
		}
		/*case DLG_PAYMENT_CHECK: {
			// Toast.makeText(getApplicationContext(),
			// "DLG_PAYMENT_CHECK",Toast.LENGTH_SHORT).show();
			AlertDialog.Builder builder = new Builder(Main.context);
			builder.setTitle(R.string.payment_title);
			builder.setMessage(getResources().getString(
					R.string.payment_confirm_clitent_msg));
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {
							String myId = mConfingData.getLastID();
							String myToken = mConfingData.getLastLoginToken();
							String productStr = NFCPaymentClient.ProductName;
							String priceStr = NFCPaymentClient.ProductTotal;
							CommunicationService.getInstance()
									.sendBuyerTradeEndFinalPalyment(myId,
											myToken, mRetailerToken,
											mTradeCode, productStr, priceStr);
						}

					});
			builder.setNegativeButton(
					getResources().getText(R.string.cancel_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			dialog = builder.create();
			break;
		}
		*/
		case DLG_PAYMENT_COMPLETE: {
			// Toast.makeText(getApplicationContext(),
			// "DLG_PAYMENT_COMPLETE",Toast.LENGTH_SHORT).show();
			AlertDialog.Builder builder = new Builder(Main.context);
			builder.setCancelable(false);
			builder.setTitle(R.string.payment_title);
			String msg = getString(R.string.payment_complete_retailer_detail_msg, NFCPaymentRetailer.FinalAmount);
			builder.setMessage(msg);
			// builder.setMessage(getResources().getString(R.string.payment_complete_msg)
			// + "\n" + "은행명 : " + mTir.bank_name + "\n" + "입금 : " + mTir.price
			// + "원\n");
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {
							finish();
							intent = new Intent(Main.this, Main.class);
							startActivity(intent);
						}

					});
			dialog = builder.create();
			break;
		}
		case DLG_PAYMENT_FAIL: {
			// Toast.makeText(getApplicationContext(),
			// "DLG_PAYMENT_FAIL",Toast.LENGTH_SHORT).show();
			AlertDialog.Builder builder = new Builder(Main.context);
			builder.setTitle(R.string.payment_title);
			builder.setMessage(R.string.payment_fail_msg);
			builder.setPositiveButton(getResources().getText(R.string.ok_str),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int paramInt) {

						}

					});
			builder.setNegativeButton(
					getResources().getText(R.string.cancel_str),
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

	private boolean paymentToServer() {
		Log.d("MainLog", "paymentToServer");
		boolean result = false;
		String myToken = mConfingData.getLastLoginToken();
		String myId = mConfingData.getLastID();
		// sendBuyerPay(mClientId, mClientToken, myToken, mBuyProductName,
		// mBuyPrice);
		CommunicationService.getInstance().sendTradeRegister(myId, myToken,
				mClientToken, mBuyProductName, mBuyPrice);

		return result;
	}

	public void onNdefPushComplete(NfcEvent event) {
		Log.d("MainLog", "onNdefPushComplete");
		if (mNfcPaymentProgressDialog.isShowing()) {
			mNfcPaymentProgressDialog.dismiss();
		}

		showDialog(DLG_PAYMENT_COMPLETE);
	}
	
	
	public void dismiss()
	{
	}
	public void show()
	{
	}
}
