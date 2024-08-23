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
import iteMate.project.uiActivities.utils.TrackAdapter;
import iteMate.project.uiActivities.MainActivity;

public class TrackActivity extends MainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewTrack);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Item list
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item(111111, "Rose Backroad", "Description 1", R.drawable.rose_bike, true, false));
        itemList.add(new Item(222222, "Nukeproof Digger", "Description 2", R.drawable.bikepacking, true, false));
        itemList.add(new Item(333333, "Cube Nuroad", "Description 3", R.drawable.rose2, true, false));

        //Initialize Contact
        Contact JohnDoe = new Contact("John", "Doe", "1234567890", "john@doe.com", "Jumpstreet 21", "Endor");

        // Initialize Track list
        List<Track> trackList = new ArrayList<>();
        trackList.add(new Track(new Date(), new Date(), JohnDoe, itemList));
        trackList.add(new Track(new Date(), new Date(), JohnDoe, itemList));
        trackList.add(new Track(new Date(), new Date(), JohnDoe, itemList));

        // Initialize Adapter and set to RecyclerView
        TrackAdapter trackAdapter = new TrackAdapter(trackList, this);
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









