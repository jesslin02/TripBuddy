package com.tripbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.tripbuddy.databinding.ActivityCreateEventBinding;

public class CreateEventActivity extends AppCompatActivity {
    public static final String TAG = "CreateEventActivity";
    ActivityCreateEventBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_create_event);

        binding = ActivityCreateEventBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}