package com.example.lapakta.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lapakta.R;
import com.example.lapakta.data.local.CartManager;
import com.example.lapakta.data.local.SessionManager;
import com.example.lapakta.data.local.ThemeManager;
import com.example.lapakta.ui.activity.ChangePasswordActivity;
import com.example.lapakta.ui.activity.EditProfileActivity;
import com.example.lapakta.ui.activity.SplashActivity;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AccountFragment extends Fragment {

    private TextView tvFullName, tvUsername, tvEmail;
    private ShapeableImageView ivProfile;
    private ImageButton btnEditPhoto;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    // Simpan gambar ke penyimpanan internal
                    String savedImagePath = saveImageToInternalStorage(uri);
                    if (savedImagePath != null) {
                        // Simpan path ke SessionManager
                        SessionManager.saveProfilePhotoPath(requireContext(), savedImagePath);
                        // Muat gambar dari file yang sudah disimpan
                        Picasso.get().load(new File(savedImagePath)).into(ivProfile);
                        Toast.makeText(requireContext(), "Foto profil berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi Views
        tvFullName = view.findViewById(R.id.tvFullName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        ivProfile = view.findViewById(R.id.ivProfile);
        btnEditPhoto = view.findViewById(R.id.btnEditPhoto);
        TextView tvEditProfile = view.findViewById(R.id.tvEditProfile);
        TextView tvChangePassword = view.findViewById(R.id.tvChangePassword);
        SwitchMaterial switchTheme = view.findViewById(R.id.switchTheme);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        btnEditPhoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        tvEditProfile.setOnClickListener(v -> startActivity(new Intent(requireActivity(), EditProfileActivity.class)));
        tvChangePassword.setOnClickListener(v -> startActivity(new Intent(requireActivity(), ChangePasswordActivity.class)));

        int currentTheme = ThemeManager.getSavedTheme(requireContext());
        switchTheme.setChecked(currentTheme == ThemeManager.THEME_DARK);
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int newTheme = isChecked ? ThemeManager.THEME_DARK : ThemeManager.THEME_LIGHT;
            ThemeManager.saveTheme(requireContext(), newTheme);
            ThemeManager.applyTheme(newTheme);
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Konfirmasi Logout")
                    .setMessage("Anda yakin ingin keluar?")
                    .setPositiveButton("Ya", (dialog, which) -> {
                        SessionManager.clearSession(requireContext());
                        CartManager.clearCart(requireContext());
                        Intent intent = new Intent(requireActivity(), SplashActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
                    .setNegativeButton("Tidak", null)
                    .show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadAndDisplayUserData();
    }

    private void loadAndDisplayUserData() {
        if (getContext() == null) return;
        tvFullName.setText(SessionManager.getFullName(requireContext()));
        tvUsername.setText("@" + SessionManager.getLoggedInUsername(requireContext()));
        tvEmail.setText(SessionManager.getEmail(requireContext()));

        // --- Logika untuk memuat foto profil ---
        String photoPath = SessionManager.getProfilePhotoPath(requireContext());
        if (photoPath != null) {
            Picasso.get()
                    .load(new File(photoPath))
                    .placeholder(R.drawable.ic_account) // Gambar default jika gagal load
                    .error(R.drawable.ic_account)       // Gambar default jika ada error
                    .into(ivProfile);
        } else {
            // Jika tidak ada foto, tampilkan ikon default
            ivProfile.setImageResource(R.drawable.ic_account);
        }
    }

    private String saveImageToInternalStorage(Uri uri) {
        if (getContext() == null) return null;

        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            // Buat nama file unik berdasarkan username
            String username = SessionManager.getLoggedInUsername(getContext());
            File file = new File(getContext().getFilesDir(), "profile_" + username + ".jpg");

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath(); // Kembalikan path absolut dari file yang disimpan
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}