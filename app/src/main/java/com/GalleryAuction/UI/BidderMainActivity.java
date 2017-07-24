package com.GalleryAuction.UI;

import android.annotation.TargetApi;
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
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.GalleryAuction.Adapter.BidderGoingAdapter;
import com.GalleryAuction.Adapter.GridAdapter;
import com.GalleryAuction.Client.FingerprintAuthenticationDialogFragment4;
import com.GalleryAuction.Dialog.TagExplanationDialog;
import com.GalleryAuction.Item.BidderGoingItem;
import com.GalleryAuction.Item.GridViewItem;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static com.GalleryAuction.Client.ImageViewItem.getImageBitmap;
import static com.GalleryAuction.Client.TagInfoClient.toHexString;
import static com.GalleryAuction.Item.HttpClientItem.ArtAlbumSelect;
import static com.GalleryAuction.Item.HttpClientItem.ArtInfo;
import static com.GalleryAuction.Item.HttpClientItem.AuctionProgress;
import static com.GalleryAuction.Item.HttpClientItem.BiddingWinUserAgree;

public class BidderMainActivity extends AppCompatActivity implements View.OnClickListener {
    BidderGoingAdapter bidderGoingAdapter;
    LinearLayout SeeLayout, WinningBidLayout;
    Button SeeBtn, WinningBidBtn;
    String name, title, auc_end, auc_start, art_content, userID, image, bidstatus, auckey, aucstatus, artistname, bidprice, bidkey;
    String[] start, end, start_hhmm, end_hhmm;
    GridViewItem gridView, gridView_Winning, listView;
    GridAdapter gridAdapter, gridAdapter_Winning;
    String imgUrl = "http://221.156.54.210:8989/NFCTEST/art_images/";
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
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
        setContentView(R.layout.gallery_biddermain_activity);
        SeeBtn = (Button) findViewById(R.id.Bidder_Main_See_List_btn);
        SeeLayout = (LinearLayout) findViewById(R.id.Bidder_Main_See_List_layout);
        gridView = (GridViewItem) findViewById(R.id.Bidder_Main_See_List_gridview);
        gridView.setExpanded(true);
        gridAdapter = new GridAdapter();
        SeeBtn.setOnClickListener(this);

        listView = (GridViewItem) findViewById(R.id.bidder_going_listview);
        bidderGoingAdapter = new BidderGoingAdapter();
        listView.setExpanded(false);
        listView.setAdapter(bidderGoingAdapter);

