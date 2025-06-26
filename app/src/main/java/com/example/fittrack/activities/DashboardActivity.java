package com.example.fittrack.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Typeface;

import com.example.fittrack.R;
import com.example.fittrack.utils.SharedPrefsManager;
import com.example.fittrack.utils.DateUtils;

import java.util.Locale;

public class DashboardActivity extends Activity {

    private SharedPrefsManager prefsManager;
    private TextView waterSummaryText, exerciseSummaryText, nutritionSummaryText, welcomeText;
    private ProgressBar waterProgressBar, exerciseProgressBar, nutritionProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefsManager = new SharedPrefsManager(this);
        applyLocale();
        checkDailyReset();
        createDashboardUI();
        updateDashboardData();

        Toast.makeText(this, getString(R.string.dashboard_loaded), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentLang = prefsManager.getSelectedLanguage();
        if (currentLang != null && !currentLang.equals(Locale.getDefault().getLanguage())) {
            applyLocale();
            recreate(); // Dil değişikliği sonrası UI’yı yeniden yükle
        }
        updateDashboardData();
    }

    private void applyLocale() {
        String language = prefsManager.getSelectedLanguage();
        if (language == null || language.isEmpty()) {
            language = "tr"; // Varsayılan dil Türkçe
        }
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        prefsManager.saveSelectedLanguage(language);
    }

    private void checkDailyReset() {
        String lastDate = prefsManager.getLastResetDate();
        if (DateUtils.isNewDay(lastDate)) {
            prefsManager.saveCurrentWaterIntake(0);
            prefsManager.saveTotalDuration(0);
            prefsManager.saveCaloriesBurned(0);
            prefsManager.saveCaloriesConsumed(0);
            prefsManager.saveLastResetDate(DateUtils.getCurrentDateString());
            Toast.makeText(this, getString(R.string.new_day_started), Toast.LENGTH_LONG).show();
        }
    }

