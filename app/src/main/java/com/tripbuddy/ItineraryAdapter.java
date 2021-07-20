package com.tripbuddy;

import android.content.Context;
import android.content.Intent;
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

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

import java.util.List;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder> {
    public static final String TAG = "ItineraryAdapter";
    Trip trip;
    Context context;
    List<Event> events;

    public ItineraryAdapter(Context c, List<Event> e, Trip trip) {
        this.context = c;
        this.events = e;
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
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvLocation;
        TextView tvStart;
        TextView tvEnd;
        ImageView ivEdit;
        ImageView ivDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvStart = itemView.findViewById(R.id.tvStart);
            tvEnd = itemView.findViewById(R.id.tvEnd);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);

            itemView.setOnClickListener(this);
        }

        public void bind(Event event) {
            tvTitle.setText(event.getTitle());
            tvLocation.setText(event.getLocation());
            tvStart.setText(event.getStart());
            tvEnd.setText(event.getEnd());
            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, CreateEventActivity.class);
                    i.putExtra("edit", true);
                    i.putExtra(Trip.class.getSimpleName(), Parcels.wrap(trip));
                    i.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
                    context.startActivity(i);
                }
            });
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    event.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error deleting event: " + event.getTitle(), e);
                                Toast.makeText(context, "Error deleting event!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            int position = getAdapterPosition();
                            events.remove(position);
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
                Event selected = events.get(position);
                Intent i = new Intent(context, EventDetailActivity.class);
                i.putExtra("edit", false);
                i.putExtra(Event.class.getSimpleName(), Parcels.wrap(selected));
                context.startActivity(i);
            }
        }
    }
}
