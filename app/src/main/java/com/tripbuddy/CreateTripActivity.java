package com.tripbuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.tripbuddy.databinding.ActivityCreateTripBinding;
import com.tripbuddy.databinding.ActivityLoginBinding;
import com.tripbuddy.models.Trip;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateTripActivity extends AppCompatActivity {
    public static final String TAG = "CreateTripActivity";
    ActivityCreateTripBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_create_trip);

        binding = ActivityCreateTripBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // binding.etStart.setInputType(InputType.TYPE_NULL);
        binding.etStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(CreateTripActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                binding.etStart.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        // binding.etEnd.setInputType(InputType.TYPE_NULL);
        binding.etEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                DatePickerDialog picker = new DatePickerDialog(CreateTripActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                binding.etEnd.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = binding.etTitle.getText().toString();
                String dest = binding.etDestination.getText().toString();
                String start = binding.etStart.getText().toString();
                String end = binding.etEnd.getText().toString();
                String notes = binding.etNotes.getText().toString();
                if (checkInput(title, dest, start, end)) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    saveTrip(currentUser, title, dest, start, end, notes);
                }
            }
        });
    }

    /**
     * checks if all required fields are filled out
     * if a required field isn't filled out, changes background color to red
     */
    private boolean checkInput(String title, String dest, String start, String end) {
        boolean validInput = true;
        if (title.isEmpty()) {
            binding.etTitle.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            // binding.etTitle.setBackgroundColor(ContextCompat.getColor(CreateTripActivity.this, R.color.light_red));
            validInput = false;
        }
        if (dest.isEmpty()) {
            binding.etDestination.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            //binding.etDestination.setBackgroundColor(ContextCompat.getColor(CreateTripActivity.this, R.color.light_red));
            validInput = false;
        }
        if (start.isEmpty()) {
            binding.etStart.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            //binding.etStart.setBackgroundColor(ContextCompat.getColor(CreateTripActivity.this, R.color.light_red));
            validInput = false;
        }
        if (end.isEmpty()) {
            binding.etEnd.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            //binding.etEnd.setBackgroundColor(ContextCompat.getColor(CreateTripActivity.this, R.color.light_red));
            validInput = false;
        }
        return validInput;
    }

    private void saveTrip(ParseUser user, String title, String dest, String start, String end, String notes) {
        Trip trip = new Trip();
        trip.setUser(user);
        trip.setTitle(title);
        trip.setDestination(dest);
        if (!notes.isEmpty()) {
            trip.setNotes(notes);
        }
        trip.setStart(convertToDateList(start));
        trip.setEnd(convertToDateList(end));
        trip.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while creating trip", e);
                    Toast.makeText(CreateTripActivity.this, "Error while creating trip!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Trip creation was successful!");
                resetInput();
            }
        });
    }

    /**
     * converts editText date input string into an int array
     * for use in the Trip setStart and setEnd methods
     * @param date in the form of "mm/dd/yyyy"
     * @return List<Integer> in the form of [mm, dd, yyyy]
     */
    private List<Integer> convertToDateList(String date) {
        List<Integer> converted = new ArrayList<>();
        String[] separated = date.split("/");
        for (int i = 0; i < separated.length; i++) {
            converted.add(i, Integer.valueOf(separated[i]));
        }
        return converted;
    }

    /**
     * changes all input fields to blank and restores original colors
     * after trip has been created
     */
    private void resetInput() {
        binding.etTitle.setText("");
        binding.etTitle.getBackground().clearColorFilter();
        binding.etDestination.setText("");
        binding.etDestination.getBackground().clearColorFilter();
        binding.etStart.setText("");
        binding.etStart.getBackground().clearColorFilter();
        binding.etEnd.setText("");
        binding.etEnd.getBackground().clearColorFilter();
        binding.etNotes.setText("");
    }
}