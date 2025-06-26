package com.example.fittrack.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fittrack.R;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText emailInput;
    private Button resetButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.forgot_password));
        }

        emailInput = findViewById(R.id.reset_email_input);
        resetButton = findViewById(R.id.reset_password_button);
        mAuth = FirebaseAuth.getInstance();

        resetButton.setOnClickListener(v -> sendPasswordResetEmail());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void sendPasswordResetEmail() {
        String email = emailInput.getText().toString().trim();

        if (email.isEmpty()) {
            Toast.makeText(this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }

        resetButton.setEnabled(false);
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.reset_link_sent), Toast.LENGTH_SHORT).show();
                    finish(); // Başarılıysa aktiviteyi kapat
                })
                .addOnFailureListener(e -> {
                    String errorMsg = e.getMessage();
                    if (errorMsg.contains("There is no user record")) {
                        Toast.makeText(ResetPasswordActivity.this, getString(R.string.no_user_found), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, getString(R.string.reset_link_failed, errorMsg), Toast.LENGTH_LONG).show();
                    }
                    resetButton.setEnabled(true); // Hata durumunda butonu tekrar etkinleştir
                });
    }
}