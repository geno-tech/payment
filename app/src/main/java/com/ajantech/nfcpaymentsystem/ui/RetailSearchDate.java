package com.ajantech.nfcpaymentsystem.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.ajantech.nfc_network.CommunicationService;
import com.ajantech.nfc_network.CommunicationService.OnNFCServiceCallBack;
import com.ajantech.nfc_network.ShareData;
import com.ajantech.nfc_network.request.DefaultRequestStruct;
import com.ajantech.nfc_network.request.SearchGoodsRequestStruct;
import com.ajantech.nfc_network.request.SearchGoodsRequestStruct.TradeData;
import com.ajantech.nfcpaymentsystem.SearchListBaseAdapter;
import com.geno.payment.R;

public class RetailSearchDate extends Activity implements OnNFCServiceCallBack {
	private final String TAG = "RetailSearchDate";
	private final boolean D = true;

	private final int DLG_DEFAULT = 0;
	private final int DLG_DATE = DLG_DEFAULT + 1;
	private final int DLG_WRONG_DATE_MSG = DLG_DATE + 1;

	private EditText mProductEdittext;
	private Button mStartDateBtn;
	private Button mEndDateBtn;
	private Button mSearchBtn;
	private ListView mProductListView;
	private SearchListBaseAdapter mBaseAdapter;
	private DatePickerDialog mTimeDialog;
	private int mSdCheck;
	private ArrayList<String> mProductPrice;
	private ArrayList<String> mProductCnt;
	private ArrayList<String> mProductDate;
	private ShareData mConfingData = null;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1025) {
				if (D) {
					Log.d(TAG, "GET MESSAGE = " + msg.obj);
				}
			} else if (msg.what == 1026) {
				DefaultRequestStruct request = (DefaultRequestStruct) msg.obj;

				Log.d(TAG, "recv runRequestType : " + request.type);
				Log.d(TAG, "recv runRequestCommand : "
						+ request.runRequestCommand);

				switch (request.type) {
				case DefaultRequestStruct.REQUEST_TYPE_ERROR: {
					try {
						// dismissDialog(DLG_COMM_SERVER);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// showDialog(DLG_PAYMENT_FAIL);
					break;
				}
				case DefaultRequestStruct.REQUEST_TYPE_SEARCH_FOR_GOODS: {
					SearchGoodsRequestStruct searchGoodsRequestStruct = (SearchGoodsRequestStruct) request;
					ArrayList<TradeData> list = searchGoodsRequestStruct
							.getTradeList();
					getProductList(list);
					break;
				}
				default: {
					break;
				}
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.searchdatenew);

		mProductEdittext = (EditText) findViewById(R.id.sp_product_name_edittext);

		mStartDateBtn = (Button) findViewById(R.id.sp_start_date_btn);
		mStartDateBtn.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				mSdCheck = 0;
				showDialog(DLG_DATE);
			}
		});
		mEndDateBtn = (Button) findViewById(R.id.sp_end_date_btn);
		mEndDateBtn.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				mSdCheck = 1;
				showDialog(DLG_DATE);
			}
		});
		mSearchBtn = (Button) findViewById(R.id.sp_search_btn);
		mSearchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchGoods();
			}
		});
		mProductListView = (ListView) findViewById(R.id.sp_product_listview);
		mProductListView.setVisibility(View.GONE);

		mProductPrice = new ArrayList<String>();
		mProductCnt = new ArrayList<String>();
		mProductDate = new ArrayList<String>();
		mBaseAdapter = new SearchListBaseAdapter(this, mProductPrice,
				mProductCnt, mProductDate);
		mProductListView.setAdapter(mBaseAdapter);
		registerForContextMenu(mProductListView);
		CommunicationService.getInstance(this).registerNFCCallback(this);
	}

	@Override
	@Deprecated
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);

		switch (id) {
		case DLG_DATE: {
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of TimePickerDialog and return it
			mTimeDialog = new DatePickerDialog(Main.context,
					new OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							String dayOfMonthStr = "";
							String monthOfYearStr = "";
							Log.d(TAG, "DATE IS = " + year + " "
									+ (monthOfYear + 1) + " " + dayOfMonth);
							if (mSdCheck == 0) {
								if (dayOfMonth < 10) {
									dayOfMonthStr = "0" + dayOfMonth;
									if (monthOfYear + 1 < 10) {
										monthOfYear = monthOfYear + 1;
										monthOfYearStr = "0" + monthOfYear;
									} else {
										monthOfYear = monthOfYear + 1;
										monthOfYearStr = monthOfYear + "";
									}
									mStartDateBtn.setText(year + "-"
											+ (monthOfYearStr) + "-"
											+ dayOfMonthStr);
								} else {
									if (monthOfYear + 1 < 10) {
										monthOfYear = monthOfYear + 1;
										monthOfYearStr = "0" + monthOfYear;
									} else {
										monthOfYear = monthOfYear + 1;
										monthOfYearStr = monthOfYear + "";
									}
									mStartDateBtn.setText(year + "-"
											+ (monthOfYearStr) + "-"
											+ dayOfMonth);
								}

								if (checkSdDate()) {
								} else {
									showDialog(DLG_WRONG_DATE_MSG);
								}
							} else {
								if (dayOfMonth < 10) {
									dayOfMonthStr = "0" + dayOfMonth;
									if (monthOfYear + 1 < 10) {
										monthOfYear = monthOfYear + 1;
										monthOfYearStr = "0" + monthOfYear;
									} else {
										monthOfYear = monthOfYear + 1;
										monthOfYearStr = monthOfYear + "";
									}
									mEndDateBtn.setText(year + "-"
											+ (monthOfYearStr) + "-"
											+ dayOfMonthStr);
								} else {
									if (monthOfYear + 1 < 10) {
										monthOfYear = monthOfYear + 1;
										monthOfYearStr = "0" + monthOfYear;
									} else {
										monthOfYear = monthOfYear + 1;
										monthOfYearStr = monthOfYear + "";
									}
									mEndDateBtn.setText(year + "-"
											+ (monthOfYearStr) + "-"
											+ dayOfMonth);
								}
								if (checkSdDate()) {

								} else {
									showDialog(DLG_WRONG_DATE_MSG);
								}
							}
						}
					}, year, month, day);
			mTimeDialog
					.setOnCancelListener(new DialogInterface.OnCancelListener() {

						@Override
						public void onCancel(DialogInterface dialog) {

						}
					});
			mTimeDialog.setCanceledOnTouchOutside(false);
			return mTimeDialog;
		}
		case DLG_WRONG_DATE_MSG: {
			new AlertDialog.Builder(Main.context)
					.setTitle(getString(R.string.rs_error_date_title))
					.setMessage(getString(R.string.rs_error_date_msg))
					.setPositiveButton(getString(R.string.ok_str),
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface paramDialogInterface,
										int paramInt) {
								}
							}).show();
			break;
		}
		case 0:{
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
		return null;
	}

	private void searchGoods() {
		mConfingData = ShareData.newInstance(this);
		if (D) {
			String id = mConfingData.getLastID();
			String token = mConfingData.getLastLoginToken();
			String userType = mConfingData.getLastLoginUserType();
			String productName = mProductEdittext.getText().toString();
			String startDate = mStartDateBtn.getText().toString();
			String endDate = mEndDateBtn.getText().toString();

			Log.d(TAG, "ID = " + id + "\n");
			Log.d(TAG, "TOKEN = " + token + "\n");
			Log.d(TAG, "USERTYPE = " + userType + "\n");
			Log.d(TAG, "PRODUCT_NAME = " + productName + "\n");
			Log.d(TAG, "START_DATE = " + startDate + "\n");
			Log.d(TAG, "END_DATE = " + endDate + "\n");
		}
		CommunicationService.getInstance().searchGoods(
				mConfingData.getLastID(), mConfingData.getLastLoginToken(),
				mConfingData.getLastLoginUserType(),
				mProductEdittext.getText().toString(),
				mStartDateBtn.getText().toString(),
				mEndDateBtn.getText().toString());
	}

	private boolean checkSdDate() {
		if (mStartDateBtn.getText().toString().equals("선택하세요.")
				|| mEndDateBtn.getText().toString().equals("선택하세요.")) {
			return true;
		} else {
			/**
			 * -가 들어있는부분은 2부분 2개의 index를 찾고 int로 저장 이후 subString
			 */
			String startDateStr = mStartDateBtn.getText().toString();
			String endDateStr = mEndDateBtn.getText().toString();
			int sSecondBar = startDateStr.indexOf("-",
					startDateStr.indexOf("-") + 1);
			int eSecondBar = endDateStr.indexOf("-",
					endDateStr.indexOf("-") + 1);
			int sYear = Integer.parseInt(startDateStr.substring(0, 4));
			int sMonth = Integer
					.parseInt(startDateStr.substring(5, sSecondBar)) - 1;
			int sDay = Integer.parseInt(startDateStr.substring(sSecondBar + 1,
					startDateStr.length()));
			int eYear = Integer.parseInt(endDateStr.substring(0, 4));
			int eMonth = Integer.parseInt(endDateStr.substring(5, eSecondBar)) - 1;
			int eDay = Integer.parseInt(endDateStr.substring(eSecondBar + 1,
					endDateStr.length()));
			Date sDate = new Date(sYear, sMonth, sDay);
			Date eDate = new Date(eYear, eMonth, eDay);

			if (sDate.compareTo(eDate) > 0) {
				return false;
			} else if (sDate.compareTo(eDate) == 0) {
				return false;
			} else {
				return true;
			}
		}
	}

	private void getProductList(ArrayList<TradeData> aList) {

		for (TradeData oneData : aList) {
			mProductPrice.add(oneData.price);
			mProductCnt.add(oneData.count.substring(0,oneData.count.length()-1));
			mProductDate.add(oneData.trade_date);
		}

		if (mBaseAdapter != null) {
			mProductListView.setVisibility(View.VISIBLE);
			mBaseAdapter.setDateList(mProductDate);
			mBaseAdapter.setPriceList(mProductPrice);
			mBaseAdapter.setCountList(mProductCnt);
			mBaseAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void OnNFCServiceMessage(DefaultRequestStruct request) {
		mHandler.obtainMessage(1026, request).sendToTarget();

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBackPressed() {
		showDialog(0);
	}

	
}
