package iteMate.project.uiActivities.trackScreens;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.controller.TrackController;
import iteMate.project.uiActivities.utils.SearchUtils;
import iteMate.project.R;
import iteMate.project.models.Track;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.repositories.TrackRepository;
import iteMate.project.uiActivities.utils.SortUtils;
import iteMate.project.uiActivities.utils.TrackAdapter;
import iteMate.project.uiActivities.MainActivity;

public class TrackActivity extends MainActivity implements GenericRepository.OnDocumentsFetchedListener<Track> {

    private RecyclerView recyclerView;
    private TrackAdapter trackAdapter;
    private List<Track> trackList;
    private List<Track> searchList;
    private TrackRepository trackRepository;
    private final TrackController trackController = TrackController.getControllerInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Track list
        trackList = new ArrayList<>();
        trackList = new ArrayList<>();
        searchList = new ArrayList<>(trackList);
        searchList = SortUtils.defaultTrackSort(searchList);

        // Initialize TrackRepository
        trackRepository = new TrackRepository();

        // Fetch tracks from Firestore
        trackRepository.getAllDocumentsFromFirestore(this);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewTrack);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Adapter and set to RecyclerView
        trackAdapter = new TrackAdapter(searchList, this);
        recyclerView.setAdapter(trackAdapter);

        // set up the add button
        findViewById(R.id.add_button_track).setOnClickListener(v -> {
            Track newTrack = new Track();
            Intent intent = new Intent(this, TrackEditActivity.class);
            trackController.setCurrentTrack(newTrack);
            startActivity(intent);
        });

        // Configure the SearchView
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String query) {
                performSearch(query);
                return true;
            }
        });
    }

    /**
     * Perform the search and update the itemList
     *
     * @param query The search query
     */
    private void performSearch(String query) {
        // reset the searchList to the itemList
        searchList.clear();
        searchList.addAll(trackList);

        // Perform the search and update the itemList
        List<Track> filteredList = SearchUtils.searchTracks(searchList, query);
        searchList.clear();
        searchList.addAll(SortUtils.defaultTrackSort(filteredList));
        trackAdapter.notifyDataSetChanged();
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
    public void onDocumentFetched(Track document) {
        // Find the index of the updated track
        int index = trackList.indexOf(document);
        if (index != -1) {
            // Update the specific item in the adapter
            trackAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void onDocumentsFetched(List<Track> documents) {
        trackList.clear();
        trackList.addAll(SortUtils.defaultTrackSort(documents));
        searchList.clear();
        searchList.addAll(SortUtils.defaultTrackSort(documents));
        trackAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        trackRepository.getAllDocumentsFromFirestore(this);
    }


}












