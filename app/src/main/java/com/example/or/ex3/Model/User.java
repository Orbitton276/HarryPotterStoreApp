package com.example.or.ex3.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class User implements Parcelable {

    private static final String TAG = "onUser>>";
    private String email;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    private String userID=null;
    private float totalPurchase;
    private List<String> myProducts = new ArrayList<>();
    private boolean isAnonymous = false;


    public void setAnonymous(boolean anonymous) {
        isAnonymous = anonymous;
    }


    public User() {
    }

    public User(String email, float totalPurchase, List<String> myProducts) {
        this.email = email;
        this.totalPurchase = totalPurchase;
        this.myProducts = myProducts;

    }

    protected User(Parcel in) {
        email = in.readString();
        totalPurchase = in.readFloat();
        myProducts = in.createStringArrayList();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public float getTotalPurchase() {
        return totalPurchase;
    }

    public void updateTotalPurchase(float newPurchasePrice) {
        this.totalPurchase = Float.sum(this.totalPurchase, newPurchasePrice);
    }

    public List<String> getMyProducts() {
        return myProducts;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeFloat(totalPurchase);
        dest.writeStringList(myProducts);
    }

    public void addProductToList(String i_productID) {
        myProducts.add(i_productID);
    }

}