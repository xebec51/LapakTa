package com.example.lapakta.ui.fragment;

import android.content.Intent;
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
import com.example.lapakta.data.local.CacheManager;
import com.example.lapakta.data.model.Product;
import com.example.lapakta.data.model.ProductResponse;
import com.example.lapakta.data.network.ApiClient;
import com.example.lapakta.data.network.ApiService;
import com.example.lapakta.ui.activity.ProductDetailActivity;

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

        // Inisialisasi semua view
        rvProducts = view.findViewById(R.id.rvProducts);
        btnRefresh = view.findViewById(R.id.btnRefresh);

        // Panggil metode untuk setup RecyclerView
        setupRecyclerView();

        btnRefresh.setOnClickListener(v -> {
            btnRefresh.setVisibility(View.GONE);
            fetchProducts();
        });

        // Muat data
        loadProductsFromCache();
        fetchProducts();
    }

    private void setupRecyclerView() {
        // Buat dan pasang adapter SEGERA
        productAdapter = new ProductAdapter(productList, product -> {
            Intent intent = new Intent(requireActivity(), ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
            startActivity(intent);
        });

        // Gunakan requireContext() untuk memastikan context tidak null
        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvProducts.setAdapter(productAdapter);
    }

    // Metode lainnya (fetchProducts, loadProductsFromCache, dll.) tidak perlu diubah

    private void fetchProducts() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ProductResponse> call = apiService.getProducts();

            try {
                Response<ProductResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> fetchedProducts = response.body().getProducts();
                    if (isAdded()) { // Pastikan Fragment masih terpasang
                        handler.post(() -> {
                            updateProductList(fetchedProducts);
                            CacheManager.saveProducts(requireContext(), fetchedProducts);
                            btnRefresh.setVisibility(View.GONE);
                        });
                    }
                } else {
                    if (isAdded()) handler.post(this::handleFetchError);
                }
            } catch (Exception e) {
                if (isAdded()) handler.post(this::handleFetchError);
            }
        });
    }

    private void loadProductsFromCache() {
        List<Product> cachedProducts = CacheManager.loadProducts(requireContext());
        if (cachedProducts != null && !cachedProducts.isEmpty()) {
            updateProductList(cachedProducts);
            Toast.makeText(requireContext(), "Menampilkan data offline.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFetchError() {
        if (productList.isEmpty()) {
            Toast.makeText(requireContext(), "Gagal memuat data. Periksa koneksi internet Anda.", Toast.LENGTH_LONG).show();
            btnRefresh.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(requireContext(), "Gagal mengambil data terbaru. Menampilkan data offline.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateProductList(List<Product> newProducts) {
        productList.clear();
        productList.addAll(newProducts);
        productAdapter.notifyDataSetChanged();
    }
}