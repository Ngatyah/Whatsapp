package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private ImageButton sendMesssageButton;
    private EditText userMessageInput;
    private ScrollView myScrollView;
    private TextView displayTexMessages;
    private String currentGroupName, currentUserId, currentUserName,currentDate,currentTime;
    private FirebaseAuth myAuth;
    private DatabaseReference userRef, groupNameRef,groupKeyMessageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);



        currentGroupName=getIntent().getExtras().get("Group Name").toString();
        Toast.makeText(GroupChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();


        myAuth=FirebaseAuth.getInstance();
        currentUserId=myAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        groupNameRef= FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);






        InitializeFields();

        GetUserinfo();

        

        sendMesssageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
                SaveMessageToDatabase();

                userMessageInput.setText("");

                myScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();


        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {

                if (dataSnapshot.exists())
                {

                    DisplayMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {


                if (dataSnapshot.exists())
                {

                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private void InitializeFields()
    {
        myToolbar=(Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(currentGroupName);
        sendMesssageButton= (ImageButton) findViewById(R.id.send_message_button);
        userMessageInput= (EditText)findViewById(R.id.input_group_message);
        myScrollView= (ScrollView) findViewById(R.id.my_scroll_view);
        displayTexMessages=(TextView)findViewById(R.id.group_chat_text_display);



    }






    private void GetUserinfo()
    {

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)

            {

                if(dataSnapshot.exists())
                {
                    currentUserName=dataSnapshot.child("name").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void SaveMessageToDatabase()
    {
        String message= userMessageInput.getText().toString();
        String messageKey= groupNameRef.push().getKey();

        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(GroupChatActivity.this,"Write message",Toast.LENGTH_SHORT).show();

        }
        else
        {
            Calendar callForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy" );

            currentDate=currentDateFormat.format(callForDate.getTime());


            Calendar callForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");

            currentTime=currentTimeFormat.format(callForTime.getTime());



            HashMap<String, Object>groupMessageKey = new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);

            groupKeyMessageRef=groupNameRef.child(messageKey);


            HashMap<String, Object>messageInfoMap = new HashMap<>();

            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("Time", currentTime);
            messageInfoMap.put("Date", currentDate);
            groupKeyMessageRef.updateChildren(messageInfoMap);



        }


    }




    private void DisplayMessages(DataSnapshot dataSnapshot)
    {

        Iterator iterator= dataSnapshot.getChildren().iterator();

        while(iterator.hasNext())
        {
            String chatDate=(String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime=(String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage=(String) ((DataSnapshot)iterator.next()).getValue();
            String chatName=(String) ((DataSnapshot)iterator.next()).getValue();



            displayTexMessages.append(chatName +": \n" + chatMessage + "\n" +chatTime  +"          "+ chatDate +"\n\n\n");



               myScrollView.fullScroll(ScrollView.FOCUS_DOWN);

        }
    }





}
