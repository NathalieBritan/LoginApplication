package com.example.ruslan.loginapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{
    private Button loginGooglePlus, loginFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginGooglePlus= (Button) findViewById(R.id.buttonLoginGPlus);
        loginFacebook= (Button) findViewById(R.id.buttonLoginFacebook);
        loginFacebook.setOnClickListener(onClickListener);
        loginGooglePlus.setOnClickListener(onClickListener);

    }
    View.OnClickListener onClickListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.buttonLoginGPlus:
                    Intent intentGPlus=new Intent(getApplicationContext(),LoginGPlus.class);
                    startActivity(intentGPlus);
                    break;
                case R.id.buttonLoginFacebook:
                    Intent intentFacebook =new Intent(getApplicationContext(),LoginFacebook.class);
                    startActivity(intentFacebook);
                    break;
            }
        }
    };



}

