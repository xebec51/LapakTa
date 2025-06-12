package com.example.lapakta.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.Locale;

import com.example.lapakta.R;
import com.example.lapakta.data.model.Product;
import com.squareup.picasso.Picasso; // Library untuk load gambar

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public ProductAdapter(List<Product> productList, OnItemClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvTitle.setText(product.getTitle());
        holder.tvBrand.setText(product.getBrand());

        if (product.getCategory() != null) {
            holder.tvCategory.setText(product.getCategory().toUpperCase());
        } else {
            holder.tvCategory.setText("UNCATEGORIZED"); // Teks default jika null
        }

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);
        holder.tvPrice.setText(format.format(product.getPrice()));
        holder.ratingBar.setRating((float) product.getRating());
        Picasso.get().load(product.getThumbnail()).into(holder.ivThumbnail);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle, tvPrice, tvCategory, tvBrand;
        RatingBar ratingBar;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
