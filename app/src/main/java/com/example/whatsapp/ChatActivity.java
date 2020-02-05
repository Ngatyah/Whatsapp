package com.example.whatsapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity
{

    private String messageReceiverID, messageReceiverName, messageReceiverImage;
    private TextView userName, userLastSeen;
    private CircleImageView userProfilePic;
    private Toolbar chatToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        messageReceiverID = getIntent().getExtras().get("UserIDs").toString();
        messageReceiverName = getIntent().getExtras().get("UserName").toString();
        messageReceiverImage = getIntent().getExtras().get("UserImage").toString();





        Initilization();

        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverID).placeholder(R.drawable.profile_image).into(userProfilePic);
    }

    private void Initilization()

    {

        chatToolbar= (Toolbar) findViewById(R.id.private_chat_toolbar);
        setSupportActionBar(chatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         ViewGroup root;
         View view = layoutInflater.inflate(R.layout.activity_custom__chat_, null);
         actionBar.setCustomView(view);

        userName = (TextView) findViewById(R.id.custom_user_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        userProfilePic = (CircleImageView) findViewById(R.id.custom_profile_image);

    }
}
