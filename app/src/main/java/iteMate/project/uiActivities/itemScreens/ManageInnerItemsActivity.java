package iteMate.project.uiActivities.itemScreens;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.uiActivities.utils.InnerItemsAdapter;
import iteMate.project.uiActivities.utils.ItemAdapter;
import iteMate.project.uiActivities.utils.ManageInnerItemsAdapter;

public class ManageInnerItemsActivity extends AppCompatActivity implements ItemRepository.OnItemsFetchedListener {

    private Item itemToDisplay;

    private RecyclerView recyclerView;
    private ManageInnerItemsAdapter adapter;

    private boolean isContainedItems;

    private List<Item> allItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_inner_items);

        // Get the item to display from the intent:
        itemToDisplay = getIntent().getParcelableExtra("item");
        isContainedItems = getIntent().getBooleanExtra("isContainedItems", true);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.manage_inner_items_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch all items from Firestore
        ItemRepository itemRepository = new ItemRepository();
        itemRepository.getAllItemsFromFirestore(this);

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
            }
            itemRepository.updateItemInFirestore(itemToDisplay);
            finish();
        });

    }

    @Override
    public void onItemsFetched(List<Item> items) {
        adapter.notifyItemsAvailable(items);
    }
}
