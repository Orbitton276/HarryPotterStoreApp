package com.example.or.ex3.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.or.ex3.Model.Product;
import com.example.or.ex3.R;
import com.example.or.ex3.Model.Review;
import com.example.or.ex3.Model.User;
import com.example.or.ex3.productDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.myViewHolder> {

    private static final String TAG = "OnMyAdapter";
    private static final int priceColor = 0xf2741010;
    private static final int purchasedColor = 0xF7696869;
    public View view;
    private User user;
    private List<Product> productsList;
    private List<Review> reviewsList = new ArrayList<Review>();

    public ProductAdapter(User user, List<Product> productList) {
        this.user = user;
        productsList = productList;
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new myViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_product, parent, false));
    }

    @Override
    public void onBindViewHolder(myViewHolder holder, int position) {

        List<String> UserProducts;
        Log.e(TAG, "onBindViewHolder>>");

        Product selectedProduct = productsList.get(position);

        holder.pID.setText(productsList.get(position).getM_productID());
        holder.pName.setText(productsList.get(position).getM_productName());
        holder.pPrice.setText(Float.toString(productsList.get(position).getM_productPrice()));
        float rating = productsList.get(position).getRating();


        Log.e(TAG, "onBindViewHolder>> rating");

        holder.rating.setNumStars((int) rating);
        if (user != null) {
            UserProducts = user.getMyProducts();
            for (String userProduct : UserProducts) {
                if (userProduct.equals(holder.pID.getText())) {
                    holder.pPrice.setTextColor(purchasedColor);
                    break;
                } else {
                    holder.pPrice.setTextColor(priceColor);
                }
            }
        }


        holder.getRating().setRating((float) (selectedProduct.getRating()));


        holder.pCategory.setText((productsList.get(position).getM_productCategory()));
        holder.setProduct(selectedProduct);
        Picasso.get().load(productsList.get(position).getM_profilePic()).into(holder.productPic);

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    class myViewHolder extends RecyclerView.ViewHolder {


        private TextView pName, pID, pPrice, pDescription, pCategory, reviewsCount;
        private ImageView productPic;
        private Product selectedProduct;
        private Context context;
        private CardView myCardView;


        public RatingBar getRating() {
            return rating;
        }

        private RatingBar rating;

        public myViewHolder(View itemView) {
            super(itemView);
            pName = (TextView) itemView.findViewById(R.id.tvProductName);
            pID = (TextView) itemView.findViewById(R.id.tvProductID);
            pPrice = (TextView) itemView.findViewById(R.id.tvProductPrice);
            productPic = (ImageView) itemView.findViewById(R.id.imgProductPic);
            pCategory = (TextView) itemView.findViewById(R.id.tvProductCategory);
            myCardView = (CardView) itemView.findViewById(R.id.card_view_product);

            reviewsCount = (TextView) itemView.findViewById(R.id.product_review_count);
            rating = (RatingBar) itemView.findViewById(R.id.product_ratingBar);
            this.context = context;

            myCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.e(TAG, "onclickCardView>>");
                    Context context = view.getContext();
                    Intent intent = new Intent(context, productDetails.class);
                    intent.putExtra("product", selectedProduct);
                    intent.putExtra("user", user);

                    context.startActivity(intent);
                }
            });
            Log.e(TAG, "myViewHolder>>");

        }



        public void setProduct(Product product) {
            this.selectedProduct = product;
        }

    }

}
