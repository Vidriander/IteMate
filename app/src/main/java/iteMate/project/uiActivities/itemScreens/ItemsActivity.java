package iteMate.project.uiActivities.itemScreens;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.models.Item;
import iteMate.project.uiActivities.utils.ItemAdapter;
import iteMate.project.R;
import iteMate.project.uiActivities.MainActivity;

public class ItemsActivity extends MainActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Item list
        itemList = new ArrayList<>();
        itemList.add(new Item(111111, "Rose Backroad", "Description 1", R.drawable.rose_bike, true, false));
        itemList.add(new Item(222222, "Nukeproof Digger", "Description 2", R.drawable.bikepacking, true, false));
        itemList.add(new Item(333333, "Cube Nuroad", "Description 3", R.drawable.rose2, true, false));

        // Initialize Adapter and set to RecyclerView
        itemAdapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    public void setLayoutResID() {
        layoutResID = R.layout.activity_items;
    }

    @Override
    public void setBottomNavID() {
        bottomNavID = R.id.items;
    }
}
