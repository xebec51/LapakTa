package com.example.lapakta.ui.fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lapakta.R;
import com.yourapp.lapakta.adapter.ProductAdapter;
import com.example.lapakta.data.model.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvProducts;
    private Button btnRefresh;
    private ProductAdapter adapter;
    private List<Product> productList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvProducts = view.findViewById(R.id.rvProducts);
        btnRefresh = view.findViewById(R.id.btnRefresh);

        // Setup RecyclerView
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList, product -> {
            // TODO: Intent ke DetailProdukActivity
        });
        rvProducts.setAdapter(adapter);

        loadDummyData();

        btnRefresh.setOnClickListener(v -> {
            loadDummyData();
        });

        return view;
    }

    private void loadDummyData() {
        productList.clear();
        productList.add(new Product(1, "Sepatu Sneakers", "Sepatu nyaman dan stylish", 350000, "https://dummyimage.com/100x100/000/fff&text=Sneakers"));
        productList.add(new Product(2, "Tas Ransel", "Tas ransel untuk aktivitas harian", 450000, "https://dummyimage.com/100x100/000/fff&text=Backpack"));
        productList.add(new Product(3, "Jam Tangan", "Jam tangan modern", 750000, "https://dummyimage.com/100x100/000/fff&text=Watch"));
        adapter.notifyDataSetChanged();
    }
}
