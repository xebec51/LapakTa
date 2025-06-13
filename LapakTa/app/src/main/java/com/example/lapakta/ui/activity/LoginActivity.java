package com.example.lapakta.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lapakta.R;
import com.example.lapakta.data.local.SessionManager;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToRegister);

        btnLogin.setOnClickListener(v -> {
            // Convert username to lowercase
            String username = etUsername.getText().toString().trim().toLowerCase();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check credentials with lowercase username
            btnLogin.setEnabled(false);
            btnLogin.setText("Mengecek...");

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (SessionManager.checkUserCredentials(this, username, password)) {
                    // Save session with lowercase username
                    SessionManager.saveLoginSession(this, username);
                    Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show();
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");
                }
            }, 500);
        });

        // Listener untuk teks "Daftar di sini"
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            // Tambahkan transisi saat pindah ke RegisterActivity
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}
