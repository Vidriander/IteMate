package iteMate.project.uiActivities.scanScreen;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.R;
import iteMate.project.controller.ItemController;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.uiActivities.ScanController;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;
import iteMate.project.uiActivities.itemScreens.ItemsEditActivity;
import iteMate.project.uiActivities.trackScreens.TrackDetailActivity;
import iteMate.project.uiActivities.trackScreens.TrackEditActivity;

/**
 * Fragment for displaying scanned item and track
 */
public class ScanItemFragment extends Fragment {

    private final ItemController itemController = ItemController.getControllerInstance();
    private final TrackController trackController = TrackController.getControllerInstance();
    private final ScanController scanController = ScanController.getControllerInstance();

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
            trackController.setCurrentTrack(trackController.getCurrentTrack());
            startActivity(intent);
        });

        // Set on click listener for item card
        view.findViewById(R.id.item_card_view_scan).setOnClickListener(v -> {
            Intent intent;
            if (trackController.getCurrentTrack() == null) {
                // if no item exists, navigate to item edit screen and create new item
                Item newItem = new Item();
                newItem.setNfcTag(scanController.getNfcTagId());
                itemController.setCurrentItem(newItem);
                intent = new Intent(getActivity(), ItemsEditActivity.class);
            } else {
                // if item exists navigate to item detail screen to display item details
                intent = new Intent(getActivity(), ItemsDetailActivity.class);
            }
            startActivity(intent);
        });

        // Set on click listener for track card
        view.findViewById(R.id.track_card_view_scan).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TrackDetailActivity.class);
            trackController.setCurrentTrack(trackController.getCurrentTrack());
            startActivity(intent);
        });

        // Set on click listener for lend button
        view.findViewById(R.id.lend_button).setOnClickListener(v -> {

            if (itemController.getCurrentItem().isAvailable()) {
                // create new track
                Track track = new Track();

                // add item to track (add item to track: setLendItemsList & setPendingItemsList)
                List<Item> itemList = new ArrayList<>();
                itemList.add(itemController.getCurrentItem());
                track.setLentItemsList(itemList);
                track.setPendingItemsList(itemList);

                // set active trackID to item and mark as lent out
                itemController.getCurrentItem().setActiveTrackID(track.getId());

                // open track edit activity
                trackController.setCurrentTrack(track);
                Intent intent = new Intent(getActivity(), TrackEditActivity.class);
                startActivity(intent);
            }
        });

        // Set on click listener for return button
        view.findViewById(R.id.return_button).setOnClickListener(v -> {

            Track currentTrack = trackController.getCurrentTrack();
            if (currentTrack != null) {
                // remove item from pending list in the track & update track in database
                currentTrack.getPendingItemIDs().remove(itemController.getCurrentItem().getId());
                currentTrack.getReturnedItemIDs().add(itemController.getCurrentItem().getId());
                trackController.saveChangesToDatabase(currentTrack);

                // reset active track id in item & update item in database
                itemController.getCurrentItem().setActiveTrackID(null);
                itemController.saveChangesToDatabase();

                Toast.makeText(getActivity(), "Item returned", Toast.LENGTH_SHORT).show();

                // reload fragment
                Intent intent = new Intent(getActivity(), ScanActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}