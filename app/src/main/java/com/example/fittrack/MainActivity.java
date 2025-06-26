package com.example.fittrack;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fittrack.activities.DashboardActivity;
import com.example.fittrack.activities.ExerciseActivity;
import com.example.fittrack.activities.LoginActivity;
import com.example.fittrack.activities.NutritionActivity;
import com.example.fittrack.activities.PersonalCalculationActivity;
import com.example.fittrack.activities.WaterTrackingActivity;
import com.example.fittrack.utils.SharedPrefsManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SharedPrefsManager prefsManager;
    private FirebaseAuth mAuth;

    // Alt menü butonları
    private Button bottomBtnDashboard, bottomBtnWater, bottomBtnExercise, bottomBtnNutrition, bottomBtnPersonalHealth, bottomBtnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefsManager = new SharedPrefsManager(this);
        mAuth = FirebaseAuth.getInstance();

        applyLocale(prefsManager.getSelectedLanguage());

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // UI elementlerini bağla
        bottomBtnDashboard = findViewById(R.id.bottom_btn_dashboard);
        bottomBtnWater = findViewById(R.id.bottom_btn_water);
        bottomBtnExercise = findViewById(R.id.bottom_btn_exercise);
        bottomBtnNutrition = findViewById(R.id.bottom_btn_nutrition);
        bottomBtnPersonalHealth = findViewById(R.id.bottom_btn_personal_health);
        bottomBtnSignOut = findViewById(R.id.bottom_btn_signout);

        setupUI(user);

        // Orta kısımdaki butonların tıklama olayları
        findViewById(R.id.btn_dashboard).setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
        findViewById(R.id.btn_water).setOnClickListener(v -> startActivity(new Intent(this, WaterTrackingActivity.class)));
        findViewById(R.id.btn_exercise).setOnClickListener(v -> startActivity(new Intent(this, ExerciseActivity.class)));
        findViewById(R.id.btn_nutrition).setOnClickListener(v -> startActivity(new Intent(this, NutritionActivity.class)));
        findViewById(R.id.btn_personal_health).setOnClickListener(v -> startActivity(new Intent(this, PersonalCalculationActivity.class)));
        findViewById(R.id.btn_signout).setOnClickListener(v -> signout());

        // Dil değiştirme butonları
        findViewById(R.id.btn_language_en).setOnClickListener(v -> changeLanguage("en"));
        findViewById(R.id.btn_language_tr).setOnClickListener(v -> changeLanguage("tr"));

        // Alt menü butonlarının tıklama olayları
        bottomBtnDashboard.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
        bottomBtnWater.setOnClickListener(v -> startActivity(new Intent(this, WaterTrackingActivity.class)));
        bottomBtnExercise.setOnClickListener(v -> startActivity(new Intent(this, ExerciseActivity.class)));
        bottomBtnNutrition.setOnClickListener(v -> startActivity(new Intent(this, NutritionActivity.class)));
        bottomBtnPersonalHealth.setOnClickListener(v -> startActivity(new Intent(this, PersonalCalculationActivity.class)));
        bottomBtnSignOut.setOnClickListener(v -> signout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyLocale(prefsManager.getSelectedLanguage());
        setupUI(mAuth.getCurrentUser());
    }

    private void setupUI(FirebaseUser user) {
        TextView welcomeText = findViewById(R.id.welcome_text);
        TextView titleText = findViewById(R.id.title_text);
        TextView appDescription = findViewById(R.id.app_description);

        if (user != null) {
            welcomeText.setText(getString(R.string.welcome) + ", " + user.getEmail());
        }

        if (titleText != null) {
            titleText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        }

        // Metinleri güncelle
        welcomeText.setText(getString(R.string.welcome) + (user != null ? ", " + user.getEmail() : ""));
        titleText.setText(getString(R.string.app_name));
        appDescription.setText(getString(R.string.app_description));

        // Orta kısımdaki butonların metinlerini ayarla
        ((Button) findViewById(R.id.btn_dashboard)).setText(getString(R.string.dashboard));
        ((Button) findViewById(R.id.btn_water)).setText(getString(R.string.water_tracking));
        ((Button) findViewById(R.id.btn_exercise)).setText(getString(R.string.exercise_tracking));
        ((Button) findViewById(R.id.btn_nutrition)).setText(getString(R.string.nutrition));
        ((Button) findViewById(R.id.btn_personal_health)).setText(getString(R.string.personal_health));
        ((Button) findViewById(R.id.btn_signout)).setText(getString(R.string.signout));

        // Alt menü butonlarının metinlerini ayarla
        bottomBtnDashboard.setText(getString(R.string.dashboard));
        bottomBtnWater.setText(getString(R.string.water_tracking));
        bottomBtnExercise.setText(getString(R.string.exercise_tracking));
        bottomBtnNutrition.setText(getString(R.string.nutrition));
        bottomBtnPersonalHealth.setText(getString(R.string.personal_health));
        bottomBtnSignOut.setText(getString(R.string.signout));
    }

    private void applyLocale(String language) {
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

    private void changeLanguage(String language) {
        applyLocale(language);
        recreate(); // Aktiviteyi yeniden yükle
    }

    private void signout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}