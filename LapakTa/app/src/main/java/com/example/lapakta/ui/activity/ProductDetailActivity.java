package com.example.lapakta.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.lapakta.R;
import com.example.lapakta.data.model.Product;
import com.squareup.picasso.Picasso;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT = "extra_product";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        // Tampilkan tombol kembali (back)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ImageView ivProductImage = findViewById(R.id.ivProductImage);
        TextView tvProductTitle = findViewById(R.id.tvProductTitle);
        TextView tvProductPrice = findViewById(R.id.tvProductPrice);
        TextView tvProductDescription = findViewById(R.id.tvProductDescription);

        Product product = getIntent().getParcelableExtra(EXTRA_PRODUCT);

        if (product != null) {
            getSupportActionBar().setTitle(product.getTitle());
            tvProductTitle.setText(product.getTitle());
            tvProductPrice.setText("Rp " + product.getPrice());
            tvProductDescription.setText(product.getDescription());
            Picasso.get().load(product.getThumbnail()).into(ivProductImage);
        }
    }

    // Handle klik pada tombol kembali di toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // Kembali ke activity sebelumnya
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}