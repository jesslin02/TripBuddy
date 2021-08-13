package com.tripbuddy;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.tripbuddy.databinding.ActivityLoginBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    String email;
    CallbackManager callbackManager;
    ActivityLoginBinding binding;
    int salmon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseUser.getCurrentUser() != null) {
            Utils.goMainActivity(LoginActivity.this);
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        callbackManager = CallbackManager.Factory.create();
        salmon = getResources().getColor(R.color.salmon);

        binding.transitionBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.transitionBtnLogin.startAnimation();
                Log.i(TAG, "onClick transition login button");
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                if (Utils.checkRequiredInput(salmon, binding.usernameLayout, binding.passwordLayout)) {
                    loginUser(username, password);
                } else {
                    binding.transitionBtnLogin.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                }
            }
        });

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick go to signup button");
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });

        binding.btnFb.setReadPermissions(Arrays.asList("email"));
        binding.btnFb.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "fb login widget onSuccess");
                ParseUser user = ParseUser.getCurrentUser();
                Log.i(TAG, "current user: " + user);
                AccessToken accessToken = loginResult.getAccessToken();
                onFBLogin();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "fb login widget onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "fb login widget onError", error);
            }
        });

    }

    /**
     * my workaround because ParseFacebookUtils is giving errors
     */
    private void onFBLogin() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i(TAG, "onCompleted GraphRequest");
                        ParseUser user = new ParseUser();
                        Log.i(TAG, "current user: " + user);
                        try {
                            if (object.has("email")) {
                                email = object.getString("email");
                                user.setUsername(email);
                            }
                            if (object.has("name"))
                                user.put("name", object.getString("name"));
                            user.setPassword("facebook");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        user.signUpInBackground(new SignUpCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                    Utils.goMainActivity(LoginActivity.this);
                                } else if (e.getCode() == ParseException.USERNAME_TAKEN){
                                    loginUser(email, "facebook");
                                } else {
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Issue with signup through facebook", e);
                                }
                            }
                        });
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    binding.passwordLayout.setErrorTextColor(ColorStateList.valueOf(salmon));
                    binding.passwordLayout.setError(e.getMessage());
                    binding.passwordLayout.setErrorEnabled(true);

                    binding.usernameLayout.setErrorTextColor(ColorStateList.valueOf(salmon));
                    binding.usernameLayout.setError(e.getMessage());
                    binding.usernameLayout.setErrorEnabled(true);

                    binding.transitionBtnLogin.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                    Log.e(TAG, "Issue with login", e);
                    return;
                }
                Toast.makeText(LoginActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                // navigate to the main activity if the user has signed in properly
                Utils.goMainActivity(LoginActivity.this);
                finish();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data); // facebook widget
        super.onActivityResult(requestCode, resultCode, data);
    }

}