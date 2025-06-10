package com.example.lapakta.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.lapakta.data.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CacheManager {
    private static final String PREF_NAME = "LapakTaCache";
    private static final String KEY_PRODUCTS = "products_cache";

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Menyimpan daftar produk ke SharedPreferences
    public static void saveProducts(Context context, List<Product> productList) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(productList);
        editor.putString(KEY_PRODUCTS, json);
        editor.apply();
    }

    // Mengambil daftar produk dari SharedPreferences
    public static List<Product> loadProducts(Context context) {
        String json = getPrefs(context).getString(KEY_PRODUCTS, null);
        if (json == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {}.getType();
        return gson.fromJson(json, type);
    }
}