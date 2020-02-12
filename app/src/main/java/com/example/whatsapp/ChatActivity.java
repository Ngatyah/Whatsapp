package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity
{

    private String messageReceiverID, messageReceiverName,messageSenderID;
    private TextView userName, userLastSeen;
    private CircleImageView userProfilePic;
    private Toolbar chatToolbar;
    private ImageButton sendMessageButton;
    private  EditText messageInputTextEdit;

    private  FirebaseAuth myAuth;
    private DatabaseReference rootRef;


    private final List <Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdpter messageAdpter;
    private RecyclerView userMessagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        myAuth = FirebaseAuth.getInstance();
        messageSenderID = myAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();



        messageReceiverID = getIntent().getExtras().get("UserIDs").toString();
        messageReceiverName = getIntent().getExtras().get("UserName").toString();






        Initilization();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendMessage();
            }
        });



        userName.setText(messageReceiverName);



        rootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild("image"))
                {
                   String messageReceiverImage =dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userProfilePic);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        View view;



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

        sendMessageButton = (ImageButton) findViewById(R.id.private_send_message_button);
        messageInputTextEdit = (EditText) findViewById(R.id.private_chat_input);

        messageAdpter =  new MessageAdpter(messagesList);
        userMessagesList = (RecyclerView) findViewById(R.id.private_chat_list);
        Context context;
        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdpter);

    }


    private void DisplayLastSeen()
    {
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy" );

        final String currentDate=currentDateFormat.format(callForDate.getTime());
        rootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if(dataSnapshot.child("userState").hasChild("status"))
                {
                    String status =dataSnapshot.child("userState").child("status").getValue().toString();
                    String date =dataSnapshot.child("userState").child("date").getValue().toString();
                    String time =dataSnapshot.child("userState").child("time").getValue().toString();


                    if(status.equals("Online"))
                    {
                        userLastSeen.setText("Online");


                    }
                    else if(status.equals("Offline"))
                    {
                        if(date.equals(currentDate))
                        {

                            userLastSeen.setText("Last Seen " + time);
                        }


                        else
                        {
                            userLastSeen.setText("Last Seen " +date +"  "+ time);


                        }


                    }


                }
                else
                {
                    userLastSeen.setText("Offline");

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }





    @Override
    protected void onStart()
    {
        super.onStart();
        DisplayLastSeen();
                



        rootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {
                        Messages messages = dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);

                        messageAdpter.notifyDataSetChanged();

                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot)
                    {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });


    }

    private void SendMessage()
    {
        String message = messageInputTextEdit.getText().toString();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Enter text", Toast.LENGTH_SHORT).show();

        }
        else
        {
             String messageSenderRef = "Messages/"+messageSenderID+ "/"+ messageReceiverID;
             String messageReceiverRef = "Messages/"+messageReceiverID+ "/"+ messageSenderID;


             DatabaseReference userMessageKeyRef = rootRef.child("Messages").child(messageSenderID)
                     .child(messageReceiverID).push();

             String userMessageKey= userMessageKeyRef.getKey();

            Map messageBody = new HashMap();
            messageBody.put("message",message);
            messageBody.put("type","text");
            messageBody.put("from",messageSenderID);


            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef +"/"+ userMessageKey ,messageBody);
            messageBodyDetails.put(messageReceiverRef +"/"+ userMessageKey ,messageBody);


            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this,"Message Sent",Toast.LENGTH_SHORT).show();
                    }
                    else
                     {
                         Toast.makeText(ChatActivity.this,"Error",Toast.LENGTH_SHORT).show();
                     }

                    messageInputTextEdit.setText("");

                }
            });



        }

    }
//    private void UpdateUserStatus(String status)
//    {
//        String saveCurrentTime, saveCurrentDate;
//
//        Calendar callForDate = Calendar.getInstance();
//        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy" );
//
//        saveCurrentDate =currentDateFormat.format(callForDate.getTime());
//
//        Calendar callForTime = Calendar.getInstance();
//        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
//
//        saveCurrentTime=currentTimeFormat.format(callForTime.getTime());
//
//
//        HashMap<String, Object>CurrentStateMap = new HashMap<>();
//        CurrentStateMap.put("time",saveCurrentTime);
//        CurrentStateMap.put("date",saveCurrentDate);
//        CurrentStateMap.put("status",status);
//
//
//        rootRef.child("Users").child(messageReceiverID).child("userState").updateChildren(CurrentStateMap);
//
//
//
//    }
}
