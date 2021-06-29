package com.example.gymapp.Exercises;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class ExerciseAdapter extends FirestoreRecyclerAdapter<Exercise, ExerciseAdapter.ExerciseHolder> {

    private OnItemClickListener listener;
    public ExerciseAdapter(@NonNull FirestoreRecyclerOptions<Exercise> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ExerciseHolder holder, int position, @NonNull Exercise model) {
        holder.exerciseTitle.setText(model.getExerciseTitle());
        holder.exerciseNo.setText(String.valueOf(model.getExerciseNo()));
        Picasso.get().load(model.getExerciseImg()).into(holder.exerciseImg);

    }

    @NonNull
    @Override
    public ExerciseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item,parent,false);
        return new ExerciseHolder(v);
    }

    class ExerciseHolder extends RecyclerView.ViewHolder{
        TextView exerciseNo;
        TextView exerciseTitle;
        ImageView exerciseImg;

        public ExerciseHolder(View itemView) {
            super(itemView);
            exerciseNo=itemView.findViewById(R.id.exerciseNoTxt);
            exerciseTitle=itemView.findViewById(R.id.exerciseNameTxt);
            exerciseImg=itemView.findViewById(R.id.exerciseImg);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });

        }


    }
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot,int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener=listener;
    }
}
