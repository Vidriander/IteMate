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
import iteMate.project.documentController.ItemController;
import iteMate.project.databaseManager.listeners.OnMultipleDocumentsFetchedListener;
import iteMate.project.utils.SearchUtils;
import iteMate.project.model.Item;
import iteMate.project.databaseManager.ItemRepository;  // TODO: remove
import iteMate.project.uiActivities.adapter.ManageInnerItemsAdapter;
import iteMate.project.utils.SortUtils;

/**
 * Activity for managing the inner items of an item
 */
public class ManageInnerItemsActivity extends AppCompatActivity implements OnMultipleDocumentsFetchedListener<Item> {

    private static Item itemToDisplay;

    private ManageInnerItemsAdapter adapter;

    private final ItemController itemController = ItemController.getControllerInstance();

    /**
     * List of Items that will change dynamically based on search
     */
    private List<Item> searchList;

    private boolean isContainedItems;

    private List<Item> allItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_inner_items);

        // Get the item to display from the controller:
        itemToDisplay = ItemController.getControllerInstance().getCurrentItem();
        isContainedItems = getIntent().getBooleanExtra("isContainedItems", true);

        // Initialize searchList
        searchList = new ArrayList<>(isContainedItems ? itemToDisplay.getContainedItems() : itemToDisplay.getAssociatedItems());

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.manage_inner_items_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch all items from database
        ItemRepository itemRepository = new ItemRepository();
        itemRepository.getAllDocumentsFromDatabase(this);

        // Setting up the adapter
        List<Item> itemsToDisplay = isContainedItems ? itemToDisplay.getContainedItems() : itemToDisplay.getAssociatedItems();
        adapter = new ManageInnerItemsAdapter(itemsToDisplay, allItems, this);
        recyclerView.setAdapter(adapter);

        // Setting on click listener for save button
        findViewById(R.id.manageInnerItemsSavebutton).setOnClickListener(click -> {
            saveChangesToItem();
            itemController.setCurrentItem(itemToDisplay);
            finish();
        });

        // Setting on click listener for cancel button
        findViewById(R.id.manageInnerItemsCancelbutton).setOnClickListener(click -> finish());

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

    /**
     * Save the changes to the item
     */
    private void saveChangesToItem() {
        List<Item> newCheckedItems = adapter.getNewCheckedItems();
        if (isContainedItems) {
            itemToDisplay.setContainedItems((ArrayList<Item>) newCheckedItems);
            itemToDisplay.setContainedItemIDs((ArrayList<String>) newCheckedItems.stream().map(Item::getId).collect(Collectors.toList()));
        } else {
            itemToDisplay.setAssociatedItems((ArrayList<Item>) newCheckedItems);
            itemToDisplay.setAssociatedItemIDs((ArrayList<String>) newCheckedItems.stream().map(Item::getId).collect(Collectors.toList()));
        }
    }

    @Override
    public void onDocumentsFetched(List<Item> documents) {
        allItems = SortUtils.sortItemsByName(documents);
        adapter.notifyItemsAvailable(allItems);
    }
}
