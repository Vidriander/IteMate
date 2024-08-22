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
        itemList.add(new Item(111111, "Item 1", "Description 1", "image1.jpg", true, false));
        itemList.add(new Item(222222, "Item 2", "Description 2", "image2.jpg", true, false));
        itemList.add(new Item(333333, "Item 3", "Description 3", "image3.jpg", true, false));

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
