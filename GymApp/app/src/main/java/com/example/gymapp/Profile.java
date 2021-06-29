package com.example.gymapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.Compressor;
import io.grpc.Context;

public class Profile extends AppCompatActivity {
    public static final String TAG = "TAG";
    private CircleImageView mProfileImg;
    private TextView mHeight,mWeight,mAge;
    private Button mUpdateBtn;
    private static final int PICK_IMAGE=1;
    Uri imgUri;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    String userID;
    double BMR;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mProfileImg=(CircleImageView) findViewById(R.id.profileImageView);
        mHeight=findViewById(R.id.heightTextView);
        mWeight=findViewById(R.id.weightTextView);
        mAge=findViewById(R.id.ageTextView);
        mUpdateBtn=findViewById(R.id.updateBtn);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        userID=fAuth.getCurrentUser().getUid();

        storageReference= FirebaseStorage.getInstance().getReference().child("images");

        DocumentReference documentReference= fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                boolean profileSet;
                profileSet=documentSnapshot.getBoolean("profileIsSet");
                if(profileSet) {

                    mHeight.setText(documentSnapshot.getString("Height"));
                    mWeight.setText(documentSnapshot.getString("Weight"));
                    mAge.setText(documentSnapshot.getString("Age"));
                    String url=documentSnapshot.getString("Image");
                    Log.d(TAG,"url: "+url);
                    if(url!=null || url!="") {
                        Glide.with(getApplicationContext()).load(url).into(mProfileImg);

                    }
                }

            }
        });

        mProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
            }
        });
        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String weight=mWeight.getText().toString().trim();
                final String height=mHeight.getText().toString().trim();
                final String age=mAge.getText().toString().trim();
                if(TextUtils.isEmpty(weight)){

                    mWeight.setError("This field is Required");
                    return;

                }
                if(TextUtils.isEmpty(height)){

                    mHeight.setError("This field is Required");
                    return;

                }
                if(TextUtils.isEmpty(age)){

                    mAge.setError("This field is Required");
                    return;

                }
                BMR=calculateBMR(Integer.parseInt(age),Double.parseDouble(weight),Integer.parseInt(height));
                final DocumentReference documentReference=fStore.collection("users").document(userID);
                final Map<String,Object> users=new HashMap<>();
                users.put("Weight",weight);
                users.put("Height",height);
                users.put("Age",age);
                users.put("profileIsSet",true);
                if(imgUri!=null) {

                    users.put("Image", imgUri.toString());
                }
                users.put("TargetBMR",BMR);
                documentReference.update(users).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                startActivity(new Intent(Profile.this,MainActivity.class));
                finish();
            }
        });
    }
    public void redirectToMain(View view){

        startActivity(new Intent(getApplicationContext(),MainActivity.class));


    }

    public double calculateBMR(int age, double weight,int height)
    {
        double BMR;
        BMR=10*weight+6.25*height-5*age +5;
        return BMR;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imgUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                mProfileImg.setImageBitmap(bitmap);
                ByteArrayOutputStream baos= new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                final StorageReference reference= FirebaseStorage.getInstance().getReference()
                        .child("images")
                        .child(userID+".jpg");
                reference.putBytes(baos.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                 imgUri=uri;
                                UserProfileChangeRequest request=new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(uri)
                                        .build();
                                user.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG,"OnSuccess: Updated successfuly"+imgUri);


                                    }
                                });
                            }

                        });

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}

