package com.ajantech.nfcpaymentsystem.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ajantech.nfc_network.CommunicationService;
import com.ajantech.nfc_network.CommunicationService.OnNFCServiceCallBack;
import com.ajantech.nfc_network.ShareData;
import com.ajantech.nfc_network.request.DefaultRequestStruct;
import com.ajantech.nfc_network.request.TradeStatusRequestStruct;
import com.ajantech.nfc_network.request.TransactionalInformationRequestStruct;
import com.geno.bill_folder.R;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;


public class NFCPaymentRetailer extends Activity implements
		CreateNdefMessageCallback, OnNdefPushCompleteCallback,
		OnNFCServiceCallBack {
	private final String TAG = "NFCPaymentRetailer";
	private final boolean D = true;
	public static String FinalBank = "";
	public static String FinalAmount = "";

	private final int DLG_EXIT = 0;
	private final int DLG_PAYMENT_CONFIRM = DLG_EXIT + 1;
	private final int DLG_PAYMENT_RETAIL_CONFIRM = DLG_PAYMENT_CONFIRM + 1;
	private final int DLG_NFC_CHECK = DLG_PAYMENT_RETAIL_CONFIRM + 1;
	private final int DLG_COMM_SERVER = DLG_NFC_CHECK + 1;
	private final int DLG_PAYMENT_CHECK = DLG_COMM_SERVER + 1;
	private final int DLG_PAYMENT_COMPLETE = DLG_PAYMENT_CHECK + 1;
	private final int DLG_PAYMENT_FAIL = DLG_PAYMENT_COMPLETE + 1; 

	private Button mOkBtn;
	private Button mCancelBtn;
	private EditText mProductNameEditText;
	private EditText mProductPriceEditText;
	private Spinner mProductCntEditText;
	private EditText mProductTotalEditText;

	private NfcAdapter mNfcAdapter;
	private ProgressDialog mNfcPaymentProgressDialog;
	private ProgressDialog mNfcPaymentSrvProgDlg;
	private PendingIntent mPendingIntent;
	private RetailerRequestThread mReqThread;

	private String mConfirmMsg;
	private String mClientId;
	private String mClientToken;
	private String mBuyProductName;
	private String mBuyPrice;
	private String mTrade_Code;

	private TransactionalInformationRequestStruct mTir;



	// private DbHelper mDbHelper;
	// private SQLiteDatabase mDb;
	private ShareData mConfingData = null;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d("RetailerRequestThread", "handler");
			if (msg.what == 1025) {
				if (D) {
					Log.d(TAG, "GET MESSAGE = " + msg.obj);
				}
			} else if (msg.what == 1027) {
				DefaultRequestStruct request = (DefaultRequestStruct) msg.obj;

				Log.d(TAG, "recv runRequestType : " + request.type);
				Log.d(TAG, "recv runRequestCommand : "
						+ request.runRequestCommand);

				switch (request.type) {
				case DefaultRequestStruct.REQUEST_TYPE_ERROR: {
					try {
						dismissDialog(DLG_COMM_SERVER);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// showDialog(DLG_PAYMENT_FAIL);
					break;
				}
				case DefaultRequestStruct.REQUEST_TYPE_TRADE_STATUS: {
					//Toast.makeText(getApplicationContext(), "retailer 1",	Toast.LENGTH_SHORT).show();
					Log.d(TAG, "REQUEST_TYPE_BUYER_PAY");

					TradeStatusRequestStruct buyerPayRequestStruct = (TradeStatusRequestStruct) request;
                    String go =SendByHttp("go");
					if (buyerPayRequestStruct.runRequestCommand == DefaultRequestStruct.REQUEST_COMMAND_TRADE_REGISTER) {
						mTrade_Code = buyerPayRequestStruct.trade_code;

						if (mReqThread == null) {
							mReqThread = new RetailerRequestThread();
							mReqThread.start();
						} else {
							mReqThread.start();
						}
					} else {
					}

					// StringBuffer sb = new StringBuffer();
					// sb.append("trade_code : " +
					// buyerPayRequestStruct.trade_code + "\n");
					// sb.append("trade_check : " +
					// buyerPayRequestStruct.trade_check + "\n");
					//
					// appendMsg(sb.toString());
					break;
				}
				case DefaultRequestStruct.REQUEST_TYPE_TRANSACTIONAL_INFORMATION: {
					//Toast.makeText(getApplicationContext(), "retailer 2",	Toast.LENGTH_SHORT).show();
					mTir = (TransactionalInformationRequestStruct) request;
					FinalBank = mTir.bank_name;
					FinalAmount = mTir.price;
					StringBuffer sb = new StringBuffer();
					sb.append("user_name : " + mTir.user_name + "\n");
					sb.append("bank_name : " + mTir.bank_name + "\n");
					sb.append("price : " + mTir.price + "\n");
					sb.append("acc_number : " + mTir.acc_number + "\n");
					sb.append("user_type : " + mTir.user_type + "\n");
					sb.append("trade_check : " + mTir.trade_check + "\n");
					Log.d(TAG, "REQUEST_TYPE_TRANSACTIONAL_INFORMATION : \n"
							+ sb.toString());

					if (mReqThread != null) {
						mReqThread.setStop(true);
						mReqThread.interrupt();
						mReqThread = null;
					}
					try {
						getParent().dismissDialog(DLG_COMM_SERVER);
                        String go =SendByHttp("go");
					} catch (Exception e) {
						e.printStackTrace();
					}					
						getParent().showDialog(DLG_PAYMENT_COMPLETE);
					break;
				}
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("RetailerRequestThread", "onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.nfc_payment_retailer);
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				Main.context.getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		CommunicationService.getInstance(this).registerNFCCallback(this);

        //String go =SendByHttp("go");

		viewInit();
		buttonInit();
		dataInit();
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);

		AlertDialog dialog = null;

		switch (id) {
		case 0: {
			// Toast.makeText(getApplicationContext(), "DLG_EXIT",
			// Toast.LENGTH_SHORT).show();
			new AlertDialog.Builder(Main.context, AlertDialog.THEME_HOLO_LIGHT)
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
		}
		return dialog;
	}

	private void viewInit() {
		Log.d("RetailerRequestThread", "viewInit");
		mProductNameEditText = (EditText) findViewById(R.id.pc_product_name_edittext);
		mProductPriceEditText = (EditText) findViewById(R.id.pc_product_price_edittext);
		mProductPriceEditText.addTextChangedListener(new TextWatcher() {

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

			}
		});
		Spinner spinner = (Spinner) findViewById(R.id.pc_product_count_edittext);
		mProductTotalEditText = (EditText) findViewById(R.id.pc_total_price_edittext);
		spinner.setOnItemSelectedListener(new SpinnerSelect());

	}

	public class SpinnerSelect implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub

			String value = (String) parent.getItemAtPosition(position);
			int totalPri = 0;
			if ((value != null) && !(value.equals(""))) {

				// int totalPri =
				// Integer.parseInt(mProductPriceEditText.getText()
				// .toString()) * Integer.parseInt(value);

				mProductTotalEditText.setText(String.valueOf(totalPri));
			} else {
				mProductTotalEditText.setText(String.valueOf(0));
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	}

	private void buttonInit() {
		Log.d("RetailerRequestThread", "buttonInit");
		mOkBtn = (Button) findViewById(R.id.pc_ok_btn);
		mOkBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DLG_PAYMENT_CONFIRM);
			}
		});

		mCancelBtn = (Button) findViewById(R.id.pc_cancel_btn);
		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				Intent intent = new Intent(NFCPaymentRetailer.this, PwCommon.class);
				startActivity(intent);
			}
		});
	}

	private void dataInit() {

		Log.d("RetailerRequestThread", "dataInit");
		// Check for available NFC Adapter
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (mNfcAdapter == null) {
			// mInfoText = (TextView) findViewById(R.id.textView);
			// mInfoText.setText("NFC is not available on this device.");
		}
		// Register callback to set NDEF message
		mNfcAdapter.setNdefPushMessageCallback(this, this);
		// Register callback to listen for message-sent success
		mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

		// mDbHelper = DbHelper.getInstance(NFCPaymentRetailer.this);
		// mDb = mDbHelper.getDatabase();
		// first!!!
		mConfingData = ShareData.newInstance(this);
		String userType = mConfingData.getLastLoginUserType();

		if (Integer.parseInt(userType) == 2) {
			mProductNameEditText.setEnabled(false);
			mProductPriceEditText.setEnabled(false);
			mProductCntEditText.setEnabled(false);
			mProductTotalEditText.setEnabled(false);
		}

		if (mReqThread == null) {
			mReqThread = new RetailerRequestThread();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d("RetailerRequestThread", "onResume");
		mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

		// Toast.makeText(getApplicationContext(), getIntent().toString(),
		// Toast.LENGTH_SHORT).show();

		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
			processIntent(getIntent());
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d("RetailerRequestThread", "onNewIntent");
		setIntent(intent);
	}

	private void processIntent(Intent intent) {
		Log.d("RetailerRequestThread", "processIntent");
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

			return;
		}

		return;
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		Log.d("RetailerRequestThread", "createNdefMessage");
		// Cursor cursor = mDb.rawQuery("select * from " + DbHelper.TABLE_NAME,
		// null);

		// if(cursor == null || cursor.getCount() == 0) {
		// cursor.close();
		// } else {
		// cursor.moveToFirst();
		String id = mConfingData.getLastID();
		String token = mConfingData.getLastLoginToken();

		String msgStr = "buyer_id:" + id + "\n" + "buyer_token:" + token + "\n"
				+ "product:" + mProductNameEditText.getText().toString() + "\n"
				+ "price:" + mProductTotalEditText.getText().toString();
		// cursor.close();
		byte[] testByte = msgStr.getBytes();
		NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord(
				"application/com.ajantech.nfcpaymentsystem", testByte) });
		return msg;
	}

	private NdefRecord createMimeRecord(String mimeType, byte[] payload) {
		Log.d("RetailerRequestThread", "createMimeRecord");
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				mimeBytes, new byte[0], payload);
		return mimeRecord;
	}

	@Override
	public void onNdefPushComplete(NfcEvent event) {
		Log.d("RetailerRequestThread", "onNdefPushComplete");
		if (mNfcPaymentProgressDialog.isShowing()) {
			mNfcPaymentProgressDialog.dismiss();
		}

		showDialog(DLG_PAYMENT_COMPLETE);
	}

	private boolean paymentToServer() {
		Log.d("RetailerRequestThread", "paymentToServer");
		boolean result = false;
		String myToken = mConfingData.getLastLoginToken();
		String myId = mConfingData.getLastID();
		// sendBuyerPay(mClientId, mClientToken, myToken, mBuyProductName,
		// mBuyPrice);
		CommunicationService.getInstance().sendTradeRegister(myId, myToken,
				mClientToken, mBuyProductName, mBuyPrice);
        String go =SendByHttp("go");
		return result;
	}

	public void OnNFCServiceMessage(DefaultRequestStruct request) {
		Log.d("RetailerRequestThread", "onNFCServiceMEssage");
		mHandler.obtainMessage(1027, request).sendToTarget();
	}

	class RetailerRequestThread extends Thread {

		private final String TAG = "RetailerRequestThread";
		private final boolean D = true;

		private boolean mStop2 = false;

		@Override
		public void run() {
			super.run();

			while (!mStop2) {
				try {
					Log.d("RetailerRequestThread", "thread1");
					CommunicationService.getInstance()
							.sendTransactionalInformation(
									mConfingData.getLastID(),
									mConfingData.getLastLoginToken(),
									mConfingData.getLastLoginUserType(),
									mTrade_Code);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void setStop(boolean aState) {
			mStop2 = aState;
		}
	}

    private String SendByHttp(String msg) {
        if(msg == null)
            msg = "";

        String URL = "http://118.40.113.241:9999/goclient.jsp";

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
	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {
		this.finish();
		Intent intent = new Intent(NFCPaymentRetailer.this, PwCommon.class);
		startActivity(intent);
	}
}