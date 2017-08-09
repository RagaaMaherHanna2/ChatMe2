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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.reg_bar)
    Toolbar regBar;
    @BindView(R.id.reg_name)
    TextInputLayout regName;
    @BindView(R.id.reg_email)
    TextInputLayout regEmail;
    @BindView(R.id.reg_pass)
    TextInputLayout regPass;
    @BindView(R.id.reg_btn)
    Button regBtn;


    ProgressDialog mRegProgress;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.bind(this);

        mRegProgress = new ProgressDialog(RegisterActivity.this);

        setSupportActionBar(regBar);
        getSupportActionBar().setTitle(R.string.reg_bar_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name = regName.getEditText().getText().toString();
                String email = regEmail.getEditText().getText().toString();
                String password = regPass.getEditText().getText().toString();

                if (!TextUtils.isEmpty(display_name) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    mRegProgress.setTitle(getString(R.string.create_acc_dialog));
                    mRegProgress.setMessage(getString(R.string.create_acc_msg));
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
                        if (!task.isSuccessful()) {
                            mRegProgress.hide();
                            Toast.makeText(RegisterActivity.this, R.string.Error_login,
                                    Toast.LENGTH_SHORT).show();
                        }


                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        String uId = currentUser.getUid();
                        myRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.root)).child(uId);
                        HashMap<String, String> userMap = new HashMap<String, String>();
                        userMap.put("name", display_name);
                        userMap.put("status", getString(R.string.default_status));
                        userMap.put("image", "default");
                        userMap.put("thumb_image", "default");

                        myRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    mRegProgress.hide();
                                    Toast.makeText(RegisterActivity.this,  R.string.Error_login,
                                            Toast.LENGTH_SHORT).show();
                                } else {

                                    mRegProgress.dismiss();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }
                        });


                    }
                });


    }
}
