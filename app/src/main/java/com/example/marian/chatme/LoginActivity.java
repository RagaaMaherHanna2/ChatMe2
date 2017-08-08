package com.example.marian.chatme;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {


    private static final String TAG = "mido";
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mLoginBtn;

    ProgressDialog mRegProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabasea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRegProgress = new ProgressDialog(LoginActivity.this);
        mUserDatabasea = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();

        mEmail = (TextInputLayout) findViewById(R.id.login_email);
        mPassword = (TextInputLayout) findViewById(R.id.login_password);
        mLoginBtn = (Button) findViewById(R.id.login_btn);


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();


                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mRegProgress.setTitle("Logging in");
                    mRegProgress.setMessage("please wait while we check your credentials");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    login_user(email, password);
                }
            }
        });


    }

    private void login_user(String email, String password) {


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, getString(R.string.signInWithEmail) + task.isSuccessful());
                        mRegProgress.dismiss();
                        String current_user_id = mAuth.getCurrentUser().getUid();
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                        if (!task.isSuccessful()) {
                            Log.w(TAG, getString(R.string.signInWithEmail), task.getException());
                            Toast.makeText(LoginActivity.this, R.string.not_correct,
                                    Toast.LENGTH_SHORT).show();
                        }

                        mUserDatabasea.child(current_user_id).child("device_token").setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Intent mainIntent =new Intent(LoginActivity.this,MainActivity.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);

                            }
                        });
//
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        // ...
                    }
                });


    }
}
