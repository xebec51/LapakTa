package com.example.lapakta.ui.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lapakta.R;
import com.example.lapakta.data.local.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private EditText etDateOfBirth;
    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText etFullName = findViewById(R.id.etFullName);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPhoneNumber = findViewById(R.id.etPhoneNumber);
        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // Setup DatePickerDialog
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        etDateOfBirth.setOnClickListener(v -> {
            new DatePickerDialog(RegisterActivity.this, dateSetListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnRegister.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String dob = etDateOfBirth.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhoneNumber.getText().toString().trim();
            
            // Convert username to lowercase
            String username = etUsername.getText().toString().trim().toLowerCase();
            
            String password = etPassword.getText().toString().trim();

            if (fullName.isEmpty() || dob.isEmpty() || email.isEmpty() || phone.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Format email tidak valid");
                return;
            }

            if (password.length() < 6) {
                etPassword.setError("Password minimal 6 karakter");
                return;
            }

            if (SessionManager.isUserExists(this, username)) {
                etUsername.setError("Username sudah digunakan");
                return;
            }

            // Save the lowercase username
            SessionManager.saveUser(this, username, password, fullName, email, phone, dob);
            Toast.makeText(this, "Registrasi berhasil! Silakan login.", Toast.LENGTH_LONG).show();
            finish();
        });

        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDateOfBirth.setText(sdf.format(myCalendar.getTime()));
    }

    // Override metode finish() untuk menambahkan transisi
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
