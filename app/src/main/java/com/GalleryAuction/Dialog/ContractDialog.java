package com.GalleryAuction.Dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
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
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.GalleryAuction.Client.FingerprintAuthenticationDialogFragment5;
import com.GalleryAuction.UI.ArtistAuctionCompleteUi;
import com.GalleryAuction.UI.ArtistMainActivity;
import com.GalleryAuction.UI.BidderMainActivity;
import com.geno.payment.R;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static com.GalleryAuction.Item.HttpClientItem.BiddingWin;
import static com.GalleryAuction.Item.HttpClientItem.contractor;

public class ContractDialog extends Activity {
    WebView webView;
    Button X_btn, Agree_btn, Read_btn;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    LinearLayout linearLayout;
    TextToSpeech tts;
    String artistID, buyerID, buyerhp, buyername, artisthp, artistname, title, auckey, contract;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gallery_contract_dialog);
        Intent intent1 = getIntent();
        artistID = intent1.getStringExtra("artistID");
        buyerID = intent1.getStringExtra("buyerID");
        buyerhp = intent1.getStringExtra("buyerhp");
        buyername = intent1.getStringExtra("buyername");
        artisthp = intent1.getStringExtra("artisthp");
        artistname = intent1.getStringExtra("artistname");
        title = intent1.getStringExtra("title");
        auckey = intent1.getStringExtra("auckey");

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
            Agree_btn.setEnabled(false);
            purchaseButtonNotInvalidated.setEnabled(false);
            return;
        }

        // Now the protection level of USE_FINGERPRINT permission is normal instead of dangerous.
        // See http://developer.android.com/reference/android/Manifest.permission.html#USE_FINGERPRINT
        // The line below prevents the false positive inspection from Android Studio
        // noinspection ResourceType
        if (!fingerprintManager.hasEnrolledFingerprints()) {
            Agree_btn.setEnabled(false);
            // This happens when no fingerprints are registered.
            Toast.makeText(this,
                    "Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint",
                    Toast.LENGTH_LONG).show();
            return;
        }
        createKey(DEFAULT_KEY_NAME, true);
        createKey(KEY_NAME_NOT_INVALIDATED, false);


        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });


        X_btn = (Button)findViewById(R.id.Contract_Dialog_X_Btx);
        Agree_btn = (Button)findViewById(R.id.Contract_Dialog_Agree_Btn);
        Read_btn = (Button)findViewById(R.id.Contract_Dialog_Reda_Btn);
        webView = (WebView)findViewById(R.id.gallery_contract_dialog_WebView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        //---you need this to prevent the webview from
        // launching another browser when a url
        // redirection occurs---
        webView.setWebViewClient(new Callback());
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        String pdfurl = "http://183.105.72.65:28989/NFCTEST/contractor/"+buyerID+"-"+artistID+"("+title+").pdf";
        webView.loadUrl(
                "http://docs.google.com/gview?embedded=true&url=" + pdfurl);
        Display display = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = (int) (display.getWidth() * 0.9);
        int height = (int) (display.getHeight() * 0.9);
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent2 = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent2, 0);
        X_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.stop();
                tts.shutdown();
                finish();
            }
        });
        Agree_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.stop();
                tts.shutdown();
            }
        });
        Read_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date dt = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
                contract = "갤러리 옥션 계약서." +
                        "본 계약은 구매자 "+buyername+"이하 갑이라 칭함.과 판매자 "+artistname+" 이하 을이라 칭함. 간의 미술품경매 운영기본과 경매참가자의 자격 및 준수 의무를 정하는 것이다." +
                        "제 1 장  총   칙." +
                        "제 1 조 계약의 목적." +
                        "본 계약은 경매를 통한 미술품의 거래에 있어서 미술품유통시장의 활성화 및 갑과 을간의 원활하고 공정한 거래가 진행되도록 하는 것을 목적으로 한다." +
                        "제 2 조 적 용." +
                        "1본 계약은 을이 개최하는 전시회에 미술품을 구매하고자 하는 모든 갑에게 적용된다." +
                        "2을은 회원가입 시 본 계약에 대하여 사전 고지하여야 하며, 갑은 이 계약을 준수하여야 한다.  " +
                        "제 3 조 입찰 참가자격." +
                        "입찰에 참가할 수 있는 자격은 다음의 각호에 해당하는 자이어야 한다." +
                        "1을의 입찰회원으로 등록한 자." +
                        "제 4 조 입찰 방식." +
                        "1갑 입찰은 갤러리옥션 앱을 통한 입찰방식으로 행한다." +
                        "2입찰방식은 1항 이외에 을이 정하는 기타의 방법으로 입찰을 진행할 수 있다." +
                        "3입찰의 개최 일시는 을이 정하며, 사전에 갑에게 고지한다." +
                        "제 5 조 면 책." +
                        "입찰진행 중에 전산이상 및 천재지변 등 불가피한 사유로 인해 입찰이 정상적으로 진행되지" +
                        "못한 경우, 이로 인한 제반 손해에 대해 을은 책임을 지지 않는다." +
                        "제 2 장  회   원.  " +
                        "제 6 조 회원 정의.  " +
                        "회원이라 함은 을이 개최하는 입찰에 참여하는 자로서 입찰회원으로 등록하고, 입찰보증금을 납부한 갑을 칭한다. " +
                        "제 7 조 회원 등록." +
                        "1을은 회원에 가입하려는 자 또는 미술품을 구매하려고 하는자를 회원으로 등록한다." +
                        "제 8 조 보증금." +
                        "1갑의 회원이 되고자 하는 자는 성실한 계약이행을 위하여 보증금을 납부하여야 하나, 베타버전 테스트시는 보증금이 없는 것으로 한다." +
                        "2갑이 자의로 탈퇴하거나 계약이 해지 또는 해제될 경우 을은 회원으로부터 받은 보증금을 반환하여야 하며, 이자는 정산하지 않는다. 단, 갑에게 채무가 있을 경우에는 채무를 우선적으로 정산한 후 잔액을 반환한다." +
                        "제 9 조 회원의 유효기간." +
                        "1을의 유효기간은 원칙적으로 1년을 기준으로 한다." +
                        "2갑 및 을의 이의가 없는 한 유효기간은 자동적으로 연장된다." +
                        "제 10 조 회원의 권리." +
                        "갑은 을이 주최하는 모든 경매에 입찰할 수 있다." +
                        "제 11 조 회원의 권리 제한." +
                        "을은 갑과의 거래에서 발생하는 미술품대금 지불지연이나 입찰 제반사항 위반 시 경매참가를 제한할 수 있다." +
                        "제 12 조 회원의 의무." +
                        "갑은 본 계약을 준수할 것을 승인하며 입회한 것이므로 본 계약을 준수해야 하며, 을에" +
                        "서 개최하는 입찰의 원활한 운영을 위하여 제정한 제 규정 및 지시를 준수해야 한다." +
                        "제 13 조 회원의 금지행위." +
                        "갑은 아래 각호에 정하는 행위에 대하여 금지한다." +
                        " 1명의를 임대하여 입찰하는 행위." +
                        " 2의도적으로 입찰금액을 도모하거나, 타인의 응찰을 방해하는 행위." +
                        " 3전시장 내에서의 고성방가, 폭언, 폭행 등 질서를 어지럽히는 행위 및 기물을 파손하는 행위." +
                        " 4기타 을이 금지하는 행위." +
                        "제 14 조 회원 제재조치." +
                        " 을이 본 규약 아래와 같은 행위 또는 기타 제 규정을 위반할 경우 갑은 그 위반의 정도에 따라, 입찰참여를 제한할 수 있다." +
                        "1타 회원의 입찰을 방해하는 행위." +
                        "2을의 지시나 안내를 따르지 않는 행위." +
                        "3기타 입찰의 원활한 흐름을 방해하는 행위." +
                        "4을과 거래에서 발생하는 거래대금 등의 납부를 지연 하는 경우." +
                        "5낙찰시 미술품 인수 기한을 3회 이상 지연하는 경우." +
                        "6전시장 내에서 폭행 및 폭언, 기물 파손 등으로 경매의 원활한 운영을 방해하는 행위." +
                        "7갤러리옥션 프로그램의 대여 및 위조작하는 행위." +
                        "8의도적으로 입찰금액을 도모하거나, 타인의 입찰을 방해하는 행위." +
                        "9기타 경매의 원활한 운영을 방해하는 심각한 위반행위." +
                        "제 15 조 가입해지." +
                        "갑은 항시 경매참가계약을 해지하는 것이 가능하다. 단 해지 예정일 1개월 전에 을에 통지하여야 한다." +
                        "제 3 장 낙찰규정." +
                        "제 16 조 낙 찰." +
                        "1갑은 낙찰희망미술품에 대하여 입찰 전 출품리스트 및 경매미술품을 충분히 보고 검토해야 한다." +
                        "2갑은 낙찰확정일로부터 3일 이내에 낙찰대금을 결제하여야 하며, 대금결제 전에는 미술품를 인수할 수 없다." +
                        "3낙찰미술품에 대한 소유권은 대금결제순간부터 이전된다." +
                        "4갑은 낙찰확정일 익일부터 발생하는 소유권과 관련한 제반 비용에 대한 책임을 져야 한다." +
                        "제 17 조 낙찰 취.소" +
                        "1갑은 낙찰 후 7일 이내 낙찰미술품에 대해 사전에 고지되지 않은 중대과실이 발견 시 낙찰취소를 요구할 수 있으며, 그 외의 사유로는 낙찰취소를 요구할 수 없다. " +
                        "2갑이 상기 1항 외 다른 사유로 낙찰취소를 할 경우, 그에 따른 손해배상을 을에게 지불하여야 하며 손해배상이 이행되지 않을 경우 을은 갑의 입찰보증금 전액을 손해배상의 예정액으로 할 수 있으며, 만일 손해배상액이 입찰보증금을 초과하는 경우에는 추가로 청구할 수 있다." +
                        "3전항에 의해 입찰보증금이 소진된 경우 재입찰을 위하여는 입찰보증금을 충당하여야 한다. 만약 입찰보증금이 7일 이내 충당되지 않은 경우 본 업무제휴 계약은 종료된 것으로 본다." +
                        "제 4 장 대금결제 및 이전등록." +
                        "제 18 조 대금 입금." +
                        "1갑은 낙찰확정일부터 3일 이내에 미술품 대금 100%를 을에게 입금한다. 입금기간 내" +
                        "미술품 대금을 납부하지 않을 경우 을은 낙찰취소는 물론 낙찰대금 불이행에 따른 손" +
                        "해 배상을 청구할 수 있다." +
                        "2을은 대금입금과 동시에 미술품 및 관련 제반 서류를 5일 이내에 갑에게 인도 하여야 " +
                        "한다.  을이 사전에 제반 서류의 지연을 통보한 경우에는 인도기간을 협의 하에 조정할" +
                        "수 있다." +
                        "3미술품배송은 갑과 을의 상호 합의하에 결정하되, 소요경비는 갑이 부담한다." +
                        "제 19 조 계산서 발행 및 소유권 이전."+
                        "1세금계산서 발행은 계약일을 기준으로 작성한다." +
                        "제 20 조 미술품 검수 및 하자담보책임." +
                        "1경매미술품 입찰 및 인도, 인수 시 갑과 을은 미술품 상태를 철저히 점검하여야 한다." +
                        "2갑은 미술품을 인수한 후에는 단순변심 등의 사유로 을에게 그 책임을 물을 수 없다. " +
                        "제 5 장 기   타." +
                        "제 21 조 계약의 개정." +
                        "1본 계약 및 관련규정은 을이 필요한 경우 개정 할 수 있으며, 을은 계약의 개정 시 개정내용 및 효력발생일을 갑에게 고지하여야 한다." +
                        "2고지의 방법은 서면 또는 을이 정한 소정의 장소에 게시하는 것으로 한다." +
                        "제 22 조 사고 책임." +
                        "경매참가자는 을로부터 미술품을 인수한 후에 발생하는 각종 사고에 대하여 모든 민." +
                        "형사상의 책임을 지며, 여기에는 낙찰 후 미술품인수 배송사고 발생을 포함한다." +
                        "제 23 조 손해 배상." +
                        "갑과 을은 본 계약을 위반하거나 기타 불법행위로 상대방에게 손해를 발생시킨 경우 상" +
                        "대방이 제시/증명하는 손해를 배상하여야 한다." +
                        "제 24 조 권리/ 의무/ 양도 등의 제한." +
                        "을은 본 계약과 관련하여 발생하는 제반 권리와 의무를 갑의 사전 서면 동의 없이 제 3자에게 전부 또는 일부를 양도, 담보제공, 하도급을 주어서는 아니 된다." +
                        "제 25 조 관할 법원." +
                        "본 계약과 관련하여 분쟁이 발생하는 경우, 양 당사자는 우호적인 협의를 통하여 분쟁을" +
                        "해결하도록 노력하여야 하며, 협의가 결렬될 경우 갑과 을의 주소지의 지방법원을 분쟁의 관할법원으로 한다." +
                        "제 26 조 분쟁 해결." +
                        "본 계약에 명시되지 아니한 사항 및 본 계약의 해석상의 이의가 있는 경우에는 양 당사자가" +
                        "협의하여 결정한다. 단, 협의가 이루어지지 않을 시에는 관련법령 및 일반 상관례에 따른다." +
                        "제 27 조 부칙." +
                        "갑 과 을은 신의를 가지고 본 계약의 각 조항을 성실히 이행하며, 본 계약의 성실한 이행을 보증하기 위하여 계약서를 2부 작성하고 갑과 을이 기명 날인하여 각각 1부씩 보관한다." +
                        "본 계약서는 기명 날인한 날로부터 그 효력을 발생한다."+
                        sdf.format(dt).toString() +
                        "갑" +
                        "연락처: " + buyerhp +"" +
                        "성명: " + buyername + " 인" +
                        "을" +
                        "연락처: " + artisthp +
                        "성명: "+artistname + "인";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ttsGreater21(contract);
                } else {
                    ttsUnder20(contract);
                }
            }
        });
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
            Toast.makeText(this, "창을 닫아주세요", Toast.LENGTH_SHORT).show();
        }
        Log.d("TAGTEST : ", ""+ tag);
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return (false);
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
            BiddingWin(auckey);


            contractor("artist : ");
            Toast.makeText(ContractDialog.this, "거래완료", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(ContractDialog.this, ArtistMainActivity.class) ;
            intent1.putExtra("artistID", artistID);
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

            Toast.makeText(ContractDialog.this, "지문인증이 필요합니다.", Toast.LENGTH_SHORT).show();
            Intent intent1 = new Intent(ContractDialog.this, ArtistMainActivity.class) ;
            intent1.putExtra("artistID", artistID);
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
}
