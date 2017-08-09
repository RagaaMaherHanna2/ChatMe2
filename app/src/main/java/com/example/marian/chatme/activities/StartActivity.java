package com.example.marian.chatme.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.marian.chatme.R;

public class StartActivity extends AppCompatActivity {

    private Button mloginBtn;
    private Button mRegBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mloginBtn = (Button) findViewById(R.id.start_login_btn);
        mRegBtn = (Button) findViewById(R.id.start_reg_btn);


        mloginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(reg_intent);
            }
        });
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent = new Intent(StartActivity.this, RegisterActivity.class);
                startActivity(reg_intent);
            }
        });
    }
}
