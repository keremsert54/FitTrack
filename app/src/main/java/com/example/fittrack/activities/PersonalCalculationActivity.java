package com.example.fittrack.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fittrack.R;
import com.example.fittrack.utils.SharedPrefsManager;
import com.example.fittrack.MainActivity; // MainActivity import eklendi

import java.text.DecimalFormat;

public class PersonalCalculationActivity extends AppCompatActivity {

    private SharedPrefsManager prefsManager;
    private EditText heightInput, weightInput, ageInput;
    private RadioGroup genderGroup;
    private TextView waterResult, calorieResult, exerciseResult, bmiResult, exerciseSuggestionResult;
    private Button calculateButton, backButton; // backButton olarak düzeltildi

    private DecimalFormat df = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_calculation);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.personal_health));
        }

        prefsManager = new SharedPrefsManager(this);

        heightInput = findViewById(R.id.height_input);
        weightInput = findViewById(R.id.weight_input);
        ageInput = findViewById(R.id.age_input);
        genderGroup = findViewById(R.id.gender_group);
        calculateButton = findViewById(R.id.calculate_button);
        backButton = findViewById(R.id.back_button); // Doğru ID ile bağlandı

        waterResult = findViewById(R.id.water_result);
        calorieResult = findViewById(R.id.calorie_result);
        exerciseResult = findViewById(R.id.exercise_result);
        bmiResult = findViewById(R.id.bmi_result);
        exerciseSuggestionResult = findViewById(R.id.exercise_suggestion_result);

        loadSavedData();

        calculateButton.setOnClickListener(v -> calculateResults());
        backButton.setOnClickListener(v -> goBackToMain()); // Tıklama olayı eklendi
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadSavedData() {
        heightInput.setText("");
        weightInput.setText("");
        ageInput.setText("");
        genderGroup.clearCheck();
    }

    private void saveData() {
        try {
            double height = Double.parseDouble(heightInput.getText().toString().trim());
            double weight = Double.parseDouble(weightInput.getText().toString().trim());
            int age = Integer.parseInt(ageInput.getText().toString().trim());
            int genderId = genderGroup.getCheckedRadioButtonId();
            RadioButton genderButton = findViewById(genderId);
            String gender = (genderButton != null) ? genderButton.getText().toString() : getString(R.string.male);

            prefsManager.saveHeight(height);
            prefsManager.saveWeight(weight);
            prefsManager.saveAge(age);
            prefsManager.saveGender(gender);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.enter_valid_numbers, Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateResults() {
        String heightStr = heightInput.getText().toString().trim();
        String weightStr = weightInput.getText().toString().trim();
        String ageStr = ageInput.getText().toString().trim();

        if (heightStr.isEmpty() || weightStr.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        int genderId = genderGroup.getCheckedRadioButtonId();
        if (genderId == -1) {
            Toast.makeText(this, R.string.select_gender, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double height = Double.parseDouble(heightStr);
            double weight = Double.parseDouble(weightStr);
            int age = Integer.parseInt(ageStr);
            RadioButton genderButton = findViewById(genderId);
            double bmr;

            saveData();

            if (genderButton != null && genderButton.getText().toString().equals(getString(R.string.male))) {
                bmr = 88.362 + (13.397 * weight) + (4.799 * (height / 100)) - (5.677 * age);
            } else {
                bmr = 447.593 + (9.247 * weight) + (3.098 * (height / 100)) - (4.330 * age);
            }

            double dailyCalories = bmr * 1.55;
            double waterIntake = weight * 35;
            double bmi = weight / Math.pow(height / 100, 2);

            String language = prefsManager.getSelectedLanguage();
            String exerciseSuggestion;
            if ("tr".equals(language)) {
                if (bmi < 18.5) {
                    exerciseSuggestion = "Hafif egzersizler yapmayı düşünün, örneğin yürüyüş veya yoga (20-30 dakika/gün).";
                } else if (bmi < 25) {
                    exerciseSuggestion = "Orta düzey egzersizler yapın, örneğin jogging (haftada 150-300 dakika).";
                } else {
                    exerciseSuggestion = "Kilo yönetimi için kardiyo yapın (günde 30-60 dakika).";
                }
            } else {
                if (bmi < 18.5) {
                    exerciseSuggestion = "Consider light exercises like walking or yoga (20-30 min/day).";
                } else if (bmi < 25) {
                    exerciseSuggestion = "Maintain with moderate exercises like jogging (150-300 min/week).";
                } else {
                    exerciseSuggestion = "Focus on weight management with cardio (30-60 min/day).";
                }
            }

            if (waterResult != null)
                waterResult.setText(getString(R.string.daily_water, (int) waterIntake));
            if (calorieResult != null)
                calorieResult.setText(getString(R.string.daily_calories, (int) dailyCalories));
            if (exerciseResult != null)
                exerciseResult.setText(getString(R.string.exercise_summary, 0, (int) dailyCalories, 0, ""));
            if (bmiResult != null)
                bmiResult.setText(getString(R.string.bmi, Double.parseDouble(df.format(bmi))));
            if (exerciseSuggestionResult != null)
                exerciseSuggestionResult.setText(exerciseSuggestion);

        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.enter_valid_numbers, Toast.LENGTH_SHORT).show();
        }
    }

    private void goBackToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}