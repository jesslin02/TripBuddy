package com.tripbuddy;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.tripbuddy.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "SignUpActivity";
    public static final String KEY_NAME = "name";
    ActivitySignUpBinding binding;
    int salmon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        salmon = getResources().getColor(R.color.salmon);

        binding.transitionBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.transitionBtnSignup.startAnimation();
                Log.i(TAG, "onClick transition signup button");
                String name = binding.etName.getText().toString();
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                String passwordConfirm = binding.etPasswordConfirm.getText().toString();
                if (Utils.checkRequiredInput(salmon, binding.nameLayout, binding.usernameLayout,
                        binding.passwordLayout, binding.passwordConfirmLayout)
                        && checkPasswords(password, passwordConfirm)) {
                    signUpUser(name, username, password);
                } else {
                    binding.transitionBtnSignup.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                }
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick return to login button");
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void signUpUser(String name, String username, String password) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.put(KEY_NAME, name);
        user.setUsername(username);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    binding.transitionBtnSignup.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Issue with signup", e);
                    return;
                }
                Toast.makeText(SignUpActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                Utils.goMainActivity(SignUpActivity.this);
            }
        });
    }

    private boolean checkPasswords(String pass, String conf) {
        if (!pass.equals(conf)) {
            binding.passwordLayout.setErrorTextColor(ColorStateList.valueOf(salmon));
            binding.passwordLayout.setError("Passwords must match");
            binding.passwordLayout.setErrorEnabled(true);

            binding.passwordConfirmLayout.setErrorTextColor(ColorStateList.valueOf(salmon));
            binding.passwordConfirmLayout.setError("Passwords must match");
            binding.passwordConfirmLayout.setErrorEnabled(true);
            return false;
        }
        return true;
    }
}