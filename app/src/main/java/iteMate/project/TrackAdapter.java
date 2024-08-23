package iteMate.project;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ViewHolder> {

    private List<Track> tracks;
    private Context context;

    /**
     * Stores the id of the clicked Track, if any.
     */
    private static Track clickedTrack;

    /**
     * Returns the id of the clicked Track in order to display the correct Track in the detail view.
     * @return the id of the clicked Track
     */
    public static Track getClickedTrack() {
        return clickedTrack;
    }

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
        Track track = tracks.get(position);  // Changed 'item' to 'track'
        holder.rendDate.setText(track.getGiveOutDate().toString());  // Changed 'item' to 'track'
        holder.contactName.setText(track.getContact().getFirstName() + " " + track.getContact().getLastName());  // Changed 'item' to 'track'

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrackDetailActivity.class);
            intent.putExtra("track", track);  // Changed 'track' to 'track'
            clickedTrack = track;
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();  // Changed 'items' to 'tracks'
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView contactName;
        public TextView rendDate;

        public ViewHolder(View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.trackcard_header_text);
            rendDate = itemView.findViewById(R.id.trackcard_subheader_text);
        }
    }
}
