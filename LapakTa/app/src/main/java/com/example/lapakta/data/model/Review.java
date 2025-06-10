package com.example.lapakta.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    private int rating;
    private String comment;
    private String date;
    private String reviewerName;
    private String reviewerEmail;

    // Getters
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getDate() { return date; }
    public String getReviewerName() { return reviewerName; }

    // --- Kode Parcelable ---
    protected Review(Parcel in) {
        rating = in.readInt();
        comment = in.readString();
        date = in.readString();
        reviewerName = in.readString();
        reviewerEmail = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(rating);
        dest.writeString(comment);
        dest.writeString(date);
        dest.writeString(reviewerName);
        dest.writeString(reviewerEmail);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
