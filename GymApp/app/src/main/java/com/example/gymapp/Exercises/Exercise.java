package com.example.gymapp.Exercises;

public class Exercise {
    private String exerciseTitle;
    private int exerciseNo;
    private String exerciseImg;

    public Exercise()
    {

    }

    public Exercise (String exerciseTitle,int exerciseNo,String exerciseImg){
        this.exerciseNo=exerciseNo;
        this.exerciseTitle=exerciseTitle;
        this.exerciseImg=exerciseImg;
    }

    public String getExerciseTitle() {
        return exerciseTitle;
    }

    public int getExerciseNo() {
        return exerciseNo;
    }

    public String getExerciseImg() {
        return exerciseImg;
    }
}
