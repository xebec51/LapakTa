package com.example.lapakta.ui.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView rvCart;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private Button btnCheckout;
    private TextView tvTotalPrice;

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
                // Jika kuantitas > 1, kurangi. Jika 1, tampilkan dialog konfirmasi hapus.
                if (item.getQuantity() > 1) {
                    CartManager.decreaseQuantity(requireContext(), item);
                    updateCartUI();
                } else {
                    showRemoveConfirmationDialog(item);
                }
            }

            @Override
            public void onRemove(CartItem item) {
                // Selalu tampilkan dialog konfirmasi saat tombol hapus utama ditekan
                showRemoveConfirmationDialog(item);
            }
        });
        rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCart.setAdapter(cartAdapter);
    }

    // --- METODE BARU UNTUK DIALOG KONFIRMASI ---
    private void showRemoveConfirmationDialog(CartItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi Hapus")
                .setMessage("Anda yakin ingin menghapus " + item.getProduct().getTitle() + " dari keranjang?")
                .setPositiveButton("Ya", (dialog, which) -> {
                    // Jika user klik "Ya", hapus item sepenuhnya
                    CartManager.removeItem(requireContext(), item);
                    updateCartUI();
                    Toast.makeText(requireContext(), item.getProduct().getTitle() + " dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Tidak", null) // Tombol "Tidak" tidak melakukan apa-apa
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void updateCartUI() {
        cartItems.clear();
        cartItems.addAll(CartManager.getCartItems(requireContext()));
        cartAdapter.notifyDataSetChanged();

        double totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getProduct().getPrice() * item.getQuantity();
        }
        tvTotalPrice.setText("Total: Rp " + String.format("%,.2f", totalPrice));
    }
}
