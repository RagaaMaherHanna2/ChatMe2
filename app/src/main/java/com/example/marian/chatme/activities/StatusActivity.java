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

public class StatusActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextInputLayout mStatus;
    private Button mSavebtn;
    private ProgressDialog dialog;


    // firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = currentUser.getUid();
        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child(getString(R.string.root)).child(uId);

        mToolbar = (Toolbar) findViewById(R.id.status_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("status_value").toString();


        // layout
        mStatus = (TextInputLayout) findViewById(R.id.status_input);
        mSavebtn = (Button) findViewById(R.id.status_save_btn);
        mStatus.getEditText().setText(status_value);

        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(StatusActivity.this);
                dialog.setTitle(getString(R.string.save_dialog));
                dialog.setMessage(getString(R.string.message_dialog1));
                dialog.show();

                String status = mStatus.getEditText().getText().toString();
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
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
//

    }
}
