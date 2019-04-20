package com.example.or.ex3;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.or.ex3.Adapter.ReviewsAdapter;
import com.example.or.ex3.Analytics.AnalyticsManager;
import com.example.or.ex3.Model.Product;
import com.example.or.ex3.Model.Review;
import com.example.or.ex3.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class productDetails extends AppCompatActivity {

    private static final String TAG = "onProductDetails";
    private TextView tvProductName, tvProductID, tvProductDescription, tvProductPrice, tvProductCategory;
    private ImageView productPic;
    private DatabaseReference myUserRef;
    private Button writeReviewButton;
    private FirebaseUser fbUser;
    private User user;
    private Product myProduct;
    private Button buyButton;
    private RecyclerView recyclerViewProductReviews;
    private DatabaseReference productReviewsRef;
    private boolean productWasPurchased=false;
    private FirebaseAuth mAuth;
    private List<Review> reviewsList =  new ArrayList<>();

    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details);
        initProductDetailsActivity();
        initReviewsRecycler();

        productReviewsRef = FirebaseDatabase.getInstance().getReference("Products/" + myProduct.getM_productID() +"/Reviews" );
        productReviewsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange() >> Products/");

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    reviewsList.add(review);
                }
                recyclerViewProductReviews.getAdapter().notifyDataSetChanged();
                Log.e(TAG, "onDataChange(Review) <<");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Review) >>" + databaseError.getMessage());
            }
        });

    }
    public void onBuyProductClicked(View view){

        if (fbUser.isAnonymous()){
            Log.e(TAG,"onBuyAnonymous");
            Toast.makeText(getApplicationContext(), "Must be signed user", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            user.setAnonymous(true);
            Intent intent = new Intent(getApplicationContext(),signIn_activity.class);
            intent.putExtra("product", myProduct);
            intent.putExtra("user",user);
            startActivity(intent);
            finish();
        }
        else{
            YoYo.with(Techniques.BounceInUp).duration(4000).playOn(buyButton);
            MediaPlayer mySong = MediaPlayer.create(productDetails.this,R.raw.magic_sound);
            mySong.start();
            productWasPurchased = true;
            myUserRef = FirebaseDatabase.getInstance().getReference("Users/");
            if (fbUser != null && !fbUser.isAnonymous())
            {
                String currentProductID = tvProductID.getText().toString();
                user.addProductToList(currentProductID);
                String productPrice = tvProductPrice.getText().toString();

                user.updateTotalPurchase(Float.parseFloat(productPrice));
                Log.e(TAG,"onBuyAnonymous");
                myUserRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                buttonBuyToPurchased();

                analyticsManager.trackPurchaseEvent(myProduct,fbUser.getUid());

                analyticsManager.setUserProperty("total_purchase",Float.toString(user.getTotalPurchase()));
                analyticsManager.setUserProperty("my_product_count",Integer.toString(user.getMyProducts().size()));
                analyticsManager.setUserProperty("last_product_category",myProduct.getM_productCategory());


            }

        }



    }

    private void buttonBuyToPurchased(){
        buyButton.setText("Purchased");
        buyButton.setClickable(false);
        buyButton.setBackgroundColor(Color.GRAY);

    }
    private void initProductDetailsActivity(){
        mAuth = FirebaseAuth.getInstance();
        myProduct = getIntent().getParcelableExtra("product");
        user = getIntent().getParcelableExtra("user");
        fbUser = FirebaseAuth.getInstance().getCurrentUser();


        writeReviewButton = (Button)findViewById(R.id.reviewButton);
        buyButton = (Button)findViewById(R.id.buyButton);
        tvProductName =(TextView)findViewById(R.id.tvProductName);
        tvProductDescription =(TextView)findViewById(R.id.tvProductDescription);
        tvProductID =(TextView)findViewById(R.id.tvProductID);
        tvProductPrice =(TextView)findViewById(R.id.tvProductPrice);
        tvProductCategory =(TextView)findViewById(R.id.tvProductCategory);
        productPic = (ImageView)findViewById(R.id.imgProductPic);

        tvProductName.setText(myProduct.getM_productName());
        tvProductDescription.setText(myProduct.getM_productDescription());
        tvProductID.setText(myProduct.getM_productID());
        tvProductPrice.setText(Float.toString(myProduct.getM_productPrice()));
        tvProductCategory.setText(myProduct.getM_productCategory());
        Picasso.get().load(myProduct.getM_profilePic()).into(productPic);
        writeReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fbUser.isAnonymous()){
                    Toast.makeText(getApplicationContext(), "Must be signed user", Toast.LENGTH_SHORT).show();
                }
                else if(!productWasPurchased){
                    Toast.makeText(getApplicationContext(), "Review is allowed to users who purchased this product", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),ReviewActivity.class);
                    intent.putExtra("product", myProduct);
                    intent.putExtra("user",user);
                    startActivity(intent);
                    finish();
                }



            }
        });
        if (user!=null){
            for (String userProduct : user.getMyProducts()) {
                if (userProduct.equals(tvProductID.getText()))
                {
                    productWasPurchased = true;
                    buttonBuyToPurchased();
                    break;
                }
            }
        }
    }
    private void initReviewsRecycler(){
        recyclerViewProductReviews = findViewById(R.id.recyclerReviews);
        recyclerViewProductReviews.setHasFixedSize(true);
        recyclerViewProductReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewProductReviews.setItemAnimator(new DefaultItemAnimator());
        ReviewsAdapter reviewsAdapter = new ReviewsAdapter(reviewsList);
        recyclerViewProductReviews.setAdapter(reviewsAdapter);
    }
}
