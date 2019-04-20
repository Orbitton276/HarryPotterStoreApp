package com.example.or.ex3.Analytics;

        import android.content.Context;
        import android.os.Bundle;
        import android.util.Log;

        import com.example.or.ex3.Model.Product;
        import com.flurry.android.FlurryAgent;
        import com.google.firebase.analytics.FirebaseAnalytics;
        import com.mixpanel.android.mpmetrics.MixpanelAPI;

        import java.util.HashMap;
        import java.util.Map;

public class AnalyticsManager {
    private static String TAG = "AnalyticsManager";
    private static AnalyticsManager mInstance = null;
    private FirebaseAnalytics mFirebaseAnalytics;
    private MixpanelAPI mMixpanel;

    private AnalyticsManager() { }

    public static AnalyticsManager getInstance() {

        if (mInstance == null) {
            mInstance = new AnalyticsManager();
        }
        return (mInstance);
    }

    public void init(Context context) {

        new FlurryAgent.Builder().withLogEnabled(true).build(context, "XSKCMVZDQ7WPV9FCZ79V");

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);


        mMixpanel = MixpanelAPI.getInstance(context, "568503bd098c76c3efc0cc42c9ce0f3e");

    }

    public void trackSearchWordAndCategoryFilterEvent(String searchString, String CategoryFilter) {

        String eventName = "Search_Word_And_Categsory_Filter";

        //Firebase
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.SEARCH_TERM, searchString);
        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,CategoryFilter);
        Log.e("trackSearchWordAndCategoryFilterEvent", "params >>"+params.toString());
        Log.e("trackSearchWordAndCategoryFilterEvent", "mFirebaseAnalytics >>"+mFirebaseAnalytics.toString());

        mFirebaseAnalytics.logEvent(eventName,params);


        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        eventParams.put(FirebaseAnalytics.Param.SEARCH_TERM, searchString);
        eventParams.put(FirebaseAnalytics.Param.ITEM_CATEGORY,CategoryFilter);
        FlurryAgent.logEvent(eventName, eventParams);

        //Mixpanel
        Map<String, Object> eventParams2 = new HashMap<String, Object>();
        eventParams2.put(FirebaseAnalytics.Param.SEARCH_TERM, searchString);
        eventParams2.put(FirebaseAnalytics.Param.ITEM_CATEGORY,CategoryFilter);


        mMixpanel.trackMap(eventName,eventParams2);

    }

    public void trackMaxPriceEvent(String maxPrice) {

        String eventName = "Max_Price_Event";

        //Firebase
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.PRICE, maxPrice);
        mFirebaseAnalytics.logEvent(eventName,params);


        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        eventParams.put(FirebaseAnalytics.Param.PRICE, maxPrice);
        FlurryAgent.logEvent(eventName, eventParams);


        //Mixpanel
        int maxPriceInt = Integer.parseInt(maxPrice);
        Map<String, Object> eventParams2 = new HashMap<String, Object>();
        eventParams2.put(FirebaseAnalytics.Param.PRICE, maxPriceInt);


        mMixpanel.trackMap(eventName,eventParams2);

    }

    public void trackSortByAndCategoryFilterEvent(String sortBy, String CategoryFilter) {

        String eventName = "Sort_By_And_Category_Filter";

        //Firebase
        Bundle params = new Bundle();
        Log.e("trackSortByAndCategoryFilterEvent", "params >>"+params.toString());
        params.putString("Sort_by", sortBy);
        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY,CategoryFilter);
        Log.e("trackSortByAndCategoryFilterEvent", "params >>"+params.toString());
        mFirebaseAnalytics.logEvent(eventName,params);


        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        eventParams.put("Sort_by", sortBy);
        eventParams.put(FirebaseAnalytics.Param.ITEM_CATEGORY, CategoryFilter);
        FlurryAgent.logEvent(eventName, eventParams);


        //Mipanel
        Map<String, Object> eventParams2 = new HashMap<String, Object>();
        eventParams2.put("Sort_by", sortBy);
        eventParams2.put(FirebaseAnalytics.Param.ITEM_CATEGORY, CategoryFilter);


        mMixpanel.trackMap(eventName,eventParams2);

    }

    public void trackSignUpEvent() {

        String eventName = "Sign_Up";
        Bundle params = new Bundle();
        mFirebaseAnalytics.logEvent(eventName,params);

        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        FlurryAgent.logEvent(eventName,eventParams);


        //Mixpanel
        Map<String, Object> eventParams2 = new HashMap<String, Object>();

        mMixpanel.trackMap(eventName,eventParams2);

    }

    public void trackSignInEvent(String loginMethod) {

        String eventName = "Sign_In";
        Bundle params = new Bundle();
        params.putString("Sign_In",loginMethod);
        mFirebaseAnalytics.logEvent(eventName,params);


        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        eventParams.put("Sign_In", loginMethod);
        FlurryAgent.logEvent(eventName,eventParams);


        //Mixpanel
        Map<String, Object> eventParams2 = new HashMap<String, Object>();
        eventParams2.put("Sign_In", loginMethod);


        mMixpanel.trackMap(eventName,eventParams2);

    }

    public void trackPurchaseEvent(Product product, String userID) {


        String eventName = "Purchase";

        //Firebase
        Bundle params = new Bundle();
        params.putDouble(FirebaseAnalytics.Param.PRICE,product.getM_productPrice());
        params.putString(FirebaseAnalytics.Param.ITEM_ID,product.getM_productID());
        params.putString("User_ID",userID);
        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, product.getM_productCategory());
        params.putString(FirebaseAnalytics.Param.ITEM_NAME,product.getM_productName());
        mFirebaseAnalytics.logEvent(eventName,params);


        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        eventParams.put(FirebaseAnalytics.Param.PRICE, String.valueOf(product.getM_productPrice()));
        eventParams.put(FirebaseAnalytics.Param.ITEM_ID,product.getM_productID());
        eventParams.put("User_ID",userID);
        eventParams.put(FirebaseAnalytics.Param.ITEM_CATEGORY, product.getM_productCategory());
        eventParams.put(FirebaseAnalytics.Param.ITEM_NAME,product.getM_productName());
        FlurryAgent.logEvent(eventName,eventParams);


        //Mixpanel
        Map<String, Object> eventParams2 = new HashMap<String, Object>();
        eventParams2.put(FirebaseAnalytics.Param.PRICE, product.getM_productPrice());
        eventParams2.put(FirebaseAnalytics.Param.ITEM_ID,product.getM_productID());
        eventParams2.put("User_ID",userID);
        eventParams2.put(FirebaseAnalytics.Param.ITEM_CATEGORY, product.getM_productCategory());
        eventParams2.put(FirebaseAnalytics.Param.ITEM_NAME,product.getM_productName());




        mMixpanel.trackMap(eventName,eventParams2);



    }

    public void trackProductRatingEvent(Product product , int userRating) {

        String eventName = "product_rating";

        //Firebase
        Bundle params = new Bundle();
        params.putString("product_description", product.getM_productDescription());
        params.putString(FirebaseAnalytics.Param.ITEM_NAME, product.getM_productName());
        params.putString(FirebaseAnalytics.Param.ITEM_ID, product.getM_productID());
        params.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, product.getM_productCategory());
        params.putDouble(FirebaseAnalytics.Param.PRICE,product.getM_productPrice());
        params.putDouble("product_reviews_count",product.getReviewsCount());
        params.putDouble("product_rating",product.getRating());
        params.putDouble("product_user_rating",userRating);

        mFirebaseAnalytics.logEvent(eventName,params);


        //Flurry
        Map<String, String> eventParams = new HashMap<String, String>();
        eventParams.put("product_description", product.getM_productDescription());
        eventParams.put(FirebaseAnalytics.Param.ITEM_NAME, product.getM_productName());
        eventParams.put(FirebaseAnalytics.Param.ITEM_ID, product.getM_productID());
        eventParams.put(FirebaseAnalytics.Param.ITEM_CATEGORY, product.getM_productCategory());
        eventParams.put(FirebaseAnalytics.Param.PRICE,String.valueOf(product.getM_productPrice()));
        eventParams.put("product_reviews_count",String.valueOf(product.getReviewsCount()));
        eventParams.put("product_rating",String.valueOf(product.getRating()));
        eventParams.put("product_user_rating",String.valueOf(userRating));

        FlurryAgent.logEvent(eventName,eventParams);


        //Mixpanel
        Map<String, Object> eventParams2 = new HashMap<String, Object>();
        eventParams2.put("product_description", product.getM_productDescription());
        eventParams2.put(FirebaseAnalytics.Param.ITEM_NAME, product.getM_productName());
        eventParams2.put(FirebaseAnalytics.Param.ITEM_ID, product.getM_productID());
        eventParams2.put(FirebaseAnalytics.Param.ITEM_CATEGORY, product.getM_productCategory());
        eventParams2.put(FirebaseAnalytics.Param.PRICE,product.getM_productPrice());
        eventParams2.put("product_reviews_count",product.getReviewsCount());
        eventParams2.put("product_rating",product.getRating());
        eventParams2.put("product_user_rating",userRating);


        mMixpanel.trackMap(eventName,eventParams2);

    }

    public void setUserID(String id) {

        mFirebaseAnalytics.setUserId(id);


        FlurryAgent.setUserId(id);

        mMixpanel.identify(id);
        mMixpanel.getPeople().identify(mMixpanel.getDistinctId());

    }

    public void setUserProperty(String name , String value) {

        mFirebaseAnalytics.setUserProperty(name,value);

        mMixpanel.getPeople().set(name,value);
    }
}