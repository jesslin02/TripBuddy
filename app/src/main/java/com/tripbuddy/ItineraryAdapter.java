package com.tripbuddy;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder> implements Adapter, Filterable {
    public static final String TAG = "ItineraryAdapter";
    Trip trip;
    Context context;
    List<Event> events;
    List<Event> eventsFiltered;

    public ItineraryAdapter(Context c, List<Event> e, Trip trip) {
        this.context = c;
        this.events = e;
        this.eventsFiltered = e;
        this.trip = trip;
    }

    @NonNull
    @Override
    public ItineraryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new ItineraryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItineraryAdapter.ViewHolder holder, int position) {
        Event event = eventsFiltered.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return eventsFiltered.size();
    }

    @Override
    public void deleteItem(int position) {
        Event recentlyDeletedEvent = eventsFiltered.get(position);
        eventsFiltered.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar(recentlyDeletedEvent, position);
    }

    private void showUndoSnackbar(Event deleted, int deletedPos) {
        View view = ((ItineraryActivity) context).findViewById(R.id.coordinator_layout);
        String sbMessage = deleted.getTitle() + " was removed";
        Snackbar sb = Snackbar.make(view, sbMessage, Snackbar.LENGTH_LONG);
        sb.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoDelete(deleted, deletedPos);
            }
        });
        sb.show();

        sb.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION) {
                    deleted.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error deleting event: " + deleted.getTitle(), e);
                                Toast.makeText(context, "Error deleting event!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Log.d(TAG, "recently deleted event: " + deleted.getTitle());
                        }
                    });
                }
                super.onDismissed(transientBottomBar, event);
            }
        });
    }

    private void undoDelete(Event deleted, int deletedPos) {
        eventsFiltered.add(deletedPos, deleted);
        notifyItemInserted(deletedPos);
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void editItem(int position) {
        Intent i = new Intent(context, CreateEventActivity.class);
        Event event = eventsFiltered.get(position);
        i.putExtra("edit", true);
        i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(trip));
        i.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
        context.startActivity(i);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString().toLowerCase();
                if (charString.isEmpty()) {
                    eventsFiltered = events;
                } else {
                    List<Event> filteredList = new ArrayList<>();
                    for (Event event : events) {
                        Log.i(TAG, "performFiltering on event at " + event.getLocation()
                                + " with query " + constraint);

                        // name match condition. we are looking for title or location match
                        if (event.getTitle().toLowerCase().contains(charString)
                                || event.getLocation().toLowerCase().contains(charString)
                                || event.getStart().toLowerCase().contains(charString)) {
                            filteredList.add(event);
                        }
                    }

                    eventsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = eventsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                eventsFiltered = (ArrayList<Event>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvLocation;
        TextView tvStart;
        TextView tvEnd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStart = itemView.findViewById(R.id.tvStart);
            tvEnd = itemView.findViewById(R.id.tvEnd);

            itemView.setOnClickListener(this);
        }

        public void bind(Event event) {
            tvTitle.setText(event.getTitle());
            tvLocation.setText(event.getLocation());
            tvStart.setText(event.getStart());
            tvEnd.setText(event.getEnd());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.i(TAG, "item clicked at position " + Integer.toString(position));
            if (position != RecyclerView.NO_POSITION) {
                Event selected = eventsFiltered.get(position);
                Intent i = new Intent(context, EventDetailActivity.class);
                i.putExtra("edit", false);
                i.putExtra(Event.class.getSimpleName(), Parcels.wrap(selected));
                context.startActivity(i);
            }
        }
    }
}
