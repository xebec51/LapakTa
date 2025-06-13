package com.example.lapakta.ui.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.lapakta.R;
import com.example.lapakta.data.local.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {
    private EditText etFullName, etUsername, etDateOfBirth, etEmail, etPhoneNumber;
    private Calendar myCalendar = Calendar.getInstance();
    private String originalUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarEditProfile);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inisialisasi Views
        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        Button btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Muat data pengguna yang ada
        loadUserData();

        // Setup DatePickerDialog
        setupDatePicker();

        // Listener untuk tombol simpan
        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void loadUserData() {
        originalUsername = SessionManager.getLoggedInUsername(this);
        etFullName.setText(SessionManager.getFullName(this));
        etUsername.setText(originalUsername);
        etEmail.setText(SessionManager.getEmail(this));
        etPhoneNumber.setText(SessionManager.getPhoneNumber(this));
        etDateOfBirth.setText(SessionManager.getDateOfBirth(this));
    }

    private void setupDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        etDateOfBirth.setOnClickListener(v -> {
            new DatePickerDialog(this, dateSetListener,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDateOfBirth.setText(sdf.format(myCalendar.getTime()));
    }

    private void saveChanges() {
        String fullName = etFullName.getText().toString().trim();
        
        // Convert username to lowercase
        String newUsername = etUsername.getText().toString().trim().toLowerCase();
        
        String dob = etDateOfBirth.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhoneNumber.getText().toString().trim();

        if (fullName.isEmpty() || newUsername.isEmpty() || dob.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate with lowercase username
        if (!newUsername.equals(originalUsername) && SessionManager.isUserExists(this, newUsername)) {
            etUsername.setError("Username ini sudah digunakan");
            return;
        }

        // Save changes with lowercase username
        SessionManager.updateUserProfileAndUsername(this, newUsername, fullName, email, phone, dob);
        Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
