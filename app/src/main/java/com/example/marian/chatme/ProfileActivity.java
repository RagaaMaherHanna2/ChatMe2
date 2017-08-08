package com.example.marian.chatme;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    ImageView mProfileImageView;
    TextView mProfileName, mProfileStatus, mProfileFriendsCount;
//    private Button mProfileSendReqbtn,mDeclineBtn;

    private DatabaseReference mUserDatabase;
//    private DatabaseReference mFriendReqDatabase;
//    private DatabaseReference mFriendDatabase;
//    private DatabaseReference mNotifactionDatabase;

    private FirebaseUser mCurrent_user;

    private ProgressDialog mProgressDialog;

//    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");


        mUserDatabase = FirebaseDatabase.getInstance().getReference().child(getString(R.string.root)).child(user_id);
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImageView = (ImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_displayName);
        mProfileStatus = (TextView) findViewById(R.id.profile_status);


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getString(R.string.dialog_title2));
        mProgressDialog.setMessage(getString(R.string.message_dialog2));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();


                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.ma).into(mProfileImageView);
                mProgressDialog.dismiss();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
