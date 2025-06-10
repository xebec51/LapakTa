package com.example.lapakta.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "LapakTaUserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_LOGGED_IN_USERNAME = "loggedInUsername";

    // Kunci untuk menyimpan data per pengguna
    private static final String SUFFIX_PASSWORD = "_password";
    private static final String SUFFIX_FULL_NAME = "_fullName";
    private static final String SUFFIX_EMAIL = "_email";
    private static final String SUFFIX_PHONE = "_phone";
    private static final String SUFFIX_DOB = "_dob";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Perbaikan: Menambahkan parameter String dob
    public static void saveUser(Context context, String username, String password, String fullName, String email, String phone, String dob) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        // Simpan setiap data dengan kunci yang unik berdasarkan username
        editor.putString(username + SUFFIX_PASSWORD, password);
        editor.putString(username + SUFFIX_FULL_NAME, fullName);
        editor.putString(username + SUFFIX_EMAIL, email);
        editor.putString(username + SUFFIX_PHONE, phone);
        editor.putString(username + SUFFIX_DOB, dob); // Sekarang variabel dob sudah ada
        editor.apply();
    }

    // Cek apakah username sudah ada
    public static boolean isUserExists(Context context, String username) {
        return getPrefs(context).contains(username + SUFFIX_PASSWORD);
    }

    // Memeriksa kredensial saat login
    public static boolean checkUserCredentials(Context context, String username, String password) {
        String savedPassword = getPrefs(context).getString(username + SUFFIX_PASSWORD, null);
        return savedPassword != null && savedPassword.equals(password);
    }

    public static void saveLoginSession(Context context, String username) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_LOGGED_IN_USERNAME, username);
        editor.apply();
    }

    public static boolean isLoggedIn(Context context) {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public static String getLoggedInUsername(Context context) {
        return getPrefs(context).getString(KEY_LOGGED_IN_USERNAME, null);
    }

    // Mengambil data pengguna yang sedang login
    public static String getFullName(Context context) {
        String username = getLoggedInUsername(context);
        if (username != null) {
            return getPrefs(context).getString(username + SUFFIX_FULL_NAME, "");
        }
        return "";
    }

    public static void clearSession(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_LOGGED_IN_USERNAME);
        editor.apply();
    }

    public static String getEmail(Context context) {
        String username = getLoggedInUsername(context);
        return (username != null) ? getPrefs(context).getString(username + SUFFIX_EMAIL, "") : "";
    }

    public static String getPhoneNumber(Context context) {
        String username = getLoggedInUsername(context);
        return (username != null) ? getPrefs(context).getString(username + SUFFIX_PHONE, "") : "";
    }

    public static String getDateOfBirth(Context context) {
        String username = getLoggedInUsername(context);
        return (username != null) ? getPrefs(context).getString(username + SUFFIX_DOB, "") : "";
    }

    // Metode untuk memperbarui profil pengguna
    public static void updateUserProfile(Context context, String fullName, String email, String phone, String dob) {
        String username = getLoggedInUsername(context);
        if (username != null) {
            SharedPreferences.Editor editor = getPrefs(context).edit();
            editor.putString(username + SUFFIX_FULL_NAME, fullName);
            editor.putString(username + SUFFIX_EMAIL, email);
            editor.putString(username + SUFFIX_PHONE, phone);
            editor.putString(username + SUFFIX_DOB, dob);
            editor.apply();
        }
    }

    // Metode untuk memperbarui password
    public static void updatePassword(Context context, String newPassword) {
        String username = getLoggedInUsername(context);
        if (username != null) {
            SharedPreferences.Editor editor = getPrefs(context).edit();
            editor.putString(username + SUFFIX_PASSWORD, newPassword);
            editor.apply();
        }
    }

    public static void updateUserProfileAndUsername(Context context, String newUsername, String fullName, String email, String phone, String dob) {
        String oldUsername = getLoggedInUsername(context);
        if (oldUsername == null) return; // Tidak ada pengguna yang login

        // 1. Baca semua data yang terhubung dengan username lama
        String password = getPrefs(context).getString(oldUsername + SUFFIX_PASSWORD, "");

        // 2. Hapus semua data yang terhubung dengan username lama
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.remove(oldUsername + SUFFIX_PASSWORD);
        editor.remove(oldUsername + SUFFIX_FULL_NAME);
        editor.remove(oldUsername + SUFFIX_EMAIL);
        editor.remove(oldUsername + SUFFIX_PHONE);
        editor.remove(oldUsername + SUFFIX_DOB);

        // 3. Simpan kembali semua data dengan username baru
        editor.putString(newUsername + SUFFIX_PASSWORD, password);
        editor.putString(newUsername + SUFFIX_FULL_NAME, fullName);
        editor.putString(newUsername + SUFFIX_EMAIL, email);
        editor.putString(newUsername + SUFFIX_PHONE, phone);
        editor.putString(newUsername + SUFFIX_DOB, dob);

        // 4. Perbarui sesi login dengan username baru
        editor.putString(KEY_LOGGED_IN_USERNAME, newUsername);

        editor.apply();
    }
}
