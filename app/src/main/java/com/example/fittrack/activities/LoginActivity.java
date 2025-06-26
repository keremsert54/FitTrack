package com.example.fittrack.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fittrack.MainActivity;
import com.example.fittrack.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button mainLoginButton, navigateToSignupButton;
    private TextView forgotPasswordText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        mainLoginButton = findViewById(R.id.main_login_button);
        navigateToSignupButton = findViewById(R.id.navigate_to_signup_button);
        forgotPasswordText = findViewById(R.id.forgot_password_text);

        mainLoginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            signIn(email, password);
        });

        navigateToSignupButton.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        forgotPasswordText.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
        });
    }

    private void signIn(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "E-posta ve şifre boş bırakılamaz.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(this, "Giriş başarılı!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        }
                    } else {
                        String errorMessage = "Giriş başarısız!";
                        boolean showResetDialog = false;

                        if (task.getException() != null) {
                            String msg = task.getException().getMessage();
                            if (msg != null) {
                                msg = msg.toLowerCase();
                                if (msg.contains("password is invalid") || msg.contains("wrong-password")) {
                                    errorMessage = "Şifre yanlış. Şifrenizi mi unuttunuz?";
                                    showResetDialog = true;
                                } else if (msg.contains("no user record") || msg.contains("user-not-found")) {
                                    errorMessage = "Kayıtlı kullanıcı bulunamadı.";
                                } else if (msg.contains("email address is badly formatted") || msg.contains("invalid-email")) {
                                    errorMessage = "Geçersiz e-posta formatı.";
                                } else if (msg.contains("network error")) {
                                    errorMessage = "İnternet bağlantısı yok veya kesildi.";
                                } else {
                                    errorMessage = msg;
                                }
                            }
                        }

                        if (showResetDialog) {
                            new AlertDialog.Builder(this)
                                    .setTitle("Şifre Hatası")
                                    .setMessage("Girdiğiniz şifre yanlış. Şifrenizi sıfırlamak ister misiniz?")
                                    .setPositiveButton("Evet", (dialog, which) -> {
                                        startActivity(new Intent(this, ResetPasswordActivity.class));
                                    })
                                    .setNegativeButton("Hayır", null)
                                    .show();
                        } else {
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
