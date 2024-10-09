package iteMate.project.uiActivities.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import iteMate.project.R;
import iteMate.project.models.Track;
import iteMate.project.uiActivities.trackScreens.TrackDetailActivity;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private final List<Track> tracks;
    private final Context context;

    public TrackAdapter(List<Track> tracks, Context context) {
        this.tracks = tracks;
        this.context = context;
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
        holder.trackIdText.setText(track.getReadableId());

        // setting the days left
        int daysLeftInt = track.getDaysLeft();
        String daysLeftString = daysLeftInt >= 0 ? "+": "";
        if (daysLeftInt < 0) {
            holder.daysLeft.setTextColor(Color.RED);
        }
        daysLeftString += daysLeftInt + "d";
        holder.daysLeft.setText(daysLeftString);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrackDetailActivity.class);
            intent.putExtra("track", track);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();  // Changed 'items' to 'tracks'
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactName;
        public TextView lendOutDate;
        public TextView daysLeft;
        public TextView trackIdText;

        public ViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.trackcard_contactname);
            lendOutDate = itemView.findViewById(R.id.track_card_date_text);
            daysLeft = itemView.findViewById(R.id.trackcard_daycounter);
            trackIdText = itemView.findViewById(R.id.track_id_text);
        }
    }
}
