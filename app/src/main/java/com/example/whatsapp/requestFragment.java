package com.example.whatsapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class requestFragment extends Fragment
{
    private View requestfragmentView;
    private RecyclerView myRequestView;
    private String currentUserID;

    private DatabaseReference chatRequestRef,userRef,contactRef;
    private FirebaseAuth myAuth;


    public requestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        requestfragmentView = inflater.inflate(R.layout.fragment_request, container, false);

        myRequestView= (RecyclerView) requestfragmentView.findViewById(R.id.friend_request_list);
        myRequestView.setLayoutManager(new LinearLayoutManager(getContext()));


         contactRef= FirebaseDatabase.getInstance().getReference().child("Contacts");
        chatRequestRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        userRef =FirebaseDatabase.getInstance().getReference().child("Users");
        myAuth=FirebaseAuth.getInstance();
        currentUserID = myAuth.getCurrentUser().getUid();



        return requestfragmentView;



    }


    @Override
    public void onStart()
    {
        super.onStart();

        Query queryRequestType = chatRequestRef.child(currentUserID).orderByChild("request_type").equalTo("received");


        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(queryRequestType, Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts, RecylerViewHolder> adapter
                = new FirebaseRecyclerAdapter<Contacts, RecylerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RecylerViewHolder recylerViewHolder, int i, @NonNull Contacts contacts)

            {
                recylerViewHolder.itemView.findViewById(R.id.accept_chat_request_button).setVisibility(View.VISIBLE);
                recylerViewHolder.itemView.findViewById(R.id.cancel_chat_request_button).setVisibility(View.VISIBLE);


                final String user_list_ids = getRef(i).getKey();

                DatabaseReference getTypeRef = getRef(i).child("request_type").getRef();


                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            String type = dataSnapshot.getValue().toString();

                            if(type.equals("received"))
                            {

                                userRef.child(user_list_ids).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if(dataSnapshot.hasChild("image"))
                                        {
                                            final String RequestImage = dataSnapshot.child("image").getValue().toString();


                                            Picasso.get().load(RequestImage).into(recylerViewHolder.userProfileImage);


                                        }


                                            final String RequestName = dataSnapshot.child("name").getValue().toString();
                                            final String RequestStatus = dataSnapshot.child("status").getValue().toString();



                                            recylerViewHolder.userName.setText(RequestName);
                                            recylerViewHolder.userStatus.setText(RequestStatus);



                                        recylerViewHolder.itemView.findViewById(R.id.accept_chat_request_button).setOnClickListener(new View
                                                .OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                contactRef.child(currentUserID).child(user_list_ids).child("Contacts").setValue("saved")
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if(task.isSuccessful())
                                                                {
                                                                    contactRef.child(user_list_ids).child(currentUserID).child("Contacts").setValue("saved")
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if(task.isSuccessful())
                                                                                    {
                                                                                        chatRequestRef.child(currentUserID).child(user_list_ids)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                                {
                                                                                                     @Override
                                                                                                     public void onComplete(@NonNull Task<Void> task)
                                                                                                     {
                                                                                                         if (task.isSuccessful())
                                                                                                         {
                                                                                                             chatRequestRef.child(user_list_ids).child(currentUserID)
                                                                                                                     .removeValue()
                                                                                                                     .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                                                     {
                                                                                                                         @Override
                                                                                                                         public void onComplete(@NonNull Task<Void> task)
                                                                                                                         {
                                                                                                                             if (task.isSuccessful())
                                                                                                                             {
                                                                                                                                 Toast.makeText(getContext(),"contact Saved",Toast.LENGTH_SHORT).show();

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
                                        });

                                        recylerViewHolder.itemView.findViewById(R.id.cancel_chat_request_button).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                chatRequestRef.child(currentUserID).child(user_list_ids)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>()
                                                        {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    chatRequestRef.child(user_list_ids).child(currentUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>()
                                                                            {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if (task.isSuccessful())
                                                                                    {
                                                                                        Toast.makeText(getContext(),"Request Deleted",Toast.LENGTH_SHORT).show();

                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });



                                            }
                                        });




                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError)
                                    {

                                    }
                                });

                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public RecylerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)

            {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);

                RecylerViewHolder recylerViewHolder = new RecylerViewHolder(view);
                return recylerViewHolder;
            }

        };

        myRequestView.setAdapter(adapter);
        adapter.startListening();
    }



    public static class RecylerViewHolder extends RecyclerView.ViewHolder
    {

        TextView userName,userStatus;
        CircleImageView userProfileImage;
        Button AcceptButton,CancelButton;


        public RecylerViewHolder(@NonNull View itemView)

        {
            super(itemView);


            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            userProfileImage = itemView.findViewById(R.id.user_profile_image);
            AcceptButton = itemView.findViewById(R.id.accept_chat_request_button);
            CancelButton = itemView.findViewById(R.id.cancel_chat_request_button);

        }
    }

}
