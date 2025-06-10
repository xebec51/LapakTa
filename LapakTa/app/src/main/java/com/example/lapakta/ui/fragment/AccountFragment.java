package com.example.lapakta.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.android.material.switchmaterial.SwitchMaterial;

public class AccountFragment extends Fragment {

    private TextView tvFullName, tvUsername, tvEmail;

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
        TextView tvEditProfile = view.findViewById(R.id.tvEditProfile);
        TextView tvChangePassword = view.findViewById(R.id.tvChangePassword);
        SwitchMaterial switchTheme = view.findViewById(R.id.switchTheme);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        // Listener
        tvEditProfile.setOnClickListener(v -> startActivity(new Intent(requireActivity(), EditProfileActivity.class)));
        tvChangePassword.setOnClickListener(v -> startActivity(new Intent(requireActivity(), ChangePasswordActivity.class)));

        // ... (Logika Switch Tema dan Logout tidak berubah)
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
        // Muat data setiap kali fragment ini ditampilkan
        loadAndDisplayUserData();
    }

    private void loadAndDisplayUserData() {
        if (getContext() == null) return; // Mencegah crash jika fragment sudah tidak ter-attach
        tvFullName.setText(SessionManager.getFullName(requireContext()));
        tvUsername.setText("@" + SessionManager.getLoggedInUsername(requireContext()));
        tvEmail.setText(SessionManager.getEmail(requireContext()));
    }
}
