package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String  receiveUserId;
    private CircleImageView userProfileImage;
    private TextView visitUserName,visitUserProfileStatus;
    private Button sendMsgRequest;
    private DatabaseReference visitRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        receiveUserId=getIntent().getExtras().get("visit_user_id").toString();





        userProfileImage=(CircleImageView) findViewById(R.id.visit_profile_image);
        visitUserName=(TextView)findViewById(R.id.visit_user_name);
        visitUserProfileStatus=(TextView)findViewById(R.id.visit_status);
        sendMsgRequest=(Button) findViewById(R.id.visit_request_send_message);
        visitRef= FirebaseDatabase.getInstance().getReference().child("Users");



        retriveUserInfo();
    }

    private void retriveUserInfo()
    {
        visitRef.child(receiveUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists() && dataSnapshot.hasChild("image"))
                {
                  String userImage = dataSnapshot.child("image").getKey().toString();
                  String userStatus= dataSnapshot.child("status").getKey().toString();
                  String userName= dataSnapshot.child("name").getKey().toString();


                  Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                  visitUserName.setText(userName);
                  visitUserProfileStatus.setText(userStatus);
                }
                else
                {
                    String userStatus= dataSnapshot.child("status").getKey().toString();
                    String userName= dataSnapshot.child("name").getKey().toString();


                    visitUserName.setText(userName);
                    visitUserProfileStatus.setText(userStatus);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
