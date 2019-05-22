package com.psm.myworkouttracker.adapter;

public class ExercisesItem {
    private String exerciseName;
    private String exerciseDesc;
    private String exerciseImg;

    public ExercisesItem(String exerciseName, String exerciseDesc, String exerciseImg) {
        this.exerciseName = exerciseName;
        this.exerciseDesc = exerciseDesc;
        this.exerciseImg = exerciseImg;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getExerciseDesc() {
        return exerciseDesc;
    }

    public String getExerciseImg() {
        return exerciseImg;
    }
}
