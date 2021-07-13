package com.tripbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.tripbuddy.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "SignUpActivity";
    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_sign_up);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick signup button");
                String name = binding.etName.getText().toString();
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                String passwordConfirm = binding.etPasswordConfirm.getText().toString();
                if (!password.equals(passwordConfirm)) {
                    Toast.makeText(SignUpActivity.this, "Passwords must match", Toast.LENGTH_SHORT).show();
                    return;
                }
                signUpUser(name, username, password);
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick return to login button");
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void signUpUser(String name, String username, String password) {
        // TODO: implement sign up
        goMainActivity();
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        // so that pressing the back button on the MainActivity doesn't go back to the login screen
        finish();
    }
}