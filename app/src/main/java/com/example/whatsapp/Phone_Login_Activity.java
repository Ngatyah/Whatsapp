package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Phone_Login_Activity extends AppCompatActivity {


    private Button sendVerificationCodeButton, verifyButton;
    private EditText phoneNumberInput, verificationCodeInput;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken  mResendToken;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone__login_);

       sendVerificationCodeButton=(Button)findViewById(R.id.send_verification_button);
       verifyButton=(Button)findViewById(R.id.confirm_verification_button);
       phoneNumberInput=(EditText)findViewById(R.id.phone_input);
       verificationCodeInput=(EditText)findViewById(R.id.verification_code_input);
       mAuth=FirebaseAuth.getInstance();
       Context context;
       loadingBar= new ProgressDialog(this);



       sendVerificationCodeButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)

           {

               String phoneNumber= phoneNumberInput.getText().toString();


               if(TextUtils.isEmpty(phoneNumber))
               {
                   Toast.makeText(Phone_Login_Activity.this,"Enter Phone Number",Toast.LENGTH_SHORT).show();

               }
               else
               {
                   loadingBar.setTitle("Phone Verification");
                   loadingBar.setMessage("Please Wait as We Verify Your Phone");
                   loadingBar.setCanceledOnTouchOutside(false);
                   loadingBar.show();
                       PhoneAuthProvider.getInstance().verifyPhoneNumber(
                               phoneNumber,        // Phone number to verify
                               60,                 // Timeout duration
                               TimeUnit.SECONDS,   // Unit of timeout
                               Phone_Login_Activity.this,               // Activity (for callback binding)
                               callBacks);        // OnVerificationStateChangedCallbacks



                   }



           }
       });





       verifyButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)
           {
               sendVerificationCodeButton.setVisibility(View.INVISIBLE);
               phoneNumberInput.setVisibility(View.INVISIBLE);

               String verifiactionCode= verificationCodeInput.getText().toString();


               if(TextUtils.isEmpty(verifiactionCode))
               {
                   Toast.makeText(Phone_Login_Activity.this,"Enter The Code First",Toast.LENGTH_SHORT).show();

               }
               else
               {
                   loadingBar.setTitle("Code Authentication");
                   loadingBar.setMessage("Please Wait as your Confirm your Code");
                   loadingBar.setCanceledOnTouchOutside(false);
                   loadingBar.show();


                   PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verifiactionCode);
                   signInWithPhoneAuthCredential(credential);
               }



           }
       });


       callBacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
           @Override
           public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
           {
               signInWithPhoneAuthCredential(phoneAuthCredential);

           }

           @Override
           public void onVerificationFailed(FirebaseException e)
            {

                Toast.makeText(Phone_Login_Activity.this,"Enter valid Phone Number with Country Code",Toast.LENGTH_SHORT).show();

                sendVerificationCodeButton.setVisibility(View.VISIBLE);
                phoneNumberInput.setVisibility(View.VISIBLE);

                verifyButton.setVisibility(View.INVISIBLE);
                verificationCodeInput.setVisibility(View.INVISIBLE);


            }

           @Override
           public void onCodeSent( String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token)
           {


               // Save verification ID and resending token so we can use them later
               mVerificationId = verificationId;
               mResendToken = token;

               Toast.makeText(Phone_Login_Activity.this,"Code Sent to Your Phone",Toast.LENGTH_SHORT).show();
               loadingBar.dismiss();


               sendVerificationCodeButton.setVisibility(View.INVISIBLE);
               phoneNumberInput.setVisibility(View.INVISIBLE);

               verifyButton.setVisibility(View.VISIBLE);
               verificationCodeInput.setVisibility(View.VISIBLE);

           }
       };


    }



    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            Toast.makeText(Phone_Login_Activity.this,"Welcome Your Are Logged In",Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();
                        }
                        else
                        {
                            String message= task.getException().toString();

                            Toast.makeText(Phone_Login_Activity.this,"Error: "+message,Toast.LENGTH_SHORT).show();




                         }

                     }



                });
    }

    private void sendUserToMainActivity()
    {

        Intent mainIntent= new Intent(Phone_Login_Activity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
