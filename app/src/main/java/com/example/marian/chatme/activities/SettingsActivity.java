package com.example.marian.chatme.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marian.chatme.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity
{
    @BindView(R.id.settings_bar)
    Toolbar settingsBar;
    @BindView(R.id.set_profile)
    CircleImageView setProfile;
    @BindView(R.id.set_name)
    TextView setName;
    @BindView(R.id.set_status)
    TextView setStatus;
    @BindView(R.id.set_status_btn)
    Button setStatusBtn;
    @BindView(R.id.set_profile_btn)
    Button setProfileBtn;

    private ProgressDialog mProgressDialog;

    private DatabaseReference myRef;
    private FirebaseUser firebaseUser;

    private StorageReference storageRef;

    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        setSupportActionBar(settingsBar);
        getSupportActionBar().setTitle(getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storageRef = FirebaseStorage.getInstance().getReference();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();


        myRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.root)).child(uId);
        myRef.keepSynced(true);
        myRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();


                SharedPreferences sharedPref = SettingsActivity.this.getSharedPreferences(getString(R.string.ticket_pref_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.tickets_number), name);
                editor.commit();


                setName.setText(name);
                setStatus.setText(status);
                if (!image.equals("default"))
                {
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(setProfile, new Callback()
                    {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default_avatar).into(setProfile);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_value = setStatus.getText().toString();
                Intent StatusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                StatusIntent.putExtra("status_value", status_value);
                startActivity(StatusIntent);

            }
        });


        setProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && requestCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
            Toast.makeText(SettingsActivity.this, imageUri.toString(), Toast.LENGTH_SHORT).show();
        }


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle(getString(R.string.dialog_title));
                mProgressDialog.setMessage(getString(R.string.dialog_message));
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                String current_user_id = firebaseUser.getUid();
                StorageReference filepath = storageRef.child("profile_images").child(current_user_id + ".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            String downLoad_url = task.getResult().getDownloadUrl().toString();
                            myRef.child("image").setValue(downLoad_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mProgressDialog.dismiss();
                                        Toast.makeText(SettingsActivity.this, R.string.succes_upload, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(SettingsActivity.this, "Error ", Toast.LENGTH_LONG).show();

                        }

                    }
                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

}
