package iteMate.project;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;


import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends MainActivity {

    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Item list
        itemList = new ArrayList<>();
        itemList.add(new Item("Item 1", "Description 1"));
        itemList.add(new Item("Item 2", "Description 2"));
        itemList.add(new Item("Item 3", "Description 3"));

        // Initialize Adapter and set to RecyclerView
        itemAdapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    void setLayoutResID() {
        layoutResID = R.layout.activity_items;
    }

    @Override
    void setBottomNavID() {
        bottomNavID = R.id.items;
    }
}
