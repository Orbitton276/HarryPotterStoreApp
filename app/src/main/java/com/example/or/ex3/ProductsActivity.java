package com.example.or.ex3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.or.ex3.Adapter.ProductAdapter;
import com.example.or.ex3.Analytics.AnalyticsManager;
import com.example.or.ex3.Model.Product;
import com.example.or.ex3.Model.User;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ProductsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private DatabaseReference allProductsRef;
    private DatabaseReference myUserRef;
    private List<Product> productList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private User myUser;
    private Button searchButton;
    private static final String TAG = "onHomeActivity";
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseUser fbUser;
    private String displayName, email;
    private String spinnerFilterSelected = "None";
    private String spinnerSortBySelected="Price";
    private TextView tvMaxPrice;
    private TextView tvSearchTerm;
    private Spinner filterByCategorySpinner;

    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.e(TAG, "onCreate() >>");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.products_activity);
        initProductsActivity();
        initSortBySpinner();
        initFilterBySpinner();

        String token = FirebaseInstanceId.getInstance().getToken();

        String msg = "Current push token is:\n" + token;
        Log.e(TAG,msg);
        allProductsRef = FirebaseDatabase.getInstance().getReference("Products");
        notificationCampaign();

    }


    private void notificationCampaign()
    {
        Intent intent = getIntent();
        if (intent.hasExtra("name")||intent.hasExtra("category")){

            if (intent.hasExtra("name")){
                Log.e(TAG,"product name is available"+intent.getStringExtra("name"));
                AnalyticsManager.getInstance().init(getApplicationContext());
                addSearchName(intent.getStringExtra("name"));

            }
            if (intent.hasExtra("category")){
                Log.e(TAG,"category name is available"+intent.getStringExtra("category"));
                AnalyticsManager.getInstance().init(getApplicationContext());
                String category = intent.getStringExtra("category");
                filterByCategorySpinner.setSelection(((ArrayAdapter<String>)filterByCategorySpinner.getAdapter()).getPosition(category));
            }
            if (intent.hasExtra("maxprice")){
                AnalyticsManager.getInstance().init(getApplicationContext());
                setMaxPrice(intent.getStringExtra("maxprice"));
            }
        }

    }

    private void setMaxPrice(String maxPrice){
        tvMaxPrice=findViewById(R.id.tvFilterMaxPrice);
        tvMaxPrice.setText(maxPrice);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);

        MenuItem item = menu.findItem(R.id.itemEmail);
        item.setTitle(email);
        item = menu.findItem(R.id.itemName);
        item.setTitle(displayName);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemSignOut: {
                onSingOutClick(null);
            }
        }
        return super.onOptionsItemSelected(item);
    }



    private void addSearchName(String productName){
        Log.e(TAG,"on product Campaign"+productName);
        searchButton = findViewById(R.id.btnSearch);
        tvSearchTerm = findViewById(R.id.tvSearchField);
        tvSearchTerm.setText(productName);
        Log.e(TAG,"TEXT IS "+tvSearchTerm.getText().toString());
    }
    private void getAllProducts() {

        productList.clear();
        if (myUser == null && !fbUser.isAnonymous()) {
            myUser = new User(fbUser.getEmail(), 0, new ArrayList<String>());
            myUser.setUserID(fbUser.getUid());
            myUserRef.setValue(myUser);
        }
        productAdapter = new ProductAdapter(myUser, productList);
        recyclerView.setAdapter(productAdapter);
        getAllProductsUsingValueListenrs();

    }




    private void getAllProductsUsingValueListenrs() {

        allProductsRef = FirebaseDatabase.getInstance().getReference("Products");

        allProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange(Product) >> " + snapshot.getKey());

                updateProductsList(snapshot);

                Log.e(TAG, "onDataChange(Product) <<");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled(Product) >>" + databaseError.getMessage());
            }
        });
    }


    private void updateProductsList(DataSnapshot snapshot) {

        float maxPriceFloat = -1;
        String maxPriceString = this.tvMaxPrice.getText().toString();
        if (!maxPriceString.isEmpty())
        {
            maxPriceFloat = Float.parseFloat(maxPriceString);
        }


        productList.clear();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            Product product = dataSnapshot.getValue(Product.class);
            if (product.getM_productCategory().equals((spinnerFilterSelected)) || spinnerFilterSelected.equals(("None"))) {
                //nothing entered ==> add product
                if (maxPriceFloat == -1 || product.getM_productPrice() < maxPriceFloat) {
                    productList.add(product);
                }
            }
        }
        if (spinnerSortBySelected.equals("Rating")){
                //reverse list to display higher rating first

            Collections.reverse(productList);
        }

        recyclerView.getAdapter().notifyDataSetChanged();




    }
