package com.example.lapakta.ui.activity;

import android.app.AlertDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.lapakta.R;
import com.example.lapakta.adapter.ImageSliderAdapter;
import com.example.lapakta.adapter.ReviewAdapter;
import com.example.lapakta.data.local.CartManager;
import com.example.lapakta.data.model.Product;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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
        }

        product = getIntent().getParcelableExtra(EXTRA_PRODUCT);

        if (product != null) {
            getSupportActionBar().setTitle(product.getTitle());
            populateUI();
        }
    }

    private void populateUI() {
        // Setup Image Slider
        ViewPager2 imageSlider = findViewById(R.id.imageSlider);
        imageSlider.setAdapter(new ImageSliderAdapter(product.getImages()));

        // Info Utama
        TextView tvTitle = findViewById(R.id.tvProductTitle);
        TextView tvBrand = findViewById(R.id.tvBrand);
        TextView tvDiscountedPrice = findViewById(R.id.tvDiscountedPrice);
        TextView tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        TextView tvRating = findViewById(R.id.tvRating);

        tvTitle.setText(product.getTitle());
        tvBrand.setText(product.getBrand());
        ratingBar.setRating((float) product.getRating());
        tvRating.setText(String.valueOf(product.getRating()));

        // Kalkulasi Harga Diskon
        double originalPrice = product.getPrice();
        double discount = product.getDiscountPercentage();
        double discountedPrice = originalPrice - (originalPrice * discount / 100);
        tvDiscountedPrice.setText("Rp " + String.format("%,.2f", discountedPrice));
        tvOriginalPrice.setText("Rp " + String.format("%,.2f", originalPrice));
        tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Info Lainnya
        TextView tvAvailability = findViewById(R.id.tvAvailability);
        TextView tvDescription = findViewById(R.id.tvProductDescription);
        TextView tvShipping = findViewById(R.id.tvShippingInfo);
        TextView tvWarranty = findViewById(R.id.tvWarrantyInfo);
        TextView tvReturnPolicy = findViewById(R.id.tvReturnPolicy);

        tvAvailability.setText("Ketersediaan: " + product.getAvailabilityStatus() + " (" + product.getStock() + " tersisa)");
        tvDescription.setText(product.getDescription());
        tvShipping.setText("Pengiriman: " + product.getShippingInformation());
        tvWarranty.setText("Garansi: " + product.getWarrantyInformation());
        tvReturnPolicy.setText("Kebijakan Pengembalian: " + product.getReturnPolicy());

        // Tags
        ChipGroup chipGroup = findViewById(R.id.chipGroupTags);
        if (product.getTags() != null) {
            for (String tag : product.getTags()) {
                Chip chip = new Chip(this);
                chip.setText(tag);
                chipGroup.addView(chip);
            }
        }

        // Reviews
        RecyclerView rvReviews = findViewById(R.id.rvReviews);
        if (product.getReviews() != null && !product.getReviews().isEmpty()) {
            rvReviews.setLayoutManager(new LinearLayoutManager(this));
            rvReviews.setAdapter(new ReviewAdapter(product.getReviews()));
        }

        // Tombol Add to Cart
        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        btnAddToCart.setOnClickListener(v -> showQuantityDialog());
    }

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
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
