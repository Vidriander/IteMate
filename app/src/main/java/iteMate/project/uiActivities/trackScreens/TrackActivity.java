package iteMate.project.uiActivities.trackScreens;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import iteMate.project.models.Contact;
import iteMate.project.models.Item;
import iteMate.project.R;
import iteMate.project.models.Track;
import iteMate.project.repositories.TrackRepository;
import iteMate.project.uiActivities.utils.TrackAdapter;
import iteMate.project.uiActivities.MainActivity;

public class TrackActivity extends MainActivity implements TrackRepository.OnTracksFetchedListener {

    private RecyclerView recyclerView;
    private TrackAdapter trackAdapter;
    private List<Track> trackList;
    private TrackRepository trackRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Test
        //Contact johnDoe = new Contact("John", "Doe", "12345", "john@doe.com", "Jumpstreet 21", "Endor");
        //Contact janeDoe = new Contact("Jane", "Doe", "54321", "jane@doe.com", "Jumpstreet 42", "Endor");
        //Contact luke = new Contact("Luke", "Skywalker", "12345", "luke@skywalker.com", "Jumpstreet 21", "Tatooine");
        //Item item1 = new Item(111111, "Rose Backroad", "Description 1", R.drawable.rose_bike, true, null);
        //Item item2 = new Item(222222, "Nukeproof Digger", "Description 2", R.drawable.bikepacking, true, null);
        //Item item3 = new Item(333333, "Cube Nuroad", "Description 3", R.drawable.rose2, true, null);
        //List<Item> itemList = new ArrayList<>();
        //itemList.add(item1);
        //itemList.add(item2);
        //itemList.add(item3);
        //Track track1 = new Track(new Date(), new Date(), johnDoe, itemList);
        //Track track2 = new Track(new Date(), new Date(), janeDoe, itemList);
        //Track track3 = new Track(new Date(), new Date(), luke, itemList);

        // Initialize Track list
        trackList = new ArrayList<>();

        // Initialize TrackRepository
        trackRepository = new TrackRepository();

        // Fetch tracks from Firestore
        trackRepository.getAllTracksFromFirestore(this);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTrack);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter and set to RecyclerView
        trackAdapter = new TrackAdapter(trackList, this);
        recyclerView.setAdapter(trackAdapter);
    }

    @Override
    public void setLayoutResID() {
        layoutResID = R.layout.activity_track;
    }

    @Override
    public void setBottomNavID() {
        bottomNavID = R.id.track;
    }

    @Override
    public void onTracksFetched(List<Track> tracks) {
        trackList.clear();
        trackList.addAll(tracks);
        trackAdapter.notifyDataSetChanged();
    }
}












