package br.com.danceapp.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by Rafael on 09/11/2016.
 */

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getSimpleName();
    public static final String SUCCESS_MESSAGE = "success_message";

    private LoginButton mLoginButton;
    private CallbackManager mCallbackManager;
    private Button mLoginLaterButton;
    private Button mContinueButton;
    private String mSuccessMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FacebookSdk.sdkInitialize(getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                mSuccessMessage = null;
            } else {
                mSuccessMessage = extras.getString(SUCCESS_MESSAGE);
            }
        } else {
            mSuccessMessage = (String) savedInstanceState.getSerializable(SUCCESS_MESSAGE);
        }

        mLoginButton = (LoginButton)findViewById(R.id.button_login);
        //mLoginButton.setReadPermissions("public_profile", "email", "user_events");
        mLoginButton.setPublishPermissions("rsvp_event");

        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String info = "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + " - " +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken();

                Log.d(TAG, "Facebook Login Success: " + info);

                if (mSuccessMessage == null) {
                    finish();
                } else {
                    mLoginButton.setVisibility(View.GONE);
                    mLoginLaterButton.setVisibility(View.GONE);
                    mContinueButton.setVisibility(View.VISIBLE);
                    TextView loginText = (TextView) findViewById(R.id.text_login);
                    loginText.setText(mSuccessMessage);
                }
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Facebook Login Cancelled.");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d(TAG, "Facebook Login Error: " + e.getLocalizedMessage());
            }
        });

        mLoginLaterButton = (Button)findViewById(R.id.button_login_later);
        mLoginLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mContinueButton = (Button)findViewById(R.id.button_continue);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String name = getString(R.string.analytics_login_screen);
        Log.i(TAG, "Setting screen name: " + name);
        DanceAppApplication application = (DanceAppApplication) getApplication();
        application.trackScreenView(name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
