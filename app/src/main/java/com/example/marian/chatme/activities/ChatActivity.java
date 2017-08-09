package com.example.marian.chatme.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marian.chatme.MessagesAdapter;
import com.example.marian.chatme.R;
import com.example.marian.chatme.model.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {



    @BindView(R.id.chat_bar)
    Toolbar chatToolbar;

    @BindView(R.id.user_name_title)
    TextView chatBarTitle;
    @BindView(R.id.chat_bar_image)
    CircleImageView chatBarImage;

    @BindView(R.id.messages_RV)
    RecyclerView messages;

    @BindView(R.id.send_img_btn)
    ImageButton sendImgBtn;
    @BindView(R.id.chat_message)
    EditText chatMessage;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    private String currentUserId,chatUser, userName;

    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;
    private List<Messages> messagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        setSupportActionBar(chatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        myRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();


        chatUser = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("user_name");

        getSupportActionBar().setTitle(userName);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_bar_item, null);
        actionBar.setCustomView(action_bar_view);

        linearLayoutManager = new LinearLayoutManager(this);
        messages.setHasFixedSize(true);
        messages.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(messagesList);
        messages.setAdapter(messagesAdapter);
        loadMessages();


//        chatBarTitle.setText(userName);


        myRef.child("Chat").child(chatUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(chatUser)) {
                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + currentUserId + "/" + chatUser, chatAddMap);
                    chatUserMap.put("Chat/" + chatUser + "/" + currentUserId, chatAddMap);

                    myRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(ChatActivity.this, databaseError.toString(), Toast.LENGTH_LONG).show();
            }
        });

        sendImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });

    }

    private void loadMessages() {
        myRef.child("messages").child(currentUserId).child(chatUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = dataSnapshot.getValue(Messages.class);

                messagesList.add(message);
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    void sendMessage() {
        String message = chatMessage.getText().toString();

        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(ChatActivity.this, message, Toast.LENGTH_LONG).show();
            String current_user_ref = "messages/" + currentUserId + "/" + chatUser;
            String chat_user_ref = "messages/" + chatUser + "/" + currentUserId;

            DatabaseReference user_message_push = myRef.child("messages")
                    .child(currentUserId).child(chatUser).push();
            String push_id = user_message_push.getKey();

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("type", "text");
            messageMap.put("time", ServerValue.TIMESTAMP);
            messageMap.put("from", currentUserId);

            Map messageUserMap = new HashMap();
            messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
            messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);


            chatMessage.setText("");

            myRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e("CHAT_APP", databaseError.getMessage().toString());
                    }
                }
            });

        }

    }


}
