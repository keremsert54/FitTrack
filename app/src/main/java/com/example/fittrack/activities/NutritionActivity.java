package com.example.fittrack.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fittrack.R;
import com.example.fittrack.utils.SharedPrefsManager;
import java.util.Locale;

public class NutritionActivity extends AppCompatActivity {
    private static final String TAG = "NutritionActivity";
    private SharedPrefsManager prefsManager;
    private int totalCalories = 0;
    private TextView totalText;
    private ProgressBar calorieProgressBar;
    private boolean goalSet = false;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefsManager = new SharedPrefsManager(this);
        applyLocale();

        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_nutrition);
        } catch (Exception e) {
            Log.e(TAG, "Layout yüklenemedi: " + e.getMessage());
            Toast.makeText(this, "Layout yüklenemedi!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        setTitle(getString(R.string.nutrition));

        EditText foodInput = findViewById(R.id.food_input);
        EditText calorieInput = findViewById(R.id.calorie_input);
        EditText proteinInput = findViewById(R.id.protein_input);
        EditText carbsInput = findViewById(R.id.carbs_input);
        EditText fatInput = findViewById(R.id.fat_input);
        EditText sugarInput = findViewById(R.id.sugar_input); // Şeker girişi
        Button addButton = findViewById(R.id.add_food_button);
        totalText = findViewById(R.id.total_calories_text);
        calorieProgressBar = findViewById(R.id.calorie_progress_bar);
        EditText calorieGoalInput = findViewById(R.id.calorie_goal_input);
        Button setGoalButton = findViewById(R.id.set_calorie_goal_button);
        backButton = findViewById(R.id.back_button);


        if (addButton == null || backButton == null || foodInput == null || calorieInput == null ||
                proteinInput == null || carbsInput == null || fatInput == null || sugarInput == null ||
                totalText == null || calorieProgressBar == null || calorieGoalInput == null || setGoalButton == null) {
            Log.e(TAG, "Bileşenler bulunamadı! Layout'u kontrol edin.");
            Toast.makeText(this, "Bileşenler bulunamadı! Layout'u kontrol edin.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        goalSet = prefsManager.getDailyCalorieGoal() > 0;
        totalCalories = prefsManager.getCaloriesConsumed();
        int goal = prefsManager.getDailyCalorieGoal();
        int waterGoal = prefsManager.getDailyWaterGoal(); // Su hedefini kontrol et
        Log.d(TAG, "Başlangıç kalori hedefi: " + goal + ", Su hedefi: " + waterGoal);
        if (calorieProgressBar != null) {
            calorieProgressBar.setMax(goal > 0 ? goal : 2000);
        }
        updateTotalText();
        updateProgressBar();
        addButton.setEnabled(goalSet);

        addButton.setOnClickListener(v -> {
            if (goalSet) {
                String calorieStr = calorieInput.getText().toString().trim();
                String proteinStr = proteinInput.getText().toString().trim();
                String carbsStr = carbsInput.getText().toString().trim();
                String fatStr = fatInput.getText().toString().trim();
                String sugarStr = sugarInput.getText().toString().trim(); // Şeker girişi

                try {
                    int calories = !calorieStr.isEmpty() ? Integer.parseInt(calorieStr) : 0;
                    int protein = !proteinStr.isEmpty() ? Integer.parseInt(proteinStr) : 0;
                    int carbs = !carbsStr.isEmpty() ? Integer.parseInt(carbsStr) : 0;
                    int fat = !fatStr.isEmpty() ? Integer.parseInt(fatStr) : 0;
                    int sugar = !sugarStr.isEmpty() ? Integer.parseInt(sugarStr) : 0; // Şeker değeri

                    int calculatedCalories = (protein * 4) + (carbs * 4) + (fat * 9) + (sugar * 4); // Şeker kalorisi eklendi
                    int addedCalories = calories > 0 ? calories + (sugar * 4) : calculatedCalories; // Şeker her zaman kaloriye eklenir
                    if (addedCalories > 0) {
                        totalCalories += addedCalories;
                        prefsManager.saveCaloriesConsumed(totalCalories);
                        updateTotalText();
                        updateProgressBar();
                        clearInputs(foodInput, calorieInput, proteinInput, carbsInput, fatInput, sugarInput);
                        Toast.makeText(this, getString(R.string.food_added, addedCalories), Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Besin eklendi, toplam kalori: " + totalCalories);
                    } else {
                        Toast.makeText(this, "Lütfen geçerli bir kalori değeri girin!", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Sayısal değer hatası: " + e.getMessage());
                    Toast.makeText(this, "Lütfen geçerli sayısal değerler girin!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Lütfen önce günlük kalori hedefi girin!", Toast.LENGTH_SHORT).show();
            }
        });

        setGoalButton.setOnClickListener(v -> {
            String goalStr = calorieGoalInput.getText().toString().trim();
            if (!goalStr.isEmpty()) {
                try {
                    int newGoal = Integer.parseInt(goalStr);
                    int prevWaterGoal = prefsManager.getDailyWaterGoal(); // Önceki su hedefini kontrol et
                    Log.d(TAG, "Yeni kalori hedefi kaydediliyor: " + newGoal + ", Önceki su hedefi: " + prevWaterGoal);
                    // Sadece kalori hedefini kaydet, su hedefini etkileyecek bir şey yapma
                    prefsManager.saveDailyCalorieGoal(newGoal);
                    int newWaterGoal = prefsManager.getDailyWaterGoal(); // Kaydedildikten sonra su hedefini kontrol et
                    Log.d(TAG, "Kaydedildi, Güncel kalori hedefi: " + prefsManager.getDailyCalorieGoal() + ", Güncel su hedefi: " + newWaterGoal);
                    // Su hedefini sıfırlama veya değiştirme yapmıyoruz, sadece kalori hedefini güncelliyoruz
                    goalSet = true;
                    if (calorieProgressBar != null) {
                        calorieProgressBar.setMax(newGoal);
                    }
                    updateProgressBar();
                    calorieGoalInput.setText("");
                    addButton.setEnabled(true);
                    Toast.makeText(this, getString(R.string.set_daily_goal), Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Hedef değeri hatası: " + e.getMessage());
                    Toast.makeText(this, "Lütfen geçerli bir hedef değeri girin!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        } else {
            Log.e(TAG, "back_button is null. Check activity_nutrition.xml");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentLang = prefsManager.getSelectedLanguage();
        if (!currentLang.equals(Locale.getDefault().getLanguage())) {
            applyLocale();
        }
        goalSet = prefsManager.getDailyCalorieGoal() > 0;
        totalCalories = prefsManager.getCaloriesConsumed();
        updateTotalText();
        updateProgressBar();
        Button addButton = findViewById(R.id.add_food_button);
        if (addButton != null) {
            addButton.setEnabled(goalSet);
        }
        Log.d(TAG, "onResume, kalori hedefi: " + prefsManager.getDailyCalorieGoal() + ", Su hedefi: " + prefsManager.getDailyWaterGoal());
    }

    private void applyLocale() {
        prefsManager.setLocale(this, prefsManager.getSelectedLanguage());
    }

    private void updateTotalText() {
        int goal = prefsManager.getDailyCalorieGoal() > 0 ? prefsManager.getDailyCalorieGoal() : 2000;
        int progress = (goal > 0) ? (totalCalories * 100) / goal : 0;
        if (totalText != null) {
            totalText.setText(String.format(getString(R.string.total_calories), totalCalories, goal, progress));
        }
    }

    private void updateProgressBar() {
        int goal = prefsManager.getDailyCalorieGoal() > 0 ? prefsManager.getDailyCalorieGoal() : 2000;
        int progress = (goal > 0) ? Math.min((totalCalories * 100) / goal, 100) : 0;
        if (calorieProgressBar != null) {
            calorieProgressBar.setProgress(progress);
        }
    }

    private void clearInputs(EditText... editTexts) {
        for (EditText editText : editTexts) {
            if (editText != null) {
                editText.setText("");
            }
        }
    }
}

