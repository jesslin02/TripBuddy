package com.tripbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tripbuddy.databinding.ActivitySettingsBinding;
import com.tripbuddy.dialogfragments.EditNameDialogFragment;

public class SettingsActivity extends AppCompatActivity implements EditNameDialogFragment.EditNameDialogListener {
    public static final String TAG = "SettingsActivity";
    public static final int NAME = 0;
    public static final int USERNAME = 1;
    public static final int PASSWORD = 2;
    ActivitySettingsBinding binding;
    FragmentManager fm;
    ParseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_settings);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        fm = getSupportFragmentManager();

        user = ParseUser.getCurrentUser();
        displayInfo();
        binding.ivEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditNameDialogFragment enDialogFragment = EditNameDialogFragment.newInstance(
                        NAME, "Edit Name", user.getString("name"));
                enDialogFragment.show(fm, "fragment_edit_name");
            }
        });

        binding.ivEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditNameDialogFragment enDialogFragment = EditNameDialogFragment.newInstance(
                        USERNAME, "Edit Username", user.getUsername());
                enDialogFragment.show(fm, "fragment_edit_username");
            }
        });

        binding.ivEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditNameDialogFragment enDialogFragment = EditNameDialogFragment.newInstance(
                        PASSWORD, "Edit Password", "");
                enDialogFragment.show(fm, "fragment_edit_password");
            }
        });
    }

    private void displayInfo() {
        binding.tvName.setText(user.getString("name"));
        binding.tvUsername.setText(user.getUsername());
    }

    @Override
    public void onFinishEditDialog(int editType, String inputText) {
        Log.i(TAG, "dialog input received: " + inputText);
        if (editType == NAME) {
            user.put("name", inputText);
            // Toast.makeText(this, "name updated to " + inputText, Toast.LENGTH_SHORT).show();
        } else if (editType == USERNAME) {
            user.setUsername(inputText);
            // Toast.makeText(this, "username updated to " + inputText, Toast.LENGTH_SHORT).show();
        } else {
            user.setPassword(inputText);
        }
        displayInfo();
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Issue with saving new user information", e);
                    return;
                }
                Toast.makeText(SettingsActivity.this, "Update saved", Toast.LENGTH_SHORT).show();
            }
        });
    }
}