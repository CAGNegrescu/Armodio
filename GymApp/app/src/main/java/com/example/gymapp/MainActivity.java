package com.example.gymapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.util.Listener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.Context;

public class MainActivity extends AppCompatActivity {

    TextView verificationMsg,profileBtn;
    Button resendCodeBtn,submitBtn;
    CircleImageView profileImg;
    ImageView dietBtn,cancelImg,dietImg,bmrBtn,designImg;
    NiceSpinner spinner;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    String userId;
    ListenerRegistration listener;
    float BMR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        resendCodeBtn=findViewById(R.id.verifyBtn);
        profileBtn=findViewById(R.id.textView2);
        verificationMsg=findViewById(R.id.verifyMsg);
        profileImg=(CircleImageView) findViewById(R.id.profileImageView);
        dietBtn=findViewById(R.id.dietBtn);
        bmrBtn=findViewById(R.id.bmrBtn);
        designImg=findViewById(R.id.designImageView);

        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        fStorage=FirebaseStorage.getInstance();
        final FirebaseUser user=fAuth.getCurrentUser();

        BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(R.layout.calculate_diet_dialog);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        final String[] item = new String[1];
        submitBtn=bottomSheetDialog.findViewById(R.id.submitBtn);
        submitBtn.setClickable(false);
        cancelImg=bottomSheetDialog.findViewById(R.id.cancelImg);
        dietImg=bottomSheetDialog.findViewById(R.id.dietImg);
        spinner= (NiceSpinner) bottomSheetDialog.findViewById(R.id.nice_spinner);
        userId=fAuth.getCurrentUser().getUid();
        DocumentReference docRef=fStore.collection("users").document(userId);
        SharedPreferences.Editor editor = getSharedPreferences("BMR", MODE_PRIVATE).edit();
        final boolean[] dietSet = new boolean[1];




        listener=docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Log.d("TAG", "onEvent: "+documentSnapshot.getString("email")+userId);
                BMR=Float.parseFloat(documentSnapshot.get("TargetBMR").toString());
                Log.d("TAG", "onEvent: "+BMR);
                editor.putFloat("BMR",BMR);
                editor.apply();

                if(documentSnapshot.getString("DietChoice")!=null || documentSnapshot.getString("DietChoice")!="")
                {
                    dietSet[0] =true;
                }
                else dietSet[0]=false;

            }
        });
        bmrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<String> dataset = new LinkedList<>(Arrays.asList("Lose Weight","Gain Weight"));
                Log.d("TAG",spinner.toString());
                spinner.attachDataSource(dataset);
                item[0]=dataset.get(0);
                spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
                    @Override
                    public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                        // This example uses String, but your type can be any
                        item[0] = parent.getItemAtPosition(position).toString();
                        submitBtn.setClickable(true);

                    }
                });
                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final DocumentReference documentReference=fStore.collection("users").document(userId);
                        documentReference.update("DietChoice",item[0]);
                        Log.d("TAG","Success adding diet choice");
                        if (item[0]=="Lose Weight")
                        {
                            BMR-=500;
                            BMR=RoundBMR(BMR);
                            documentReference.update("TargetBMR",BMR);
                        }
                        else{
                            BMR+=500;
                            BMR=RoundBMR(BMR);
                            documentReference.update("TargetBMR",BMR);
                        }
                        bottomSheetDialog.dismiss();


                    }
                });
                bottomSheetDialog.show();
                cancelImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();

                    }
                });

            }

        });
        dietBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dietSet[0]) {
                    startActivity(new Intent(MainActivity.this, Diet.class));
                }else
                {
                    Toast.makeText(MainActivity.this,"You need to set a Diet Regim first",Toast.LENGTH_SHORT).show();
                }
            }
        });




        if(!user.isEmailVerified()){
            resendCodeBtn.setVisibility(View.VISIBLE);
            verificationMsg.setVisibility(View.VISIBLE);
            profileImg.setVisibility(View.INVISIBLE);
            designImg.setVisibility(View.INVISIBLE);

            resendCodeBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(final View v){
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(),"Verification Email has been sent.",Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d("tag","onFailure:Email not Sent"+e.getMessage());
                        }
                    });
                }
            });
        }



        StorageReference storageReference= fStorage.getReference();
        storageReference.child("images/"+userId+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(uri!=null || uri.toString()!="") {
                    Glide.with(getApplicationContext()).load(uri).into(profileImg);

                }
            }
        });

    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        if(listener!=null)
        {
            listener.remove();
        }
        startActivity(new Intent(MainActivity.this, Login.class));
        finish();
    }
    public void profile(View view)
    {
        startActivity(new Intent(MainActivity.this,Profile.class));
    }
    public void training(View view)
    {
        startActivity(new Intent(MainActivity.this, Training.class));

    }
    public float RoundBMR(float BMR){
        if(BMR>1000 && BMR<=1500){
            BMR=1500;
        }
        else if(BMR>1500 && BMR<=2000){

            BMR=2000;
        }
        else if (BMR>2000 && BMR<=2500){

            BMR=2500;
        }
        else if(BMR>2500 && BMR<=3000){
            BMR=3000;
        }
        else if(BMR>3000 && BMR<=3500){
            BMR=3000;
        }
        return BMR;
    }

}
