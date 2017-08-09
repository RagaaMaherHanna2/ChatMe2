package com.example.marian.chatme.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.marian.chatme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.login_bar)
    Toolbar loginBar;
    @BindView(R.id.reg_email)
    TextInputLayout login_Email;
    @BindView(R.id.reg_pass)
    TextInputLayout login_password;
    @BindView(R.id.reg_btn)
    Button loginBtn;

    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        setSupportActionBar(loginBar);
        getSupportActionBar().setTitle("login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressDialog = new ProgressDialog(LoginActivity.this);
        myRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = login_Email.getEditText().getText().toString();
                String password = login_password.getEditText().getText().toString();


                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    progressDialog.setTitle("Logging in");
                    progressDialog.setMessage("please wait while we check your credentials");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    UserLogin(email, password);
                }
            }
        });


    }

    private void UserLogin(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        String currentUid = mAuth.getCurrentUser().getUid();
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.not_correct,
                                    Toast.LENGTH_SHORT).show();
                        }

                        myRef.child(currentUid).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            }
                        });

                    }
                });


    }
}
