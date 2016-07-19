package com.example.ruslan.loginapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.InputStream;

public class LoginGPlus extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private SignInButton btnSignIn;
    private TextView textName;
    private ImageView imageUser;
    private Button btnSignOut;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;
    private static final int REQUEST_CODE = 100;
    private ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    private static final String NAME_USER = "NAME_USER";
    private static final String ID_USER = "ID_USER";
    private static final String IMAGE_USER = "IMAGE_USER";
    private static final String SIGNIN_BOOLEAN = "SIGNIN_BOOLEAN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SIGNIN_BOOLEAN, false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gplus_loggin);
        imageUser = (ImageView) findViewById(R.id.imageUser);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        textName = (TextView) findViewById(R.id.textName);
        btnSignOut = (Button) findViewById(R.id.buttonSignOut);
        btnSignOut.setOnClickListener(onClickListener);
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
        btnSignIn = (SignInButton) findViewById(R.id.btnSignIn);
        btnSignIn.setSize(SignInButton.SIZE_WIDE);
        btnSignIn.setScopes(googleSignInOptions.getScopeArray());
        btnSignIn.setOnClickListener(onClickListener);
        if (sharedPreferences.getBoolean(SIGNIN_BOOLEAN, true)) {
            textName.setText(sharedPreferences.getString(NAME_USER, ""));
            new LoadProfileImage(imageUser).execute(sharedPreferences.getString(IMAGE_USER, ""));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                finish();
                break;

        }

        return true;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.buttonSignOut:
                    SignOut();
                    break;
                case R.id.btnSignIn:
                    SignIn();
                    break;
            }
        }
    };

    private void SignIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = result.getSignInAccount();
            if (account != null) {
                textName.setText(account.getDisplayName());
                new LoadProfileImage(imageUser).execute(account.getPhotoUrl().toString());
                SaveUser(account.getId(), account.getDisplayName(), account.getPhotoUrl().toString());
            }


        }
    }

    private void SignOut() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {

                @Override
                public void onResult(Status status) {
                    googleApiClient.disconnect();
                    sharedPreferences = getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(SIGNIN_BOOLEAN, false);
                    editor.putString(ID_USER, null);
                    editor.putString(NAME_USER, null);
                    editor.putString(IMAGE_USER, null);
                    editor.commit();
                    textName.setText(null);
                    imageUser.setImageBitmap(null);

                }
            });

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void SaveUser(String id, String name, String imageUrl) {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SIGNIN_BOOLEAN, true);
        editor.putString(ID_USER, id);
        editor.putString(NAME_USER, name);
        editor.putString(IMAGE_USER, imageUrl);
        editor.commit();
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            progressBar.setVisibility(View.GONE);
            bmImage.setImageBitmap(result);

        }
    }

}
