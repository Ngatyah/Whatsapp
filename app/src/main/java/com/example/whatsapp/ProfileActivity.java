package com.example.whatsapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String  receiveUserId,senderUserId, current_State;
    private CircleImageView userProfileImage;
    private TextView visitUserName,visitUserProfileStatus;
    private Button sendMsgRequest, decline_message_request_Button;

    private DatabaseReference visitRef,chatRequestRef,contactRef,notificationRef;
    private FirebaseAuth MyAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();


        receiveUserId=intent.getExtras().getString("visitUserId");

        visitRef= FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactRef= FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationRef= FirebaseDatabase.getInstance().getReference().child("Notifications");
        MyAuth=FirebaseAuth.getInstance();




        userProfileImage=(CircleImageView) findViewById(R.id.visit_profile_image);
        visitUserName=(TextView)findViewById(R.id.visit_user_name);
        visitUserProfileStatus=(TextView)findViewById(R.id.visit_status);
        sendMsgRequest=(Button) findViewById(R.id.visit_request_send_message);
        decline_message_request_Button=(Button)findViewById(R.id.decline_message_request_Button);


        senderUserId=MyAuth.getCurrentUser().getUid();
        current_State="new";




        retrieveUserInfo();
    }

    private void retrieveUserInfo()
    {
        visitRef.child(receiveUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists() && dataSnapshot.hasChild("image"))
                {
                  String userImage = dataSnapshot.child("image").getValue().toString();
                  String userStatus= dataSnapshot.child("status").getValue().toString();
                  String userName= dataSnapshot.child("name").getValue().toString();


                  Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                  visitUserName.setText(userName);
                  visitUserProfileStatus.setText(userStatus);



                  ManageChatRequests();


                }
                else
                {
                    String userStatus= dataSnapshot.child("status").getValue().toString();
                    String userName= dataSnapshot.child("name").getValue().toString();


                    visitUserName.setText(userName);
                    visitUserProfileStatus.setText(userStatus);



                    ManageChatRequests();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void ManageChatRequests()

    {
        chatRequestRef.child(senderUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.hasChild(receiveUserId))
                {
                    String request_type = dataSnapshot.child(receiveUserId).child("request_type")
                            .getValue().toString();
                    if(request_type.equals("sent"))
                    {
                        current_State="request_sent";
                        sendMsgRequest.setText("Cancel Request");
                    }
                    else if (request_type.equals("received"))
                    {
                        current_State="request_received";
                        sendMsgRequest.setText("Accept Message Request");

                        decline_message_request_Button.setVisibility(View.VISIBLE);
                        decline_message_request_Button.setEnabled(true);
                        decline_message_request_Button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                CancelChatRequest();
                            }
                        });

                    }
                }
                else
                {
                    contactRef.child(senderUserId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if(dataSnapshot.hasChild(receiveUserId))
                                    {
                                        current_State="friends";
                                        sendMsgRequest.setText("Remove Contact");
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError)
                                {

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(!senderUserId.equals(receiveUserId))
        {

            sendMsgRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                  sendMsgRequest.setEnabled(false);


                  if(current_State.equals("new"))
                  {
                      sendChatRequest();
                  }
                  if(current_State.equals("request_sent"))
                  {
                    CancelChatRequest();
                  }
                 if(current_State.equals("request_received"))
                 {
                     AcceptChatRequest();
                 }
                 if(current_State.equals("friends"))
                 {
                        RemoveContact();
                 }

                }
            });


        }
        else
        {
            sendMsgRequest.setVisibility(View.INVISIBLE);

        }

    }

    private void RemoveContact()
    {
        contactRef.child(senderUserId).child(receiveUserId).child("Contacts").removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            contactRef.child(receiveUserId).child(senderUserId).child("Contacts").removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            sendMsgRequest.setText("Contact Removed");
                                            sendMsgRequest.setEnabled(false);

                                        }
                                    });

                        }

                    }
                });
    }

    private void AcceptChatRequest()

    {
        contactRef.child(senderUserId).child(receiveUserId).child("Contacts")
                .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    contactRef.child(receiveUserId).child(senderUserId).child("Contacts")
                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {

                                chatRequestRef.child(senderUserId).child(receiveUserId)
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            chatRequestRef.child(receiveUserId).child(senderUserId)
                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if(task.isSuccessful())
                                                    {
                                                        sendMsgRequest.setEnabled(true);
                                                        current_State="friends";
                                                        sendMsgRequest.setText("Remove Contact");

                                                        decline_message_request_Button.setVisibility(View.INVISIBLE);
                                                        decline_message_request_Button.setEnabled(false);
                                                    }

                                                }
                                            });
                                        }

                                    }
                                });





                            }

                        }
                    });
                }

            }
        });

    }

    private void CancelChatRequest()
    {
        chatRequestRef.child(senderUserId).child(receiveUserId)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    chatRequestRef.child(receiveUserId).child(senderUserId)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                notificationRef.child(receiveUserId).removeValue();
                                sendMsgRequest.setEnabled(true);
                                current_State="new";
                                sendMsgRequest.setText("Send Chat Message");

                                decline_message_request_Button.setVisibility(View.INVISIBLE);
                            }

                        }
                    });

                }

            }
        });
    }


    private void sendChatRequest()
    {
        chatRequestRef.child(senderUserId).child(receiveUserId).child("request_type")
                .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
               if(task.isSuccessful())
               {

                   chatRequestRef.child(receiveUserId).child(senderUserId).child("request_type")
                           .setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task)
                       {
                           if(task.isSuccessful())
                           {

                               HashMap<String, String>chatNotification = new HashMap<>();
                               chatNotification.put("from",senderUserId);
                               chatNotification.put("type","request");


                               notificationRef.child(receiveUserId).push().setValue(chatNotification)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task)
                                   {
                                       if(task.isSuccessful())
                                       {
                                           sendMsgRequest.setEnabled(true);
                                           current_State="request_sent";
                                           sendMsgRequest.setText("Cancel Request");

                                       }

                                   }
                               });





                           }

                       }
                   });
               }
            }
        });

    }
}
