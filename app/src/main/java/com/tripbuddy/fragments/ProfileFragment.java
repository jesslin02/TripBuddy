package com.tripbuddy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.parse.ParseUser;
import com.tripbuddy.LoginActivity;
import com.tripbuddy.R;
import com.tripbuddy.SettingsActivity;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#//newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    public static final String KEY_NAME = "name";
    TextView tvName;
    Button btnLogout;
    Button btnSettings;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvName = view.findViewById(R.id.tvName);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnSettings = view.findViewById(R.id.btnSettings);

        tvName.setText(ParseUser.getCurrentUser().getString(KEY_NAME));

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                LoginManager.getInstance().logOut();
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        tvName.setText(ParseUser.getCurrentUser().getString(KEY_NAME));
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull @NotNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem progress = menu.findItem(R.id.action_progress);
        progress.setVisible(false);
    }
}