    private void createDashboardUI() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFFF5F5F5);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(16, 16, 16, 16);

        createHeader(mainLayout);
        createSummaryCards(mainLayout);
        createQuickActions(mainLayout);
        createBackButton(mainLayout);

        scrollView.addView(mainLayout);
        setContentView(scrollView);
    }

    private void createHeader(LinearLayout parent) {
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        headerLayout.setLayoutParams(headerParams);

        ImageView dashboardIcon = new ImageView(this);
        try {
            dashboardIcon.setImageResource(R.drawable.ic_dashboard);
        } catch (Exception e) {
            dashboardIcon.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        dashboardIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(100, 100);
        iconParams.setMargins(0, 0, 10, 0);
        dashboardIcon.setLayoutParams(iconParams);
        headerLayout.addView(dashboardIcon);

        TextView title = new TextView(this);
        title.setText(getString(R.string.dashboard));
        title.setTextSize(30);
        title.setTextColor(0xFF1976D2);
        title.setGravity(android.view.Gravity.CENTER);
        headerLayout.addView(title);

        parent.addView(headerLayout);

        welcomeText = new TextView(this);
        welcomeText.setTextSize(16);
        welcomeText.setTextColor(0xFF666666);
        welcomeText.setGravity(android.view.Gravity.CENTER);
        welcomeText.setPadding(0, 0, 0, 30);
        parent.addView(welcomeText);
    }

    private void createSummaryCards(LinearLayout parent) {
        TextView sectionTitle = new TextView(this);
        sectionTitle.setText(getString(R.string.todays_progress));
        sectionTitle.setTextSize(20);
        sectionTitle.setTextColor(0xFF1976D2);
        sectionTitle.setPadding(0, 0, 0, 20);
        parent.addView(sectionTitle);

        LinearLayout waterCard = createSummaryCard(getString(R.string.hydration), 0xFFF5F5F5);
        waterSummaryText = (TextView) waterCard.getChildAt(2);
        waterProgressBar = (ProgressBar) waterCard.getChildAt(1);
        waterProgressBar.setProgressTintList(ColorStateList.valueOf(0xFF00BCD4));
        parent.addView(waterCard);

        LinearLayout exerciseCard = createSummaryCard(getString(R.string.exercise), 0xFFF5F5F5);
        exerciseSummaryText = (TextView) exerciseCard.getChildAt(2);
        exerciseProgressBar = (ProgressBar) exerciseCard.getChildAt(1);
        exerciseProgressBar.setProgressTintList(ColorStateList.valueOf(0xFF4CAF50));
        parent.addView(exerciseCard);

        LinearLayout nutritionCard = createSummaryCard(getString(R.string.nutrition), 0xFFF5F5F5);
        nutritionSummaryText = (TextView) nutritionCard.getChildAt(2);
        nutritionProgressBar = (ProgressBar) nutritionCard.getChildAt(1);
        nutritionProgressBar.setProgressTintList(ColorStateList.valueOf(0xFFFF9800));
        parent.addView(nutritionCard);
    }

    private LinearLayout createSummaryCard(String title, int backgroundColor) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackgroundColor(backgroundColor);
        card.setPadding(20, 20, 20, 20);
        card.setElevation(4);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(0, 0, 0, 15);
        card.setLayoutParams(cardParams);

        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(18);
        titleText.setTextColor(0xFF1976D2);
        titleText.setPadding(0, 0, 0, 10);
        card.addView(titleText);

        ProgressBar progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 20));
        card.addView(progressBar);

        TextView contentText = new TextView(this);
        contentText.setTextSize(14);
        contentText.setTextColor(0xFF666666);
        contentText.setTypeface(null, Typeface.BOLD);
        card.addView(contentText);

        return card;
    }

    private void createQuickActions(LinearLayout parent) {
        TextView actionTitle = new TextView(this);
        actionTitle.setText(getString(R.string.quick_actions));
        actionTitle.setTextSize(20);
        actionTitle.setTextColor(0xFF1976D2);
        actionTitle.setGravity(android.view.Gravity.CENTER);
        actionTitle.setPadding(0, 20, 0, 20);
        parent.addView(actionTitle);

        Button waterBtn = createQuickActionButton(getString(R.string.water_tracking), 0xFF00BCD4,
                () -> startActivity(new Intent(DashboardActivity.this, WaterTrackingActivity.class)));
        Button exerciseBtn = createQuickActionButton(getString(R.string.exercise_tracking), 0xFF4CAF50,
                () -> startActivity(new Intent(DashboardActivity.this, ExerciseActivity.class)));
        Button nutritionBtn = createQuickActionButton(getString(R.string.nutrition), 0xFFFF9800,
                () -> startActivity(new Intent(DashboardActivity.this, NutritionActivity.class)));
        Button settingsBtn = createQuickActionButton(getString(R.string.settings), 0xFF9E9E9E,
                this::showSettingsDialog);
        Button healthBtn = createQuickActionButton(getString(R.string.personal_health), 0xFF9C27B0,
                () -> startActivity(new Intent(DashboardActivity.this, PersonalCalculationActivity.class)));

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        btnParams.setMargins(10, 10, 10, 10);

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(android.view.Gravity.CENTER);

        waterBtn.setLayoutParams(btnParams);
        exerciseBtn.setLayoutParams(btnParams);
        nutritionBtn.setLayoutParams(btnParams);

        topRow.addView(waterBtn);
        topRow.addView(exerciseBtn);
        topRow.addView(nutritionBtn);

        LinearLayout bottomRow = new LinearLayout(this);
        bottomRow.setOrientation(LinearLayout.HORIZONTAL);
        bottomRow.setGravity(android.view.Gravity.CENTER);

        settingsBtn.setLayoutParams(btnParams);
        healthBtn.setLayoutParams(btnParams);

        bottomRow.addView(settingsBtn);
        bottomRow.addView(healthBtn);

        parent.addView(topRow);
        parent.addView(bottomRow);
    }

    private Button createQuickActionButton(String text, int color, Runnable action) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextSize(12);
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(15);
        shape.setColor(color);
        button.setBackground(shape);
        button.setTextColor(0xFFFFFFFF);
        button.setPadding(20, 15, 20, 15);
        button.setOnClickListener(v -> action.run());
        return button;
    }

    private void createBackButton(LinearLayout parent) {
        Button backButton = new Button(this);
        backButton.setText(getString(R.string.back_to_menu));
        backButton.setTextSize(16);
        backButton.setBackgroundColor(0xFF666666);
        backButton.setTextColor(0xFFFFFFFF);
        backButton.setPadding(30, 20, 30, 20);

        LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        backParams.setMargins(0, 30, 0, 0);
        backButton.setLayoutParams(backParams);

        backButton.setOnClickListener(v -> finish());

        parent.addView(backButton);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.settings);

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(24, 24, 24, 24);
        GradientDrawable dialogBackground = new GradientDrawable();
        dialogBackground.setColor(0xFFFFFFFF);
        dialogBackground.setCornerRadius(12);
        dialogBackground.setStroke(2, 0xFFCCCCCC);
        dialogLayout.setBackground(dialogBackground);

        TextView waterLabel = new TextView(this);
        waterLabel.setText(R.string.water_goal);
        waterLabel.setTextSize(16);
        waterLabel.setTextColor(0xFF1976D2);
        waterLabel.setPadding(0, 0, 0, 8);
        dialogLayout.addView(waterLabel);

        final EditText waterGoalInput = new EditText(this);
        waterGoalInput.setHint(R.string.water_goal);
        waterGoalInput.setText(String.valueOf(prefsManager.getDailyWaterGoal()));
        waterGoalInput.setTextSize(16);
        waterGoalInput.setPadding(8, 8, 8, 8);
        waterGoalInput.setBackgroundResource(android.R.drawable.edit_text);
        dialogLayout.addView(waterGoalInput);

        TextView calorieLabel = new TextView(this);
        calorieLabel.setText(R.string.set_daily_calorie_goal);
        calorieLabel.setTextSize(16);
        calorieLabel.setTextColor(0xFF1976D2);
        calorieLabel.setPadding(0, 16, 0, 8);
        dialogLayout.addView(calorieLabel);

        final EditText calorieGoalInput = new EditText(this);
        calorieGoalInput.setHint(R.string.set_daily_calorie_goal);
        calorieGoalInput.setText(String.valueOf(prefsManager.getDailyCalorieGoal()));
        calorieGoalInput.setTextSize(16);
        calorieGoalInput.setPadding(8, 8, 8, 8);
        calorieGoalInput.setBackgroundResource(android.R.drawable.edit_text);
        dialogLayout.addView(calorieGoalInput);

        TextView resetLabel = new TextView(this);
        resetLabel.setText(R.string.reset_options);
        resetLabel.setTextSize(16);
        resetLabel.setTextColor(0xFF1976D2);
        resetLabel.setPadding(0, 16, 0, 8);
        dialogLayout.addView(resetLabel);

        builder.setView(dialogLayout);

        builder.setPositiveButton(R.string.save, (dialog, which) -> {
            try {
                int newWaterGoal = Integer.parseInt(waterGoalInput.getText().toString());
                int newCalorieGoal = Integer.parseInt(calorieGoalInput.getText().toString());
                if (newWaterGoal > 0 && newCalorieGoal > 0) {
                    prefsManager.saveDailyWaterGoal(newWaterGoal);
                    prefsManager.saveDailyCalorieGoal(newCalorieGoal);
                    Toast.makeText(this, R.string.goals_updated, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.invalid_input, Toast.LENGTH_SHORT).show();
                }
                updateDashboardData();
            } catch (NumberFormatException e) {
                Toast.makeText(this, R.string.invalid_input, Toast.LENGTH_SHORT).show();
            }
        });

        String[] resetOptions = {
                getString(R.string.reset_hydration),
                getString(R.string.reset_exercise),
                getString(R.string.reset_nutrition),
                getString(R.string.reset_all)
        };
        builder.setItems(resetOptions, (dialog, which) -> confirmReset(which));

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void confirmReset(int option) {
        String message = "";
        switch (option) {
            case 0: message = getString(R.string.confirm_hydration_reset); break;
            case 1: message = getString(R.string.confirm_exercise_reset); break;
            case 2: message = getString(R.string.confirm_nutrition_reset); break;
            case 3: message = getString(R.string.confirm_all_reset); break;
        }
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.yes, (dialog, which) -> performReset(option))
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void performReset(int option) {
        switch (option) {
            case 0: prefsManager.saveCurrentWaterIntake(0); break;
            case 1: prefsManager.saveTotalDuration(0); prefsManager.saveCaloriesBurned(0); break;
            case 2: prefsManager.saveCaloriesConsumed(0); break;
            case 3: prefsManager.saveCurrentWaterIntake(0); prefsManager.saveTotalDuration(0);
                prefsManager.saveCaloriesBurned(0); prefsManager.saveCaloriesConsumed(0); break;
        }
        Toast.makeText(this, getResetMessage(option), Toast.LENGTH_SHORT).show();
        updateDashboardData();
    }

    private String getResetMessage(int option) {
        switch (option) {
            case 0: return getString(R.string.hydration_reset);
            case 1: return getString(R.string.exercise_reset);
            case 2: return getString(R.string.nutrition_reset);
            case 3: return getString(R.string.all_reset);
            default: return "";
        }
    }

    private void updateDashboardData() {
        welcomeText.setText(String.format(getString(R.string.good_day_message), DateUtils.getCurrentDate()));

        // Water data
        int waterIntake = Math.min(Math.max(prefsManager.getCurrentWaterIntake(), 0), 5000);
        int waterGoal = prefsManager.getDailyWaterGoal() > 0 ? prefsManager.getDailyWaterGoal() : 2000;
        int waterProgress = waterGoal > 0 ? Math.min((waterIntake * 100) / waterGoal, 100) : 0;
        String hydrationStatus = getHydrationStatus(waterProgress);
        waterSummaryText.setText(String.format(getString(R.string.water_summary), waterIntake, waterGoal, waterProgress, hydrationStatus) + "\n" + getString(R.string.remaining) + ": " + Math.max(waterGoal - waterIntake, 0) + " " + getString(R.string.ml));
        waterProgressBar.setMax(100);
        waterProgressBar.setProgress(waterProgress);

        // Exercise data
        int duration = Math.min(Math.max(prefsManager.getTotalDuration(), 0), 600);
        int exerciseGoal = 60;
        int exerciseProgress = duration > 0 ? Math.min((duration * 100) / exerciseGoal, 100) : 0;
        int caloriesBurned = Math.min(Math.max(prefsManager.getCaloriesBurned(), 0), 5000);
        String exerciseStatus = getExerciseStatus(exerciseProgress);
        int remainingMinutes = Math.max(exerciseGoal - duration, 0);
        exerciseSummaryText.setText(String.format(getString(R.string.exercise_summary), duration, caloriesBurned, exerciseProgress, exerciseStatus) + "\n" + getString(R.string.remaining) + ": " + remainingMinutes + " " + getString(R.string.min));
        exerciseProgressBar.setMax(100);
        exerciseProgressBar.setProgress(exerciseProgress);

        // Nutrition data
        int caloriesConsumed = Math.min(Math.max(prefsManager.getCaloriesConsumed(), 0), 5000);
        int calorieGoal = prefsManager.getDailyCalorieGoal() > 0 ? prefsManager.getDailyCalorieGoal() : 2000;
        int nutritionProgress = calorieGoal > 0 ? Math.min((caloriesConsumed * 100) / calorieGoal, 100) : 0;
        String nutritionStatus = getNutritionStatus(nutritionProgress);
        if (caloriesConsumed > calorieGoal) {
            nutritionStatus = getString(R.string.excess_status);
        }
        nutritionSummaryText.setText(String.format(getString(R.string.nutrition_summary), caloriesConsumed, calorieGoal, nutritionProgress, nutritionStatus) + "\n" + getString(R.string.remaining) + ": " + Math.max(calorieGoal - caloriesConsumed, 0) + " " + getString(R.string.kcal));
        nutritionProgressBar.setMax(100);
        nutritionProgressBar.setProgress(nutritionProgress);

        Log.d("Dashboard", "Water: " + waterIntake + "/" + waterGoal);
        Log.d("Dashboard", "Exercise: " + duration + " min, " + caloriesBurned + " kcal");
        Log.d("Dashboard", "Nutrition: " + caloriesConsumed + "/" + calorieGoal);
    }

    private String getHydrationStatus(int progress) {
        if (progress >= 100) return getString(R.string.excellent_status);
        else if (progress >= 75) return getString(R.string.good_status);
        else if (progress >= 50) return getString(R.string.moderate_status);
        else return getString(R.string.needs_attention_status);
    }

    private String getExerciseStatus(int progress) {
        if (progress >= 100) return getString(R.string.excellent_status);
        else if (progress >= 75) return getString(R.string.good_status);
        else if (progress >= 50) return getString(R.string.moderate_status);
        else return getString(R.string.needs_attention_status);
    }

    private String getNutritionStatus(int progress) {
        if (progress >= 100) return getString(R.string.excellent_status);
        else if (progress >= 75) return getString(R.string.good_status);
        else if (progress >= 50) return getString(R.string.moderate_status);
        else return getString(R.string.needs_attention_status);
    }
}