package iteMate.project.uiActivities.scanScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import iteMate.project.R;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;
import iteMate.project.uiActivities.itemScreens.ItemsEditActivity;
import iteMate.project.uiActivities.trackScreens.TrackDetailActivity;

/**
 * Fragment for displaying scanned item and track
 */
public class ScanItemFragment extends Fragment {

    private Item itemToDisplay;
    private Track trackToDisplay;
    private String tagId;
    private Button returnButton;
    private final TrackController trackController = TrackController.getControllerInstance();

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

        // Get tag ID from arguments
        if (getArguments() != null) {
            tagId = getArguments().getString("tagId");
        }

        // Set item details for track card view
        view.findViewById(R.id.track_card_view_scan).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TrackDetailActivity.class);
            trackController.setCurrentTrack(trackToDisplay);
            startActivity(intent);
        });

        // Set on click listener for item card view
        view.findViewById(R.id.item_card_view_scan).setOnClickListener(v -> {
            if (itemToDisplay == null) {
                // if no item exists, navigate to item edit screen and create new item
                Item newItem = new Item();
                newItem.setNfcTag(tagId);
                Intent intent = new Intent(getActivity(), ItemsEditActivity.class);
                intent.putExtra("item", newItem);
                startActivity(intent);
            } else {
                // if item exists navigate to item detail screen to display item details
                Intent intent = new Intent(getActivity(), ItemsDetailActivity.class);
                intent.putExtra("item", itemToDisplay);
                startActivity(intent);
            }
        });

        // Set item details for track card view
        view.findViewById(R.id.track_card_view_scan).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TrackDetailActivity.class);
            trackController.setCurrentTrack(trackToDisplay);
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

    public void setTrackToDisplay(Track track) {
        this.trackToDisplay = track;
    }

    public void setNfcTagId(String tagId) {
        this.tagId = tagId;
    }
}