package com.example.lapakta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lapakta.R;
import com.example.lapakta.data.model.CartItem;
import com.example.lapakta.data.model.Product;
import com.squareup.picasso.Picasso;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private CartListener listener;

    // Listener untuk menangani semua aksi dari item di keranjang
    public interface CartListener {
        void onIncrease(CartItem item);
        void onDecrease(CartItem item);
        void onRemove(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItems, CartListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        Product product = cartItem.getProduct();

        // hitung subtotal untuk setiap item
        double subtotal = product.getPrice() * cartItem.getQuantity();

        // Bind data produk dan kuantitas ke view
        holder.tvTitle.setText(product.getTitle());
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        holder.tvPrice.setText(format.format(product.getPrice()));
        holder.tvSubtotal.setText("Subtotal: " + format.format(subtotal));
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
        Picasso.get().load(product.getThumbnail()).into(holder.ivThumbnail);

        // Set listener untuk setiap tombol
        holder.btnIncrease.setOnClickListener(v -> listener.onIncrease(cartItem));
        holder.btnDecrease.setOnClickListener(v -> listener.onDecrease(cartItem));
        holder.btnRemove.setOnClickListener(v -> listener.onRemove(cartItem));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle, tvPrice, tvQuantity, tvSubtotal;
        ImageButton btnRemove, btnIncrease, btnDecrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            // Inisialisasi view untuk kuantitas
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal); // Inisialisasi TextView untuk subtotal
        }
    }
}
