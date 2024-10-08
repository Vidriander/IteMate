package iteMate.project.uiActivities.scanScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;
import iteMate.project.uiActivities.trackScreens.TrackDetailActivity;

/**
 * Fragment for displaying scanned item and track
 */
public class ScanItemFragment extends Fragment {

    private Item itemToDisplay;
    private Track trackToDisplay;
    private Button returnButton;

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

        // Set item details for track card view
        view.findViewById(R.id.track_card_view_scan).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TrackDetailActivity.class);
            intent.putExtra("track", trackToDisplay);
            startActivity(intent);
        });

        // Set up return button
        returnButton = view.findViewById(R.id.return_button);

        // Set on click listener for item card view
        view.findViewById(R.id.item_card_view_scan).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ItemsDetailActivity.class);
            intent.putExtra("item", itemToDisplay);
            startActivity(intent);
        });

        // Set on click listener for lend button
        view.findViewById(R.id.lend_button).setOnClickListener(v -> {
            // TODO Handle lend button click
        });

        // Set on click listener for return button
        view.findViewById(R.id.return_button).setOnClickListener(v -> {
            // TODO Handle return button click
        });

        return view;
    }

    /**
     * Set the item to display in the fragment
     *
     * @param item Item to display
     */
    void setItemToDisplay(Item item) {
        this.itemToDisplay = item;
    }

    /**
     * Set the track to display in the fragment
     *
     * @param track Track to display
     */
    void setTrackToDisplay(Track track) {
        this.trackToDisplay = track;
    }

    public Button getReturnButton() { // Add this method
        return returnButton;
    }

}