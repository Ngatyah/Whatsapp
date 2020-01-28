package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText userEmail, userPassword;
    private TextView alreadyHaveAccount;
    private FirebaseAuth myAuth;
    private ProgressDialog laodingBar;
    private DatabaseReference rootref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InitializeFields();

        myAuth= FirebaseAuth.getInstance();
        rootref= FirebaseDatabase.getInstance().getReference();


        alreadyHaveAccount.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v)
            {
                sendUserToLoginActivity();

            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateNewAccount();
            }
        });


    }


    private void CreateNewAccount()

    {
        String email=userEmail.getText().toString();
        String password= userPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {

            Toast.makeText(this,"Please Enter Email",Toast.LENGTH_SHORT).show();
        }


        if(TextUtils.isEmpty(password))
        {

            Toast.makeText(this,"Please Enter password",Toast.LENGTH_SHORT).show();
        }

        else
        {

            laodingBar.setTitle("Creating a New Account");
            laodingBar.setMessage("Please Wait as we Create your Account");
            laodingBar.setCanceledOnTouchOutside(true);
            laodingBar.show();

            myAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                String currentUserId=myAuth.getCurrentUser().getUid();
                                rootref.child("Users").child(currentUserId).setValue("");
                                sendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this,"Account Created Successful",Toast.LENGTH_SHORT).show();

                                laodingBar.dismiss();

                            }
                            else
                            {
                               String message= task.getException().toString();
                               Toast.makeText(RegisterActivity.this,"Error :"+message,Toast.LENGTH_SHORT).show();

                               laodingBar.dismiss();
                            }

                        }
                    });


        }

    }


    private void InitializeFields()

    {

        createAccountButton=(Button)findViewById(R.id.register_button);
        userEmail=(EditText)findViewById(R.id.register_email);
        userPassword=(EditText)findViewById(R.id.register_password);
        alreadyHaveAccount=(TextView)findViewById(R.id.already_have_account);
        laodingBar= new ProgressDialog(this);
    }


    private void sendUserToLoginActivity() {

        Intent loginIntent= new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    private void sendUserToMainActivity() {

        Intent mainIntent= new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);

    }
}
