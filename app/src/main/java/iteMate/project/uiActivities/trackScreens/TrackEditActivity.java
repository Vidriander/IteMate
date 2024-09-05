package iteMate.project.uiActivities.trackScreens;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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
import iteMate.project.repositories.TrackRepository;
import iteMate.project.uiActivities.utils.ContainedItemAdapter;
import iteMate.project.uiActivities.utils.TrackAdapter;

public class TrackEditActivity extends AppCompatActivity{

    private Track trackToDisplay;
    private RecyclerView horizontalRecyclerView;
    private ContainedItemAdapter horizontalAdapter;
    private List<Item> itemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_track_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Get the Track object from the intent
        trackToDisplay = getIntent().getParcelableExtra("track");


        // Initialize Item list
        itemList = new ArrayList<>();
        itemList.add(new Item());
        itemList.add(new Item(111111, "Rose Backroad", "Description 1", "https://firebasestorage.googleapis.com/v0/b/itematedb-f0396.appspot.com/o/itemImages%2Fbikepacking.jpg?alt=media&token=824e1bc4-84cc-4f91-8a48-32358d47f91a", true, null));
        itemList.add(new Item(222222, "Nukeproof Digger", "Description 2", "https://firebasestorage.googleapis.com/v0/b/itematedb-f0396.appspot.com/o/itemImages%2Frose_bike.jpg?alt=media&token=860ef9d4-a586-470f-9e10-166efe1ba067", true, null));
        itemList.add(new Item(333333, "Cube Nuroad", "Description 3", "https://firebasestorage.googleapis.com/v0/b/itematedb-f0396.appspot.com/o/itemImages%2Fbikepacking.jpg?alt=media&token=824e1bc4-84cc-4f91-8a48-32358d47f91a", true, null));

        horizontalRecyclerView = findViewById(R.id.trackedit_recycler);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        horizontalAdapter = new ContainedItemAdapter(itemList, this, true);
        horizontalRecyclerView.setAdapter(horizontalAdapter);
    }

    private void setDetailViewContents() {
        if (trackToDisplay != null) {
            ((TextView) findViewById(R.id.trackedit_title)).setText(trackToDisplay.getContact().getFirstName() + trackToDisplay.getContact().getLastName());
        } else {
            Log.e("ItemsDetailActivity", "itemToDisplay is null in setDetailViewContents");
        }
    }
}