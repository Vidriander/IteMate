package iteMate.project.uiActivities.trackScreens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import iteMate.project.R;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.uiActivities.scanScreen.ReturnScanActivity;
import iteMate.project.uiActivities.adapter.InnerItemsAdapter;

public class TrackDetailActivity extends AppCompatActivity implements GenericRepository.OnDocumentsFetchedListener<Track> {

    private Track trackToDisplay;
    private InnerItemsAdapter horizontalAdapter;
    private List<Item> itemList;
    private final TrackController trackController = TrackController.getControllerInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_track_detail);

        // Get the track to display from the controller
        trackToDisplay = trackController.getCurrentTrack();
        if (trackToDisplay == null) {
           finish();
           return;
        }

        itemList = trackToDisplay.getLentItemsList();

        if (trackToDisplay != null) {
            setDetailViewContents();
        } else {
            finish();
            Log.e("TrackDetailActivity", "Track is null");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView  for horizontal list of items
        RecyclerView horizontalRecyclerView = findViewById(R.id.trackdetailview_lentitems_recyclerview);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        horizontalAdapter = new InnerItemsAdapter(itemList, this);
        horizontalRecyclerView.setAdapter(horizontalAdapter);

        // on click listener for back button
        findViewById(R.id.detailtrack_back_button).setOnClickListener(v -> finish());

        // on click listener for edit button
        findViewById(R.id.detailtrack_edit_button).setOnClickListener(v -> {
             Intent intent = new Intent(TrackDetailActivity.this, TrackEditActivity.class);
             trackController.setCurrentTrack(trackToDisplay);
             startActivity(intent);
        });

        // on click listener for scan button
        findViewById(R.id.detail_track_scan_button).setOnClickListener(v -> {
            Intent intent = new Intent(TrackDetailActivity.this, ReturnScanActivity.class);
            trackController.setCurrentTrack(trackToDisplay);
            startActivity(intent);
        });
    }

    /*
      * Sets the contents of the detail view
     */
    private void setDetailViewContents() {
        if (trackToDisplay != null) {
            // Setting the title of the detail card
            String titleText = trackToDisplay.getContact().getFirstName() + " " + trackToDisplay.getContact().getLastName();
            ((TextView)findViewById(R.id.track_detailcard_title)).setText(titleText);
            // Setting the itemcount of the detail card
            String itemCountText = String.valueOf(trackToDisplay.getNumberOfItems());
            itemCountText += (trackToDisplay.getNumberOfItems() == 1) ? " item" : " items";
            ((TextView)findViewById(R.id.track_detailcard_sideheader)).setText(itemCountText);
            // Setting the person to whom the item is given
            ((TextView)findViewById(R.id.lentToName_Text)).setText(titleText);
            // setting lent on date
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault());
            ((TextView) findViewById(R.id.lentOnDate_text)).setText(sdf.format(trackToDisplay.getGiveOutDate().toDate()));
            // setting return date
            ((TextView) findViewById(R.id.returnDate_Text)).setText(sdf.format(trackToDisplay.getReturnDate().toDate()));
            // Setting the additional info
            ((TextView)findViewById(R.id.trackdetailcard_itemdescription)).setText(trackToDisplay.getAdditionalInfo());

        } else {
            Log.e("TrackDetailActivity", "trackToDisplay is null in setDetailViewContents");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackToDisplay = trackController.getCurrentTrack();
        setDetailViewContents();
    }

    @Override
    public void onDocumentFetched(Track document) {
        trackToDisplay = document;
        itemList = document.getLentItemsList();
        Log.w("TrackDetailActivity", "LentItemList: " + itemList);
        horizontalAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDocumentsFetched(List<Track> documents) {
        Log.d("TrackDetailActivity", "onTracksFetched called with " + documents.size() + " tracks");
        itemList.clear();
        for (Track track : documents) {
            List<Item> lendList = track.getLentItemsList();
            Log.w("TrackDetailActivity", "LentItemList: " + lendList);
            if (lendList != null) {   // Check if lendList is not null
                itemList.addAll(lendList);
                Log.d("TrackDetailActivity", "Added " + lendList.size() + " items to itemList");
            }
        }
        horizontalAdapter.notifyDataSetChanged();
    }
}