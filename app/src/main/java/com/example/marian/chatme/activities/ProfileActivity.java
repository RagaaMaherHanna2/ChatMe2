package com.example.marian.chatme.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.marian.chatme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity
{

    @BindView(R.id.profile_image)
    ImageView userProfile;
    @BindView(R.id.profile_displayName)
    TextView profileDisplayName;
    @BindView(R.id.profile_displayStatus)
    TextView ProfileDisplayStatus;

    private DatabaseReference myRef;
    private FirebaseUser firebaseUser;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);
        final String user_id = getIntent().getStringExtra(getString(R.string.user_id));

        myRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.root)).child(user_id);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.dialog_title2));
        mProgressDialog.setMessage(getString(R.string.message_dialog2));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();


                profileDisplayName.setText(display_name);
                ProfileDisplayStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.default_avatar).into(userProfile);
                mProgressDialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
