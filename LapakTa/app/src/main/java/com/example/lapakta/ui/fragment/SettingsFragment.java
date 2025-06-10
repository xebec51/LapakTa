package com.example.lapakta.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.lapakta.R;
import com.example.lapakta.data.local.ThemeManager;

public class SettingsFragment extends Fragment {

    private Switch switchTheme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        switchTheme = view.findViewById(R.id.switchTheme);

        // Atur status switch berdasarkan tema yang tersimpan
        int currentTheme = ThemeManager.getSavedTheme(requireContext());
        switchTheme.setChecked(currentTheme == ThemeManager.THEME_DARK);

        // Tambahkan listener untuk menangani perubahan pada switch
        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int newTheme = isChecked ? ThemeManager.THEME_DARK : ThemeManager.THEME_LIGHT;

            // Simpan dan terapkan tema baru
            ThemeManager.saveTheme(requireContext(), newTheme);
            ThemeManager.applyTheme(newTheme);
        });
    }
}