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
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.GalleryAuction.Adapter.BidderGoingAdapter;
import com.GalleryAuction.Adapter.GridAdapter;
import com.GalleryAuction.Client.FingerprintAuthenticationDialogFragment4;
import com.GalleryAuction.Dialog.ContractDialog;
import com.GalleryAuction.Dialog.TagExplanationDialog;
import com.GalleryAuction.Item.GridViewItem;
import com.geno.payment.R;

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
import static com.GalleryAuction.Item.HttpClientItem.AuctionProgress;
import static com.GalleryAuction.Item.HttpClientItem.contractor;

public class BidderMainActivity extends AppCompatActivity implements View.OnClickListener {
    BidderGoingAdapter bidderGoingAdapter;
    LinearLayout SeeLayout, WinningBidLayout;
    Button SeeBtn, WinningBidBtn;
    TextView ArtistNamePhone_txt;
    String name, title, auc_end, auc_start, art_content, userID, image, bidstatus, auckey, aucstatus, artistname, bidprice, bidkey, tagid, artistphone,biddername,bidderhp;
    String[] start, end, start_hhmm, end_hhmm;
    GridViewItem gridView, gridView_Winning, listView;
    GridAdapter gridAdapter, gridAdapter_Winning;
    String imgUrl = "http://183.105.72.65:28989/NFCTEST/art_images/";
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    TextToSpeech tts;
    String contract = "";
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
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

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
        ArtistNamePhone_txt = (TextView) findViewById(R.id.Bidder_Main_WinningBid_ArtistNamePhone_txt);
        gridView_Winning.setExpanded(true);
        gridAdapter_Winning = new GridAdapter();
        WinningBidBtn.setOnClickListener(this);
        ArtistNamePhone_txt.setText("");
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
                Log.d("@@@@@@", name);

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
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {

                    JSONArray ja = new JSONArray(ArtAlbumSelect(userID));

                        JSONObject job = (JSONObject) ja.get(i);
                        tagid = job.get("nfc_id").toString();
                        image = job.get("art_image").toString();
                        bidstatus = job.get("bid_status").toString();
                        name = job.get("artist_name").toString();
                    Intent intent1 = new Intent(BidderMainActivity.this, ArtInformation.class);
                    intent1.putExtra("tagid", tagid);
                    intent1.putExtra("userID", userID);
                    intent1.putExtra("name", name);
                    startActivity(intent1);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
                    artistname = job.get("artist_name").toString();
                    biddername = job.get("bidder_name").toString();
                    bidderhp = job.get("bidder_hp").toString();
                        bidprice = job.get("bid_price").toString();
                        bidkey = job.get("bid_seq").toString();
                        artistphone = job.get("artist_hp").toString();
                    Date dt = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
                    contract = "<갤러리 옥션 계약서>" +
                            "\n" +
                            "본 계약은 구매자 "+biddername+"(이하 “갑”이라 칭함)과(와) 판매자 "+artistname+" (이하 “을”이라 칭함) 간의 미술품경매 운영기본과 “경매참가자”의 자격 및 준수 의무를 정하는 것이다.\n" +
                            "\n" +
                            "\n" +
                            "제 1 장  총   칙\n" +
                            "\n" +
                            "제 1 조 [계약의 목적]\n" +
                            "\n" +
                            "본 계약은 경매를 통한 미술품의 거래에 있어서 미술품유통시장의 활성화 및 “갑”과 “을”간의 원활하고 공정한 거래가 진행되도록 하는 것을 목적으로 한다.\n" +
                            "\n" +
                            "제 2 조 [적 용]\n" +
                            "\n" +
                            "○1본 계약은 “을”이 개최하는 전시회에 미술품을 구매하고자 하는 모든 “갑”에게 적용된다.\n" +
                            "○2”을”은 회원가입 시 본 계약에 대하여 사전 고지하여야 하며, “갑”은 이 계약을 준수하여야 한다.\n" +
                            "\n" +
                            "제 3 조 [입찰 참가자격]\n" +
                            "\n" +
                            "입찰에 참가할 수 있는 자격은 다음의 각호에 해당하는 자이어야 한다.\n" +
                            "○1“을”의 입찰회원으로 등록한 자.\n" +
                            "\n" +
                            "제 4 조 [입찰 방식]\n" +
                            "\n" +
                            "○1”갑” 입찰은 갤러리옥션 앱(App)을 통한 입찰방식으로 행한다.\n" +
                            "○2입찰방식은 1항 이외에 “을”이 정하는 기타의 방법으로 입찰을 진행할 수 있다.\n" +
                            "○3입찰의 개최 일시는 “을”이 정하며, 사전에 “갑”에게 고지한다.\n" +
                            "\n" +
                            "제 5 조 [면 책]\n" +
                            "\n" +
                            "입찰진행 중에 전산이상 및 천재지변 등 불가피한 사유로 인해 입찰이 정상적으로 진행되지\n" +
                            "못한 경우, 이로 인한 제반 손해에 대해 “을”은 책임을 지지 않는다.\n" +
                            "\n" +
                            "\n" +
                            "제 2 장  회   원\n" +
                            "\n" +
                            "제 6 조 [회원 정의]n" +
                            "\n" +
                            "회원이라 함은 “을”이 개최하는 입찰에 참여하는 자로서 입찰회원으로 등록하고, 입찰보증금을 납부한 “갑”을 칭한다.\n" +
                            "\n" +
                            "제 7 조 [B회원 등록]\n" +
                            "\n" +
                            "○1”을”은 회원에 가입하려는 자(또는 미술품을 구매하려고 하는자)를 회원으로 등록한다.\n" +
                            "\n" +
                            "제 8 조 [보증금]\n" +
                            "\n" +
                            "○1”갑”의 회원이 되고자 하는 자는 성실한 계약이행을 위하여 보증금을 납부하여야 하나, 베타버전 테스트시는 보증금이 없는 것으로 한다.\n" +
                            "○2”갑”이 자의로 탈퇴하거나 계약이 해지 또는 해제될 경우 “을”은 회원으로부터 받은 보증금을 반환하여야 하며, 이자는 정산하지 않는다. 단, “갑”에게 채무가 있을 경우에는 채무를 우선적으로 정산한 후 잔액을 반환한다.\n" +
                            "\n" +
                            "제 9 조 [회원의 유효기간]\n" +
                            "\n" +
                            "○1”을”의 유효기간은 원칙적으로 1년을 기준으로 한다.\n" +
                            "○2”갑” 및 “을”의 이의가 없는 한 유효기간은 자동적으로 연장된다.\n" +
                            "\n" +
                            "제 10 조 [회원의 권리]\n" +
                            "\n" +
                            "“갑”은 “을”이 주최하는 모든 경매에 입찰할 수 있다.\n" +
                            "\n" +
                            "제 11 조 [회원의 권리 제한]\n" +
                            "\n" +
                            "“을”은 “갑”과의 거래에서 발생하는 미술품대금 지불지연이나 입찰 제반사항 위반 시 경매참가를 제한할 수 있다.\n" +
                            "\n" +
                            "제 12 조 [회원의 의무]\n" +
                            "\n" +
                            "“갑”은 본 계약을 준수할 것을 승인하며 입회한 것이므로 본 계약을 준수해야 하며, “을”에\n" +
                            "서 개최하는 입찰의 원활한 운영을 위하여 제정한 제 규정 및 지시를 준수해야 한다.\n" +
                            "\n" +
                            "제 13 조 [회원의 금지행위]\n" +
                            "\n" +
                            "“갑”은 아래 각호에 정하는 행위에 대하여 금지한다.\n" +
                            "\n" +
                            " ○1명의를 임대하여 입찰하는 행위\n" +
                            " ○2의도적으로 입찰금액을 도모하거나, 타인의 응찰을 방해하는 행위\n" +
                            " ○3전시장 내에서의 고성방가, 폭언, 폭행 등 질서를 어지럽히는 행위 및 기물을 파손하는 행위\n" +
                            " ○4기타 “을”이 금지하는 행위\n" +
                            "\n" +
                            "제 14 조 [회원 제재조치]\n" +
                            "\n" +
                            " “을”이 본 규약 아래와 같은 행위 또는 기타 제 규정을 위반할 경우 “갑”은 그 위반의 정도에 따라, 입찰참여를 제한할 수 있다.\n" +
                            "\n" +
                            "○1타 회원의 입찰을 방해하는 행위\n" +
                            "○2”을”의 지시나 안내를 따르지 않는 행위\n" +
                            "○3기타 입찰의 원활한 흐름을 방해하는 행위\n" +
                            "○4”을”과 거래에서 발생하는 거래대금 등의 납부를 지연 하는 경우\n" +
                            "⑤낙찰시 미술품 인수 기한을 3회 이상 지연하는 경우\n" +
                            "⑥전시장 내에서 폭행 및 폭언, 기물 파손 등으로 경매의 원활한 운영을 방해하는 행위\n" +
                            "⑦갤러리옥션 프로그램의 대여 및 위조작하는 행위\n" +
                            "⑧의도적으로 입찰금액을 도모하거나, 타인의 입찰을 방해하는 행위\n" +
                            "⑨기타 경매의 원활한 운영을 방해하는 심각한 위반행위\n" +
                            "\n" +
                            "제 15 조 [가입해지]\n" +
                            "\n" +
                            "“갑”은 항시 경매참가계약을 해지하는 것이 가능하다. 단 해지 예정일 1개월 전에 “을”에 통지하여야 한다.\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t제 3 장 낙찰규정\n" +
                            "\n" +
                            "제 16 조 [낙 찰]\n" +
                            "\n" +
                            "○1”갑”은 낙찰희망미술품에 대하여 입찰 전 출품리스트 및 경매미술품을 충분히 보고 검토해야 한다.\n" +
                            "○2”갑”은 낙찰확정일로부터 3일 이내에 낙찰대금을 결제하여야 하며, 대금결제 전에는 미술품를 인수할 수 없다.\n" +
                            "○3낙찰미술품에 대한 소유권은 대금결제순간부터 이전된다.\n" +
                            "○4”갑”은 낙찰확정일 익일부터 발생하는 소유권과 관련한 제반 비용에 대한 책임을 져야 한다.\n" +
                            "\n" +
                            "\n" +
                            "제 17 조 [낙찰 취소]\n" +
                            "\n" +
                            "○1”갑”은 낙찰 후 7일 이내 낙찰미술품에 대해 사전에 고지되지 않은 중대과실이 발견 시 낙찰취소를 요구할 수 있으며, 그 외의 사유로는 낙찰취소를 요구할 수 없다. \n" +
                            "○2”갑”이 상기 ○1항 외 다른 사유로 낙찰취소를 할 경우, 그에 따른 손해배상을 “을”에게 지불하여야 하며 손해배상이 이행되지 않을 경우 “을”은 “갑”의 입찰보증금 전액을 손해배상의 예정액으로 할 수 있으며, 만일 손해배상액이 입찰보증금을 초과하는 경우에는 추가로 청구할 수 있다.\n" +
                            "○3전항에 의해 입찰보증금이 소진된 경우 재입찰을 위하여는 입찰보증금을 충당하여야 한다. 만약 입찰보증금이 7일 이내 충당되지 않은 경우 본 업무제휴 계약은 종료된 것으로 본다.\n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t제 4 장 대금결제 및 이전등록\n" +
                            "\n" +
                            "제 18 조 [대금 입금] \n" +
                            "\n" +
                            "○1”갑”은 낙찰확정일부터 3일 이내에 미술품 대금 100%를 “을”에게 입금한다. 입금기간 내\n" +
                            "미술품 대금을 납부하지 않을 경우 “을”은 낙찰취소는 물론 낙찰대금 불이행에 따른 손\n" +
                            "해 배상을 청구할 수 있다.\n" +
                            "○2”을”은 대금입금과 동시에 미술품 및 관련 제반 서류를 5일 이내에 “갑”에게 인도 하여야 \n" +
                            "한다.  “을”이 사전에 제반 서류의 지연을 통보한 경우에는 인도기간을 협의 하에 조정할\n" +
                            "수 있다.\n" +
                            "○3미술품배송은 “갑”과 “을”의 상호 합의하에 결정하되, 소요경비는 “갑”이 부담한다.\n" +
                            "\n" +
                            "제 19 조 [계산서 발행 및 소유권 이전]\n" +
                            "\n" +
                            "○1세금계산서(영수증) 발행은 계약일(낙찰결제일)을 기준으로 작성한다.\n" +
                            "\n" +
                            "제 20 조 [미술품 검수 및 하자담보책임]\n" +
                            "\n" +
                            "○1경매미술품 입찰 및 인도, 인수 시 “갑”과 “을”은 미술품 상태를 철저히 점검하여야 한다.\n" +
                            "○2”갑”은 미술품을 인수한 후에는 단순변심 등의 사유로 “을”에게 그 책임을 물을 수 없다. \n" +
                            "\n" +
                            "\n" +
                            "\t\t\t\t제 5 장 기   타\n" +
                            "\n" +
                            "제 21 조 [계약의 개정]\n" +
                            "\n" +
                            "○1본 계약 및 관련규정은 “을”이 필요한 경우 개정 할 수 있으며, “을”은 계약의 개정 시 개정내용 및 효력발생일을 “갑”에게 고지하여야 한다.\n" +
                            "○2고지의 방법은 서면 또는 “을”이 정한 소정의 장소에 게시하는 것으로 한다.\n" +
                            "\n" +
                            "제 22 조 [사고 책임]\n" +
                            "\n" +
                            "“경매참가자”는 “을”로부터 미술품을 인수한 후에 발생하는 각종 사고에 대하여 모든 민/\n" +
                            "형사상의 책임을 지며, 여기에는 낙찰 후 미술품인수 배송사고 발생을 포함한다.\n" +
                            "\n" +
                            "제 23 조 [손해 배상]\n" +
                            "\n" +
                            "“갑”과 “을”은 본 계약을 위반하거나 기타 불법행위로 상대방에게 손해를 발생시킨 경우 상\n" +
                            "대방이 제시/증명하는 손해를 배상하여야 한다.\n" +
                            "\n" +
                            "제 24 조 [권리/ 의무/ 양도 등의 제한]\n" +
                            "\n" +
                            "“을”은 본 계약과 관련하여 발생하는 제반 권리와 의무를 “갑”의 사전 서면 동의 없이 제 3자에게 전부 또는 일부를 양도, 담보제공, 하도급을 주어서는 아니 된다.\n" +
                            "\n" +
                            "제 25 조 [관할 법원]\n" +
                            "\n" +
                            "본 계약과 관련하여 분쟁이 발생하는 경우, 양 당사자는 우호적인 협의를 통하여 분쟁을\n" +
                            "해결하도록 노력하여야 하며, 협의가 결렬될 경우 갑과 을의 주소지의 지방법원을 분쟁의 관할법원으로 한다.\n" +
                            "\n" +
                            "제 26 조 [분쟁 해결]\n" +
                            "\n" +
                            "본 계약에 명시되지 아니한 사항 및 본 계약의 해석상의 이의가 있는 경우에는 양 당사자가\n" +
                            "협의하여 결정한다. 단, 협의가 이루어지지 않을 시에는 관련법령 및 일반 상관례에 따른다.\n" +
                            "\n" +
                            "제 27 조 [부칙" +
                            "\n" +
                            "\n" +
                            "“갑” 과 “을”은 신의를 가지고 본 계약의 각 조항을 성실히 이행하며, 본 계약의 성실한 이행을 보증하기 위하여 계약서를 2부 작성하고 “갑”과 “을”이 기명 날인하여 각각 1부씩 보관한다.\n" +
                            "본 계약서는 기명 날인한 날로부터 그 효력을 발생한다.\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            sdf.format(dt).toString()+"\n" +
                            "\n" +
                            "\n" +
                            "“(갑)”                                                    \n" +
                            "연락처: " + bidderhp +"\n" +
                            "성명: " + biddername + "  (인)\n" +
                            "\t“(을)”\n" +
                            "연락처: " + artistphone+"\n" +
                            "성명: "+artistname + "      (인)\n" +
                            "\n" +
                            "\n";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("@@@@@@@@@@", title + image + auckey + aucstatus + artistname + bidkey + bidprice);
                    if (aucstatus.equals("5")) {
                        Intent intent1 = new Intent(BidderMainActivity.this, ContractDialog.class);
                        startActivity(intent1);

//                        final AlertDialog.Builder alert = new AlertDialog.Builder(BidderMainActivity.this);
//
//                        alert.setCancelable(false).setPositiveButton("거부", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                tts.stop();
//
//                            }
//                        }).setNeutralButton("동의", new PurchaseButtonClickListener(defaultCipher, DEFAULT_KEY_NAME) {
//
//                        }).setNegativeButton("계약서 읽기", null);

//                        final AlertDialog alertdialog = alert.create();
//                        alertdialog.setView();
//                        alertdialog.setOnShowListener(new DialogInterface.OnShowListener() {
//                                @Override
//                            public void onShow(DialogInterface dialog) {
//                                Button b = alertdialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                                b.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Date dt = new Date();
//                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일");
//                                        String test = "<갤러리 옥션 계약서>" +
//                                                "\n" +
//                                                "본 계약은 구매자 "+biddername+"(이하 “갑”이라 칭함)과(와) 판매자 "+artistname+" (이하 “을”이라 칭함) 간의 미술품경매 운영기본과 “경매참가자”의 자격 및 준수 의무를 정하는 것이다.\n" +
//                                                "\n" +
//                                                "\n" +
//                                                "제 1 장  총   칙\n" +
//                                                "\n" +
//                                                "제 1 조 [계약의 목적]\n" +
//                                                "\n" +
//                                                "본 계약은 경매를 통한 미술품의 거래에 있어서 미술품유통시장의 활성화 및 “갑”과 “을”간의 원활하고 공정한 거래가 진행되도록 하는 것을 목적으로 한다.\n" +
//                                                "\n" +
//                                                "제 2 조 [적 용]\n" +
//                                                "\n" +
//                                                "○1본 계약은 “을”이 개최하는 전시회에 미술품을 구매하고자 하는 모든 “갑”에게 적용된다.\n" +
//                                                "○2”을”은 회원가입 시 본 계약에 대하여 사전 고지하여야 하며, “갑”은 이 계약을 준수하여야 한다.\n" +
//                                                "\n" +
//                                                "제 3 조 [입찰 참가자격]\n" +
//                                                "\n" +
//                                                "입찰에 참가할 수 있는 자격은 다음의 각호에 해당하는 자이어야 한다.\n" +
//                                                "○1“을”의 입찰회원으로 등록한 자.\n" +
//                                                "\n" +
//                                                "제 4 조 [입찰 방식]\n" +
//                                                "\n" +
//                                                "○1”갑” 입찰은 갤러리옥션 앱(App)을 통한 입찰방식으로 행한다.\n" +
//                                                "○2입찰방식은 1항 이외에 “을”이 정하는 기타의 방법으로 입찰을 진행할 수 있다.\n" +
//                                                "○3입찰의 개최 일시는 “을”이 정하며, 사전에 “갑”에게 고지한다.\n" +
//                                                "\n" +
//                                                "제 5 조 [면 책]\n" +
//                                                "\n" +
//                                                "입찰진행 중에 전산이상 및 천재지변 등 불가피한 사유로 인해 입찰이 정상적으로 진행되지\n" +
//                                                "못한 경우, 이로 인한 제반 손해에 대해 “을”은 책임을 지지 않는다.\n" +
//                                                "\n" +
//                                                "\n" +
//                                                "제 2 장  회   원\n" +
//                                                "\n" +
//                                                "제 6 조 [회원 정의]n" +
//                                                "\n" +
//                                                "회원이라 함은 “을”이 개최하는 입찰에 참여하는 자로서 입찰회원으로 등록하고, 입찰보증금을 납부한 “갑”을 칭한다.\n" +
//                                                "\n" +
//                                                "제 7 조 [B회원 등록]\n" +
//                                                "\n" +
//                                                "○1”을”은 회원에 가입하려는 자(또는 미술품을 구매하려고 하는자)를 회원으로 등록한다.\n" +
//                                                "\n" +
//                                                "제 8 조 [보증금]\n" +
//                                                "\n" +
//                                                "○1”갑”의 회원이 되고자 하는 자는 성실한 계약이행을 위하여 보증금을 납부하여야 하나, 베타버전 테스트시는 보증금이 없는 것으로 한다.\n" +
//                                                "○2”갑”이 자의로 탈퇴하거나 계약이 해지 또는 해제될 경우 “을”은 회원으로부터 받은 보증금을 반환하여야 하며, 이자는 정산하지 않는다. 단, “갑”에게 채무가 있을 경우에는 채무를 우선적으로 정산한 후 잔액을 반환한다.\n" +
//                                                "\n" +
//                                                "제 9 조 [회원의 유효기간]\n" +
//                                                "\n" +
//                                                "○1”을”의 유효기간은 원칙적으로 1년을 기준으로 한다.\n" +
//                                                "○2”갑” 및 “을”의 이의가 없는 한 유효기간은 자동적으로 연장된다.\n" +
//                                                "\n" +
//                                                "제 10 조 [회원의 권리]\n" +
//                                                "\n" +
//                                                "“갑”은 “을”이 주최하는 모든 경매에 입찰할 수 있다.\n" +
//                                                "\n" +
//                                                "제 11 조 [회원의 권리 제한]\n" +
//                                                "\n" +
//                                                "“을”은 “갑”과의 거래에서 발생하는 미술품대금 지불지연이나 입찰 제반사항 위반 시 경매참가를 제한할 수 있다.\n" +
//                                                "\n" +
//                                                "제 12 조 [회원의 의무]\n" +
//                                                "\n" +
//                                                "“갑”은 본 계약을 준수할 것을 승인하며 입회한 것이므로 본 계약을 준수해야 하며, “을”에\n" +
//                                                "서 개최하는 입찰의 원활한 운영을 위하여 제정한 제 규정 및 지시를 준수해야 한다.\n" +
//                                                "\n" +
//                                                "제 13 조 [회원의 금지행위]\n" +
//                                                "\n" +
//                                                "“갑”은 아래 각호에 정하는 행위에 대하여 금지한다.\n" +
//                                                "\n" +
//                                                " ○1명의를 임대하여 입찰하는 행위\n" +
//                                                " ○2의도적으로 입찰금액을 도모하거나, 타인의 응찰을 방해하는 행위\n" +
//                                                " ○3전시장 내에서의 고성방가, 폭언, 폭행 등 질서를 어지럽히는 행위 및 기물을 파손하는 행위\n" +
//                                                " ○4기타 “을”이 금지하는 행위\n" +
//                                                "\n" +
//                                                "제 14 조 [회원 제재조치]\n" +
//                                                "\n" +
//                                                " “을”이 본 규약 아래와 같은 행위 또는 기타 제 규정을 위반할 경우 “갑”은 그 위반의 정도에 따라, 입찰참여를 제한할 수 있다.\n" +
//                                                "\n" +
//                                                "○1타 회원의 입찰을 방해하는 행위\n" +
//                                                "○2”을”의 지시나 안내를 따르지 않는 행위\n" +
//                                                "○3기타 입찰의 원활한 흐름을 방해하는 행위\n" +
//                                                "○4”을”과 거래에서 발생하는 거래대금 등의 납부를 지연 하는 경우\n" +
//                                                "⑤낙찰시 미술품 인수 기한을 3회 이상 지연하는 경우\n" +
//                                                "⑥전시장 내에서 폭행 및 폭언, 기물 파손 등으로 경매의 원활한 운영을 방해하는 행위\n" +
//                                                "⑦갤러리옥션 프로그램의 대여 및 위조작하는 행위\n" +
//                                                "⑧의도적으로 입찰금액을 도모하거나, 타인의 입찰을 방해하는 행위\n" +
//                                                "⑨기타 경매의 원활한 운영을 방해하는 심각한 위반행위\n" +
//                                                "\n" +
//                                                "제 15 조 [가입해지]\n" +
//                                                "\n" +
//                                                "“갑”은 항시 경매참가계약을 해지하는 것이 가능하다. 단 해지 예정일 1개월 전에 “을”에 통지하여야 한다.\n" +
//                                                "\n" +
//                                                "\n" +
//                                                "\t\t\t\t제 3 장 낙찰규정\n" +
//                                                "\n" +
//                                                "제 16 조 [낙 찰]\n" +
//                                                "\n" +
//                                                "○1”갑”은 낙찰희망미술품에 대하여 입찰 전 출품리스트 및 경매미술품을 충분히 보고 검토해야 한다.\n" +
//                                                "○2”갑”은 낙찰확정일로부터 3일 이내에 낙찰대금을 결제하여야 하며, 대금결제 전에는 미술품를 인수할 수 없다.\n" +
//                                                "○3낙찰미술품에 대한 소유권은 대금결제순간부터 이전된다.\n" +
//                                                "○4”갑”은 낙찰확정일 익일부터 발생하는 소유권과 관련한 제반 비용에 대한 책임을 져야 한다.\n" +
//                                                "\n" +
//                                                "\n" +
//                                                "제 17 조 [낙찰 취소]\n" +
//                                                "\n" +
//                                                "○1”갑”은 낙찰 후 7일 이내 낙찰미술품에 대해 사전에 고지되지 않은 중대과실이 발견 시 낙찰취소를 요구할 수 있으며, 그 외의 사유로는 낙찰취소를 요구할 수 없다. \n" +
//                                                "○2”갑”이 상기 ○1항 외 다른 사유로 낙찰취소를 할 경우, 그에 따른 손해배상을 “을”에게 지불하여야 하며 손해배상이 이행되지 않을 경우 “을”은 “갑”의 입찰보증금 전액을 손해배상의 예정액으로 할 수 있으며, 만일 손해배상액이 입찰보증금을 초과하는 경우에는 추가로 청구할 수 있다.\n" +
//                                                "○3전항에 의해 입찰보증금이 소진된 경우 재입찰을 위하여는 입찰보증금을 충당하여야 한다. 만약 입찰보증금이 7일 이내 충당되지 않은 경우 본 업무제휴 계약은 종료된 것으로 본다.\n" +
//                                                "\n" +
//                                                "\n" +
//                                                "\t\t\t\t제 4 장 대금결제 및 이전등록\n" +
//                                                "\n" +
//                                                "제 18 조 [대금 입금] \n" +
//                                                "\n" +
//                                                "○1”갑”은 낙찰확정일부터 3일 이내에 미술품 대금 100%를 “을”에게 입금한다. 입금기간 내\n" +
//                                                "미술품 대금을 납부하지 않을 경우 “을”은 낙찰취소는 물론 낙찰대금 불이행에 따른 손\n" +
//                                                "해 배상을 청구할 수 있다.\n" +
//                                                "○2”을”은 대금입금과 동시에 미술품 및 관련 제반 서류를 5일 이내에 “갑”에게 인도 하여야 \n" +
//                                                "한다.  “을”이 사전에 제반 서류의 지연을 통보한 경우에는 인도기간을 협의 하에 조정할\n" +
//                                                "수 있다.\n" +
//                                                "○3미술품배송은 “갑”과 “을”의 상호 합의하에 결정하되, 소요경비는 “갑”이 부담한다.\n" +
//                                                "\n" +
//                                                "제 19 조 [계산서 발행 및 소유권 이전]\n" +
//                                                "\n" +
//                                                "○1세금계산서(영수증) 발행은 계약일(낙찰결제일)을 기준으로 작성한다.\n" +
//                                                "\n" +
//                                                "제 20 조 [미술품 검수 및 하자담보책임]\n" +
//                                                "\n" +
//                                                "○1경매미술품 입찰 및 인도, 인수 시 “갑”과 “을”은 미술품 상태를 철저히 점검하여야 한다.\n" +
//                                                "○2”갑”은 미술품을 인수한 후에는 단순변심 등의 사유로 “을”에게 그 책임을 물을 수 없다. \n" +
//                                                "\n" +
//                                                "\n" +
//                                                "\t\t\t\t제 5 장 기   타\n" +
//                                                "\n" +
//                                                "제 21 조 [계약의 개정]\n" +
//                                                "\n" +
//                                                "○1본 계약 및 관련규정은 “을”이 필요한 경우 개정 할 수 있으며, “을”은 계약의 개정 시 개정내용 및 효력발생일을 “갑”에게 고지하여야 한다.\n" +
//                                                "○2고지의 방법은 서면 또는 “을”이 정한 소정의 장소에 게시하는 것으로 한다.\n" +
//                                                "\n" +
//                                                "제 22 조 [사고 책임]\n" +
//                                                "\n" +
//                                                "“경매참가자”는 “을”로부터 미술품을 인수한 후에 발생하는 각종 사고에 대하여 모든 민/\n" +
//                                                "형사상의 책임을 지며, 여기에는 낙찰 후 미술품인수 배송사고 발생을 포함한다.\n" +
//                                                "\n" +
//                                                "제 23 조 [손해 배상]\n" +
//                                                "\n" +
//                                                "“갑”과 “을”은 본 계약을 위반하거나 기타 불법행위로 상대방에게 손해를 발생시킨 경우 상\n" +
//                                                "대방이 제시/증명하는 손해를 배상하여야 한다.\n" +
//                                                "\n" +
//                                                "제 24 조 [권리/ 의무/ 양도 등의 제한]\n" +
//                                                "\n" +
//                                                "“을”은 본 계약과 관련하여 발생하는 제반 권리와 의무를 “갑”의 사전 서면 동의 없이 제 3자에게 전부 또는 일부를 양도, 담보제공, 하도급을 주어서는 아니 된다.\n" +
//                                                "\n" +
//                                                "제 25 조 [관할 법원]\n" +
//                                                "\n" +
//                                                "본 계약과 관련하여 분쟁이 발생하는 경우, 양 당사자는 우호적인 협의를 통하여 분쟁을\n" +
//                                                "해결하도록 노력하여야 하며, 협의가 결렬될 경우 갑과 을의 주소지의 지방법원을 분쟁의 관할법원으로 한다.\n" +
//                                                "\n" +
//                                                "제 26 조 [분쟁 해결]\n" +
//                                                "\n" +
//                                                "본 계약에 명시되지 아니한 사항 및 본 계약의 해석상의 이의가 있는 경우에는 양 당사자가\n" +
//                                                "협의하여 결정한다. 단, 협의가 이루어지지 않을 시에는 관련법령 및 일반 상관례에 따른다.\n" ;
//
//                                            Log.d("@!@!@@@@", contract);
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                            ttsGreater21(test);
//                                        } else {
//                                            ttsUnder20(test);
//                                        }
//                                    }
//                                });
//                            }
//                        });
//                        alertdialog.setCancelable(false);
//                        alertdialog.show();

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
                        intent1.putExtra("biddername",biddername);
                        intent1.putExtra("bidderhp",bidderhp);
                        startActivity(intent1);
                    } else if (aucstatus.equals("7")) {
                        ArtistNamePhone_txt.setText("아티스트 이름 : " + artistname +"\n아티스트 휴대폰번호 : "+ artistphone );
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
                    intent.putExtra("tagid", toHexString(tagId));
                    intent.putExtra("userID", userID);
                    intent.putExtra("name", name);

                    Log.d("tag",toHexString(tagId));
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
//            BiddingWinUserAgree(auckey);
            contractor("<갤러리 옥션 계약서>" +
                    "\n" +
                    "본 계약은 구매자 "+biddername+"(이하 “갑”이라 칭함)과(와) 판매자 "+artistname+" (이하 “을”이라 칭함) 간의 미술품경매 운영기본과 “경매참가자”의 자격 및 준수 의무를 정하는 것이다.\n" +
                    "\n" +
                    "\n" +
                    "제 1 장  총   칙\n" +
                    "\n" +
                    "제 1 조 [계약의 목적]\n" +
                    "\n" +
                    "본 계약은 경매를 통한 미술품의 거래에 있어서 미술품유통시장의 활성화 및 “갑”과 “을”간의 원활하고 공정한 거래가 진행되도록 하는 것을 목적으로 한다.\n" +
                    "\n" +
                    "제 2 조 [적 용]\n" +
                    "\n" +
                    "○1본 계약은 “을”이 개최하는 전시회에 미술품을 구매하고자 하는 모든 “갑”에게 적용된다.\n" +
                    "○2”을”은 회원가입 시 본 계약에 대하여 사전 고지하여야 하며, “갑”은 이 계약을 준수하여야 한다.\n" +
                    "\n" +
                    "제 3 조 [입찰 참가자격]\n" +
                    "\n" +
                    "입찰에 참가할 수 있는 자격은 다음의 각호에 해당하는 자이어야 한다.\n" +
                    "○1“을”의 입찰회원으로 등록한 자.\n" +
                    "\n" +
                    "제 4 조 [입찰 방식]\n" +
                    "\n" +
                    "○1”갑” 입찰은 갤러리옥션 앱(App)을 통한 입찰방식으로 행한다.\n" +
                    "○2입찰방식은 1항 이외에 “을”이 정하는 기타의 방법으로 입찰을 진행할 수 있다.\n" +
                    "○3입찰의 개최 일시는 “을”이 정하며, 사전에 “갑”에게 고지한다.\n" +
                    "\n" +
                    "제 5 조 [면 책]\n" +
                    "\n" +
                    "입찰진행 중에 전산이상 및 천재지변 등 불가피한 사유로 인해 입찰이 정상적으로 진행되지\n" +
                    "못한 경우, 이로 인한 제반 손해에 대해 “을”은 책임을 지지 않는다.\n" +
                    "\n" +
                    "\n" +
                    "제 2 장  회   원\n" +
                    "\n" +
                    "제 6 조 [회원 정의]\n" +
                    "\n" +
                    "회원이라 함은 “을”이 개최하는 입찰에 참여하는 자로서 입찰회원으로 등록하고, 입찰보증금을 납부한 “갑”을 칭한다.\n" +
                    "\n" +
                    "제 7 조 [B회원 등록]\n" +
                    "\n" +
                    "○1”을”은 회원에 가입하려는 자(또는 미술품을 구매하려고 하는자)를 회원으로 등록한다.\n" +
                    "\n" +
                    "제 8 조 [보증금]\n" +
                    "\n" +
                    "○1”갑”의 회원이 되고자 하는 자는 성실한 계약이행을 위하여 보증금을 납부하여야 하나, 베타버전 테스트시는 보증금이 없는 것으로 한다.\n" +
                    "○2”갑”이 자의로 탈퇴하거나 계약이 해지 또는 해제될 경우 “을”은 회원으로부터 받은 보증금을 반환하여야 하며, 이자는 정산하지 않는다. 단, “갑”에게 채무가 있을 경우에는 채무를 우선적으로 정산한 후 잔액을 반환한다.\n" +
                    "\n" +
                    "제 9 조 [회원의 유효기간]\n" +
                    "\n" +
                    "○1”을”의 유효기간은 원칙적으로 1년을 기준으로 한다.\n" +
                    "○2”갑” 및 “을”의 이의가 없는 한 유효기간은 자동적으로 연장된다.\n" +
                    "\n" +
                    "제 10 조 [회원의 권 ");

            Intent intent1 = new Intent(BidderMainActivity.this, BidderAddress.class);
            intent1.putExtra("title", title);
            intent1.putExtra("image", image);
            intent1.putExtra("auckey", auckey);
            intent1.putExtra("aucstatus", aucstatus);
            intent1.putExtra("artistname", artistname);
            intent1.putExtra("bidprice", bidprice);
            intent1.putExtra("userID", userID);
            intent1.putExtra("bidkey",bidkey);
            Log.d("@@@@", bidprice);
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

            Toast.makeText(BidderMainActivity.this, "지문인증이 필요합니다.", Toast.LENGTH_SHORT).show();
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
