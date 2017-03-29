package com.ajantech.nfcpaymentsystem;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.ajantech.nfcpaymentsystem.ui.Start;
import com.geno.MainActivity;
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

            startActivity(new Intent(getApplication(), Start.class));

            LodingActivity.this.finish();

        }

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}