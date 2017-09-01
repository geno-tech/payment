package com.geno;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import com.geno.payment.R;

public class DbConnect extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dbconnect);

		Intent intent = new Intent(this.getIntent());
		
		String bankName = intent.getStringExtra("bankName");
		String bankAccount = intent.getStringExtra("bankAccount");
		String passWd = intent.getStringExtra("passWd");
		String faceValue = intent.getStringExtra("faceValue");
		String storeName = intent.getStringExtra("storeName");
		String userName = intent.getStringExtra("userName");

		// 반은 값을 Toast메세지로 출력
		Toast.makeText(
				this,
				"받은 값 :" + bankName + "\n받은 값 :" + bankAccount + "\n받은 값 :"
						+ passWd + "\n받은 값 :" + faceValue + "\n받은 값 :"
						+ storeName + "\n받은 값 :" + userName, Toast.LENGTH_LONG).show();

		WebView wv=(WebView)findViewById(R.id.webView); 
		  wv.getSettings().setJavaScriptEnabled(true);
		  wv.loadUrl("http://183.105.72.65:29999/db_connect.jsp?bankName="+bankName+
				  "&bankAccount="+bankAccount+"&passWd="+passWd+
				  "&faceValue="+faceValue+"&storeName="+storeName+"&userName="+userName);

		  /*Intent men1 = new Intent(DbConnect.this, coupon_list.class); 
		  startActivity(men1);*/
	}
}