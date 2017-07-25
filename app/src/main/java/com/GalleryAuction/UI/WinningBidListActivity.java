package com.GalleryAuction.UI;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.GalleryAuction.Adapter.WinningBidInfoAdapter;
import com.GalleryAuction.Client.FingerprintAuthenticationDialogFragment4;
import com.GalleryAuction.Client.FingerprintAuthenticationDialogFragment5;
import com.GalleryAuction.Item.HttpClientItem;
import com.geno.bill_folder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static com.GalleryAuction.Client.TagInfoClient.toHexString;
import static com.GalleryAuction.Item.HttpClientItem.ArtInfo;
import static com.GalleryAuction.Item.HttpClientItem.BiddingInfo;
import static com.GalleryAuction.Item.HttpClientItem.BiddingInfoBest;
import static com.GalleryAuction.Item.HttpClientItem.BiddingList;
import static com.GalleryAuction.Item.HttpClientItem.BiddingWinArtistAgree;
import static com.GalleryAuction.Item.HttpClientItem.BiddingWinUserAgree;
import static com.GalleryAuction.Item.HttpClientItem.BiddingWinUserCancel;

public class WinningBidListActivity extends Activity {
    TextView tv1, tv2, tv3;
    Button btn1, btn2, btn3;
    String auckey, userID, mybest, best, bid, bidkey, bidprice, bidcontime;
    long min_bidding, min_bidding2;
    private ListView listView;
    private WinningBidInfoAdapter adapter;
    NfcAdapter  nfcAdapter;
    PendingIntent pendingIntent;
    TextToSpeech tts;


