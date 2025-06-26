package com.example.fittrack.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SharedPrefsManager {
    private static final String PREFS_NAME = "FitTrackPrefs";
    private static final String KEY_LANGUAGE = "selected_language";
    private static final String KEY_CURRENT_WATER = "current_water_intake";
    private static final String KEY_DAILY_WATER_GOAL = "daily_water_goal";
    private static final String KEY_LAST_RESET_DATE = "last_reset_date";
    private static final String KEY_TOTAL_DURATION = "total_duration";
    private static final String KEY_CALORIES_BURNED = "calories_burned"; // Egzersiz kalorileri
    private static final String KEY_CALORIES_CONSUMED = "calories_consumed"; // Beslenme kalorileri
    private static final String KEY_DAILY_CALORIE_GOAL = "daily_calorie_goal";
    private static final String KEY_HEIGHT = "user_height";
    private static final String KEY_WEIGHT = "user_weight";
    private static final String KEY_AGE = "user_age";
    private static final String KEY_GENDER = "user_gender";
    private static final String KEY_CUSTOM_EXERCISES = "custom_exercises";
    private static final String KEY_EXERCISE_CALORIES = "exercise_calories";

    private SharedPreferences prefs;
    private Gson gson = new Gson();

    public SharedPrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        saveSelectedLanguage(language);
    }

    public String getSelectedLanguage() {
        return prefs.getString(KEY_LANGUAGE, "en");
    }

    public void saveSelectedLanguage(String language) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply();
    }

    public int getCurrentWaterIntake() {
        return prefs.getInt(KEY_CURRENT_WATER, 0);
    }

    public void saveCurrentWaterIntake(int water) {
        prefs.edit().putInt(KEY_CURRENT_WATER, water).apply();
    }

    public int getDailyWaterGoal() {
        return prefs.getInt(KEY_DAILY_WATER_GOAL, 2000);
    }

    public void saveDailyWaterGoal(int goal) {
        prefs.edit().putInt(KEY_DAILY_WATER_GOAL, goal).apply();
    }

    public String getLastResetDate() {
        return prefs.getString(KEY_LAST_RESET_DATE, "");
    }

    public void saveLastResetDate(String date) {
        prefs.edit().putString(KEY_LAST_RESET_DATE, date).apply();
    }

    public int getTotalDuration() {
        return prefs.getInt(KEY_TOTAL_DURATION, 0);
    }

    public void saveTotalDuration(int duration) {
        prefs.edit().putInt(KEY_TOTAL_DURATION, duration).apply();
    }

    public int getCaloriesBurned() {
        return prefs.getInt(KEY_CALORIES_BURNED, 0);
    }

    public void saveCaloriesBurned(int calories) {
        prefs.edit().putInt(KEY_CALORIES_BURNED, calories).apply();
    }

    public int getCaloriesConsumed() {
        return prefs.getInt(KEY_CALORIES_CONSUMED, 0);
    }

    public void saveCaloriesConsumed(int calories) {
        prefs.edit().putInt(KEY_CALORIES_CONSUMED, calories).apply();
    }

    public int getDailyCalorieGoal() {
        return prefs.getInt(KEY_DAILY_CALORIE_GOAL, 2000);
    }

    public void saveDailyCalorieGoal(int goal) {
        prefs.edit().putInt(KEY_DAILY_CALORIE_GOAL, goal).apply();
    }

    // Yeni metotlar (kullanıcı verileri için)
    public double getHeight() {
        return prefs.getFloat(KEY_HEIGHT, 0.0f); // Float olarak sakla, double dönüşümü için
    }

    public void saveHeight(double height) {
        prefs.edit().putFloat(KEY_HEIGHT, (float) height).apply();
    }

    public double getWeight() {
        return prefs.getFloat(KEY_WEIGHT, 0.0f);
    }

    public void saveWeight(double weight) {
        prefs.edit().putFloat(KEY_WEIGHT, (float) weight).apply();
    }

    public int getAge() {
        return prefs.getInt(KEY_AGE, 0);
    }

    public void saveAge(int age) {
        prefs.edit().putInt(KEY_AGE, age).apply();
    }

    public String getGender() {
        return prefs.getString(KEY_GENDER, null);
    }

    public void saveGender(String gender) {
        prefs.edit().putString(KEY_GENDER, gender).apply();
    }

    // Yeni metotlar (özel egzersizler için)
    public List<String> getCustomExercises() {
        String json = prefs.getString(KEY_CUSTOM_EXERCISES, null);
        if (json != null) {
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public void saveCustomExercises(List<String> exercises) {
        String json = gson.toJson(exercises);
        prefs.edit().putString(KEY_CUSTOM_EXERCISES, json).apply();
    }

    // Yeni metotlar (egzersiz kalorileri için)
    public void saveExerciseCalories(String exercise, int caloriesPerMinute) {
        String json = prefs.getString(KEY_EXERCISE_CALORIES, "{}");
        Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
        HashMap<String, Integer> exerciseCalories = gson.fromJson(json, type);
        if (exerciseCalories == null) {
            exerciseCalories = new HashMap<>();
        }
        exerciseCalories.put(exercise.toLowerCase(), caloriesPerMinute);
        prefs.edit().putString(KEY_EXERCISE_CALORIES, gson.toJson(exerciseCalories)).apply();
    }

    public int getCaloriesPerMinute(String exercise) {
        String json = prefs.getString(KEY_EXERCISE_CALORIES, "{}");
        Type type = new TypeToken<HashMap<String, Integer>>(){}.getType();
        HashMap<String, Integer> exerciseCalories = gson.fromJson(json, type);
        if (exerciseCalories != null && exerciseCalories.containsKey(exercise.toLowerCase())) {
            return exerciseCalories.get(exercise.toLowerCase());
        }
        // Varsayılan kalori değerleri
        switch (exercise.toLowerCase()) {
            case "walking": return 4;
            case "yoga": return 3;
            case "swimming": return 8;
            case "running": return 10;
            case "cycling": return 7;
            default: return 5; // Bilinmeyen veya kullanıcı ekli egzersizler için varsayılan
        }
    }
}
