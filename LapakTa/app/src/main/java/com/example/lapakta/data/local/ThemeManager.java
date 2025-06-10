package com.example.lapakta.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {
    private static final String PREF_NAME = "LapakTaTheme";
    private static final String KEY_THEME = "theme_mode";

    // Constants for theme modes
    public static final int THEME_LIGHT = AppCompatDelegate.MODE_NIGHT_NO;
    public static final int THEME_DARK = AppCompatDelegate.MODE_NIGHT_YES;

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Menyimpan preferensi tema
    public static void saveTheme(Context context, int themeMode) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putInt(KEY_THEME, themeMode);
        editor.apply();
    }

    // Mengambil preferensi tema yang tersimpan
    public static int getSavedTheme(Context context) {
        // Defaultnya adalah Light Mode
        return getPrefs(context).getInt(KEY_THEME, THEME_LIGHT);
    }

    // Menerapkan tema ke aplikasi
    public static void applyTheme(int themeMode) {
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }
}