package com.example.gymapp.Dietdays;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

public class Wednesday extends Fragment {
    View view;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DocumentReference documentReference;
    String userID;


    public Wednesday() {

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.monday,container,false);
        final int[] id = {R.id.BreakfastTxt};
        fAuth= FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        userID=fAuth.getCurrentUser().getUid();
        RelativeLayout constraintLayout = (RelativeLayout) view.findViewById(R.id.monday_layout);

        SharedPreferences preferences = this.getActivity().getSharedPreferences("BMR", Context.MODE_PRIVATE);
        int BMR = (int)preferences.getFloat("BMR", 0);
        Log.d("TAG", "BMR: "+BMR);

        documentReference=fStore.collection("Diets/"+String.valueOf(BMR)+"/days").document("Wednesday");
        //CollectionReference documentReference=fStore.collection("Diets").document(String.valueOf(getBMR())).collection("Monday");


        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                List<Object> group = (List<Object>) documentSnapshot.get("breakfast");
                Log.d("TAG", "onCreateView: "+documentSnapshot.get("breakfast"));
                RelativeLayout.LayoutParams params=null;

                AddFood(group,params,constraintLayout,id);
                AddTextView(group,params,documentSnapshot,constraintLayout,id,"Lunch","lunch");
                group = (List<Object>) documentSnapshot.get("lunch");
                AddFood(group,params,constraintLayout,id);
                AddTextView(group,params,documentSnapshot,constraintLayout,id,"Dinner","dinner");
                group = (List<Object>) documentSnapshot.get("dinner");
                AddFood(group,params,constraintLayout,id);
                AddTextView(group,params,documentSnapshot,constraintLayout,id,"Snack","snack");
                group = (List<Object>) documentSnapshot.get("snack");
                AddFood(group,params,constraintLayout,id);

            }
        });
        return view;
    }

    public void AddFood(List<Object> group,RelativeLayout.LayoutParams params,RelativeLayout constraintLayout,final int[] id)
    {
        for(int i=0; i<group.size();i+=2)
        {
            TextView Food = new TextView(getContext());
            TextView Callories=new TextView(getContext());
            Food.setText(group.get(i).toString());
            Callories.setText(group.get(i+1).toString()+" callories");

            params= new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW,id[0]);
            id[0] =view.generateViewId();
            Food.setLayoutParams(params);
            Food.setId(id[0]);
            Food.setPadding(16,16,16,16);
            Food.setTextSize(20);
            params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_BASELINE,id[0]);
            params.addRule(RelativeLayout.RIGHT_OF,id[0]);
            Callories.setLayoutParams(params);
            Callories.setPadding(16,0,0,0);
            Callories.setTextSize(20);



            constraintLayout.addView(Food);
            constraintLayout.addView(Callories);
        }
    }
    public void AddTextView(List<Object> group,
                            RelativeLayout.LayoutParams params,
                            DocumentSnapshot documentSnapshot,
                            RelativeLayout constraintLayout,
                            final int[] id,String timeText,String document)
    {
        group = (List<Object>) documentSnapshot.get(document);
        TextView textView = new TextView(getContext());
        textView.setText(timeText);
        params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW,id[0]);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textView.setLayoutParams(params);

        textView.setTextSize(30);
        textView.setPadding(16,16,16,16);
        textView.setTypeface(Typeface.DEFAULT_BOLD);

        id[0]=view.generateViewId();
        textView.setId(id[0]);
        constraintLayout.addView(textView);


    }
    /*public float getBMR()
    {

    }*/
}
