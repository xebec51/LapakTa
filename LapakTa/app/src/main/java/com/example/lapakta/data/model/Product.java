package com.example.lapakta.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

public class Product implements Parcelable {
    // Variabel yang sudah ada
    private int id;
    private String title;
    private String description;
    private double price;
    private String thumbnail;
    private String category;
    private String brand;
    private double rating;

    // --- Variabel Baru dari API ---
    private double discountPercentage;
    private int stock;
    private String availabilityStatus;
    private String warrantyInformation;
    private String shippingInformation;
    private String returnPolicy;
    private List<String> images;
    private List<String> tags;
    private List<Review> reviews;

    // --- GETTER YANG HILANG (SEKARANG DITAMBAHKAN KEMBALI) ---
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getThumbnail() { return thumbnail; }
    public String getCategory() { return category; }
    public String getBrand() { return brand; }
    public double getRating() { return rating; }

    // --- Getters untuk data baru (sudah ada) ---
    public double getDiscountPercentage() { return discountPercentage; }
    public int getStock() { return stock; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public String getWarrantyInformation() { return warrantyInformation; }
    public String getShippingInformation() { return shippingInformation; }
    public String getReturnPolicy() { return returnPolicy; }
    public List<String> getImages() { return images; }
    public List<String> getTags() { return tags; }
    public List<Review> getReviews() { return reviews; }

    // --- Kode Parcelable yang Diperbarui ---

    protected Product(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        price = in.readDouble();
        thumbnail = in.readString();
        category = in.readString();
        brand = in.readString();
        rating = in.readDouble();
        // Baca data baru
        discountPercentage = in.readDouble();
        stock = in.readInt();
        availabilityStatus = in.readString();
        warrantyInformation = in.readString();
        shippingInformation = in.readString();
        returnPolicy = in.readString();
        images = in.createStringArrayList();
        tags = in.createStringArrayList();
        reviews = in.createTypedArrayList(Review.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeString(thumbnail);
        dest.writeString(category);
        dest.writeString(brand);
        dest.writeDouble(rating);
        // Tulis data baru
        dest.writeDouble(discountPercentage);
        dest.writeInt(stock);
        dest.writeString(availabilityStatus);
        dest.writeString(warrantyInformation);
        dest.writeString(shippingInformation);
        dest.writeString(returnPolicy);
        dest.writeStringList(images);
        dest.writeStringList(tags);
        dest.writeTypedList(reviews);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
