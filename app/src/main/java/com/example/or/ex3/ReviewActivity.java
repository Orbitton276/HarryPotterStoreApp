package com.example.or.ex3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.or.ex3.Analytics.AnalyticsManager;
import com.example.or.ex3.Model.Product;
import com.example.or.ex3.Model.Review;
import com.example.or.ex3.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;


public class ReviewActivity extends AppCompatActivity {
    private final String TAG = "ReviewActivity";
    private Product myproduct;
    private String key;
    private User user;
    private String userID;
    private int prevRating = -1;
    private TextView userReview;
    private RatingBar userRating;
    private DatabaseReference reviewRef;
    private DatabaseReference productRef;

    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate() >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        initReviewActivity();

        Log.e(TAG, "onCreate() <<");

    }

    public void onSubmitClick(View v) {

        Log.e(TAG, "onSubmitClick() >>");

        reviewRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData)
            {
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {

                Log.e(TAG, "onComplete() >>");
                if (databaseError != null) {
                    Log.e(TAG, "onComplete() << Error:" + databaseError.getMessage());
                    return;
                }

                if (committed) {
                    Review review = new Review(
                            userReview.getText().toString(),
                            (int) userRating.getRating(),
                            user.getEmail(), FirebaseAuth.getInstance().getCurrentUser().getUid());
                    reviewRef.setValue(review);
                }
                myproduct.incrementReviewCount();
                float newRatingSum = userRating.getRating() + myproduct.getRating();
                productRef.child("reviewsCount").setValue(myproduct.getReviewsCount());
                productRef.child("ratingSum").setValue(newRatingSum);

                int rating=0;
                if (myproduct.getReviewsCount() != 0) {
                    rating  = (int)(newRatingSum / myproduct.getReviewsCount());
                }

                productRef.child("rating").setValue(rating);

                analyticsManager.trackProductRatingEvent(myproduct,(int)userRating.getRating());

                Intent intent = new Intent(getApplicationContext(), productDetails.class);

                intent.putExtra("product", myproduct);
                intent.putExtra("key", myproduct.getM_productID());
                intent.putExtra("user", user);
                startActivity(intent);
                finish();

                Log.e(TAG, "onComplete() <<");
            }
        });


        Log.e(TAG, "onSubmitClick() <<");
    }

    private void initReviewActivity(){

        myproduct = getIntent().getParcelableExtra("product");
        key = myproduct.getM_productID();
        user = getIntent().getParcelableExtra("user");

        userReview = findViewById(R.id.tvUser_review);
        userRating = findViewById(R.id.tvUser_rating);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        productRef = FirebaseDatabase.getInstance().getReference("Products/" + key);
        reviewRef = FirebaseDatabase.getInstance().getReference("Products/" + key).child("Reviews").child(userID);



        reviewRef.
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Review review = snapshot.getValue(Review.class);
                        if (review != null) {
                            Log.e(TAG, "in existing review ");
                            userReview.setText(review.getUserReview());
                            userRating.setRating(review.getUserRating());
                            prevRating = review.getUserRating();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        Log.e(TAG, "onCancelled(com.example.or.ex3.Model.Review) >>" + databaseError.getMessage());
                    }
                });
    }
}
