package com.example.lapakta.data.network;

import com.example.lapakta.data.model.ProductResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("products")
    Call<ProductResponse> getProducts();
}