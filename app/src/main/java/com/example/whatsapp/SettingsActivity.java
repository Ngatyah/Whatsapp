package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class SettingsActivity extends AppCompatActivity {

    private Button updateAccountSettingsButton;
    private EditText userName, userStatus;
    private CircleImageView userProfileImage;
    private String currentUserID;
    private FirebaseAuth myAuth;
    private DatabaseReference rootRef;
    private final static int gallareyPick=1;
    private StorageReference userProfileImageReference;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        InitializeFields();

        myAuth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();
        currentUserID=myAuth.getCurrentUser().getUid();


        userProfileImageReference= FirebaseStorage.getInstance().getReference().child("Profile Images");

        updateAccountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {
                UpdateSettings();
            }
        });

        RetrieveUserInfo();
        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent gallareyIntnet = new Intent();
                gallareyIntnet.setAction(Intent.ACTION_GET_CONTENT);
                gallareyIntnet.setType("image/*");
                startActivityForResult(gallareyIntnet,gallareyPick);
            }
        });

    }




    private void InitializeFields()
    {

        updateAccountSettingsButton=(Button)findViewById(R.id.update_setting_button);
        userName=(EditText)findViewById(R.id.set_username);
        userStatus=(EditText)findViewById(R.id.set_status);
        userProfileImage=(CircleImageView) findViewById(R.id.set_profile_image);
        loadingbar=new ProgressDialog(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)

    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallareyPick && resultCode == RESULT_OK && data != null)

        {
            Uri imageuri=data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if(resultCode== RESULT_OK)
            {
                loadingbar.setTitle(" Profile Pic Updating");
                loadingbar.setMessage("Pleae wait as we Update your profile....");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
                Uri resultUri = result.getUri();
                final StorageReference filePath = userProfileImageReference.child(currentUserID+".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)

                            {
                                Uri pictureUrl = uri;
                                String downloadUrl=pictureUrl.toString();
                                rootRef.child("Users").child(currentUserID).child("image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(SettingsActivity.this, "Picture Saved Successfully", Toast.LENGTH_SHORT).show();
                                            loadingbar.dismiss();
                                        }
                                        else
                                        {
                                            String message = task.getException().toString();
                                            Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                            loadingbar.dismiss();
                                        }

                                    }
                                });
                            }
                        });

                    }
                });



            }


        }
    }






    private void UpdateSettings()
    {
        String setUserName= userName.getText().toString();
        String setUserStatus= userStatus.getText().toString();

        if(TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(SettingsActivity.this,"Enter User Name", Toast.LENGTH_SHORT).show();

        }
        if(TextUtils.isEmpty(setUserStatus))
        {
            Toast.makeText(SettingsActivity.this,"Add Your Status", Toast.LENGTH_SHORT).show();

        }
        else
        {

            HashMap<String,String>profileMap=new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("name",setUserName);
            profileMap.put("status",setUserStatus);

            rootRef.child("Users").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if(task.isSuccessful())
                            {
                                sendUserToMainActivity();
                                Toast.makeText(SettingsActivity.this,"Profile updated",Toast.LENGTH_SHORT).show();


                            }
                            else
                            {
                                String message= task.getException().toString();
                                Toast.makeText(SettingsActivity.this,"Error: "+message,Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

        }


    }

    private void RetrieveUserInfo()
    {

        rootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)

            {
                if((dataSnapshot.exists()) &&(dataSnapshot.hasChild("name"))&&(dataSnapshot.hasChild("image")))
                {

                    String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                    String retrieveUserStatus=dataSnapshot.child("status").getValue().toString();
                    String retrieveImageProfile=dataSnapshot.child("image").getValue().toString();



                        userName.setText(retrieveUserName);
                        userStatus.setText(retrieveUserStatus);
                        Picasso.get().load(retrieveImageProfile).into(userProfileImage);


                }
                else if((dataSnapshot.exists()) &&(dataSnapshot.hasChild("name")))
                {

                    String retrieveUserName=dataSnapshot.child("name").getValue().toString();
                    String retrieveUserStatus=dataSnapshot.child("status").getValue().toString();




                    userName.setText(retrieveUserName);
                    userStatus.setText(retrieveUserStatus);

                }
                else
                {

                    Toast.makeText(SettingsActivity.this,"set and Update Profile",Toast.LENGTH_SHORT).show();

                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendUserToMainActivity() {

        Intent mainIntent= new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(mainIntent);

    }
}
