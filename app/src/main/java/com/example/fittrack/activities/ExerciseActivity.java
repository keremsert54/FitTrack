package com.example.fittrack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.example.fittrack.R;
import com.example.fittrack.utils.SharedPrefsManager;

public class ExerciseActivity extends AppCompatActivity {
    private SharedPrefsManager prefsManager;
    private int totalDuration = 0;
    private TextView totalDurationText;
    private TextView caloriesBurnedText;
    private Spinner exerciseSpinner;
    private EditText durationInput;
    private EditText customExerciseInput;
    private EditText caloriesPerInput;
    private Button saveCustomExerciseButton;
    private ArrayList<String> exercises;
    private ArrayAdapter<String> adapter;

    private static final String KEY_CUSTOM_EXERCISES = "custom_exercises";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefsManager = new SharedPrefsManager(this);
        applyLocale();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        setTitle(getString(R.string.exercise_tracking));

        exerciseSpinner = findViewById(R.id.exercise_spinner);
        durationInput = findViewById(R.id.duration_input);
        Button addExerciseButton = findViewById(R.id.add_exercise_button);
        Button addCustomExerciseButton = findViewById(R.id.add_custom_exercise_button);
        customExerciseInput = findViewById(R.id.custom_exercise_input);
        caloriesPerInput = findViewById(R.id.calories_per_input);
        saveCustomExerciseButton = findViewById(R.id.save_custom_exercise_button);
        totalDurationText = findViewById(R.id.total_duration_text);
        caloriesBurnedText = findViewById(R.id.calories_burned_text);
        Button viewExercisesButton = findViewById(R.id.view_exercises_button);
        Button backButton = findViewById(R.id.back_button);

        // Sadece varsayılan egzersiz listesi
        exercises = new ArrayList<>();
        exercises.add("Walking");
        exercises.add("Yoga");
        exercises.add("Swimming");
        exercises.add("Running");
        exercises.add("Cycling");
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, exercises);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(adapter);

        totalDuration = prefsManager.getTotalDuration();
        updateTotalText();

        addExerciseButton.setOnClickListener(v -> {
            String durationStr = durationInput.getText().toString().trim();
            if (!durationStr.isEmpty()) {
                try {
                    int duration = Integer.parseInt(durationStr);
                    String selectedExercise = exerciseSpinner.getSelectedItem().toString();
                    int caloriesPerMinute = prefsManager.getCaloriesPerMinute(selectedExercise);
                    totalDuration += duration;
                    int caloriesBurned = duration * caloriesPerMinute;
                    prefsManager.saveCaloriesBurned(prefsManager.getCaloriesBurned() + caloriesBurned);
                    prefsManager.saveTotalDuration(totalDuration);
                    updateTotalText();
                    durationInput.setText("");
                    Toast.makeText(this, getString(R.string.exercise_added), Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                }
            }
        });

        addCustomExerciseButton.setOnClickListener(v -> {
            customExerciseInput.setVisibility(View.VISIBLE);
            caloriesPerInput.setVisibility(View.VISIBLE);
            saveCustomExerciseButton.setVisibility(View.VISIBLE);
        });

        saveCustomExerciseButton.setOnClickListener(v -> {
            String customExercise = customExerciseInput.getText().toString().trim();
            String caloriesPerStr = caloriesPerInput.getText().toString().trim();
            if (!customExercise.isEmpty() && !caloriesPerStr.isEmpty()) {
                try {
                    int caloriesPer = Integer.parseInt(caloriesPerStr);
                    List<String> customExercisesList = prefsManager.getCustomExercises();
                    if (customExercisesList == null) {
                        customExercisesList = new ArrayList<>();
                    }
                    if (!customExercisesList.contains(customExercise)) {
                        customExercisesList.add(customExercise);
                        prefsManager.saveCustomExercises(customExercisesList);
                        prefsManager.saveExerciseCalories(customExercise, caloriesPer); // Kalori kaydet
                        customExerciseInput.setText("");
                        caloriesPerInput.setText("");
                        customExerciseInput.setVisibility(View.GONE);
                        caloriesPerInput.setVisibility(View.GONE);
                        saveCustomExerciseButton.setVisibility(View.GONE);
                        Toast.makeText(this, getString(R.string.exercise_added), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Bu egzersiz zaten ekli!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, getString(R.string.invalid_input), Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewExercisesButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewExercises.class);
            startActivity(intent);
        });

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        } else {
            android.util.Log.e("ExerciseActivity", "back_button is null. Check activity_exercise.xml");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentLang = prefsManager.getSelectedLanguage();
        if (!currentLang.equals(Locale.getDefault().getLanguage())) {
            applyLocale();
        }
        totalDuration = prefsManager.getTotalDuration();
        updateTotalText();
    }

    private void applyLocale() {
        prefsManager.setLocale(this, prefsManager.getSelectedLanguage());
    }

    private void updateTotalText() {
        int caloriesBurned = prefsManager.getCaloriesBurned();
        totalDurationText.setText(String.format(getString(R.string.total_duration), totalDuration));
        caloriesBurnedText.setText(String.format(getString(R.string.calories_burned), caloriesBurned));
    }
}
