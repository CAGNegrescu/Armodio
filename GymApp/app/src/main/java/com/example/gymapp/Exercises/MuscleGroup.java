package com.example.gymapp.Exercises;

import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MuscleGroup {
    private int muscleGroupNo;
    private String muscleGroupImg;
    private String muscleGroupTitle,url;
    FirebaseFirestore fStore=FirebaseFirestore.getInstance();

    public MuscleGroup(){

    }
    public MuscleGroup(String muscleGroupTitle, String muscleGroupImg, int muscleGroupNo)
    {
        this.muscleGroupTitle=muscleGroupTitle;
        this.muscleGroupImg=muscleGroupImg;
        this.muscleGroupNo=muscleGroupNo;
        fStore=FirebaseFirestore.getInstance();

    }

    public int getMuscleGroupNo() {
        return muscleGroupNo;
    }

    public String getMuscleGroupImg() {
        return muscleGroupImg;
    }



    public String getMuscleGroupTitle() {
        return muscleGroupTitle;
    }
}
