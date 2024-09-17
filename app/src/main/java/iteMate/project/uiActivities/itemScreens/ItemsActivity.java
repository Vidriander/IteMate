package iteMate.project.uiActivities.itemScreens;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import iteMate.project.SearchUtils;
import iteMate.project.models.Item;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.uiActivities.utils.ItemAdapter;
import iteMate.project.R;
import iteMate.project.uiActivities.MainActivity;

public class ItemsActivity extends MainActivity implements ItemRepository.OnItemsFetchedListener {

    private RecyclerView recyclerView;
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
        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Item list
        itemList = new ArrayList<>();
        searchList = new ArrayList<>(itemList);

        fetchItems();

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
    }

    /**
     * Perform the search and update the itemList
     * @param query The search query
     */
    private void performSearch(String query) {
        // reset the searchList to the itemList
        searchList.clear();
        searchList.addAll(itemList);

        // Perform the search and update the itemList
        List<Item> filteredList = SearchUtils.searchItems(searchList, query);
        searchList.clear();
        searchList.addAll(filteredList);
        itemAdapter.notifyDataSetChanged();
    }

    // Fetch items from Firestore
    private void fetchItems() {
        Log.d("ItemsActivity", "Fetching items from Firestore");
        itemRepository.getAllItemsFromFirestore(this);
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
    public void onItemsFetched(List<Item> items) {
        itemList.clear();
        itemList.addAll(items);

        searchList.clear();
        searchList.addAll(itemList);

        itemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        ItemsEditActivity.resetItemToDisplay();
        ManageInnerItemsActivity.resetUpdatedItem();
        fetchItems();
    }
}