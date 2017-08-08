package com.example.marian.chatme.adapter;


import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.marian.chatme.R;
import com.example.marian.chatme.model.Messages;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    private List<Messages> mMessagesList;
    private FirebaseAuth mAuth;

    // constructor
    public MessagesAdapter(List<Messages> mMessagesList) {
        this.mMessagesList = mMessagesList;
    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);
        return new MessagesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MessagesViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        String current_user_id = mAuth.getCurrentUser().getUid();
        Messages c = mMessagesList.get(position);
        String from_user = c.getFrom();
        if (from_user.equals(current_user_id)) {

            holder.messageText.setBackgroundColor(Color.WHITE);
            holder.messageText.setTextColor(Color.BLACK);
        } else {
            holder.messageText.setBackgroundResource(R.drawable.message_text_background);
            holder.messageText.setTextColor(Color.WHITE);
        }

        holder.messageText.setText(c.getMessage());
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }


    //      innner class
    public class MessagesViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;

        public MessagesViewHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_layout);
        }
    }
}
