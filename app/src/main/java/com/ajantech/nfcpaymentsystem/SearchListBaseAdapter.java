package com.ajantech.nfcpaymentsystem;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.geno.payment.R;

public class SearchListBaseAdapter extends BaseAdapter{
	private final String TAG = "SearchListBaseAdapter";
	private final boolean D = true;

	private Context mContext;
	private LayoutInflater mInflater;
	private ViewHolder mViewHolder;
	private ArrayList<String> mPrice;
	private ArrayList<String> mCount;
	private ArrayList<String> mDate;
	
	public SearchListBaseAdapter(Context aContext, ArrayList<String> aPrice, ArrayList<String> aCount, ArrayList<String> aDate) {
		mContext = aContext;
		mInflater = LayoutInflater.from(aContext);
		mPrice = aPrice;
		mCount = aCount;
		mDate = aDate;
		
	}

	@Override
	public int getCount() {
		return mPrice.size();
	}

	@Override
	public Object getItem(int position) {
		return mPrice.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if(v == null) {
			v = mInflater.inflate(R.layout.search_item, null);
			mViewHolder = new ViewHolder();
			mViewHolder.mDateTextView = (TextView) v.findViewById(R.id.sl_date_view);
			mViewHolder.mCountTextView = (TextView) v.findViewById(R.id.sl_cnt_view);
			mViewHolder.mPriceTextView = (TextView) v.findViewById(R.id.sl_price_view);
			v.setTag(mViewHolder);			
		} else {
			mViewHolder = (ViewHolder)v.getTag();
		}

		if(mDate != null) {
			int size = mDate.size();

			if(size >= position && mDate.get(0) != null) {
				mViewHolder.mDateTextView.setText(mDate.get(position));
			}
		}
		
		if(mCount != null && !mCount.isEmpty()) {
			mViewHolder.mCountTextView.setText(mCount.get(position));
		}

		if(mPrice != null && !mPrice.isEmpty()) {
			mViewHolder.mPriceTextView.setText(mPrice.get(position));
		}
		return v;
	}

	public void setPriceList(ArrayList<String> aPrice) {
		mPrice = aPrice;
	}

	public void setCountList(ArrayList<String> aCount) {
		mCount = aCount;
	}

	public void setDateList(ArrayList<String> aDate) {
		mDate = aDate;
	}

	class ViewHolder {
		private TextView mDateTextView;
		private TextView mCountTextView;
		private TextView mPriceTextView;
	}
}
