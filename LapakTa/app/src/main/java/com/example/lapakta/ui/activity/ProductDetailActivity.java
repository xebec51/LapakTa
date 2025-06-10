package com.example.lapakta.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.lapakta.R;
import com.example.lapakta.data.local.CartManager;
import com.example.lapakta.data.model.Product;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "extra_product";
    private Product product;
    private int quantity = 1; // Variabel untuk menyimpan kuantitas di dialog

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Inisialisasi Views
        ImageView ivProductImage = findViewById(R.id.ivProductImage);
        TextView tvProductTitle = findViewById(R.id.tvProductTitle);
        TextView tvProductPrice = findViewById(R.id.tvProductPrice);
        TextView tvProductDescription = findViewById(R.id.tvProductDescription);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);

        product = getIntent().getParcelableExtra(EXTRA_PRODUCT);

        if (product != null) {
            // Set data produk ke view
            getSupportActionBar().setTitle(product.getTitle());
            tvProductTitle.setText(product.getTitle());
            tvProductPrice.setText("Rp " + String.format("%,.2f", product.getPrice()));
            tvProductDescription.setText(product.getDescription());
            Picasso.get().load(product.getThumbnail()).into(ivProductImage);

            // Set listener untuk tombol Add to Cart
            btnAddToCart.setOnClickListener(v -> showQuantityDialog());
        }
    }

    // --- METODE BARU UNTUK MENAMPILKAN DIALOG KUANTITAS ---
    private void showQuantityDialog() {
        // Inflate layout custom
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_quantity_picker, null);

        // Buat AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Inisialisasi view di dalam dialog
        TextView tvDialogQuantity = dialogView.findViewById(R.id.tvDialogQuantity);
        ImageButton btnDialogDecrease = dialogView.findViewById(R.id.btnDialogDecrease);
        ImageButton btnDialogIncrease = dialogView.findViewById(R.id.btnDialogIncrease);

        // Set kuantitas awal
        quantity = 1;
        tvDialogQuantity.setText(String.valueOf(quantity));

        // Logic untuk tombol kurang (-)
        btnDialogDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvDialogQuantity.setText(String.valueOf(quantity));
            }
        });

        // Logic untuk tombol tambah (+)
        btnDialogIncrease.setOnClickListener(v -> {
            quantity++;
            tvDialogQuantity.setText(String.valueOf(quantity));
        });

        // Atur tombol positif (Tambah ke Keranjang) dan negatif (Batal)
        builder.setPositiveButton("Tambah ke Keranjang", (dialog, which) -> {
            CartManager.addProductWithQuantity(this, product, quantity);
            Toast.makeText(this, quantity + " " + product.getTitle() + " ditambahkan", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        // Tampilkan dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
