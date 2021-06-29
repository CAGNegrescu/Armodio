package com.example.gymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";
    EditText mFirstName, mLastName, mEmail,mPassword;
    Button mRegisTerBtn;
    TextView mLoginLink;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    ProgressBar mProgressbar;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirstName= findViewById(R.id.firstNameText);
        mLastName=findViewById(R.id.lastNameText);
        mEmail=findViewById(R.id.Email);
        mPassword=findViewById(R.id.Password);
        mRegisTerBtn=findViewById((R.id.registerBtn));
        mLoginLink=findViewById(R.id.loginLinkText);
        mProgressbar=findViewById(R.id.progressBar);



        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        //fAuth.signOut();
        if(fAuth.getCurrentUser()!=null)
        {
            startActivity(new Intent(Register.this,MainActivity.class));
            finish();
        }

        mRegisTerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V){
                final String email=mEmail.getText().toString().trim();
                String password= mPassword.getText().toString().trim();
                final String firstName=mFirstName.getText().toString().trim();
                final String lastName=mLastName.getText().toString().trim();

                if(TextUtils.isEmpty(firstName)){

                    mPassword.setError("First Name is Required");
                    return;

                }
                if(TextUtils.isEmpty(firstName)){

                    mPassword.setError("Last Name is Required");
                    return;

                }

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required");
                    return;
                }

                if(password.length()<8){
                    mPassword.setError("Password must have at least 8 characters");
                    return;
                }
                if(!EmailFormatCheck.isValidEmailAddress(email))
                {
                    mEmail.setError("Email format is not valid");
                    return;
                }

                mProgressbar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser currentUser=fAuth.getCurrentUser();
                            currentUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this,"Verification Email has been sent.",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d(TAG,"onFailure:Email not Sent"+e.getMessage());
                                }
                            });


                            Toast.makeText(Register.this, "User Created Successfuly.", Toast.LENGTH_SHORT).show();

                            mProgressbar.setVisibility(View.INVISIBLE);

                            userID=fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference=fStore.collection("users").document(userID);
                            final Map<String,Object> users=new HashMap<>();
                            users.put("firstName",firstName);
                            users.put("lastName",lastName);
                            users.put("email",email);
                            users.put("profileIsSet",false);
                            users.put("TargetBMR",0);
                            users.put("DietChoice",null);
                            documentReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG,"onSuccess user data added for user"+userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"onFailure: "+e.toString());
                                }
                            });
                            startActivity(new Intent(Register.this,Login.class));
                            
                        }else{
                            Toast.makeText(Register.this, "Error!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            mProgressbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this,Login.class));
            }
        });
    }
}
