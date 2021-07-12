package com.tripbuddy;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tripbuddy.models.Event;
import com.tripbuddy.models.Trip;

import org.parceler.Parcels;

import java.util.List;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ViewHolder> {
    public static final String TAG = "ItineraryAdapter";
    Context context;
    List<Event> events;

    public ItineraryAdapter(Context c, List<Event> e) {
        this.context = c;
        this.events = e;
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
            // TODO: update Event.java to return dates as strings
            tvStart.setText("8:00 AM");
            tvEnd.setText("11:00 AM");
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.i(TAG, "item clicked at position " + Integer.toString(position));
            if (position != RecyclerView.NO_POSITION) {
                Event selected = events.get(position);
                Intent i = new Intent(context, EventDetailActivity.class);
                i.putExtra(Event.class.getSimpleName(), Parcels.wrap(selected));
                context.startActivity(i);
            }
        }
    }
}
