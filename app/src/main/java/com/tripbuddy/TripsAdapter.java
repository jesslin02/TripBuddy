package com.tripbuddy;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

import java.util.List;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.ViewHolder> implements Adapter {
    public static final String TAG = "TripsAdapter";
    Context context;
    List<Trip> trips;
    Trip recentlyDeletedTrip;
    int recentlyDeletedTripPosition;

    public TripsAdapter(Context c, List<Trip> t) {
        this.context = c;
        this.trips = t;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripsAdapter.ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.bind(trip);
    }

    @Override
    public int getItemCount() {
        return this.trips.size();
    }

    @Override
    public void deleteItem(int position) {
        recentlyDeletedTrip = trips.get(position);
        recentlyDeletedTripPosition = position;
        trips.remove(recentlyDeletedTripPosition);
        notifyItemRemoved(recentlyDeletedTripPosition);
        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        View view = ((MainActivity) context).findViewById(R.id.coordinator_layout);
        Snackbar sb = Snackbar.make(view, R.string.undo, Snackbar.LENGTH_LONG);
        sb.setAnchorView(((MainActivity) context).findViewById(R.id.bottom_navigation));
        sb.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoDelete();
            }
        });
        sb.show();

        sb.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event == BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_TIMEOUT) {
                    recentlyDeletedTrip.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error deleting event: " + recentlyDeletedTrip.getTitle(), e);
                                Toast.makeText(context, "Error deleting event!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Log.d(TAG, "recently deleted event: " + recentlyDeletedTrip.getTitle());
                        }
                    });
                }
                super.onDismissed(transientBottomBar, event);
            }
        });
    }

    private void undoDelete() {
        trips.add(recentlyDeletedTripPosition, recentlyDeletedTrip);
        notifyItemInserted(recentlyDeletedTripPosition);
    }

    @Override
    public Context getContext() {
        return context;
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvDestination;
        TextView tvDate;
        ImageView ivEdit;
        ImageView ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDestination = itemView.findViewById(R.id.tvDestination);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);

            itemView.setOnClickListener(this);
        }

        public void bind(Trip trip) {
            tvTitle.setText(trip.getTitle());
            tvDestination.setText(trip.getDestination());
            String fullDate = trip.getStart() + " - " + trip.getEnd();
            tvDate.setText(fullDate);
            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, CreateTripActivity.class);
                    i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(trip));
                    i.putExtra("edit", true);
                    context.startActivity(i);
                }
            });

            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    trip.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error deleting trip: " + trip.getTitle(), e);
                                Toast.makeText(context, "Error deleting trip!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            int position = getAdapterPosition();
                            trips.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.i(TAG, "item clicked at position " + Integer.toString(position));
            if (position != RecyclerView.NO_POSITION) {
                Trip selected = trips.get(position);
                Intent i = new Intent(context, TripDetailActivity.class);
                i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(selected));
                context.startActivity(i);
            }
        }
    }

}
