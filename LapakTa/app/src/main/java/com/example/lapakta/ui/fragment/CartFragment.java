package com.example.lapakta.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lapakta.R;
import com.example.lapakta.adapter.CartAdapter;
import com.example.lapakta.data.local.CartManager;
import com.example.lapakta.data.model.CartItem;
import com.example.lapakta.ui.activity.CheckoutActivity;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment {

    private RecyclerView rvCart;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private Button btnCheckout;
    private TextView tvTotalPrice;
    private LinearLayout bottomLayout; // Variabel untuk layout bawah
    private View emptyCartView;        // Variabel untuk tampilan keranjang kosong

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvCart = view.findViewById(R.id.rvCart);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        bottomLayout = view.findViewById(R.id.bottomLayout); // Inisialisasi layout bawah
        emptyCartView = view.findViewById(R.id.emptyCartView); // Inisialisasi view kosong

        setupRecyclerView();

        btnCheckout.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(requireContext(), "Keranjang Anda kosong!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Buka CheckoutActivity
            Intent intent = new Intent(getActivity(), CheckoutActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartUI();
    }

    private void setupRecyclerView() {
        cartItems = CartManager.getCartItems(requireContext());
        cartAdapter = new CartAdapter(cartItems, new CartAdapter.CartListener() {
            @Override
            public void onIncrease(CartItem item) {
                CartManager.addProduct(requireContext(), item.getProduct());
                updateCartUI();
            }

            @Override
            public void onDecrease(CartItem item) {
                if (item.getQuantity() > 1) {
                    CartManager.decreaseQuantity(requireContext(), item);
                    updateCartUI();
                } else {
                    showRemoveConfirmationDialog(item);
                }
            }

            @Override
            public void onRemove(CartItem item) {
                showRemoveConfirmationDialog(item);
            }
        });
        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCart.setAdapter(cartAdapter);
    }

    private void showRemoveConfirmationDialog(CartItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Hapus")
                .setMessage("Anda yakin ingin menghapus " + item.getProduct().getTitle() + " dari keranjang?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    CartManager.removeItem(requireContext(), item);
                    updateCartUI();
                    Toast.makeText(requireContext(), item.getProduct().getTitle() + " dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Tidak", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void updateCartUI() {
        // Muat ulang item dari CartManager
        cartItems.clear();
        cartItems.addAll(CartManager.getCartItems(requireContext()));
        cartAdapter.notifyDataSetChanged();

        // Cek apakah keranjang kosong
        if (cartItems.isEmpty()) {
            rvCart.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            emptyCartView.setVisibility(View.VISIBLE);
        } else {
            rvCart.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
            emptyCartView.setVisibility(View.GONE);
        }

        double totalPrice = 0;
        for (CartItem item : cartItems) {
            // Kalkulasi harga diskon sebelum menjumlahkan
            double originalPrice = item.getProduct().getPrice();
            double discountPercentage = item.getProduct().getDiscountPercentage();
            double discountedPrice = originalPrice - (originalPrice * discountPercentage / 100);
            totalPrice += discountedPrice * item.getQuantity();
        }
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        tvTotalPrice.setText("Total: " + format.format(totalPrice));
    }
}