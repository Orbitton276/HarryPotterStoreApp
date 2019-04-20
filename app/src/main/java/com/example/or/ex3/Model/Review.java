package com.example.or.ex3.Model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Review {

    private String userReview;
    private int userRating;
    private String userEmail;
   // private String userID;

    /*public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }*/

    public Review(String userReview, int userRating, String userEmail, String userID) {
        this.userReview = userReview;
        this.userRating = userRating;
        this.userEmail = userEmail;
        //this.userID = userID;
    }

    public Review() {
    }

    public String getUserReview() {
        return userReview;
    }

    public int getUserRating() {
        return userRating;
    }

    public String getUserEmail() {
        return userEmail;
    }


    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("userReview", userReview);
        result.put("userRating", userRating);
        result.put("userEmail", userEmail);
        return result;
    }
}
