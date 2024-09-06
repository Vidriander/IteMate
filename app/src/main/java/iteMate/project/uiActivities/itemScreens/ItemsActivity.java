package iteMate.project.uiActivities.itemScreens;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import iteMate.project.models.Item;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.uiActivities.utils.ItemAdapter;
import iteMate.project.R;
import iteMate.project.uiActivities.MainActivity;

public class ItemsActivity extends MainActivity implements ItemRepository.OnItemsFetchedListener {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
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

        // Initialize Adapter and set to RecyclerView
        itemAdapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(itemAdapter);

        fetchItems();
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
        itemAdapter.notifyDataSetChanged();
    }
}