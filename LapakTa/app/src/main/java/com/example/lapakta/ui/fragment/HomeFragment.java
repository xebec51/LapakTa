package com.example.lapakta.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapakta.R;
import com.example.lapakta.adapter.ProductAdapter;
import com.example.lapakta.data.model.Product;
import com.example.lapakta.data.model.ProductResponse;
import com.example.lapakta.data.network.ApiClient;
import com.example.lapakta.data.network.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private Button btnRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvProducts = view.findViewById(R.id.rvProducts);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup Adapter
        productAdapter = new ProductAdapter(productList, product -> {
            // Aksi saat item produk di-klik (bisa diimplementasikan nanti)
            Toast.makeText(getContext(), "Clicked: " + product.getTitle(), Toast.LENGTH_SHORT).show();
        });
        rvProducts.setAdapter(productAdapter);

        btnRefresh.setOnClickListener(v -> {
            btnRefresh.setVisibility(View.GONE);
            fetchProducts();
        });

        fetchProducts();
    }

    private void fetchProducts() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            // Operasi background thread
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ProductResponse> call = apiService.getProducts();

            try {
                Response<ProductResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> fetchedProducts = response.body().getProducts();
                    handler.post(() -> {
                        // Update UI di main thread
                        productList.clear();
                        productList.addAll(fetchedProducts);
                        productAdapter.notifyDataSetChanged();
                        btnRefresh.setVisibility(View.GONE);
                    });
                } else {
                    handler.post(() -> showError());
                }
            } catch (Exception e) {
                handler.post(() -> showError());
            }
        });
    }

    private void showError() {
        Toast.makeText(getContext(), "Gagal memuat data. Periksa koneksi internet Anda.", Toast.LENGTH_LONG).show();
        btnRefresh.setVisibility(View.VISIBLE);
    }
}