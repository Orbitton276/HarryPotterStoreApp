package com.example.or.ex3.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Product implements Parcelable {

    private String m_productID;
    private String m_profilePic;
    private String m_productName;
    private String m_productDescription;
    private float m_productPrice;
    private String m_productCategory;
    private int ratingSum;
    private int reviewsCount;
    private int rating;
    //private List<Review> myReviews;

    public int getRating() {
        return rating;
    }


    public int getReviewsCount() {
        return reviewsCount;
    }



    public Product() {
    }


    public Product(String m_productName, String m_productDescription, float m_productPrice, String m_productID, String m_profilePic, String m_productCategory, int ratingSum, int reviewsCount, List<Review> reviews, int rating) {
        this.m_productName = m_productName;
        this.m_productDescription = m_productDescription;
        this.m_productPrice = m_productPrice;
        this.m_productID = m_productID;
        this.m_profilePic = m_profilePic;
        this.m_productCategory = m_productCategory;
        this.ratingSum = ratingSum;
        this.reviewsCount = reviewsCount;
        //this.myReviews = reviews;
        this.rating = rating;
    }

    protected Product(Parcel in) {
        m_productID = in.readString();
        m_profilePic = in.readString();
        m_productName = in.readString();
        m_productDescription = in.readString();
        m_productPrice = in.readFloat();
        m_productCategory = in.readString();
        ratingSum = in.readInt();
        reviewsCount = in.readInt();
        rating = in.readInt();
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

    public String getM_productName() {
        return m_productName;
    }


    public String getM_productDescription() {
        return m_productDescription;
    }


    public float getM_productPrice() {
        return m_productPrice;
    }


    public String getM_productID() {
        return m_productID;
    }


    public String getM_profilePic() {
        return m_profilePic;
    }


    public String getM_productCategory() {
        return m_productCategory;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(m_productID);
        dest.writeString(m_profilePic);
        dest.writeString(m_productName);
        dest.writeString(m_productDescription);
        dest.writeFloat(m_productPrice);
        dest.writeString(m_productCategory);
        dest.writeInt(ratingSum);
        dest.writeInt(reviewsCount);
        dest.writeInt(rating);
    }

    public void incrementReviewCount() {
        reviewsCount++;
    }

}
