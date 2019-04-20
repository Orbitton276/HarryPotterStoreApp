package com.example.or.ex3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.or.ex3.Analytics.AnalyticsManager;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class signIn_activity extends AppCompatActivity {

    public static final String TAG = "onSignInActivity";
    public static final int RC_GOOGLE_SIGN_IN = 1001;
    private SignInButton mGoogleSignInBtn;
    private FirebaseAuth mAuth;
    private FirebaseRemoteConfig mConfig;
    private CallbackManager mCallbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private AccessTokenTracker accessTokenTracker;
    private TextView textView,mEmail,mPass;

    private AnalyticsManager analyticsManager = AnalyticsManager.getInstance();
    private String signInMethod = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);

        AnalyticsManager.getInstance().init(getApplicationContext());

        initSignInActivity();
        firebaseInit();
        googleSignInInit();
        facebookLoginInit();
        initRemoteConfig();
    }



    //------------------------------------------------------------------------------------

    private void initSignInActivity(){
        mGoogleSignInBtn = (SignInButton)findViewById(R.id.google_sign_in_button);

        mAuth = FirebaseAuth.getInstance();
        mConfig = FirebaseRemoteConfig.getInstance();
        mEmail = findViewById(R.id.tvSigninEmail);
        mPass = findViewById(R.id.tvSigninPassword);
    }

    private void initRemoteConfig(){
        mConfig.setDefaults(R.xml.remote_defaults);
        mConfig.fetch(0).addOnCompleteListener(
                this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "Fetch succeeded" );
                            mConfig.activateFetched();
                        }
                        else{
                            Log.e(TAG, "Fetch failed" );
                        }

                    }
                });
    }

    public void onEmailPasswordAuthClick(View V) {

        Log.e(TAG, "onEmailPasswordSignUpClick() >>");

        String email = mEmail.getText().toString();
        String pass = mPass.getText().toString();

        if (email.isEmpty()||pass.isEmpty()){
            displayMessage("Please fill in both Email and Password fields");
            return;
        }

        Task<AuthResult> authResult;

        authResult = mAuth.signInWithEmailAndPassword(email, pass);
        authResult.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.e(TAG, "Email/Pass Auth: onComplete() >> " + task.isSuccessful());
                if (task.isSuccessful()){
                    displayMessage("successfully signed in");

                    analyticsManager.trackSignInEvent("Email_Password");
                    analyticsManager.setUserID(mAuth.getCurrentUser().getUid());
                    analyticsManager.setUserProperty("email",mAuth.getCurrentUser().getEmail());

                    Intent intent;
                    intent = new Intent(signIn_activity.this, ProductsActivity.class);
                    startActivity(intent);
                    finish();


                }
                else{
                    displayMessage("Problem signing in\ndetails: "+task.getException().getMessage());
                }
                Log.e(TAG, "Email/Pass Auth: onComplete() <<");
            }
        });

        Log.e(TAG, "onEmailPasswordSignUpClick() <<");
    }
    public void onForgotPasswordClicked(View v){
        Intent intent = new Intent(signIn_activity.this,resetPassword_activity.class);
        startActivity(intent);
    }

    public void onToSignUpClicked(View v)
    {
        Intent intent = new Intent(signIn_activity.this, signUp_activity.class);
        startActivity(intent);
    }
    private void googleSignInInit() {

        Log.e(TAG, "googleSigninInit() >>" );

        // Configure Google Sign In
        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.google_sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onGoogleSignIn();
            }
        });
        googleButtonInit();
        Log.e(TAG, "googleSigninInit() <<" );
    }

    private void googleButtonInit(){
        textView = (TextView) mGoogleSignInBtn.getChildAt(0);
        textView.setText("Countinue with Google");
        textView.setTextSize(20);
        textView.setPadding(40,0,0,0);
    }

    private void onGoogleSignIn() {

        Log.e(TAG, "onGoogleSignIn() >>" );

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);

        Log.e(TAG, "onGoogleSignIn() <<" );
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        Log.e(TAG, "firebaseAuthWithGoogle() >>");

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            analyticsManager.trackSignInEvent("Google Sign In");
                            analyticsManager.setUserID(mAuth.getCurrentUser().getUid());
                            analyticsManager.setUserProperty("email",mAuth.getCurrentUser().getEmail());

                            displayMessage("Signing in with Google");
                            Intent intent = new Intent(signIn_activity.this, ProductsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            displayMessage("Problem signing in with Google");
                        }
                    }
                });

        Log.e(TAG, "firebaseAuthWithGoogle() <<");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        Log.e(TAG, "onActivityResult () >>");

        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            //Google Login...
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Log.e(TAG, "try google sign in success () >>");
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e);
            }
        }
        else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
            Log.e(TAG, "On activity result in facebook");
        }

        Log.e(TAG, "onActivityResult () <<");
    }

    @Override
    protected void onStart() {

        Log.e(TAG, "onStart() >>");
        String name;
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){

            Log.e(TAG,"user is already signed up");
            Intent intent = new Intent(signIn_activity.this,ProductsActivity.class);
            startActivity(intent);
            finish();
        }

        Log.e(TAG, "onStart() <<");

    }

    @Override
    protected void onStop() {

        Log.e(TAG, "onStop() >>");

        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        Log.e(TAG, "onStop() <<");

    }


    private void firebaseInit() {
        Log.e(TAG, "firebaseAuthenticationInit() >>");
        //Obtain reference to the current authentication
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.e(TAG, "onAuthStateChanged() >>");

                Log.e(TAG, "onAuthStateChanged() <<");
            }
        };

        Log.e(TAG, "firebaseAuthenticationInit() <<");


    }

    private void facebookLoginInit() {


        Log.e(TAG, "facebookLoginInit() >>");
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "facebook:onSuccess () >>");

                handleFacebookAccessToken(loginResult.getAccessToken());
                Log.e(TAG, "facebook:onSuccess () <<");
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "facebook:onCancel() >>");
                displayMessage("Facebook sign in canceled");
                Log.e(TAG, "facebook:onCancel() <<");

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook:onError () >>" + error.getMessage());
                displayMessage("Facebook sign in failed\ndetails: "+error.getMessage());
                Log.e(TAG, "facebook:onError <<");
            }
        });

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    mAuth.signOut();
                }
                Log.e(TAG,"onCurrentAccessTokenChanged() >> currentAccessToken="+
                        (currentAccessToken !=null ? currentAccessToken.getToken():"Null") +
                        " ,oldAccessToken=" +
                        (oldAccessToken !=null ? oldAccessToken.getToken():"Null"));


            }
        };

        Log.e(TAG, "facebookLoginInit() <<");
    }

    private void handleFacebookAccessToken(AccessToken token) {

        Log.e(TAG, "handleFacebookAccessToken () >>" + token.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            displayMessage("Signing in with Facebook");
                            analyticsManager.trackSignInEvent("Facebook Sign In");
                            Intent intent = new Intent(signIn_activity.this, ProductsActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Log.e(TAG,task.getException().getMessage());
                            displayMessage("Facebook sign in failed "+task.getException().getMessage());
                            LoginManager.getInstance().logOut();
                        }
                        Log.e(TAG, "Facebook: onComplete() <<");
                    }
                });

        Log.e(TAG, "handleFacebookAccessToken () <<");

    }


    public void onSignInAnonymouslyClicked(View v){

        if ( mConfig.getBoolean("allow_anonymous_user")){
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");
                                displayMessage("Signing in anonymously");
                                analyticsManager.trackSignInEvent("Anonymous Sign In");
                                Intent intent = new Intent(signIn_activity.this, ProductsActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                displayMessage("Authentication failed.");

                            }


                        }
                    });
        }
        else{
            displayMessage("Anonymous sign in is not available at the moment");
        }


    }

    public void displayMessage(String message) {
        if (message != null) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

}