package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter myTabsAccessorAdapter;
    private FirebaseUser currentUser;
    private FirebaseAuth myAuth;
    private DatabaseReference rootRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAuth=FirebaseAuth.getInstance();
        currentUser=myAuth.getCurrentUser();
        rootRef=FirebaseDatabase.getInstance().getReference();
        Log.d("begin_TOKEN", "------->");
        currentUserID = myAuth.getCurrentUser().getUid();
        Log.d("end_TOKEN", currentUserID);

        mToolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Whatsapp");

        myViewPager= (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter= new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);


        myTabLayout= (TabLayout)findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        if(currentUser==null)
        {
           sendUserToLoginActivity();

        }
        else
        {
            UpdateUserStatus("Online");
            verifyUserExistence();
        }
    }

    @Override
    protected void onStop()
    {

        super.onStop();
        if(currentUser !=null)
        {
            UpdateUserStatus("Offline");

        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        if(currentUser !=null)
        {
            UpdateUserStatus("Offline");

        }


    }

    private void verifyUserExistence()

    {
        String currentUserID=myAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this,"Welcome",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendUserToSettingsActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToLoginActivity()
    {
        Intent loginIntent= new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu,menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         if(item.getItemId()==R.id.logout_option)
         {
             myAuth.signOut();

             sendUserToLoginActivity();
             Toast.makeText(this,"Logged out Successfully",Toast.LENGTH_SHORT).show();

         }
        if(item.getItemId()==R.id.create_group_option)
        {

            requestNewGroup();
        }


        if(item.getItemId()==R.id.settings_option)
        {
            sendUserToSettingsActivity();

        }

        if(item.getItemId()==R.id.find_friends)
        {

            sendUserToFindFriendsActivity();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
        }

        return true;

    }

    private void requestNewGroup()
    {

        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Create Group: ");


        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("eg. Wazito, Kamiti etc");
        builder.setView(groupNameField);


        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                String groupName=groupNameField.getText().toString();

                if(TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Add Group Name!", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    CreateNewGroup(groupName);

                }
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });


        builder.show();
    }

    private void CreateNewGroup(final String groupName)
    {
        rootRef.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this,groupName+" Created Successfully",Toast.LENGTH_SHORT).show();

                }

            }
        });

    }


    private void sendUserToSettingsActivity()
    {
        Intent settingsIntent= new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(settingsIntent);

    }



    private void sendUserToFindFriendsActivity()
    {
        Intent findFreindsInent= new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(findFreindsInent);


    }




    private void UpdateUserStatus(String status)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy" );

        saveCurrentDate =currentDateFormat.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");

        saveCurrentTime=currentTimeFormat.format(callForTime.getTime());


        HashMap<String, Object>CurrentStateMap = new HashMap<>();
        CurrentStateMap.put("time",saveCurrentTime);
        CurrentStateMap.put("date",saveCurrentDate);
        CurrentStateMap.put("status",status);


        rootRef.child("Users").child(currentUserID).child("userState").updateChildren(CurrentStateMap);



    }
}   
