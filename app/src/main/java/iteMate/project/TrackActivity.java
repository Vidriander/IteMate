package iteMate.project;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackActivity extends MainActivity {

    private RecyclerView recyclerView;
    private TrackAdapter trackAdapter;
    private List<Track> trackList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Item list
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item(111111, "Rose Backroad", "Description 1", R.drawable.rose_bike, true, false));
        itemList.add(new Item(222222, "Nukeproof Digger", "Description 2", R.drawable.bikepacking, true, false));
        itemList.add(new Item(333333, "Cube Nuroad", "Description 3", R.drawable.rose2, true, false));

        //Initialize Contact
        Contact JohnDoe = new Contact("John", "Doe", "1234567890", "john@doe.com", "Jumpstreet 21", "Endor");

        // Initialize Track list
        trackList = new ArrayList<>();
        trackList.add(new Track(new Date(), new Date(), JohnDoe, itemList));
        trackList.add(new Track(new Date(), new Date(), JohnDoe, itemList));
        trackList.add(new Track(new Date(), new Date(), JohnDoe, itemList));

        // Initialize Adapter and set to RecyclerView
        trackAdapter = new TrackAdapter(trackList, this);
        recyclerView.setAdapter(trackAdapter);
    }

    @Override
    void setLayoutResID() {
        layoutResID = R.layout.activity_track;
    }

    @Override
    void setBottomNavID() {
        bottomNavID = R.id.track;
    }
}