    private static final String DIALOG_FRAGMENT_TAG = "myFragment";
    private static final String SECRET_MESSAGE = "Very secret message";
    private static final String KEY_NAME_NOT_INVALIDATED = "key_not_invalidated";
    public static final String DEFAULT_KEY_NAME = "default_key";
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_winningbidlist);
        tv2 = (TextView)findViewById(R.id.winningbidlistbest_txt);
        tv3 = (TextView)findViewById(R.id.winningbidlistmybest_txt);
        btn1 = (Button)findViewById(R.id.winningbidlist_btnagree);
        btn2 = (Button)findViewById(R.id.winningbidlist_btnX);
        btn3 = (Button)findViewById(R.id.winningbidlist_btnExit);
        listView = (ListView)findViewById(R.id.winningbidlist_list);

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
        final Cipher defaultCipher;
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


        Button purchaseButtonNotInvalidated = (Button) findViewById(
                R.id.purchase_button_not_invalidated5);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            purchaseButtonNotInvalidated.setEnabled(true);
            purchaseButtonNotInvalidated.setOnClickListener(
                    (View.OnClickListener) new PurchaseButtonClickListener(cipherNotInvalidated,
                            KEY_NAME_NOT_INVALIDATED));
        } else {
            // Hide the purchase button which uses a non-invalidated key
            // if the app doesn't work on Android N preview
            purchaseButtonNotInvalidated.setVisibility(View.GONE);
            findViewById(R.id.purchase_button_not_invalidated_description5)
                    .setVisibility(View.GONE);
        }

        if (!keyguardManager.isKeyguardSecure()) {
            // Show a message that the user hasn't set up a fingerprint or lock screen.
            Toast.makeText(this,
                    "Secure lock screen hasn't set up.\n"
                            + "Go to 'Settings -> Security -> Fingerprint' to set up a fingerprint",
                    Toast.LENGTH_LONG).show();
            btn1.setEnabled(false);
            purchaseButtonNotInvalidated.setEnabled(false);
            return;
        }

        // Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
        // See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            btn1.setEnabled(false);
            // This happens when no fingerprints are registered.
            Toast.makeText(this,
                    "Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint",
                    Toast.LENGTH_LONG).show();
            return;
        }
        createKey(DEFAULT_KEY_NAME, true);
        createKey(KEY_NAME_NOT_INVALIDATED, false);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent0 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent0, 0);
        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        final Intent intent = getIntent();
        auckey = intent.getStringExtra("auckey");
        userID = intent.getStringExtra("userID");
        bid = intent.getStringExtra("bid");
        bidkey = intent.getStringExtra("bidkey");
        adapter = new WinningBidInfoAdapter();
        try {
            JSONObject job2 = new JSONObject(BiddingInfoBest(auckey));
            best = job2.get("bid_price").toString();
            Log.d("SSAAAA", ""+job2);

            JSONObject job = new JSONObject(BiddingInfo(userID, auckey));
            mybest = job.get("bid_price").toString();
            Log.d("SSAAAA", ""+job);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        min_bidding = mybest==null?0:Long.parseLong(mybest);
        min_bidding2 = best==null?0:Long.parseLong(best);

        tv2.setText("구매가격은 " + ReBidding.currentpoint(String.valueOf(min_bidding2)) + "원 입니다");
        tv3.setText("당신의 추가금액은 " + ReBidding.currentpoint(String.valueOf(min_bidding)) + "원 입니다");
        if (bid.equals("0")|| bid.equals("2")) {
            btn1.setVisibility(View.GONE);
            btn2.setVisibility(View.GONE);
            btn3.setVisibility(View.VISIBLE);
            btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder alert = new AlertDialog.Builder(WinningBidListActivity.this);
                    alert.setMessage(getString(R.string.ct_msg)).setCancelable(false).setPositiveButton("거부", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tts.stop();

                        }
                    }).setNeutralButton("동의", new PurchaseButtonClickListener(defaultCipher, DEFAULT_KEY_NAME) {

                        //                            tts.stop();
//                            tts.shutdown();
////                            BiddingWinUserAgree(auckey);
//
//                            Toast.makeText(WinningBidListActivity.this, "거래완료", Toast.LENGTH_SHORT).show();
//                            Intent intent1 = new Intent(WinningBidListActivity.this, ArtInfoTagList.class) ;
//                            intent1.putExtra("userID", userID);
//                            startActivity(intent1);
//                            finish();
                    }).setNegativeButton("계약서 읽기", null);
                    final AlertDialog alertdialog = alert.create();
                    alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button b = alertdialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                            b.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        ttsGreater21(getString(R.string.ct_msg).toString());
                                    } else {
                                        ttsUnder20(getString(R.string.ct_msg).toString());
                                    }
                                }
                            });
                        }
                    });
                    alertdialog.setCancelable(false);
                    alertdialog.show();
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alert2 = new AlertDialog.Builder(WinningBidListActivity.this);
                    alert2.setMessage("거부하시면 가계약금을 받을 수 없습니다.\n그래도 거부하시겠습니까?").setCancelable(false).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BiddingWinUserCancel(auckey);
                            Toast.makeText(WinningBidListActivity.this, "판매를 취소하였습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alert2.setCancelable(false);
                    alert2.show();
                }
            });
        }
        try {

            String albumlist = BiddingList(userID, auckey);
            JSONArray ja = new JSONArray(albumlist);
            Log.d("auctime", albumlist);

            for (int i = 0; i < ja.length(); i++) {

                JSONObject job = (JSONObject) ja.get(i);
                bidprice = job.get("bid_price").toString();
                bidcontime = job.get("bid_con_time").toString();
                adapter.addItem(bidprice, bidcontime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        listView.setAdapter(adapter);

    }



    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
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
            final byte[] tagId = tag.getId();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intent0 = getIntent();
                    String userID = intent0.getStringExtra("userID");
                    Intent intent = new Intent(WinningBidListActivity.this, ArtInformation.class);
                    intent.putExtra("artinfo",ArtInfo(toHexString(tagId)));
                    intent.putExtra("userID",userID);
                    //Log.d("tag",ArtInfo(toHexString(tagId)));
                    startActivity(intent);
                    finish();
                }
            }).start();

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
    public void onPurchased5(boolean withFingerprint,
                             @Nullable FingerprintManager.CryptoObject cryptoObject) {
        if (withFingerprint) {
            // If the user has authenticated with fingerprint, verify that using cryptography and
            // then show the confirmation message.
                            tts.stop();
                            tts.shutdown();
                            BiddingWinUserAgree(auckey);

                            Toast.makeText(WinningBidListActivity.this, "거래완료", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(WinningBidListActivity.this, ArtInfoTagList.class) ;
                            intent1.putExtra("userID", userID);
                            startActivity(intent1);
                            finish();
            assert cryptoObject != null;
            //tryEncrypt(cryptoObject.getCipher());

            //여기는 아마 지문 비밀번호 입력 시 나타나는창
        } else {
            // Authentication happened with backup password. Just show the confirmation message.
            tts.stop();
            tts.shutdown();
//            BiddingWinUserAgree(auckey);

            Toast.makeText(WinningBidListActivity.this, "임시거래완료", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(WinningBidListActivity.this, ArtInfoTagList.class) ;
            intent1.putExtra("userID", userID);
            startActivity(intent1);
            finish();
        }
    }
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
    private class PurchaseButtonClickListener implements DialogInterface.OnClickListener {

        final Cipher mCipher;
        String mKeyName;

        PurchaseButtonClickListener(final Cipher cipher, String keyName) {
            mCipher = cipher;
            mKeyName = keyName;
        }



        @Override
        public void onClick(DialogInterface dialog, int which) {
            findViewById(R.id.confirmation_message5).setVisibility(View.GONE);
            findViewById(R.id.encrypted_message5).setVisibility(View.GONE);

            // Set up the crypto object for later. The object will be authenticated by use
            // of the fingerprint.
            if (initCipher(mCipher, mKeyName)) {

                // Show the fingerprint dialog. The user has the option to use the fingerprint with
                // crypto, or you can fall back to using a server-side verified password.
                FingerprintAuthenticationDialogFragment5 fragment
                        = new FingerprintAuthenticationDialogFragment5();
                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                boolean useFingerprintPreference = mSharedPreferences
                        .getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                                true);
                if (useFingerprintPreference) {
                    fragment.setStage(
                            FingerprintAuthenticationDialogFragment5.Stage.FINGERPRINT);
                } else {
                    fragment.setStage(
                            FingerprintAuthenticationDialogFragment5.Stage.PASSWORD);
                }
                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            } else {
                // This happens if the lock screen has been disabled or or a fingerprint got
                // enrolled. Thus show the dialog to authenticate with their password first
                // and ask the user if they want to authenticate with fingerprints in the
                // future
                FingerprintAuthenticationDialogFragment5 fragment
                        = new FingerprintAuthenticationDialogFragment5();
                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                fragment.setStage(
                        FingerprintAuthenticationDialogFragment5.Stage.NEW_FINGERPRINT_ENROLLED);
                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        }
    }
    }
