package iteMate.project.uiActivities.scanScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;
import iteMate.project.uiActivities.trackScreens.TrackDetailActivity;

public class ScanItemFragment extends Fragment {

    private Item itemToDisplay;
    private Track trackToDisplay;

    public ScanItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_item, container, false);

        // Set on click listener for item card view
        view.findViewById(R.id.item_card_view_scan).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ItemsDetailActivity.class);
            intent.putExtra("item", itemToDisplay);
            startActivity(intent);
        });

        // Set item details for track card view
        view.findViewById(R.id.track_card_view_scan).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TrackDetailActivity.class);
            intent.putExtra("track", trackToDisplay);
            startActivity(intent);
        });

        return view;
    }

    void setItemToDisplay(Item item) {
        this.itemToDisplay = item;
    }

    void setTrackToDisplay(Track track) {
        this.trackToDisplay = track;
    }

}