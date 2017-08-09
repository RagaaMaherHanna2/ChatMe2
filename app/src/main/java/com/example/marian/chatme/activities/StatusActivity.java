package com.example.marian.chatme.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.marian.chatme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatusActivity extends AppCompatActivity {

    @BindView(R.id.status_bar)
    Toolbar mToolbar;
    @BindView(R.id.status_update)
    TextInputLayout statusUpdate;
    @BindView(R.id.status_update_btn)
    Button statusUpdateBtn;


    private ProgressDialog dialog;

    private DatabaseReference myRef;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        ButterKnife.bind(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.root)).child(uId);

        mToolbar = (Toolbar) findViewById(R.id.status_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(getString(R.string.update_status));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("status_value").toString();

        statusUpdate.getEditText().setText(status_value);

        statusUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(StatusActivity.this);
                dialog.setTitle(getString(R.string.save_dialog));
                dialog.setMessage(getString(R.string.message_dialog1));
                dialog.show();

                String status = statusUpdate.getEditText().getText().toString();
                myRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            finish();
                        } else {
                            Toast.makeText(StatusActivity.this, R.string.error_updata, Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });

    }
}
