package com.GalleryAuction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.geno.bill_folder.R;

public class Agreement extends AppCompatActivity implements View.OnClickListener {
Button btn_agree, btn_disagree;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agreement);
        btn_agree = (Button)findViewById(R.id.btn_agree);
        btn_disagree = (Button)findViewById(R.id.btn_disagree);
        btn_agree.setOnClickListener(this);
        btn_disagree.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_agree :
                Intent intent = new Intent(Agreement.this, SignUp.class);
                startActivity(intent);
                Agreement.this.finish();
                break;
            case R.id.btn_disagree :
                Intent intent2 = new Intent(Agreement.this, Main.class);
                startActivity(intent2);
                Agreement.this.finish();
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
