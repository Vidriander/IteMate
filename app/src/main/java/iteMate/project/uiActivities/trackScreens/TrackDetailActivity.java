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

import java.util.ArrayList;
import java.util.List;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.uiActivities.utils.ContainedItemAdapter;

public class TrackDetailActivity extends AppCompatActivity {

    private Track trackToDisplay;

    private RecyclerView horizontalRecyclerView;
    private ContainedItemAdapter horizontalAdapter;

    private List<Item> itemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_track_detail);

        // Get the track to display from the intent:
        trackToDisplay = getIntent().getParcelableExtra("track");

        if (trackToDisplay == null) {
            Log.e("TrackDetailActivity", "trackToDisplay is null");
            finish(); // Close the activity if trackToDisplay is null
            return;
        }
        setDetailViewContents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Item list
        itemList = new ArrayList<>();
        itemList.add(new Item(111111, "Rose Backroad", "Description 1", "https://firebasestorage.googleapis.com/v0/b/itematedb-f0396.appspot.com/o/itemImages%2Fbikepacking.jpg?alt=media&token=824e1bc4-84cc-4f91-8a48-32358d47f91a", true, null));
        itemList.add(new Item(222222, "Nukeproof Digger", "Description 2", "https://firebasestorage.googleapis.com/v0/b/itematedb-f0396.appspot.com/o/itemImages%2Frose_bike.jpg?alt=media&token=860ef9d4-a586-470f-9e10-166efe1ba067", true, null));
        itemList.add(new Item(333333, "Cube Nuroad", "Description 3", "https://firebasestorage.googleapis.com/v0/b/itematedb-f0396.appspot.com/o/itemImages%2Fbikepacking.jpg?alt=media&token=824e1bc4-84cc-4f91-8a48-32358d47f91a", true, null));

        horizontalRecyclerView = findViewById(R.id.trackdetailview_lentitems_recyclerview);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<Item> emptyItemList = new ArrayList<>();
        horizontalAdapter = new ContainedItemAdapter(itemList, this);
        horizontalRecyclerView.setAdapter(horizontalAdapter);

        // on click listener for back button
        findViewById(R.id.detailtrack_back_button).setOnClickListener(v -> onBackPressed());

        // on click listener for edit button
        findViewById(R.id.detailtrack_edit_button).setOnClickListener(v -> {
             Intent intent = new Intent(TrackDetailActivity.this, TrackEditActivity.class);
             intent.putExtra("track", trackToDisplay);
             startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setDetailViewContents() {
        if (trackToDisplay != null) {
//            ((TextView)findViewById(R.id.track_detailcard_sideheader)).setText(trackToDisplay.getGiveOutDate().toString());
            ((TextView)findViewById(R.id.track_detailcard_title)).setText(trackToDisplay.getContact().getFirstName() + " " + trackToDisplay.getContact().getLastName());
        } else {
            Log.e("TrackDetailActivity", "trackToDisplay is null in setDetailViewContents");
        }
    }
}