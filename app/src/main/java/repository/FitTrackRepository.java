package com.example.fittrack.repository;

import java.util.Date;

public class FitTrackRepository {
    private static FitTrackRepository instance;

    public static FitTrackRepository getInstance() {
        if (instance == null) {
            instance = new FitTrackRepository();
        }
        return instance;
    }

    private FitTrackRepository() {}

    public void addWaterIntake(int amount, Date date) {
        // TODO: Save to Firebase Firestore
        // For now, just log
        android.util.Log.d("FitTrack", "Added " + amount + "ml water");
    }
}