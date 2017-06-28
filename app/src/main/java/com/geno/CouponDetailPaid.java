package com.geno;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geno.bill_folder.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class CouponDetailPaid extends Activity implements
		CreateNdefMessageCallback, OnNdefPushCompleteCallback {
	public static Context context;

	private ProgressDialog mNfcPaymentProgressDialog;
	private NfcAdapter mNfcAdapter;
	private final int DLG_EXIT = 0;
	private ProgressDialog dialog;
	final int DIALOG_1 = 1;
	final int DIALOG_2 = 2;
	final int DIALOG_3 = 3;
	private final int DLG_NFC_CHECK = 4;
	private static final int MESSAGE_SENT = 10;
	LayoutInflater inflater1;
	LayoutInflater inflater2;
	Intent intent, intent2, intent3;
	String dbPosition;
	DBHelper dbHelper, dbHelper2, dbHelper3;
	final static String dbName = "user_info.db";
	final static String dbName2 = "basic_coupon.db";
	final static String dbName3 = "payment_coupon.db";
	final static int dbVersion = 1;
	Cursor cursor;
	String voucher_no, issuer, issue_date, issue_time, expired_date,
			expired_time, purchaser, current_owner, available_value;
	String userName;
	TextView valueText, issuerText, expiredText;
	ArrayList<String> basicDbList = null;
	String returnValue;
	String returnVoucherNo, returnUsedValue, returnIssuer, returnFinalSeller,
			returnSellDate, returnSellTime;
	String[] parseData;
	ImageView imgView;

	// 지문 관련

	private static final String DIALOG_FRAGMENT_TAG = "myFragment";
	private static final String SECRET_MESSAGE = "Very secret message";
	private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
	static final String DEFAULT_KEY_NAME = "default_key";

	private KeyStore mKeyStore;
	private KeyGenerator mKeyGenerator;
	private SharedPreferences mSharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.detail_coupon_paid);

		context = this;
		inflater1 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		intent = getIntent();
		dbPosition = intent.getStringExtra("dbPosition");
		basicDbList = new ArrayList<String>();

		imgView = (ImageView) findViewById(R.id.img1);
		valueText = (TextView) findViewById(R.id.tv2);
		issuerText = (TextView) findViewById(R.id.tv4);
		expiredText = (TextView) findViewById(R.id.tv6);
		intent2 = new Intent(CouponDetailPaid.this, coupon_list.class);
		searchDB();
		viewInit();
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

		if (mNfcAdapter != null) {
			/*Toast.makeText(getApplicationContext(),
					"Tap to beam to another NFC device", Toast.LENGTH_SHORT)
					.show();*/
		} else {
			Toast.makeText(getApplicationContext(),
					"This phone is not NFC enabled.", Toast.LENGTH_SHORT)
					.show();
		}
		// Register callback
		mNfcAdapter.setNdefPushMessageCallback(this, this);
		mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

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
		Button purchaseButton = (Button) findViewById(R.id.tradebutton);
		Button purchaseButton2 = (Button) findViewById(R.id.paybutton);
		Button purchaseButton3 = (Button) findViewById(R.id.purchase_button2);

		purchaseButton3.setVisibility(View.GONE);

		Button purchaseButtonNotInvalidated = (Button) findViewById(
				R.id.purchase_button_not_invalidated2);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			purchaseButtonNotInvalidated.setEnabled(true);
			purchaseButtonNotInvalidated.setOnClickListener(
					new PurchaseButtonClickListener(cipherNotInvalidated,
							KEY_NAME_NOT_INVALIDATED));
		} else {
			// Hide the purchase button which uses a non-invalidated key
			// if the app doesn't work on Android N preview
			purchaseButtonNotInvalidated.setVisibility(View.GONE);
			findViewById(R.id.purchase_button_not_invalidated_description2)
					.setVisibility(View.GONE);
		}

		if (!keyguardManager.isKeyguardSecure()) {
			// Show a message that the user hasn't set up a fingerprint or lock screen.
			Toast.makeText(this,
					"Secure lock screen hasn't set up.\n"
							+ "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",
					Toast.LENGTH_LONG).show();
			purchaseButton.setEnabled(false);
			purchaseButton2.setEnabled(false);
			purchaseButtonNotInvalidated.setEnabled(false);
			return;
		}

		// Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
		// See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
		// The line below prevents the false positive inspection from Android Studio
		// noinspection ResourceType
		if (!fingerprintManager.hasEnrolledFingerprints()) {
			purchaseButton.setEnabled(false);
			purchaseButton2.setEnabled(false);
			// This happens when no fingerprints are registered.
			Toast.makeText(this,
					"Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint",
					Toast.LENGTH_LONG).show();
			return;
		}
		createKey(DEFAULT_KEY_NAME, true);
		createKey(KEY_NAME_NOT_INVALIDATED, false);
		purchaseButton.setEnabled(true);
		purchaseButton2.setEnabled(true);
		purchaseButton.setOnClickListener(
				new PurchaseButtonClickListener(defaultCipher, DEFAULT_KEY_NAME));
		purchaseButton2.setOnClickListener(
				new PurchaseButtonClickListener2(defaultCipher, DEFAULT_KEY_NAME));

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

			showDialog(DLG_NFC_CHECK);
			assert cryptoObject != null;
			//tryEncrypt(cryptoObject.getCipher());
		} else {
			// Authentication happened with backup password. Just show the confirmation message.

			showDialog(DLG_NFC_CHECK);
			//showConfirmation(null);
		}
	}

	public void onPurchased2(boolean withFingerprint,
							@Nullable FingerprintManager.CryptoObject cryptoObject) {
		if (withFingerprint) {
			// If the user has authenticated with fingerprint, verify that using cryptography and
			// then show the confirmation message.

			showDialog(DIALOG_2);
			assert cryptoObject != null;
			//tryEncrypt(cryptoObject.getCipher());
		} else {
			// Authentication happened with backup password. Just show the confirmation message.

			showDialog(DIALOG_2);
			//showConfirmation(null);
		}
	}

	// Show confirmation, if fingerprint was used show crypto information.
	private void showConfirmation(byte[] encrypted) {
		findViewById(R.id.confirmation_message2).setVisibility(View.VISIBLE);
		if (encrypted != null) {
			TextView v = (TextView) findViewById(R.id.encrypted_message2);
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
			//Log.e(TAG, "Failed to encrypt the data with the generated key." + e.getMessage());
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
			findViewById(R.id.confirmation_message2).setVisibility(View.GONE);
			findViewById(R.id.encrypted_message2).setVisibility(View.GONE);

			// Set up the crypto object for later. The object will be authenticated by use
			// of the fingerprint.
			if (initCipher(mCipher, mKeyName)) {

				// Show the fingerprint dialog. The user has the option to use the fingerprint with
				// crypto, or you can fall back to using a server-side verified password.
				FingerprintAuthenticationDialogFragment2 fragment
						= new FingerprintAuthenticationDialogFragment2();
				fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
				boolean useFingerprintPreference = mSharedPreferences
						.getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
								true);
				if (useFingerprintPreference) {
					fragment.setStage(
							FingerprintAuthenticationDialogFragment2.Stage.FINGERPRINT);
				} else {
					fragment.setStage(
							FingerprintAuthenticationDialogFragment2.Stage.PASSWORD);
				}
				fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
			} else {
				// This happens if the lock screen has been disabled or or a fingerprint got
				// enrolled. Thus show the dialog to authenticate with their password first
				// and ask the user if they want to authenticate with fingerprints in the
				// future
				FingerprintAuthenticationDialogFragment2 fragment
						= new FingerprintAuthenticationDialogFragment2();
				fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
				fragment.setStage(
						FingerprintAuthenticationDialogFragment2.Stage.NEW_FINGERPRINT_ENROLLED);
				fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
			}
		}
	}

	private class PurchaseButtonClickListener2 implements View.OnClickListener {

		Cipher mCipher;
		String mKeyName;

		PurchaseButtonClickListener2(Cipher cipher, String keyName) {
			mCipher = cipher;
			mKeyName = keyName;
		}

		@Override
		public void onClick(View view) {
			findViewById(R.id.confirmation_message2).setVisibility(View.GONE);
			findViewById(R.id.encrypted_message2).setVisibility(View.GONE);

			// Set up the crypto object for later. The object will be authenticated by use
			// of the fingerprint.
			if (initCipher(mCipher, mKeyName)) {

				// Show the fingerprint dialog. The user has the option to use the fingerprint with
				// crypto, or you can fall back to using a server-side verified password.
				FingerprintAuthenticationDialogFragment3 fragment
						= new FingerprintAuthenticationDialogFragment3();
				fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
				boolean useFingerprintPreference = mSharedPreferences
						.getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
								true);
				if (useFingerprintPreference) {
					fragment.setStage(
							FingerprintAuthenticationDialogFragment3.Stage.FINGERPRINT);
				} else {
					fragment.setStage(
							FingerprintAuthenticationDialogFragment3.Stage.PASSWORD);
				}
				fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
			} else {
				// This happens if the lock screen has been disabled or or a fingerprint got
				// enrolled. Thus show the dialog to authenticate with their password first
				// and ask the user if they want to authenticate with fingerprints in the
				// future
				FingerprintAuthenticationDialogFragment3 fragment
						= new FingerprintAuthenticationDialogFragment3();
				fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
				fragment.setStage(
						FingerprintAuthenticationDialogFragment3.Stage.NEW_FINGERPRINT_ENROLLED);
				fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
			}
		}
	}
	// 이 위에까지가 지문관련 함수 ㅇㅇ

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
		switch (v.getId()) {
		case R.id.tradebutton:
			showDialog(DLG_NFC_CHECK);
			break;
		case R.id.paybutton:
			showDialog(DIALOG_2);
			break;
		case R.id.yesbutton:
			paymentCoupon();
			break;
		case R.id.nobutton:
			dismissDialog(DIALOG_2);
			break;
		case R.id.titlebutton:
			finish();
			intent = new Intent(this, coupon_list.class);
			startActivity(intent);
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_1:
			final LinearLayout linear1 = (LinearLayout) inflater1.inflate(
					R.layout.nfc_ready, null);
			return new AlertDialog.Builder(CouponDetailPaid.this).setView(
					linear1).create();
		case DIALOG_2:
			final LinearLayout linear2 = (LinearLayout) inflater2.inflate(
					R.layout.sell_1, null);


			return new AlertDialog.Builder(CouponDetailPaid.this).setView(
					linear2).create();
		case DLG_EXIT:
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
		case DLG_NFC_CHECK:
			// Toast.makeText(getApplicationContext(), "DLG_NFC_CHECK",
			// Toast.LENGTH_SHORT).show();
			Drawable drawable = getResources().getDrawable(R.drawable.custom_progressbar);
			mNfcPaymentProgressDialog = new ProgressDialog(context);
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

			return mNfcPaymentProgressDialog;

		}
		return null;
	}

	public void searchDB() {
		SQLiteDatabase db;
		String sql;

		dbHelper2 = new DBHelper(this, dbName2, null, dbVersion);
		db = dbHelper2.getReadableDatabase();

		int j = Integer.parseInt(dbPosition);
		sql = "SELECT * FROM basic_coupon;";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				if (cursor.getPosition() == j) {
					for (int i = 1; i < cursor.getColumnCount(); i++) {
						basicDbList.add(cursor.getString(i));
					}
				}
			}
		}
		cursor.close();
		dbHelper = new DBHelper(this, dbName, null, dbVersion);
		db = dbHelper.getReadableDatabase();

		sql = "SELECT * FROM user_info;";
		cursor = db.rawQuery(sql, null);
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				userName = cursor.getString(1);
			}

		}
		cursor.close();
		dbHelper3 = new DBHelper(this, dbName3, null, dbVersion);
		// Toast.makeText(getApplicationContext(), userName, 500).show();

	}

	public void viewInit() {

		valueText.setText(basicDbList.get(8));
		issuerText.setText(basicDbList.get(1));
		expiredText.setText(basicDbList.get(4) + "," + basicDbList.get(5));
		Resources res = getResources();
		int couponNum;
		if (basicDbList.get(1).equals("롯데백화점")) {
			couponNum = res.getIdentifier("coupon_1", "drawable",
					"com.geno.bill_folder");
		} else if (basicDbList.get(1).equals("신세계백화점")) {
			couponNum = res.getIdentifier("coupon_2", "drawable",
					"com.geno.bill_folder");
		} else if (basicDbList.get(1).equals("NC")) {
			couponNum = res.getIdentifier("coupon_3", "drawable",
					"com.geno.bill_folder");
		} else if (basicDbList.get(1).equals("현대백화점")) {
			couponNum = res.getIdentifier("coupon_4", "drawable",
					"com.geno.bill_folder");
		} else if (basicDbList.get(1).equals("온누리")) {
			couponNum = res.getIdentifier("coupon_7", "drawable",
					"com.geno.bill_folder");
		} else {
			couponNum = res.getIdentifier("coupon_5", "drawable",
					"com.geno.bill_folder");
		}
		imgView.setBackgroundResource(couponNum);

	}

	class DBHelper extends SQLiteOpenHelper {
		public DBHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE basic_coupon (id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "voucher_no VARCHAR(16), issuer VARCHAR(10), issue_date varchar(10), issue_time varchar(8),"
					+ "expired_date varchar(10), expired_time varchar(8), purchaser varchar(5),"
					+ "current_owner varchar(5), available_value varchar(10), transfer_date varchar(10),"
					+ "transfer_time varchar(8), transferor varchar(10), transferee varchar(10));");
			db.execSQL("CREATE TABLE user_info (id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "name VARCHAR(5), tell VARCHAR(11), email varchar(30), password varchar(12));");
			db.execSQL("CREATE TABLE payment_coupon (id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "voucher_no VARCHAR(16), used_value varchar(10), issuer varchar(10), final_seller VARCHAR(10), sell_date varchar(10), sell_time varchar(8));");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXITS user_info");
			onCreate(db);
		}

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();

		showDialog(DLG_EXIT);
	}

	public void paymentCoupon() {
		dialog = new ProgressDialog(this);
		dialog.setTitle("Loading...");
		dialog.setMessage("정산 처리중");
		dialog.setCancelable(false);
		dialog.setMax(3);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.show();
		mTask.execute();
	}

	private AsyncTask<Void, Integer, Void> mTask = new AsyncTask<Void, Integer, Void>() {
		protected Void doInBackground(Void... p) {
			try {
				publishProgress(1); // 접속 중
				HttpClient httpClient = new DefaultHttpClient();
				// replace with your url
				HttpPost httpPost = new HttpPost(
						"http://221.156.54.210:9999/pay_connect.jsp");

				// Post Data
				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(
						2);
				nameValuePair.add(new BasicNameValuePair("voucher_no",
						basicDbList.get(0)));
				nameValuePair.add(new BasicNameValuePair("userName", userName));

				publishProgress(2); // 데이터 수신 중
				httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair,
						"euc-kr"));
				HttpResponse response = httpClient.execute(httpPost);

				publishProgress(3); // 데이터 분석중
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream instream = entity.getContent();
					int l;
					byte[] tmp = new byte[4096];
					while ((l = instream.read(tmp)) != -1) {
						returnValue = new String(tmp, 0, tmp.length);
					}
				}

				parseData = returnValue.split("::");
			} catch (Exception e) {
				publishProgress(-1);
				Log.e("net", "오류", e);
			}

			return null;
		}

		protected void onProgressUpdate(Integer[] values) {
			switch (values[0]) {
			case -1:
				//Toast.makeText(getApplicationContext(), "통신 오류", 0).show();
				break;
			case 1:
				dialog.setMessage("연결 중...");
				break;
			case 2:
				dialog.setMessage("데이터 전송 중...");
				break;
			case 3:
				dialog.setMessage("정산 중...");
				break;
			}

			dialog.setProgress(values[0]);
		}

		protected void onPostExecute(Void result) {
			dialog.dismiss();
			//Toast.makeText(getApplicationContext(), "정산완료", 0).show();
			returnVoucherNo = parseData[1];
			returnIssuer = basicDbList.get(1);
			returnUsedValue = basicDbList.get(8);
			returnFinalSeller = userName;
			returnSellDate = parseData[3];
			returnSellTime = parseData[4];

			SQLiteDatabase db;
			String sql;

			db = dbHelper3.getReadableDatabase();//dbHelper3을 불러서
			sql = String
					.format("INSERT INTO payment_coupon VALUES (NULL,'%s', '%s', '%s', '%s', '%s', '%s');",
							returnVoucherNo, returnIssuer, returnUsedValue,
							returnFinalSeller, returnSellDate, returnSellTime);
			db.execSQL(sql);//sql문 : payment_coupon 에 다음 값을 넣으라는 것

			db = dbHelper2.getWritableDatabase();//dbHelper2를 쓰거나 읽거나 할 것임
			sql = String.format(
					"delete from basic_coupon where voucher_no = '%s';",
					returnVoucherNo);
			db.execSQL(sql);//sql문 : voucher_no값이 returnVoucherNo인 basic_coupon의 값을 지움

			finish();
			startActivity(intent2);
		}
	};

	protected void startAcitivity(Intent intent) {
		// TODO Auto-generated method stub
		startActivity(intent);
	}

	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		Log.d("onNdefPushComplete", "onNdefPushComplete");

		mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SENT:
				
				SQLiteDatabase db;
				String sql;

				db = dbHelper2.getReadableDatabase();
				sql = String
						.format("delete from basic_coupon where voucher_no = '%s';",basicDbList.get(0)
								);
				db.execSQL(sql);
				dismissDialog(DLG_NFC_CHECK);
				finish();//내 처음으로 제대로 고친거 ^ㅗ^
				startActivity(intent2);
				break;
			}
		}
	};

	public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
		byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
		NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				mimeBytes, new byte[0], payload);
		Log.d("createMimeRecord", "createMimeRecord");

		return mimeRecord;
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		String nowDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
		String nowTime = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
		String text = ("vou:" + basicDbList.get(0) + "\niss:"
				+ basicDbList.get(1) + "\nissd:" + basicDbList.get(2)
				+ "\nisst:" + basicDbList.get(3) + "\nexpd:"
				+ basicDbList.get(4) + "\nexpt:" + basicDbList.get(5)
				+ "\npur:" + basicDbList.get(6) + "\ncur:"
				+ basicDbList.get(7) + "\nava:"
				+ basicDbList.get(8) + "\ntrad:" + nowDate
				+ "\ntrat:" + nowTime + "\ntraf:"
				+ userName);
		NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord(
				"application/com.geno_bill_folder", text.getBytes())
		/**
		 * The Android Application Record (AAR) is commented out. When a device
		 * receives a push with an AAR in it, the application specified in the
		 * AAR is guaranteed to run. The AAR overrides the tag dispatch system.
		 * You can add it back in to guarantee that this activity starts when
		 * receiving a beamed message. For now, this code uses the tag dispatch
		 * system.
		 */
		// ,NdefRecord.createApplicationRecord("com.example.android.beam")
				});
		Log.d("createNdefMessage", text);
		return msg;
	}

}
