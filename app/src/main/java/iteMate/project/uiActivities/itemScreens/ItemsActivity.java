package iteMate.project.uiActivities.itemScreens;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import iteMate.project.utils.SearchUtils;
import iteMate.project.models.Item;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.uiActivities.adapter.ItemAdapter;
import iteMate.project.R;
import iteMate.project.uiActivities.MainActivity;
import iteMate.project.utils.SortUtils;

/**
 * Activity for managing the items
 */
public class ItemsActivity extends MainActivity implements GenericRepository.OnDocumentsFetchedListener<Item> {

    private ItemAdapter itemAdapter;
    /**
     * List of Items that will change dynamically based on search
     */
    private List<Item> searchList;
    /**
     * List of Items that will be used to reset to after a search
     */
    private List<Item> itemList;
    private ItemRepository itemRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ItemRepository
        itemRepository = new ItemRepository();

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Item list
        itemList = new ArrayList<>();
        searchList = new ArrayList<>(itemList);

        // Fetch all items from Firestore
        itemRepository.getAllDocumentsFromFirestore(this);

        // Initialize Adapter and set to RecyclerView
        itemAdapter = new ItemAdapter(searchList, this);
        recyclerView.setAdapter(itemAdapter);

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

        // Set add button functionality
        findViewById(R.id.add_button_items).setOnClickListener(v -> {
            Item newItem = new Item();
            Intent intent = new Intent(this, ItemsEditActivity.class);
            intent.putExtra("item", newItem);
            startActivity(intent);
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
        searchList.addAll(itemList);

        // Perform the search and update the itemList
        List<Item> filteredList = SearchUtils.searchItems(searchList, query);
        searchList.clear();
        searchList.addAll(SortUtils.defaultItemSort(filteredList));
        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void setLayoutResID() {
        layoutResID = R.layout.activity_items;
    }

    @Override
    public void setBottomNavID() {
        bottomNavID = R.id.items;
    }


    @Override
    public void onResume() {
        super.onResume();
        ItemsEditActivity.resetItemToDisplay();
        ManageInnerItemsActivity.resetUpdatedItem();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() ->
            itemRepository.getAllDocumentsFromFirestore(this)
        , 1, TimeUnit.SECONDS);
    }

    @Override
    public void onDocumentFetched(Item document) {
        // Not used
    }

    @Override
    public void onDocumentsFetched(List<Item> documents) {
        itemList.clear();
        itemList.addAll(documents);

        searchList.clear();
        searchList.addAll(SortUtils.defaultItemSort(itemList));

        itemAdapter.notifyDataSetChanged();
    }

}