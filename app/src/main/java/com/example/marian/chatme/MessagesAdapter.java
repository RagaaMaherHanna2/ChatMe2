package com.example.marian.chatme;


import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.marian.chatme.model.Messages;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    private List<Messages> messages;
    private FirebaseAuth mAuth;

    public MessagesAdapter(List<Messages> mMessagesList) {
        this.messages = mMessagesList;
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessagesViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        Messages message = this.messages.get(position);
        String from_user = message.getFrom();

        if (from_user.equals(uid)) {

            holder.messageText.setBackgroundColor(Color.WHITE);
            holder.messageText.setTextColor(Color.BLACK);
        } else {
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
        }

        holder.messageText.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;

        public MessagesViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.message_text);
            profileImage = (CircleImageView) itemView.findViewById(R.id.message_profile);
        }
    }
}
