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

    public TrackAdapter(List<Track> tracks, Context context) {
        this.tracks = tracks;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Track track = tracks.get(position);  // Changed 'item' to 'track'
        holder.textView.setText(track.getGiveOutDate().toString());  // Changed 'item' to 'track'

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TrackDetailActivity.class);
            intent.putExtra("track", track);  // Changed 'track' to 'track'
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return tracks.size();  // Changed 'items' to 'tracks'
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
