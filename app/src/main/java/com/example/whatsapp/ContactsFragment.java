package com.example.whatsapp;


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

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment
{
    private View contactView;
    private RecyclerView myContactList;
    private DatabaseReference contactRef,usersRef;
    private FirebaseAuth myAuth;
    private String currentUserId;


    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contactView = inflater.inflate(R.layout.fragment_contacts, container, false);

        myContactList = (RecyclerView) contactView.findViewById(R.id.contact_list);
        myContactList.setLayoutManager(new LinearLayoutManager(getContext()));


        myAuth =FirebaseAuth.getInstance();

        currentUserId=myAuth.getCurrentUser().getUid();

        contactRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserId);
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return contactView;
    }


    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactRef,Contacts.class)
                .build();



        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder > adapter
                = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder contactsViewHolder, int i, @NonNull Contacts contacts)
            {
                String userIDs= getRef(i).getKey();

                usersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.hasChild("image"))
                        {
                            String userImage = dataSnapshot.child("image").getValue().toString();
                            String userStatus = dataSnapshot.child("status").getValue().toString();
                            String userName = dataSnapshot.child("name").getValue().toString();



                            contactsViewHolder.profileName.setText(userName);
                            contactsViewHolder.profileStatus.setText(userStatus);

                            Picasso.get().load(userImage).into(contactsViewHolder.profileImage);



                        }
                        else
                        {
                            String userStatus= dataSnapshot.child("status").getValue().toString();
                            String userName = dataSnapshot.child("name").getValue().toString();


                            contactsViewHolder.profileName.setText(userName);
                            contactsViewHolder.profileStatus.setText(userStatus);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);

                ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                return viewHolder;

            }
        };


        myContactList.setAdapter(adapter);
        adapter.startListening();
    }



    public static class ContactsViewHolder extends RecyclerView.ViewHolder
    {

        TextView profileName,profileStatus;
        CircleImageView profileImage;


        public ContactsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            profileImage= itemView.findViewById(R.id.user_profile_image);
            profileName= itemView.findViewById(R.id.user_profile_name);
           profileStatus = itemView.findViewById(R.id.user_status);
        }
    }
}