        WinningBidBtn = (Button) findViewById(R.id.Bidder_Main_WinningBid_btn);
        WinningBidLayout = (LinearLayout) findViewById(R.id.Bidder_Main_WinningBid_Layout);
        gridView_Winning = (GridViewItem) findViewById(R.id.Bidder_Main_WinningBid_GridView);
        gridView_Winning.setExpanded(true);
        gridAdapter_Winning = new GridAdapter();
        WinningBidBtn.setOnClickListener(this);
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
//            purchaseButtonNotInvalidated.setEnabled(true);
//            purchaseButtonNotInvalidated.setOnClickListener(
//                    (View.OnClickListener) new PurchaseButtonClickListener(cipherNotInvalidated,
//                            KEY_NAME_NOT_INVALIDATED));
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
            gridView_Winning.setEnabled(false);
            purchaseButtonNotInvalidated.setEnabled(false);
            return;
        }

        // Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
        // See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            // This happens when no fingerprints are registered.
            Toast.makeText(this,
                    "Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint",
                    Toast.LENGTH_LONG).show();
            return;
        }
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        createKey(DEFAULT_KEY_NAME, true);
        createKey(KEY_NAME_NOT_INVALIDATED, false);
        Intent intent = new Intent(BidderMainActivity.this, TagExplanationDialog.class);
        startActivity(intent);
        Intent intent2 = getIntent();
        userID = intent2.getStringExtra("userID");

        try {
            JSONArray ja = new JSONArray(AuctionProgress("", "art"));

            for (int i = 0; i < ja.length(); i++) {

                JSONObject job = (JSONObject) ja.get(i);
                title = job.get("art_title").toString();
                name = job.get("USER_NAME").toString();
                auc_start = job.get("auc_start").toString();
                auc_end = job.get("auc_end").toString();
                art_content = job.get("ART_CONTENT").toString();
                start = auc_start.split(" ");
                start_hhmm = start[1].split(":");

                end = auc_end.split(" ");
                end_hhmm = end[1].split(":");

                bidderGoingAdapter.addItem(title, name, "진행시간 : " + start_hhmm[0] + "시" + start_hhmm[1] + "분" + " - " + end_hhmm[0] + "시" + end_hhmm[1] + "분");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            JSONArray ja = new JSONArray(ArtAlbumSelect(userID));

            for (int i = 0; i < ja.length(); i++) {

                JSONObject job = (JSONObject) ja.get(i);
                title = job.get("art_title").toString();
                image = job.get("art_image").toString();
                bidstatus = job.get("bid_status").toString();
                gridAdapter.addItem(title, getImageBitmap(imgUrl + image));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            JSONArray ja = new JSONArray(AuctionProgress(userID, "bid_win"));

            for (int i = 0; i < ja.length(); i++) {

                JSONObject job = (JSONObject) ja.get(i);
                title = job.get("art_title").toString();
                image = job.get("art_image").toString();
                auckey = job.get("auc_seq").toString();
                aucstatus = job.get("auc_status").toString();
                gridAdapter_Winning.addItem(title, getImageBitmap(imgUrl + image));


        }

    } catch (JSONException e) {
        e.printStackTrace();
    }
        gridView.setAdapter(gridAdapter);
        gridView_Winning.setAdapter(gridAdapter_Winning);
        gridView_Winning.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    JSONArray ja = new JSONArray(AuctionProgress(userID, "bid_win"));


                        JSONObject job = (JSONObject) ja.get(i);
                        title = job.get("art_title").toString();
                        image = job.get("art_image").toString();
                        auckey = job.get("auc_seq").toString();
                        aucstatus = job.get("auc_status").toString();
                        artistname = job.get("USER_NAME").toString();
                    bidprice = job.get("bid_price").toString();
                    bidkey = job.get("bid_seq").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                    if (aucstatus.equals("5")) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(BidderMainActivity.this);
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
                    } else if (aucstatus.equals("6")) {
                        Intent intent1 = new Intent(BidderMainActivity.this, BidderAddress.class);
                        intent1.putExtra("title", title);
                        intent1.putExtra("image", image);
                        intent1.putExtra("auckey", auckey);
                        intent1.putExtra("aucstatus", aucstatus);
                        intent1.putExtra("artistname", artistname);
                        intent1.putExtra("bidprice", bidprice);
                        intent1.putExtra("userID", userID);
                        intent1.putExtra("bidkey",bidkey);
                        startActivity(intent1);

                    } else if (aucstatus.equals("7")) {
                        Toast.makeText(BidderMainActivity.this, "거래 완료", Toast.LENGTH_SHORT).show();
                    }

//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent3 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent3, 0);
    }

    @Override
    protected void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            final byte[] tagId = tag.getId();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Intent intent0 = getIntent();
                    String userID = intent0.getStringExtra("userID");
                    Intent intent = new Intent(BidderMainActivity.this, ArtInformation.class);
                    intent.putExtra("artinfo", ArtInfo(toHexString(tagId)));
                    intent.putExtra("userID", userID);
                    intent.putExtra("name", name);


                    //Log.d("tag",ArtInfo(toHexString(tagId)));
                    startActivity(intent);
                    finish();
                }
            }).start();

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Bidder_Main_See_List_btn:
                if (SeeLayout.getVisibility() == View.VISIBLE) {
                    SeeLayout.setVisibility(View.GONE);
                } else {
                    SeeLayout.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.Bidder_Main_WinningBid_btn:
                if (WinningBidLayout.getVisibility() == View.VISIBLE) {
                    WinningBidLayout.setVisibility(View.GONE);
                } else {
                    WinningBidLayout.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
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

            Intent intent1 = new Intent(BidderMainActivity.this, BidderAddress.class);
            intent1.putExtra("title", title);
            intent1.putExtra("image", image);
            intent1.putExtra("auckey", auckey);
            intent1.putExtra("aucstatus", aucstatus);
            intent1.putExtra("artistname", artistname);
            intent1.putExtra("bidprice", bidprice);
            intent1.putExtra("userID", userID);
            intent1.putExtra("bidstatus",bidkey);
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

            Toast.makeText(BidderMainActivity.this, "임시거래완료", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(BidderMainActivity.this, BidderMainActivity.class);
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
                FingerprintAuthenticationDialogFragment4 fragment
                        = new FingerprintAuthenticationDialogFragment4();
                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                boolean useFingerprintPreference = mSharedPreferences
                        .getBoolean(getString(R.string.use_fingerprint_to_authenticate_key),
                                true);
                if (useFingerprintPreference) {
                    fragment.setStage(
                            FingerprintAuthenticationDialogFragment4.Stage.FINGERPRINT);
                } else {
                    fragment.setStage(
                            FingerprintAuthenticationDialogFragment4.Stage.PASSWORD);
                }
                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            } else {
                // This happens if the lock screen has been disabled or or a fingerprint got
                // enrolled. Thus show the dialog to authenticate with their password first
                // and ask the user if they want to authenticate with fingerprints in the
                // future
                FingerprintAuthenticationDialogFragment4 fragment
                        = new FingerprintAuthenticationDialogFragment4();
                fragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                fragment.setStage(
                        FingerprintAuthenticationDialogFragment4.Stage.NEW_FINGERPRINT_ENROLLED);
                fragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        }
    }
}
