package com.example.whatsapp;

import android.graphics.Color;
import android.hardware.camera2.params.BlackLevelPattern;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.ConversationActions;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdpter extends RecyclerView.Adapter<MessageAdpter.MessageViewHolder>
{

    private List<Messages> usermessageList;
    private FirebaseAuth MyAuth;
    private DatabaseReference userRef;

    public MessageAdpter (List<Messages> usermessageList)
    {

        this.usermessageList = usermessageList;

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder

    {
        public TextView sendMessageText, receiverMessageText;
        public CircleImageView receiverMessageProfilepic;

        public MessageViewHolder(@NonNull View itemView)

        {
            super(itemView);

            sendMessageText = (TextView) itemView.findViewById(R.id.sender_message_TextView);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_TextView);
            receiverMessageProfilepic = (CircleImageView) itemView. findViewById(R.id.message_profile_pic);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_messages_layout, parent, false);

        MyAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position)
    {
        String messageSenderID = MyAuth.getCurrentUser().getUid();

        Messages message = usermessageList.get(position);


        String fromUserID = message.getFrom();
        String fromMessageType = message.getType();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        userRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild("image"))
                {
                    final String receiverImage =  dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile_image).into(holder.receiverMessageProfilepic);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });


        if(fromMessageType.equals("text"))
        {
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.receiverMessageProfilepic.setVisibility(View.INVISIBLE);
            holder.sendMessageText.setVisibility(View.INVISIBLE);
        }

        if(fromUserID.equals(messageSenderID))

        {
            holder.sendMessageText.setVisibility(View.VISIBLE);
            holder.sendMessageText.setBackgroundResource(R.drawable.sender_messages_layerout);
            holder.sendMessageText.setText(message.getMessage());
            holder.sendMessageText.setTextColor(Color.BLACK);
        }
        else
        {

            holder.receiverMessageProfilepic.setVisibility(View.VISIBLE);
            holder.receiverMessageText.setVisibility(View.VISIBLE);


            holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_message_layout);
            holder.receiverMessageText.setText(message.getMessage());
            holder.receiverMessageText.setTextColor(Color.BLACK);

        }




    }

    @Override
    public int getItemCount()
    {
        return usermessageList.size();
    }




}
