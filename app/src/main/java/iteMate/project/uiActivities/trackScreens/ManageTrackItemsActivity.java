package iteMate.project.uiActivities.trackScreens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.R;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.repositories.OnMultipleDocumentsFetchedListener;
import iteMate.project.uiActivities.adapter.ManageInnerItemsAdapter;
import iteMate.project.utils.SearchUtils;
import iteMate.project.utils.SortUtils;

public class ManageTrackItemsActivity extends AppCompatActivity implements OnMultipleDocumentsFetchedListener<Item> {
    /**
     * Adapter for the RecyclerView that displays the lent items
     */
    private ManageInnerItemsAdapter adapter;

    /**
     * List of Items that will change dynamically based on search
     */
    private List<Item> searchList;

    /**
     * List of all items available to lend
     */
    private List<Item> allItems = new ArrayList<>();

    private final TrackController trackController = TrackController.getControllerInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_inner_items);

        // Initialize RecyclerView and Adapter
        RecyclerView recyclerView = findViewById(R.id.manage_inner_items_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ManageInnerItemsAdapter(trackController.getCurrentTrack().getLentItemsList(), allItems, this);
        recyclerView.setAdapter(adapter);

        // Fetching all lendable items
        trackController.getLendableItemsList(this);

        // Setting on click listener for save button
        findViewById(R.id.manageInnerItemsSavebutton).setOnClickListener(click -> {
            List<Item> newCheckedItems = adapter.getNewCheckedItems();
            trackController.getCurrentTrack().setLentItemsList(newCheckedItems);
            for (Item item : newCheckedItems) {
                if (!trackController.getCurrentTrack().getReturnedItemsList().contains(item)) {
                    trackController.getCurrentTrack().getPendingItemsList().add(item);
                }
            }
            trackController.getCurrentTrack().setPendingItemsList(trackController.getCurrentTrack().getPendingItemsList());
            finish();
        });

        // Setting on click listener for cancel button
        findViewById(R.id.manageInnerItemsCancelbutton).setOnClickListener(click ->
            finish()
        );

        // Configure the SearchView
        SearchView searchView = findViewById(R.id.manageInnerItemsSearchView);
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
     * @param query The search query
     */
    private void performSearch(String query) {
        // reset the searchList to the itemList
        searchList.clear();
        searchList.addAll(allItems);

        // Perform the search and update the itemList
        List<Item> filteredList = SearchUtils.searchItems(searchList, query);
        searchList.clear();
        searchList.addAll(filteredList);
        adapter.setSearchList(SortUtils.sortItemsByName(searchList));
    }

    @Override
    public void onDocumentsFetched(List<Item> documents) {
        allItems = SortUtils.sortItemsByName(documents);
        adapter.notifyItemsAvailable(allItems);
    }
}
