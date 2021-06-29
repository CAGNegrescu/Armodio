package com.example.gymapp.Exercises;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gymapp.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class MuscleGroupAdapter extends FirestoreRecyclerAdapter<MuscleGroup, MuscleGroupAdapter.MuscleGroupHolder>{

    private onItemClickListener listener;
    public MuscleGroupAdapter(@NonNull FirestoreRecyclerOptions<MuscleGroup> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MuscleGroupHolder holder, int position, @NonNull MuscleGroup model) {
        holder.muscleGroupTitle.setText(model.getMuscleGroupTitle());
        Log.d("TAG", "onBindViewHolder: "+model.getMuscleGroupImg());
        Picasso.get().load(model.getMuscleGroupImg()).into(holder.muscleGroupImg);
        holder.muscleGroupNo.setText(String.valueOf(model.getMuscleGroupNo()));
    }

    @NonNull
    @Override
    public MuscleGroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.muscle_group_cardview,parent,false);
        return new MuscleGroupHolder(v);
    }

    class MuscleGroupHolder extends RecyclerView.ViewHolder{
        TextView muscleGroupNo;
        ImageView muscleGroupImg;
        TextView muscleGroupTitle;

        public MuscleGroupHolder(@NonNull View itemView) {
            super(itemView);
            muscleGroupNo=itemView.findViewById(R.id.muscleGroupNoTxt);
            muscleGroupImg=itemView.findViewById(R.id.muscleGroupImg);
            muscleGroupTitle=itemView.findViewById(R.id.muscleGroupNameTxt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    int position=getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION && listener!=null ){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }

                }

            });
        }
    }
    public interface onItemClickListener{
        void onItemClick(DocumentSnapshot documentSnapshot,int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.listener=listener;
    }
}
