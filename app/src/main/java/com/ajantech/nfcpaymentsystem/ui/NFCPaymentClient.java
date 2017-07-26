package com.ajantech.nfcpaymentsystem.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ajantech.nfc_network.CommunicationService;
import com.ajantech.nfc_network.CommunicationService.OnNFCServiceCallBack;
import com.ajantech.nfc_network.ShareData;
import com.ajantech.nfc_network.request.DefaultRequestStruct;
import com.ajantech.nfc_network.request.TradeStatusRequestStruct;
import com.ajantech.nfc_network.request.TransactionalInformationRequestStruct;
import com.geno.payment.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class NFCPaymentClient extends Activity implements
		CreateNdefMessageCallback, OnNdefPushCompleteCallback,
		OnNFCServiceCallBack {
	private final String TAG = "NFCPaymentClient";
	private final boolean D = true;

	public static String ProductName = "";
	public static String ProductTotal = "";
	public static String FinalAmount = "";
	public static String FinalBank = "";
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
	private TransactionalInformationRequestStruct mTir;

	private String mConfirmMsg;
	private String mClientId;
	private String mClientToken;
	private String mBuyProductName;
	private String mBuyPrice;
	private String mRetailerToken;

	private ClientRequestThread mReqThread;
	private String mTradeCode = "";
	private boolean mTranInfo = false;
	Intent intent;
	int count;
	long start;
	long end;

	//지문관련

	private static final String DIALOG_FRAGMENT_TAG = "myFragment";
	private static final String SECRET_MESSAGE = "Very secret message";
	private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
	static final String DEFAULT_KEY_NAME = "default_key";

	private KeyStore mKeyStore;
	private KeyGenerator mKeyGenerator;
	private SharedPreferences mSharedPreferences;

	// private DbHelper mDbHelper;
	// private SQLiteDatabase mDb;
	private ShareData mConfingData = null;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			Log.d("ClientLog", "handler");
			if (msg.what == 1025) {
				if (D) {
					Log.d(TAG, "GET MESSAGE = " + msg.obj);
				}
			} else if (msg.what == 1026) {
				DefaultRequestStruct request = (DefaultRequestStruct) msg.obj;

				Log.d(TAG, "recv runRequestType : " + request.type);
				Log.d(TAG, "recv runRequestCommand : "
						+ request.runRequestCommand);
				//Toast.makeText(getApplicationContext(), request.type+""	, Toast.LENGTH_LONG).show();
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
						//Toast.makeText(getApplicationContext(), "client 1", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "REQUEST_TYPE_BUYER_PAY");

						TradeStatusRequestStruct buyerPayRequestStruct = (TradeStatusRequestStruct) request;
						mTradeCode = buyerPayRequestStruct.trade_code;
						// mRetailerToken =
						Log.d(TAG, "runRequestCommand : "
								+ buyerPayRequestStruct.runRequestCommand);
						try {
							dismissDialog(DLG_COMM_SERVER);
						} catch (Exception e) {
							e.printStackTrace();
						}

						switch (buyerPayRequestStruct.runRequestCommand) {
							case DefaultRequestStruct.REQUEST_COMMAND_BUYING_REQUEST: {
								mRetailerToken = buyerPayRequestStruct.seller_token;

								//showDialog(DLG_PAYMENT_CHECK);
								String myId = mConfingData.getLastID();
								String myToken = mConfingData.getLastLoginToken();
								String productStr = mProductNameEditText.getText()
										.toString();
								String priceStr = mProductTotalEditText.getText()
										.toString();
								CommunicationService.getInstance()
										.sendBuyerTradeEndFinalPalyment(myId,
												myToken, mRetailerToken,
												mTradeCode, productStr, priceStr);

								break;
							}
							case DefaultRequestStruct.REQUEST_COMMAND_BUYER_TRADE_END_COMPLETE_FINAL_PAYMENT: {
								mTranInfo = true;
								break;
							}
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
						//Toast.makeText(getApplicationContext(), "client 2", Toast.LENGTH_SHORT).show();
						mTir = (TransactionalInformationRequestStruct) request;
						FinalBank = mTir.bank_name;
						FinalAmount = mTir.price;
						if (mReqThread != null) {
							mTranInfo = false;
							mReqThread.setStop(true);
							mReqThread.interrupt();
							mReqThread = null;
						}
						try {
							dismissDialog(DLG_COMM_SERVER);
						} catch (Exception e) {
							e.printStackTrace();
						}
						showDialog(DLG_PAYMENT_COMPLETE);
						break;
					}
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("ClientLog", "onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.nfc_payment_client);
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(Main.context, Main.context.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		CommunicationService.getInstance(this).registerNFCCallback(this);

		viewInit();
		//buttonInit();
		dataInit();

		mCancelBtn = (Button) findViewById(R.id.pc_cancel_btn);
		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				Intent intent = new Intent(NFCPaymentClient.this, PwCommon.class);
				startActivity(intent);
			}
		});

		//지문관련

		try {
			mKeyStore = KeyStore.getInstance("AndroidKeyStore");
		} catch (KeyStoreException e) {
			throw new RuntimeException("Failed to get an instance of KeyStore", e);
		}
		try {
			mKeyGenerator = KeyGenerator
					.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
		}
		Cipher defaultCipher;
		Cipher cipherNotInvalidated;
		try {
			defaultCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
					+ KeyProperties.BLOCK_MODE_CBC + "/"
					+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
			cipherNotInvalidated = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
					+ KeyProperties.BLOCK_MODE_CBC + "/"
					+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException("Failed to get an instance of Cipher", e);
		}
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		KeyguardManager keyguardManager = getSystemService(KeyguardManager.class);
		FingerprintManager fingerprintManager = getSystemService(FingerprintManager.class);
		Button purchaseButton = (Button) findViewById(R.id.pc_ok_btn);

		Button purchaseButton3 = (Button) findViewById(R.id.purchase_button);
		purchaseButton3.setVisibility(View.GONE);

		Button purchaseButtonNotInvalidated = (Button) findViewById(
				R.id.purchase_button_not_invalidated);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			purchaseButtonNotInvalidated.setEnabled(true);
			purchaseButtonNotInvalidated.setOnClickListener(
					new PurchaseButtonClickListener(cipherNotInvalidated,
							KEY_NAME_NOT_INVALIDATED));
		} else {
			// Hide the purchase button which uses a non-invalidated key
			// if the app doesn't work on Android N preview
			purchaseButtonNotInvalidated.setVisibility(View.GONE);
			findViewById(R.id.purchase_button_not_invalidated_description)
					.setVisibility(View.GONE);
		}

		if (!keyguardManager.isKeyguardSecure()) {
			// Show a message that the user hasn't set up a fingerprint or lock screen.
			Toast.makeText(this,
					"Secure lock screen hasn't set up.\n"
							+ "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",
					Toast.LENGTH_LONG).show();
			purchaseButton.setEnabled(false);
			purchaseButtonNotInvalidated.setEnabled(false);
			return;
		}

		// Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
		// See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
		// The line below prevents the false positive inspection from Android Studio
		// noinspection ResourceType
		if (!fingerprintManager.hasEnrolledFingerprints()) {
			purchaseButton.setEnabled(false);
			// This happens when no fingerprints are registered.
			Toast.makeText(this,
					"Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint",
					Toast.LENGTH_LONG).show();
			return;
		}
		createKey(DEFAULT_KEY_NAME, true);
		createKey(KEY_NAME_NOT_INVALIDATED, false);
		purchaseButton.setEnabled(true);
		purchaseButton.setOnClickListener(
				new PurchaseButtonClickListener(defaultCipher, DEFAULT_KEY_NAME));
	}


	// 지문관련 함수들

	private boolean initCipher(Cipher cipher, String keyName) {
		try {
			mKeyStore.load(null);
			SecretKey key = (SecretKey) mKeyStore.getKey(keyName, null);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return true;
		} catch (KeyPermanentlyInvalidatedException e) {
			return false;
		} catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
				| NoSuchAlgorithmException | InvalidKeyException e) {
			throw new RuntimeException("Failed to init Cipher", e);
		}
	}

	/**
	 * Proceed the purchase operation
	 *
	 * @param withFingerprint {@code true} if the purchase was made by using a fingerprint
	 * @param cryptoObject the Crypto object
	 */
	public void onPurchased(boolean withFingerprint,
							@Nullable FingerprintManager.CryptoObject cryptoObject) {
		if (withFingerprint) {
			// If the user has authenticated with fingerprint, verify that using cryptography and
			// then show the confirmation message.
			showDialog(DLG_PAYMENT_CONFIRM);
			assert cryptoObject != null;
			//tryEncrypt(cryptoObject.getCipher());
		} else {
			// Authentication happened with backup password. Just show the confirmation message.
			showDialog(DLG_PAYMENT_CONFIRM);
			//showConfirmation(null);
		}
	}

	// Show confirmation, if fingerprint was used show crypto information.
	private void showConfirmation(byte[] encrypted) {
		findViewById(R.id.confirmation_message).setVisibility(View.VISIBLE);
		if (encrypted != null) {
			TextView v = (TextView) findViewById(R.id.encrypted_message);
			v.setVisibility(View.VISIBLE);
			v.setText(Base64.encodeToString(encrypted, 0 /* flags */));
		}
	}


	private void tryEncrypt(Cipher cipher) {
		try {
			byte[] encrypted = cipher.doFinal(SECRET_MESSAGE.getBytes());
			showConfirmation(encrypted);
		} catch (BadPaddingException | IllegalBlockSizeException e) {
			Toast.makeText(this, "Failed to encrypt the data with the generated key. "
					+ "Retry the purchase", Toast.LENGTH_LONG).show();
			Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
		}
	}

	public void createKey(String keyName, boolean invalidatedByBiometricEnrollment) {
		// The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
		// for your flow. Use of keys is necessary if you need to know if the set of
		// enrolled fingerprints has changed.
		try {
			mKeyStore.load(null);
			// Set the alias of the entry in Android KeyStore where the key will appear
			// and the constrains (purposes) in the constructor of the Builder

			KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName,
					KeyProperties.PURPOSE_ENCRYPT |
							KeyProperties.PURPOSE_DECRYPT)
					.setBlockModes(KeyProperties.BLOCK_MODE_CBC)
					// Require the user to authenticate with a fingerprint to authorize every use
					// of the key
					.setUserAuthenticationRequired(true)
					.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

			// This is a workaround to avoid crashes on devices whose API level is < 24
			// because KeyGenParameterSpec.Builder#setInvalidatedByBiometricEnrollment is only
			// visible on API level +24.
			// Ideally there should be a compat library for KeyGenParameterSpec.Builder but
			// which isn't available yet.
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				builder.setInvalidatedByBiometricEnrollment(invalidatedByBiometricEnrollment);
			}
			mKeyGenerator.init(builder.build());
			mKeyGenerator.generateKey();
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException
				| CertificateException | IOException e) {
			throw new RuntimeException(e);
		}
	}

	private class PurchaseButtonClickListener implements View.OnClickListener {

		Cipher mCipher;
		String mKeyName;

		PurchaseButtonClickListener(Cipher cipher, String keyName) {
			mCipher = cipher;
			mKeyName = keyName;
		}

		@Override
		public void onClick(View view) {
			findViewById(R.id.confirmation_message).setVisibility(View.GONE);
			findViewById(R.id.encrypted_message).setVisibility(View.GONE);

			// Set up the crypto object for later. The object will be authenticated by use
			// of the fingerprint.
			if (initCipher(mCipher, mKeyName)) {

				// Show the fingerprint dialog. The user has the option to use the fingerprint with
				// crypto, or you can fall back to using a server-side verified password.
				FingerprintAuthenticationDialogFragment fragment
						= new FingerprintAuthenticationDialogFragment();
				fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
				boolean useFingerprintPreference = mSharedPreferences
						.getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
								true);
				if (useFingerprintPreference) {
					fragment.setStage(
							FingerprintAuthenticationDialogFragment.Stage.FINGERPRINT);
				} else {
					fragment.setStage(
							FingerprintAuthenticationDialogFragment.Stage.PASSWORD);
				}
				fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
			} else {
				// This happens if the lock screen has been disabled or or a fingerprint got
				// enrolled. Thus show the dialog to authenticate with their password first
				// and ask the user if they want to authenticate with fingerprints in the
				// future
				FingerprintAuthenticationDialogFragment fragment
						= new FingerprintAuthenticationDialogFragment();
				fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
				fragment.setStage(
						FingerprintAuthenticationDialogFragment.Stage.NEW_FINGERPRINT_ENROLLED);
				fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
			}
		}
	}

	// 이 위에까지가 지문관련 함수 ㅇㅇ

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		Log.d("ClientLog", "onNewIntent");
	}

	private void viewInit() {
		Log.d("ClientLog", "viewInit");
		Spinner spinner = (Spinner) findViewById(R.id.pc_product_count_edittext);
		spinner.setOnItemSelectedListener(new SpinnerSelect());
		mProductNameEditText = (EditText) findViewById(R.id.pc_product_name_edittext);
		mProductNameEditText.setText("배추");
		mProductTotalEditText = (EditText) findViewById(R.id.pc_total_price_edittext);
		mProductPriceEditText = (EditText) findViewById(R.id.pc_product_price_edittext);
		TextWatcher textWatcher = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// 텍스트가 변경되기전 입력한 내용에 대해
				// s는 변경할 수 없음
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// 텍스트를 변경된 후 변경 사항과 함께
				// s는 변경할 수 없음
			}

			@Override
			public void afterTextChanged(Editable s) {
				if(!mProductPriceEditText.getText().toString().equals(""))
				{
					mProductTotalEditText.setText(String.valueOf(count*Integer.parseInt(mProductPriceEditText.getText().toString())));
				}
			}

		};
		InputFilter[] FilerArray = new InputFilter[1];
		FilerArray[0] = new InputFilter.LengthFilter(6);
		mProductPriceEditText.setFilters(FilerArray);
		mProductPriceEditText.setText("1000");
		mProductPriceEditText.addTextChangedListener(textWatcher);
	}


	public class SpinnerSelect implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
								   int position, long id) {
			// TODO Auto-generated method stub

			String value = (String) parent.getItemAtPosition(position);
			if ((value != null) && !(value.equals(""))) {
				count = Integer.parseInt(value);
				if(!mProductPriceEditText.getText().toString().equals("")) {
					int totalPri = Integer.parseInt(mProductPriceEditText.getText()
							.toString()) * Integer.parseInt(value);
					mProductTotalEditText.setText(String.valueOf(totalPri));
				}
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
		Log.d("ClientLog", "buttonInit");
		mOkBtn = (Button) findViewById(R.id.pc_ok_btn);
		mOkBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/* 갤럭시 오류로 일단 없이 if(gpstf()) {
					showDialog(DLG_PAYMENT_CONFIRM);
				}
				else {
					showDialog(19);
				}
				*/
				showDialog(DLG_PAYMENT_CONFIRM);
			}
		});

		mCancelBtn = (Button) findViewById(R.id.pc_cancel_btn);
		mCancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
				Intent intent = new Intent(NFCPaymentClient.this, PwCommon.class);
				startActivity(intent);
			}
		});
	}

	private void dataInit() {
		Log.d("ClientLog", "dataInit");
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

		// mDbHelper = DbHelper.getInstance(NFCPaymentClient.this);
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
			mReqThread = new ClientRequestThread();
			mReqThread.start();
		} else {
			mReqThread.start();
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.d("ClientLog", "onResume");
		//mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

		//if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
		//	processIntent(getIntent());
		//}
	}

	private void processIntent(Intent intent) {

		Log.d("ClientLog", "processIntent");
		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		// only one message sent during the beam
		NdefMessage msg = (NdefMessage) rawMsgs[0];

		String strSite = new String(msg.getRecords()[0].getPayload());
		mClientId = strSite.substring(strSite.indexOf("buyer_id") + 9,
				strSite.indexOf("\n"));
		mClientToken = strSite.substring(strSite.indexOf("buyer_token") + 12,
				strSite.indexOf("\n", strSite.indexOf("buyer_token") + 12));
		mBuyProductName = strSite.substring(strSite.indexOf("product") + 8,
				strSite.indexOf("\n", strSite.indexOf("product") + 8));
		mBuyPrice = strSite.substring(strSite.indexOf("price") + 6,
				strSite.length());
		mConfirmMsg = getString(R.string.payment_product_name)
				+ mBuyProductName + "\n"
				+ getString(R.string.payment_product_price) + mBuyPrice;

		showDialog(DLG_PAYMENT_RETAIL_CONFIRM);
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);

		AlertDialog dialog = null;

		switch (id) {
			case 0:{
				//Toast.makeText(getApplicationContext(), "DLG_EXIT",	Toast.LENGTH_SHORT).show();
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
			case 19: {
				AlertDialog.Builder builder = new AlertDialog.Builder(Main.context,
						AlertDialog.THEME_HOLO_LIGHT);
				builder.setTitle(R.string.gpse_title);
				builder.setMessage(R.string.gps_error);
				builder.setPositiveButton(getResources().getText(R.string.ok_str),
						new DialogInterface.OnClickListener() {

							@Override

							public void onClick(DialogInterface dialog, int paramInt) {

							}

						});
				dialog = builder.create();
				break;
			}
			case DLG_PAYMENT_CONFIRM: {
				//Toast.makeText(getApplicationContext(), "DLG_PAYMENT_CONFIRM",	Toast.LENGTH_SHORT).show();
				ProductName = mProductNameEditText.getText().toString();
				ProductTotal = mProductTotalEditText.getText().toString();
				String message = getString(R.string.payment_product_msg,
						mProductNameEditText.getText().toString(),
						mProductTotalEditText.getText().toString());
				AlertDialog.Builder builder = new Builder(Main.context);
				builder.setTitle(R.string.payment_title);
				builder.setMessage(message);
				builder.setPositiveButton(getResources().getText(R.string.ok_str),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int paramInt) {

								dismissDialog(DLG_PAYMENT_CONFIRM);
								showDialog(DLG_NFC_CHECK);
								removeDialog(DLG_PAYMENT_CONFIRM);
							}

						});
				builder.setNegativeButton(
						getResources().getText(R.string.cancel_str),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int paramInt) {
								removeDialog(DLG_PAYMENT_CONFIRM);
							}

						});
				dialog = builder.create();
				break;
			}
			case DLG_PAYMENT_RETAIL_CONFIRM: {
				//Toast.makeText(getApplicationContext(), "DLG_PAYMENT_RETAIL_CONFIRM",	Toast.LENGTH_SHORT).show();
				AlertDialog.Builder builder = new Builder(Main.context);
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

							}
						});
				dialog = builder.create();
				break;
			}
			case DLG_NFC_CHECK: {
				//Toast.makeText(getApplicationContext(), "DLG_NFC_CHECK",	Toast.LENGTH_SHORT).show();
				mNfcPaymentProgressDialog = new ProgressDialog(
						Main.context);
                Drawable drawable = getResources().getDrawable(R.drawable.custom_progressbar);
                Drawable drawable2 = getResources().getDrawable(R.drawable.dialoag_view);
                mNfcPaymentProgressDialog.setIndeterminateDrawable(drawable);
				mNfcPaymentProgressDialog.setMessage(getResources().getString(
						R.string.payment_nfc_info_msg));

				mNfcPaymentProgressDialog.setButton(
						getResources().getString(R.string.cancel_str),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dismissDialog(DLG_NFC_CHECK);
							}
						});
                mNfcPaymentProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(
                        Color.TRANSPARENT));

				return mNfcPaymentProgressDialog;
			}
			case DLG_COMM_SERVER: {
				//Toast.makeText(getApplicationContext(), "DLG_COMM_SERVER",	Toast.LENGTH_SHORT).show();
				mNfcPaymentSrvProgDlg = new ProgressDialog(Main.context);
				mNfcPaymentSrvProgDlg.setMessage(getResources().getString(
						R.string.payment_connection_server_msg));
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
			case DLG_PAYMENT_CHECK: {
				//Toast.makeText(getApplicationContext(), "DLG_PAYMENT_CHECK",	Toast.LENGTH_SHORT).show();
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
								String productStr = mProductNameEditText.getText()
										.toString();
								String priceStr = mProductTotalEditText.getText()
										.toString();
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
			case DLG_PAYMENT_COMPLETE: {
				end = System.currentTimeMillis();
				Log.w("IntroActivity","paytime start " + start + " millisecond");
				Log.w("IntroActivity","paytime end " + end + " millisecond");
				Log.w("IntroActivity","paytime result " + (end - start) + " millisecond");
				//Toast.makeText(getApplicationContext(), "DLG_PAYMENT_COMPLETE",	Toast.LENGTH_SHORT).show();
				AlertDialog.Builder builder = new Builder(Main.context);
				builder.setCancelable(false);
				builder.setTitle(R.string.payment_title);
				String msg = getString(R.string.payment_complete_client_detail_msg,
						mTir.price);
				builder.setMessage(msg);
				// builder.setMessage(getResources().getString(
				// R.string.payment_complete_msg)
				// + "\n"
				// + "Bank Name : "
				// + mTir.bank_name
				// + "\n"
				// + "Price : " + mTir.price + "USD\n");
				builder.setPositiveButton(getResources().getText(R.string.ok_str),
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface paramDialogInterface,
									int paramInt) {
								String change_count = change_count(mTradeCode+"-"+String.valueOf(count));
								finish();
								intent = new Intent(NFCPaymentClient.this, Main.class);
								startActivity(intent);

							}
						});
				dialog = builder.create();
				break;
			}
			case DLG_PAYMENT_FAIL: {
				//Toast.makeText(getApplicationContext(), "DLG_PAYMENT_FAIL",	Toast.LENGTH_SHORT).show();
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

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		Log.d("ClientLog", "createNdefMessage");
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

		// if (mReqThread == null) {
		// mReqThread = new ClientRequestThread();
		// mReqThread.start();
		// } else {
		// mReqThread.start();
		// }
		return msg;
	}

	private NdefRecord createMimeRecord(String mimeType, byte[] payload) {
		Log.d("ClientLog", "createMimeRecord");
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				mimeBytes, new byte[0], payload);
		return mimeRecord;
	}

	@Override
	public void onNdefPushComplete(NfcEvent event) {
		start = System.currentTimeMillis();
		Log.d("ClientLog", "onNdefPushComplete");
		if (mNfcPaymentProgressDialog.isShowing()) {
			mNfcPaymentProgressDialog.dismiss();
		}

		// showDialog(DLG_PAYMENT_COMPLETE);
	}

	private boolean paymentToServer() {
		Log.d("ClientLog", "paymentToServer");
		boolean result = false;
		String myToken = mConfingData.getLastLoginToken();
		CommunicationService.getInstance().sendBuyerPay(mClientId,
				mClientToken, myToken, mBuyProductName, mBuyPrice);

		return result;
	}

	public void OnNFCServiceMessage(DefaultRequestStruct request) {
		Log.d("ClientLog", "OnNFCServiceMessage");

		mHandler.obtainMessage(1026, request).sendToTarget();
	}

	class ClientRequestThread extends Thread {
		private final String TAG = "ClientRequestThread";
		private final boolean D = true;

		private boolean mStop1 = false;

		@Override
		public void run() {
			super.run();

			while (!mStop1) {
				try {
					if (!mTranInfo) {
						CommunicationService.getInstance().sendBuyingRequest(
								mConfingData.getLastID(),
								mConfingData.getLastLoginToken(),
								mProductNameEditText.getText().toString(),
								mProductTotalEditText.getText().toString());
						Log.d("ClientLog", "thread1");
					} else {
						CommunicationService.getInstance()
								.sendTransactionalInformation(
										mConfingData.getLastID(),
										mConfingData.getLastLoginToken(),
										mConfingData.getLastLoginUserType(),
										mTradeCode);
						Log.d("ClientLog", "thread2");
					}
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void setStop(boolean aState) {
			mStop1 = aState;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {
		this.finish();
		Intent intent = new Intent(NFCPaymentClient.this, PwCommon.class);
		startActivity(intent);
	}


	private boolean gpstf()
	{
		GpsInfo gps;
		gps = new GpsInfo(NFCPaymentClient.this);
		if(gps.isGetLocation())
		{
			double lat = gps.getLatitude();
			double lon = gps.getLongitude();

			if(lat < 30 || lon < 110)
			{
				gps.stopUsingGPS();
				return false;
			}
			else
			{
				gps.stopUsingGPS();
				return true;
			}
		}
		else
		{
			gps.stopUsingGPS();
			return false;
		}
	}
	private String change_count(String msg) {
		if(msg == null)
			msg = "";

		String URL = "http://221.156.54.210:9999/change_count.jsp";

		DefaultHttpClient client = new DefaultHttpClient();
		try {
			/* ???? id?? pwd?? ?????? ???? */

			HttpPost post = new HttpPost(URL+"?msg="+msg);

			/* ??????? ??? 5?? */
			HttpParams params = client.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 30000);
			HttpConnectionParams.setSoTimeout(params, 30000);

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
}


