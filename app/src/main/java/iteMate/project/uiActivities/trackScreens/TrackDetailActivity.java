package iteMate.project.uiActivities.trackScreens;

import android.content.Intent;
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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.repositories.TrackRepository;
import iteMate.project.uiActivities.utils.ContainedItemAdapter;

public class TrackDetailActivity extends AppCompatActivity implements TrackRepository.OnTracksFetchedListener, ItemRepository.OnItemsFetchedListener {

    private Track trackToDisplay;
    private RecyclerView horizontalRecyclerView;
    private ContainedItemAdapter horizontalAdapter;
    private List<Item> itemList;
    private TrackRepository trackRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_track_detail);

        // Get the track to display from the intent:
        trackToDisplay = getIntent().getParcelableExtra("track");

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

        // Initialize ItemRepository
        ItemRepository itemRepository = new ItemRepository();

        // Initialize Item list
        itemList = new ArrayList<>();

         // Fetch tracks from Firestore
        itemRepository.getAllItemsFromFirestore(this);

        // Initialize RecyclerView  for horizontal list of items
        horizontalRecyclerView = findViewById(R.id.trackdetailview_lentitems_recyclerview);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        horizontalAdapter = new ContainedItemAdapter(itemList, this,false);
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

    /*
      * Sets the contents of the detail view
     */
    private void setDetailViewContents() {
        if (trackToDisplay != null) {
//            ((TextView)findViewById(R.id.track_detailcard_sideheader)).setText(trackToDisplay.getGiveOutDate().toString());
            ((TextView)findViewById(R.id.track_detailcard_title)).setText(trackToDisplay.getContact().getFirstName() + " " + trackToDisplay.getContact().getLastName());
        } else {
            Log.e("TrackDetailActivity", "trackToDisplay is null in setDetailViewContents");
        }
    }

    /**
     * Called when the tracks are fetched from Firestore
     * @param tracks the list of tracks fetched
     */
    @Override
    public void onTracksFetched(List<Track> tracks) {
        Log.d("TrackDetailActivity", "onTracksFetched called with " + tracks.size() + " tracks");
        itemList.clear();
        for (Track track : tracks) {
            List<Item> lendList = track.getLendList();
            if (lendList != null) {   // Check if lendList is not null
                itemList.addAll(lendList);
                Log.d("TrackDetailActivity", "Added " + lendList.size() + " items to itemList");
            }
        }
        horizontalAdapter.notifyDataSetChanged();
    }



    @Override
    public void onItemsFetched(List<Item> items) {
        Log.d("TrackDetailActivity", "Fetched " + items.size() + " items");
        itemList.clear();
        itemList.addAll(items);
        horizontalAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTrackFetched(Track track) {
        // Handle the fetched track here
        // For example, you can update the UI with the track details
        trackToDisplay = track;
        setDetailViewContents();
    }
}