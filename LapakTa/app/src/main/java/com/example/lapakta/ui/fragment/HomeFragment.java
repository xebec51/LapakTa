package com.example.lapakta.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private ShimmerFrameLayout shimmerContainer;
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private Button btnRefresh;
    private TextView tvNoData;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        shimmerContainer = view.findViewById(R.id.shimmer_view_container);
        rvProducts = view.findViewById(R.id.rvProducts);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        tvNoData = view.findViewById(R.id.tvNoData);

        setupRecyclerView();

        btnRefresh.setOnClickListener(v -> {
            btnRefresh.setVisibility(View.GONE);
            fetchProducts();
        });

        loadProductsFromCache();
        fetchProducts();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    performSearch(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(@NonNull MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(@NonNull MenuItem item) {
                fetchProducts();
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(productList, product -> {
            Intent intent = new Intent(requireActivity(), ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT, product);
            startActivity(intent);
        });
        rvProducts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvProducts.setAdapter(productAdapter);
    }

    private void startLoading() {
        shimmerContainer.setVisibility(View.VISIBLE);
        shimmerContainer.startShimmer();
        rvProducts.setVisibility(View.GONE);
        btnRefresh.setVisibility(View.GONE);
        tvNoData.setVisibility(View.GONE);
    }

    private void stopLoading() {
        shimmerContainer.stopShimmer();
        shimmerContainer.setVisibility(View.GONE);
        rvProducts.setVisibility(View.VISIBLE);
    }

    private void performSearch(String query) {
        startLoading();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ProductResponse> call = apiService.searchProducts(query);
            try {
                Response<ProductResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> searchResults = response.body().getProducts();
                    handler.post(() -> {
                        stopLoading();
                        updateProductList(searchResults);
                        if (searchResults.isEmpty()) {
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    handler.post(() -> {
                        stopLoading();
                        handleFetchError();
                    });
                }
            } catch (Exception e) {
                handler.post(() -> {
                    stopLoading();
                    handleFetchError();
                });
            }
        });
    }

    private void fetchProducts() {
        if (productList.isEmpty()) {
            startLoading();
        }
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            ApiService apiService = ApiClient.getClient().create(ApiService.class);
            Call<ProductResponse> call = apiService.getProducts();

            try {
                Response<ProductResponse> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    List<Product> fetchedProducts = response.body().getProducts();
                    if (isAdded()) {
                        handler.post(() -> {
                            stopLoading();
                            updateProductList(fetchedProducts);
                            CacheManager.saveProducts(requireContext(), fetchedProducts);
                            btnRefresh.setVisibility(View.GONE);
                        });
                    }
                } else {
                    if (isAdded()) handler.post(() -> {
                        stopLoading();
                        handleFetchError();
                    });
                }
            } catch (Exception e) {
                if (isAdded()) handler.post(() -> {
                    stopLoading();
                    handleFetchError();
                });
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
        stopLoading();
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