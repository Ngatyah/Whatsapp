package com.example.whatsapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChartFragment extends Fragment
{
    private View privateChatView;
    private  RecyclerView chatList;

    private DatabaseReference chatRef,usersRef;
    private FirebaseAuth myAuth;
    private  String currentUserID;



    public ChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChatView = inflater.inflate(R.layout.fragment_chart, container, false);

        myAuth = FirebaseAuth.getInstance();
        currentUserID= myAuth.getCurrentUser().getUid();
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        chatRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);

        chatList =(RecyclerView) privateChatView.findViewById(R.id.chat_list);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));



        return  privateChatView;
    }


    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions<Contacts>options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatRef, Contacts.class)
                .build();



        FirebaseRecyclerAdapter<Contacts,ChatViewHolder > adapter=
        new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull final ChatViewHolder chatViewHolder, int i, @NonNull Contacts contacts)
            {
                Calendar callForDate = Calendar.getInstance();
                SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy" );

                final String currentDate=currentDateFormat.format(callForDate.getTime());

                Calendar callForTime = Calendar.getInstance();
                SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");

               String currentTime=currentTimeFormat.format(callForTime.getTime());


                final String usersIDs= getRef(i).getKey();

                usersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {


                        if(dataSnapshot.hasChild("image"))
                        {
                            final String chatImage  = dataSnapshot.child("image").getValue().toString();

                           Picasso.get().load(chatImage).into(chatViewHolder.privateChatProfileImage);
                        }

                      if(dataSnapshot.child("userState").hasChild("status"))
                      {
                            String status =dataSnapshot.child("userState").child("status").getValue().toString();
                            String date =dataSnapshot.child("userState").child("date").getValue().toString();
                            String time =dataSnapshot.child("userState").child("time").getValue().toString();


                            if(status.equals("Online"))
                            {
                                chatViewHolder.privatechatStatus.setText("Online");


                            }
                            else if(status.equals("Offline"))
                            {
                                if(date.equals(currentDate))

                                    chatViewHolder.privatechatStatus.setText("Last Seen" + "\n"+ time);


                                else
                                {
                                    chatViewHolder.privatechatStatus.setText("Last Seen" + "\n"+date +"  "+ time);


                                }

                            }
                      }


                      else
                      {
                          chatViewHolder.privatechatStatus.setText("Offline");

                       }

                        final String chatImage  = dataSnapshot.child("image").getValue().toString();

                        Picasso.get().load(chatImage).into(chatViewHolder.privateChatProfileImage);

                        final String chatUserName = dataSnapshot.child("name").getValue().toString();
                        final String chatStatus = dataSnapshot.child("status").getValue().toString();


                        final Uri myUri = Uri.parse(chatImage);


                        chatViewHolder.privateChatUserName.setText(chatUserName);
                        chatViewHolder.itemView.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent chatIntent = new Intent(getContext(),ChatActivity.class);
                                chatIntent.putExtra("UserName",chatUserName);
                                chatIntent.putExtra("UserImage", myUri);
                                chatIntent.putExtra("UserIDs", usersIDs);
                                startActivity(chatIntent);


//                                Toast.makeText(getContext(), chatImage + "\n"+ chatUserName, Toast.LENGTH_SHORT).show();




                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });

 

            }

            @NonNull
            @Override
            public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);

                return  new ChatViewHolder(view);

            }
        };


        chatList.setAdapter(adapter);
        adapter.startListening();
    }



    public  static class ChatViewHolder extends  RecyclerView.ViewHolder

    {

        CircleImageView privateChatProfileImage;
        TextView privateChatUserName, privatechatStatus;




        public ChatViewHolder(@NonNull View itemView)
        {
            super(itemView);


            privateChatProfileImage= (CircleImageView) itemView.findViewById(R.id.user_profile_image);
            privateChatUserName  = (TextView) itemView. findViewById(R.id.user_profile_name);
            privatechatStatus = (TextView) itemView. findViewById(R.id.user_status);






        }
    }
}
