package iteMate.project.uiActivities.itemScreens;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import iteMate.project.controller.ItemController;
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
public class ItemsActivity extends MainActivity {

    private ItemAdapter itemAdapter;
    /**
     * List of Items that will change dynamically based on search
     */
    private List<Item> searchList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private final ItemController itemController = ItemController.getControllerInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Item list
        searchList = new ArrayList<>();

        // Initialize Adapter and set to RecyclerView
        itemAdapter = new ItemAdapter(searchList, this);
        recyclerView.setAdapter(itemAdapter);

        refreshItemList();

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

        // Set on refresh listen for pull to refresh
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutItems);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshItemList();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Set add button functionality
        findViewById(R.id.add_button_items).setOnClickListener(v -> {
            Item newItem = new Item();
            itemController.setCurrentItem(newItem);
            Intent intent = new Intent(this, ItemsEditActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Perform the search and update the itemList
     *
     * @param query The search query
     */
    private void performSearch(String query) {
        // Perform the search and update the itemList
        List<Item> filteredList = SearchUtils.searchItems(itemController.getCurrentItemList(), query);
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
        itemController.resetCurrentItem();
        itemController.refreshCurrentItemList();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(this::refreshItemList
        , 1, TimeUnit.SECONDS);
    }

    /**
     * Gets the current item list from the ItemController and refreshes the itemList sorted
     */
    private void refreshItemList() {

        searchList.clear();
        searchList.addAll(SortUtils.defaultItemSort(itemController.getCurrentItemList()));

        itemAdapter.notifyDataSetChanged();
    }

}