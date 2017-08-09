package com.example.marian.chatme.activities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marian.chatme.R;
import com.example.marian.chatme.model.Users;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_bar)
    Toolbar mainBar;
    @BindView(R.id.users_list)
    RecyclerView usersList;

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;


    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mAuth = FirebaseAuth.getInstance();

        setSupportActionBar(mainBar);
        getSupportActionBar().setTitle(R.string.bar_title);

        usersList.setHasFixedSize(true);
        usersList.setLayoutManager(new LinearLayoutManager(this));
        myRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.root));

     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {


            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            sendToStart();
        }


        FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(
                Users.class,
                R.layout.user_item,
                UsersViewHolder.class,myRef)

        {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, final Users model, int position)
            {
                usersViewHolder.setName(model.getName());
                usersViewHolder.setUserStatus(model.getStatus());
                usersViewHolder.setImage(model.getImage(), getApplicationContext());

                final String user_id = getRef(position).getKey();
                usersViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                        builder1.setMessage(R.string.set_message);
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                R.string.show_profile,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                                        profileIntent.putExtra("user_id", user_id);
                                        startActivity(profileIntent);

                                        dialog.cancel();
                                    }
                                });

                        builder1.setNegativeButton(
                                R.string.send_messages,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent chatIntent = new Intent(MainActivity.this, ChatActivity.class);
                                        chatIntent.putExtra("user_id", user_id);
                                        chatIntent.putExtra(getString(R.id.get_user_name), model.getName());
                                        startActivity(chatIntent);

                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                });
            }
        };
        usersList.setAdapter(firebaseRecyclerAdapter);


    }

    private void sendToStart() {
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        } else if (item.getItemId() == R.id.settings) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (resultCode == RESULT_OK) {
            }
            else
            {
                Toast.makeText(this, R.string.permission, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        TextView userName;
        TextView userStatus;

        CircleImageView userProfile;

        View view;

        public UsersViewHolder(View itemView)
        {
            super(itemView);
            view = itemView;
            userName = (TextView) view.findViewById(R.id.user_name);
            userStatus = (TextView) view.findViewById(R.id.user_status);
            userProfile = (CircleImageView) view.findViewById(R.id.user_profile);
        }


        public void setName(String name) {
            userName.setText(name);
        }

        public void setUserStatus(String userStatus) {
            this.userStatus.setText(userStatus);
        }

        public void setImage(String image, Context applicationContext)
        {
                Picasso.with(applicationContext).load(image).error(R.drawable.ma).placeholder(R.drawable.ma).into(userProfile);

        }
    }


}


