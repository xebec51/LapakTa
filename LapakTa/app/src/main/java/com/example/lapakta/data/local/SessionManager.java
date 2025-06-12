package com.example.lapakta.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "LapakTaUserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_LOGGED_IN_USERNAME = "loggedInUsername";

    private static final String SUFFIX_PASSWORD = "_password";
    private static final String SUFFIX_FULL_NAME = "_fullName";
    private static final String SUFFIX_EMAIL = "_email";
    private static final String SUFFIX_PHONE = "_phone";
    private static final String SUFFIX_DOB = "_dob";
    private static final String SUFFIX_PHOTO_PATH = "_photoPath"; // KUNCI BARU

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveUser(Context context, String username, String password, String fullName, String email, String phone, String dob) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(username + SUFFIX_PASSWORD, password);
        editor.putString(username + SUFFIX_FULL_NAME, fullName);
        editor.putString(username + SUFFIX_EMAIL, email);
        editor.putString(username + SUFFIX_PHONE, phone);
        editor.putString(username + SUFFIX_DOB, dob);
        editor.apply();
    }

    public static boolean isUserExists(Context context, String username) {
        return getPrefs(context).contains(username + SUFFIX_PASSWORD);
    }

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
        if (oldUsername == null) return;

        String password = getPrefs(context).getString(oldUsername + SUFFIX_PASSWORD, "");
        String photoPath = getPrefs(context).getString(oldUsername + SUFFIX_PHOTO_PATH, null); // Ambil path foto lama

        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.remove(oldUsername + SUFFIX_PASSWORD);
        editor.remove(oldUsername + SUFFIX_FULL_NAME);
        editor.remove(oldUsername + SUFFIX_EMAIL);
        editor.remove(oldUsername + SUFFIX_PHONE);
        editor.remove(oldUsername + SUFFIX_DOB);
        editor.remove(oldUsername + SUFFIX_PHOTO_PATH); // Hapus path foto lama

        editor.putString(newUsername + SUFFIX_PASSWORD, password);
        editor.putString(newUsername + SUFFIX_FULL_NAME, fullName);
        editor.putString(newUsername + SUFFIX_EMAIL, email);
        editor.putString(newUsername + SUFFIX_PHONE, phone);
        editor.putString(newUsername + SUFFIX_DOB, dob);
        if (photoPath != null) {
            editor.putString(newUsername + SUFFIX_PHOTO_PATH, photoPath); // Simpan path foto dengan username baru
        }

        editor.putString(KEY_LOGGED_IN_USERNAME, newUsername);
        editor.apply();
    }

    public static void saveProfilePhotoPath(Context context, String path) {
        String username = getLoggedInUsername(context);
        if (username != null) {
            SharedPreferences.Editor editor = getPrefs(context).edit();
            editor.putString(username + SUFFIX_PHOTO_PATH, path);
            editor.apply();
        }
    }

    public static String getProfilePhotoPath(Context context) {
        String username = getLoggedInUsername(context);
        if (username != null) {
            return getPrefs(context).getString(username + SUFFIX_PHOTO_PATH, null);
        }
        return null;
    }
}