//============================================================================================


    public void onSingOutClick(View v) {

        mAuth.signOut();
        LoginManager.getInstance().logOut();
        displayMessage("Signing out");
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);

        startActivity(new Intent(ProductsActivity.this, signIn_activity.class));
        finish();
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    private void getUserDetails() {
        FirebaseUser user = mAuth.getCurrentUser();
        Intent intent = getIntent();
        displayName = user.getDisplayName();
        if (intent.hasExtra("Email")) {
            displayName = intent.getStringExtra("displayName");
            email = intent.getStringExtra("Email");
        } else {
            if (user.getEmail() == null) {
                displayName = "N.A";
            } else {
                email = user.getEmail();
            }
        }
        displayMessage("Welcome " + displayName);
    }


    public void displayMessage(String message) {
        if (message != null) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }


    private void performSearchTask(){
        Query searchProduct;
        String searchString = ((EditText) findViewById(R.id.tvSearchField)).getText().toString();

        Log.e(TAG,"searchString >> "+ searchString);
        productList.clear();

        if ((searchString != null && !searchString.isEmpty()))
        {
            searchProduct = allProductsRef.orderByChild("m_productName").startAt(searchString).endAt(searchString + "\uf8ff");
            analyticsManager.trackSearchWordAndCategoryFilterEvent(searchString,spinnerFilterSelected);
            analyticsManager.setUserProperty("last_search_word",searchString);
            //flurry tracker

        } else {
            //searchString is empty means ORDER BY AND CATEGORIZED
            if (spinnerSortBySelected.equals("Price")) {
                searchProduct = allProductsRef.orderByChild("m_productPrice");
            } else {
                //spinnerSortBySelected = Reviews count
                Log.e(TAG, "onDataChange(Query) >>  in reviews count sorting" );
                searchProduct = allProductsRef.orderByChild("rating");
            }
            analyticsManager.trackSortByAndCategoryFilterEvent(spinnerSortBySelected,spinnerFilterSelected);
        }

        String maxPrice = tvMaxPrice.getText().toString();
        if(!maxPrice.isEmpty())
        {
            analyticsManager.trackMaxPriceEvent(maxPrice);
            analyticsManager.setUserProperty("last_max_price",maxPrice);
        }
        searchProduct.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                Log.e(TAG, "onDataChange(Query) >> " + snapshot.getKey());

                updateProductsList(snapshot);

                Log.e(TAG, "onDataChange(Query) <<");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, "onCancelled() >>" + databaseError.getMessage());
            }

        });
        Log.e(TAG, "onSearchButtonClick() <<");
    }
    public void onSearchButtonClick(View v) {

        performSearchTask();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        switch (parent.getId()) {
            case R.id.filterBySpinner: {
                spinnerFilterSelected = parent.getItemAtPosition(position).toString();
                Log.e(TAG, "in filter spinner>>" + parent.getId());
                break;
            }
            case R.id.sortBySpinner: {
                spinnerSortBySelected = parent.getItemAtPosition(position).toString();
                Log.e(TAG, "in sort spinner>>" + spinnerFilterSelected);
                break;
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void initProductsActivity(){

        tvMaxPrice = (TextView) findViewById(R.id.tvFilterMaxPrice);
        recyclerView = (RecyclerView) findViewById(R.id.productsRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        getUserDetails();
        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.e(TAG, "fbUser.isAnonymous() >>" + fbUser.isAnonymous());



        if (!fbUser.isAnonymous()) {
            myUserRef = FirebaseDatabase.getInstance().getReference("Users/" + fbUser.getUid());
            myUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    Log.e(TAG, "onDataChange(User) >> " + snapshot.getKey());

                    myUser = snapshot.getValue(User.class);

                    getAllProducts();

                    Log.e(TAG, "onDataChange(User) <<");

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.e(TAG, "onCancelled(Users) >>" + databaseError.getMessage());
                }
            });

            Log.e(TAG, "onCreate() <<");
        } else {
            myUser = new User("N/A", 0, new ArrayList<String>());
            myUser.setUserID(fbUser.getUid());
            getAllProducts();
        }


    }

    private void initSortBySpinner(){
        Spinner sortBySpinner = findViewById(R.id.sortBySpinner);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this, R.array.SortBy, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(adapter2);
        sortBySpinner.setOnItemSelectedListener(this);
    }

    private void initFilterBySpinner(){
        filterByCategorySpinner = findViewById(R.id.filterBySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterByCategorySpinner.setAdapter(adapter);
        filterByCategorySpinner.setOnItemSelectedListener(this);
    }
}
//============================================================================================




