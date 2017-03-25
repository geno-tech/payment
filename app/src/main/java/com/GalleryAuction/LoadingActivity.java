package com.GalleryAuction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.geno.bill_folder.R;

public class LoadingActivity extends AppCompatActivity {

    int r = (int)(Math.random()*5);

    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        img = (ImageView) findViewById(R.id.imageView);

        Handler handler = new Handler();
        handler.postDelayed(new splashhandler(), 1500);

        Animation[] ani = {
                AnimationUtils.loadAnimation(this, R.anim.rotate),
                AnimationUtils.loadAnimation(this, R.anim.loading2),
                AnimationUtils.loadAnimation(this, R.anim.loading3),
                AnimationUtils.loadAnimation(this, R.anim.loading4),
                AnimationUtils.loadAnimation(this, R.anim.loading5),
        };
        img.startAnimation(ani[r]);


    }



    private class splashhandler implements Runnable {

        @Override
        public void run() {
            startActivity(new Intent(getApplication(), Main.class));

            LoadingActivity.this.finish();

        }

    }

    @Override

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
