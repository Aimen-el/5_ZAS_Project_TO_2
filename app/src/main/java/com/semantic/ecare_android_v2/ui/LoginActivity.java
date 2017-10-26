package com.semantic.ecare_android_v2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.semantic.ecare_android_v2.R;

public class LoginActivity extends AppCompatActivity {
    private TextView name_tv;
    private TextView password_tv;
    private Button login_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //link views
        name_tv = (TextView) findViewById(R.id.name_field);
        password_tv = (TextView) findViewById(R.id.pwd_field);
        login_btn = (Button) findViewById(R.id.signin_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name_tv.getText() != null && password_tv.getText() != null){
                    Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
                    LoginActivity.this.startActivity(intent);
                    LoginActivity.this.finish();
                }
            }
        });

    }
}
