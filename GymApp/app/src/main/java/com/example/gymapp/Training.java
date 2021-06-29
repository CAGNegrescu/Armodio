package com.example.gymapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.gymapp.Exercises.Exercises;
import com.example.gymapp.Exercises.MuscleGroup;
import com.example.gymapp.Exercises.MuscleGroupAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Training extends AppCompatActivity {
    private FirebaseFirestore fstore=FirebaseFirestore.getInstance();
    private CollectionReference collectionReference=fstore.collection("ExerciseTypes");
    private MuscleGroupAdapter adapter;
    SharedPreferences prefs;

    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        editor = getSharedPreferences("Muscle Group", MODE_PRIVATE).edit();
         prefs = getSharedPreferences("Muscle Group", MODE_PRIVATE);
        setUpRecyclerView();

    }

    private void setUpRecyclerView(){
        Query query=collectionReference.orderBy("muscleGroupNo", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<MuscleGroup> option=new FirestoreRecyclerOptions.Builder<MuscleGroup>()
                .setQuery(query, MuscleGroup.class)
                .build();
        adapter= new MuscleGroupAdapter(option);
        RecyclerView recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MuscleGroupAdapter.onItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                MuscleGroup muscleGroup=documentSnapshot.toObject(MuscleGroup.class);
                String id=documentSnapshot.getId();

                editor.putString("Group",id);
                editor.apply();
                Log.d("TAG", "onItemClick: "+prefs.getString("Group",null));
                startActivity(new Intent(getApplicationContext(), Exercises.class) );
            }
        });

    }
    @Override
    protected  void onStart()
    {
        super.onStart();
        adapter.startListening();

    }
    @Override
    protected void onStop(){
        super.onStop();
        adapter.startListening();
    }
}
