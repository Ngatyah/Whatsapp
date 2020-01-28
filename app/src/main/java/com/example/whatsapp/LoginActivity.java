package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
{


    private Button loginButton, phoneLoginButton;
    private EditText userEmail, userPassword;
    private TextView newAccount, forgetPassword;
    private FirebaseAuth myAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitializeField();
        myAuth= FirebaseAuth.getInstance();




        newAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)

            {
                sendUserToRegisterActivity();

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                AllowUserToLogin();
            }
        });

        phoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToPhoneLoginActivity();
            }
        });



    }



    private void AllowUserToLogin()
    {
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
                loadingBar.setTitle("Logging In");
                loadingBar.setMessage("Please Wait as we Log in");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();
                myAuth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            sendUserToMainActivity();
                            Toast.makeText(LoginActivity.this,"Logged In Successfully",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }
                        else
                            {
                            String message = task.getException().toString();
                            Toast.makeText(LoginActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();

                        }

                    }
                });

            }
        }

    }

    private void InitializeField()
    {
        loginButton=(Button) findViewById(R.id.login_button);
        phoneLoginButton=(Button)findViewById(R.id.phone_login_button);
        userEmail=(EditText)findViewById(R.id.login_email);
        userPassword=(EditText)findViewById(R.id.login_password);
        newAccount= (TextView)findViewById(R.id.new_account);
        forgetPassword=(TextView)findViewById(R.id.forget_password_link);
        loadingBar= new ProgressDialog(this);
    }


    private void sendUserToMainActivity() {

        Intent mainIntent= new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void sendUserToRegisterActivity() {

        Intent registerIntent= new Intent(LoginActivity.this,RegisterActivity.class);
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(registerIntent);
    }


    private void SendUserToPhoneLoginActivity()
    {
        Intent phoneloginIntent= new Intent(LoginActivity.this,Phone_Login_Activity.class);
        phoneloginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(phoneloginIntent);
        finish();

    }




}
