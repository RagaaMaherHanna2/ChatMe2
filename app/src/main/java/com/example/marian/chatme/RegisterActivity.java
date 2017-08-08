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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mDisplayName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;

    private Toolbar mToolbar;
    ProgressDialog mRegProgress;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mRegProgress = new ProgressDialog(RegisterActivity.this);

        mToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create new Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mDisplayName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.login_email);
        mPassword = (TextInputLayout) findViewById(R.id.login_password);

        mCreateBtn = (Button) findViewById(R.id.login_btn);
        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                if (!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mRegProgress.setTitle("Registerating user");
                    mRegProgress.setMessage("please wait while we create your account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    register_user(display_name, email, password);
                }
            }
        });
    }

    private void register_user(final String display_name, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("ssssss", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            mRegProgress.hide();
                            Toast.makeText(RegisterActivity.this, R.string.Error_login,
                                    Toast.LENGTH_SHORT).show();
                        }


                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        String uId = currentUser.getUid();
                        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
                        HashMap<String, String> userMap = new HashMap<String, String>();
                        userMap.put("name", display_name);
                        userMap.put("status", "Hi there I'm Using Mido Chat App.");
                        userMap.put("image", "defauld");
                        userMap.put("thumb_image", "defauld");

                        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    mRegProgress.hide();
                                    Toast.makeText(RegisterActivity.this,  R.string.Error_login,
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    mRegProgress.dismiss();
                                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                }
                            }
                        });


                    }
                });


    }
}
