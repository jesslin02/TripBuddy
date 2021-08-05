package com.tripbuddy;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tripbuddy.databinding.ActivityEventDetailBinding;
import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

public class EventDetailActivity extends AppCompatActivity {
    public static final String TAG = "EventDetailActivity";
    ActivityEventDetailBinding binding;
    Event event;
    MenuItem editEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        event = Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));
        Log.d(TAG, String.format("Showing details for '%s'", event.getTitle()));

        getSupportActionBar().setTitle(event.getTitle());
        displayDetails();
    }

    private void displayDetails() {
        binding.tvTitle.setText(event.getTitle());
        binding.tvLocation.setText(event.getLocation());
        binding.tvStart.setText(event.getStart());
        binding.tvEnd.setText(event.getEnd());

        String phone = event.getPhoneString();
        if (phone != null) {
            binding.ivPhone.setVisibility(View.VISIBLE);
            binding.tvPhone.setText(phone);
            binding.tvPhone.setVisibility(View.VISIBLE);
        }

        String web = event.getWebsite();
        if (web != null && !web.isEmpty()) {
            binding.ivWebsite.setVisibility(View.VISIBLE);
            binding.tvWebsite.setText(web);
            binding.tvWebsite.setVisibility(View.VISIBLE);
        }

        String notes = event.getNotes();
        if (notes != null && !notes.isEmpty()) {
            binding.tvNoteTitle.setVisibility(View.VISIBLE);
            binding.tvNotes.setText(notes);
            binding.tvNotes.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(@NonNull Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu");
        editEvent = menu.findItem(R.id.edit);
        editEvent.getIcon().setTint(Color.WHITE);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.edit) {
            onEditButton();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onEditButton() {
        Intent i = new Intent(this, CreateEventActivity.class);
        i.putExtra("edit", true);
        i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(event.getTrip()));
        i.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
        startActivity(i);
        finish();
    }
}