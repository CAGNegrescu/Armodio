package com.example.gymapp.Exercises;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gymapp.MainActivity;
import com.example.gymapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class Exercises extends AppCompatActivity  {
    private FirebaseFirestore fStore=FirebaseFirestore.getInstance();
    private ExerciseAdapter adapter;
    private CollectionReference reference;
    private Button submitBtn;
    private ImageView cancelImg,exerciseImg;
    private TextView exerciseDescr;
    SharedPreferences prefs;
    String exerciseDescription;
    DocumentReference documentReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);
        prefs = getSharedPreferences("Muscle Group", MODE_PRIVATE);
        reference=fStore.collection(prefs.getString("Group",null));

        setUpRecylerView();

    }

    private void setUpRecylerView() {
        Query query=reference.orderBy("exerciseNo", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Exercise> options=new FirestoreRecyclerOptions.Builder<Exercise>()
                .setQuery(query,Exercise.class)
                .build();
        adapter=new ExerciseAdapter(options);
        RecyclerView recyclerView=findViewById(R.id.exerciseRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ExerciseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Exercise exercise=documentSnapshot.toObject(Exercise.class);
                String id=documentSnapshot.getId();
                exerciseDescription=documentSnapshot.get("Description").toString();
                Log.d("TAG", "Description "+exerciseDescription);
                BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(Exercises.this);
                bottomSheetDialog.setContentView(R.layout.exercise_dialog);
                bottomSheetDialog.setCanceledOnTouchOutside(false);

                submitBtn=bottomSheetDialog.findViewById(R.id.submitBtn);
                cancelImg=bottomSheetDialog.findViewById(R.id.cancelImg);
                exerciseImg=bottomSheetDialog.findViewById(R.id.exerciseImage);
                exerciseDescr=bottomSheetDialog.findViewById(R.id.exerciseDescription);
                exerciseDescr.setText(exerciseDescription);
                Picasso.get().load(documentSnapshot.getString("exerciseImg")).into(exerciseImg);
                bottomSheetDialog.show();
                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        bottomSheetDialog.dismiss();


                    }
                });

                cancelImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });
    }
    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
