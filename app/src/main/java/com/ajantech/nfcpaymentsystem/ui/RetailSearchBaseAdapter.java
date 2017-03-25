package com.ajantech.nfcpaymentsystem.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RetailSearchBaseAdapter extends BaseAdapter{
	private final String TAG = "RetailSearchBaseAdapter";
	private final boolean D = true;

	private Context mContext;
	private LayoutInflater mInflater;
	private ViewHolder mViewHolder;
	private ArrayList<String> mEpcList;
	private ArrayList<String> mCntList;

	public RetailSearchBaseAdapter(Context context, ArrayList<String> epcList, ArrayList<String> cntList) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mEpcList = epcList;
		mCntList = cntList;
	}
	public int getCount() {
		return mEpcList.size();
	}

	public Object getItem(int index) {
		return mEpcList.get(index);
	}

	public long getItemId(int index) {
		return index;
	}

	public View getView(int position, View contentView, ViewGroup parent) {
		View v = contentView;

		if(v == null) {
			
		} else {
			mViewHolder = (ViewHolder)v.getTag();
		}

		if(mEpcList != null) {
			int size = mEpcList.size();

			if(size >= position && mEpcList.get(0) != null) {
				mViewHolder.mEpcTextView.setText(mEpcList.get(position));
			}
		}
		
		if(mCntList != null && !mCntList.isEmpty()) {
			mViewHolder.mCntTextView.setText(mCntList.get(position));
		}
		return v;
	}

	public void setEpcList(ArrayList<String> epcList) {
		mEpcList = epcList;
	}

	public void setCntList(ArrayList<String> cntList) {
		mCntList = cntList;
	}

	class ViewHolder {
		private TextView mEpcTextView;
		private TextView mCntTextView;
	}
}
