package com.example.lapakta.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.lapakta.data.model.CartItem;
import com.example.lapakta.data.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static final String PREF_NAME = "LapakTaCart";
    private static final String KEY_CART_ITEMS = "cart_items_v2"; // Gunakan key baru untuk struktur data baru

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static List<CartItem> getCartItems(Context context) {
        String json = getPrefs(context).getString(KEY_CART_ITEMS, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private static void saveCartItems(Context context, List<CartItem> cartItems) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(cartItems);
        editor.putString(KEY_CART_ITEMS, json);
        editor.apply();
    }

    public static void addProduct(Context context, Product product) {
        List<CartItem> cartItems = getCartItems(context);
        // Cek apakah produk sudah ada di keranjang
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                // Jika sudah ada, tambah kuantitasnya
                item.setQuantity(item.getQuantity() + 1);
                saveCartItems(context, cartItems);
                return;
            }
        }
        // Jika belum ada, tambahkan sebagai item baru dengan kuantitas 1
        cartItems.add(new CartItem(product, 1));
        saveCartItems(context, cartItems);
    }

    // Hapus item sepenuhnya dari keranjang
    public static void removeItem(Context context, CartItem cartItem) {
        List<CartItem> cartItems = getCartItems(context);
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getProduct().getId() == cartItem.getProduct().getId()) {
                cartItems.remove(i);
                break;
            }
        }
        saveCartItems(context, cartItems);
    }

    // Kurangi kuantitas, atau hapus jika kuantitas jadi 0
    public static void decreaseQuantity(Context context, CartItem cartItem) {
        List<CartItem> cartItems = getCartItems(context);
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == cartItem.getProduct().getId()) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    // Jika kuantitas 1, hapus item
                    removeItem(context, cartItem);
                    return; // Return agar saveCartItems tidak dipanggil dua kali
                }
                break;
            }
        }
        saveCartItems(context, cartItems);
    }

    public static void clearCart(Context context) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.remove(KEY_CART_ITEMS);
        editor.apply();
    }

    public static void addProductWithQuantity(Context context, Product product, int quantity) {
        List<CartItem> cartItems = getCartItems(context);

        // Cek apakah produk sudah ada di keranjang
        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == product.getId()) {
                // Jika sudah ada, tambahkan kuantitasnya
                item.setQuantity(item.getQuantity() + quantity);
                saveCartItems(context, cartItems);
                return;
            }
        }

        // Jika belum ada, tambahkan sebagai item baru
        cartItems.add(new CartItem(product, quantity));
        saveCartItems(context, cartItems);
    }
}