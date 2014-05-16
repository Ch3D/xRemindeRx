
package com.ch3d.xreminderx.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.ch3d.xreminderx.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;

public class GooglePlusSignInActivity extends Activity implements
        OnConnectionFailedListener,
        ConnectionCallbacks, OnClickListener {
    private static final String LOG_TAG = GooglePlusSignInActivity.class.getSimpleName();

    private static final int RESULT_CODE_SIGN_IN = 0;

    private GoogleApiClient mPlusClient;
    private ConnectionResult mConnectionResult;

    private boolean mSignInClicked;
    private boolean mIntentInProgress;

    @Override
    protected void onActivityResult(final int requestCode, final int responseCode,
            final Intent intent) {
        if ((requestCode == RESULT_CODE_SIGN_IN) && (responseCode == RESULT_OK)) {
            mConnectionResult = null;
            mPlusClient.connect();
        }
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.x_login.btn_sign_gplus:
                signInGooglePlus();
                break;

            default:
                break;
        }
    }

    @Override
    public void onConnected(final Bundle arg0) {
        mSignInClicked = false;
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onConnectionFailed(final ConnectionResult result) {
        setResult(RESULT_CANCELED);
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            finish();
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
        mConnectionResult = result;
    }

    @Override
    public void onConnectionSuspended(final int arg0) {
        Log.d(LOG_TAG, "disconnected");
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            finish();
            return;
        }
        setContentView(R.layout.x_login);
        final SignInButton btnPlusSignIn = (SignInButton) findViewById(R.x_login.btn_sign_gplus);
        btnPlusSignIn.setOnClickListener(this);

        mPlusClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API, null)
                .addScope(Plus.SCOPE_PLUS_LOGIN).addScope(Plus.SCOPE_PLUS_PROFILE).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPlusClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPlusClient.isConnected()) {
            mPlusClient.disconnect();
        }
    }

    private void resolveSignInError() {
        if ((mConnectionResult != null) && mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RESULT_CODE_SIGN_IN);
            } catch (final SendIntentException e) {
                mIntentInProgress = false;
                mPlusClient.connect();
            }
        }
    }

    private void signInGooglePlus() {
        if (!mPlusClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }
}
