package com.example.fittrack.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fittrack.R;
import com.example.fittrack.utils.SharedPrefsManager;
import java.util.Locale;

public class WaterTrackingActivity extends AppCompatActivity {
    private SharedPrefsManager prefsManager;
    private TextView totalWaterText, waterSummaryText;
    private EditText waterInput, waterGoalInput;
    private Button addButton, setGoalButton, add250mlButton;
    private ProgressBar waterProgressBar;
    private boolean goalSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefsManager = new SharedPrefsManager(this);
        applyLocale();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tracking);
        setTitle(getString(R.string.water_tracking));

        totalWaterText = findViewById(R.id.total_water_text);
        waterSummaryText = findViewById(R.id.water_summary_text);
        waterInput = findViewById(R.id.water_input);
        waterGoalInput = findViewById(R.id.water_goal_input);
        addButton = findViewById(R.id.add_water_button);
        setGoalButton = findViewById(R.id.set_water_goal_button);
        add250mlButton = findViewById(R.id.add_250ml_button);
        waterProgressBar = findViewById(R.id.water_progress_bar);
        Button backButton = findViewById(R.id.back_button);

        goalSet = prefsManager.getDailyWaterGoal() > 0;
        updateWaterData();

        setGoalButton.setOnClickListener(v -> {
            String goalStr = waterGoalInput.getText().toString();
            if (!goalStr.isEmpty()) {
                int newGoal = Integer.parseInt(goalStr);
                if (newGoal > 0) {
                    prefsManager.saveDailyWaterGoal(newGoal);
                    goalSet = true;
                    updateWaterData();
                    waterGoalInput.setText("");
                    Toast.makeText(this, getString(R.string.set_daily_goal), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.invalid_input, Toast.LENGTH_SHORT).show();
                }
            }
        });

        addButton.setOnClickListener(v -> {
            if (goalSet) {
                String waterStr = waterInput.getText().toString();
                if (!waterStr.isEmpty()) {
                    int water = Integer.parseInt(waterStr);
                    int currentTotal = prefsManager.getCurrentWaterIntake();
                    int goal = prefsManager.getDailyWaterGoal();
                    if (water > 0 && (currentTotal + water <= goal)) {
                        prefsManager.saveCurrentWaterIntake(currentTotal + water);
                        updateWaterData();
                        waterInput.setText("");
                    } else {
                        Toast.makeText(this, "Daily goal exceeded or invalid amount!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Please set daily water goal first!", Toast.LENGTH_SHORT).show();
            }
        });

        add250mlButton.setOnClickListener(v -> {
            if (goalSet) {
                int currentTotal = prefsManager.getCurrentWaterIntake();
                int goal = prefsManager.getDailyWaterGoal();
                if (currentTotal + 250 <= goal) {
                    prefsManager.saveCurrentWaterIntake(currentTotal + 250);
                    updateWaterData();
                    Toast.makeText(this, getString(R.string.water_added_250ml), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Daily goal exceeded!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please set daily water goal first!", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentLang = prefsManager.getSelectedLanguage();
        if (!currentLang.equals(Locale.getDefault().getLanguage())) {
            applyLocale();
        }
        updateWaterData();
    }

    private void applyLocale() {
        prefsManager.setLocale(this, prefsManager.getSelectedLanguage());
    }

    private void updateWaterData() {
        int waterIntake = Math.min(Math.max(prefsManager.getCurrentWaterIntake(), 0), 5000);
        int waterGoal = prefsManager.getDailyWaterGoal() > 0 ? prefsManager.getDailyWaterGoal() : 2000;
        int waterProgress = waterGoal > 0 ? Math.min((waterIntake * 100) / waterGoal, 100) : 0;
        String hydrationStatus = waterProgress >= 100 ? getString(R.string.excellent_status) :
                waterProgress >= 75 ? getString(R.string.good_status) :
                        waterProgress >= 50 ? getString(R.string.moderate_status) : getString(R.string.needs_attention_status);
        waterSummaryText.setText(String.format(getString(R.string.water_summary), waterIntake, waterGoal, waterProgress, hydrationStatus) + "\nRemaining: " + Math.max(waterGoal - waterIntake, 0) + " ml");
        totalWaterText.setText(String.format(getString(R.string.total_water), waterIntake));
        waterProgressBar.setMax(100);
        waterProgressBar.setProgress(waterProgress);
        addButton.setEnabled(goalSet);
        add250mlButton.setEnabled(goalSet);
    }
}
