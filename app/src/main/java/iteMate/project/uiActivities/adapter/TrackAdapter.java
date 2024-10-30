package iteMate.project.uiActivities.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import iteMate.project.R;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Track;
import iteMate.project.uiActivities.trackScreens.TrackDetailActivity;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private final List<Track> tracks;
    private final Context context;
    private final TrackController trackController;

    public TrackAdapter(List<Track> tracks, Context context) {
        this.tracks = tracks;
        this.context = context;
        this.trackController = TrackController.getControllerInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.track_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Track track = tracks.get(position);

        // Check if contact is not null before accessing its methods
        if (track.getContact() != null) {
            // setting the name of the contact
            String displayName = track.getContact().getName();
            holder.contactName.setText(displayName);
        }

        //  setting the lend out date text
        holder.lendOutDate.setText(track.getReadableGiveOutDate());

        // setting the track id text
        int numberOfAllItems = track.getLentItemIDs().size();
        int numberOfPendingItems = track.getPendingItemIDs().size();
        String itemsLentText = numberOfPendingItems + " of " + numberOfAllItems + " pending";
        holder.itemsLentText.setText(itemsLentText);

        // setting the days left
        int daysLeftInt = track.getDaysLeft();
        String daysLeftString = daysLeftInt >= 0 ? "+": "";
        if (daysLeftInt < 0) {
            holder.daysLeft.setTextColor(Color.RED);
        } else {
            holder.daysLeft.setTextColor(ContextCompat.getColor(context, R.color.positive_days_left_green));
        }
        daysLeftString += daysLeftInt + "d";
        holder.daysLeft.setText(daysLeftString);
        if (track.isDone()) {
            holder.daysLeft.setText("✓");
            holder.daysLeft.setTextColor(Color.GRAY);
        }

        // Setting transparency of the cardview to signal if a track is done
        if (track.getPendingItemIDs() == null || track.getPendingItemIDs().isEmpty()) {
            // Grey out if track has pending items
            holder.cardView.setAlpha(0.4f);
        } else {
            // Show track if items are pending
            holder.cardView.setAlpha(1f);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrackDetailActivity.class);
            trackController.setCurrentTrack(track);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();  // Changed 'items' to 'tracks'
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView contactName;
        public TextView lendOutDate;
        public TextView daysLeft;
        public TextView itemsLentText;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.track_cardview);
            contactName = itemView.findViewById(R.id.trackcard_contactname);
            lendOutDate = itemView.findViewById(R.id.track_card_date_text);
            daysLeft = itemView.findViewById(R.id.trackcard_daycounter);
            itemsLentText = itemView.findViewById(R.id.numberItemsLentText);
        }
    }
}
