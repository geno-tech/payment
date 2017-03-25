package com.GalleryAuction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.geno.bill_folder.R;

public class Login extends AppCompatActivity implements View.OnClickListener {
Button btn_login, btn_logincancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        btn_login = (Button)findViewById(R.id.btn_loginok);
        btn_logincancel = (Button)findViewById(R.id.btn_logincancel);
        btn_login.setOnClickListener(this);
        btn_logincancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_loginok :
                Intent intent = new Intent(Login.this, ArtInformation.class);
                startActivity(intent);
                Login.this.finish();
                break;
            case R.id.btn_logincancel :
                Intent intent2 = new Intent(Login.this, Main.class);
                startActivity(intent2);
                Login.this.finish();
        }
    }
}
