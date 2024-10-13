package iteMate.project.uiActivities.itemScreens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import iteMate.project.R;
import iteMate.project.controller.ItemController;
import iteMate.project.utils.SearchUtils;
import iteMate.project.models.Item;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.uiActivities.adapter.ManageInnerItemsAdapter;
import iteMate.project.utils.SortUtils;

public class ManageInnerItemsActivity extends AppCompatActivity implements GenericRepository.OnDocumentsFetchedListener<Item> {

    private static Item itemToDisplay;

    private ManageInnerItemsAdapter adapter;
    /**
     * List of Items that will change dynamically based on search
     */
    private List<Item> searchList;

    /**
     * Get the updated item.
     * @return The updated item, null if not initialized.
     */
    public static Item getUpdatedItem() {
        return itemToDisplay;
    }
    public static void resetUpdatedItem() {
        itemToDisplay = null;
    }

    private boolean isContainedItems;

    private List<Item> allItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_inner_items);

        // Get the item to display from the intent:
        itemToDisplay = ItemController.getControllerInstance().getCurrentItem();
        isContainedItems = getIntent().getBooleanExtra("isContainedItems", true);

        // Initialize searchList
        searchList = new ArrayList<>(isContainedItems ? itemToDisplay.getContainedItems() : itemToDisplay.getAssociatedItems());

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.manage_inner_items_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch all items from Firestore
        ItemRepository itemRepository = new ItemRepository();
        itemRepository.getAllDocumentsFromFirestore(this);

        // Setting up the adapter
        List<Item> itemsToDisplay = isContainedItems ? itemToDisplay.getContainedItems() : itemToDisplay.getAssociatedItems();
        adapter = new ManageInnerItemsAdapter(itemsToDisplay, allItems, this);
        recyclerView.setAdapter(adapter);

        // Setting on click listener for save button
        findViewById(R.id.manageInnerItemsSavebutton).setOnClickListener(click -> {
            List<Item> newCheckedItems = adapter.getNewCheckedItems();
            if (isContainedItems) {
                itemToDisplay.setContainedItems((ArrayList<Item>) newCheckedItems);
                itemToDisplay.setContainedItemIDs((ArrayList<String>) newCheckedItems.stream().map(Item::getId).collect(Collectors.toList()));
            } else {
                itemToDisplay.setAssociatedItems((ArrayList<Item>) newCheckedItems);
                itemToDisplay.setAssociatedItemIDs((ArrayList<String>) newCheckedItems.stream().map(Item::getId).collect(Collectors.toList()));
            }
            finish();
        });

        // Setting on click listener for cancel button
        findViewById(R.id.manageInnerItemsCancelbutton).setOnClickListener(click -> {
            resetUpdatedItem();
            finish();
        });

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
    public void onDocumentFetched(Item document) {
        // Not used
    }

    @Override
    public void onDocumentsFetched(List<Item> documents) {
        allItems = SortUtils.sortItemsByName(documents);
        adapter.notifyItemsAvailable(allItems);
    }
}
