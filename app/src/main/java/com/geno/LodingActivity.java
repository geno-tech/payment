package com.geno;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.geno.bill_folder.R;

public class LodingActivity extends FragmentActivity {


    private CharSequence mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loding);

        Handler handler = new Handler();
        handler.postDelayed(new splashhandler(),1500);

    }

    private class splashhandler implements Runnable {

        @Override
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class));

            LodingActivity.this.finish();

        }

    }
}