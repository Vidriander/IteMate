package iteMate.project.uiActivities.itemScreens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.models.Item;
import iteMate.project.R;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.uiActivities.utils.ContainedItemAdapter;

public class ItemsDetailActivity extends AppCompatActivity  implements ItemRepository.OnItemsFetchedListener {

    private Item itemToDisplay;
    private RecyclerView horizontalRecyclerView;
    private ContainedItemAdapter horizontalAdapter;
    private RecyclerView associatedItemsRecyclerView;
    private ContainedItemAdapter horizontalAdapterAssociatedItems;
    private List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_items_detail);

        // Get the item to display from the intent:
        itemToDisplay = getIntent().getParcelableExtra("item");

        if (itemToDisplay == null) {
            Log.e("ItemsDetailActivity", "itemToDisplay is null");
            finish(); // Close the activity if itemToDisplay is null
            return;
        }
        setDetailViewContents();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initilize ItemRepository
        ItemRepository itemRepository = new ItemRepository();

        // Initialize Item list
        itemList = new ArrayList<>();
        itemList.add(new Item(111111, "Rose Backroad", "Description 1", "https://firebasestorage.googleapis.com/v0/b/itematedb-f0396.appspot.com/o/itemImages%2Fbikepacking.jpg?alt=media&token=824e1bc4-84cc-4f91-8a48-32358d47f91a", true, null));
        itemList.add(new Item(222222, "Nukeproof Digger", "Description 2", "https://firebasestorage.googleapis.com/v0/b/itematedb-f0396.appspot.com/o/itemImages%2Frose_bike.jpg?alt=media&token=860ef9d4-a586-470f-9e10-166efe1ba067", true, null));
        itemList.add(new Item(333333, "Cube Nuroad", "Description 3", "https://firebasestorage.googleapis.com/v0/b/itematedb-f0396.appspot.com/o/itemImages%2Fbikepacking.jpg?alt=media&token=824e1bc4-84cc-4f91-8a48-32358d47f91a", true, null));

        //  Glide is configured to load images from the given paths
        Glide.with(this)
                .load(itemToDisplay.getImagePath())
                .into((ImageView) findViewById(R.id.item_detailcard_image));


        horizontalRecyclerView = findViewById(R.id.itemdetailview_containeditems_recyclerview);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<Item> emptyItemList = new ArrayList<>();
        horizontalAdapter = new ContainedItemAdapter(itemList, this);
        horizontalRecyclerView.setAdapter(horizontalAdapter);

        associatedItemsRecyclerView = findViewById(R.id.itemdetailview_associateditems_recyclerview);
        associatedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
        horizontalAdapterAssociatedItems = new ContainedItemAdapter(itemList,this);
        associatedItemsRecyclerView.setAdapter(horizontalAdapterAssociatedItems);

        // on click listener for back button
        findViewById(R.id.detailitem_back_button).setOnClickListener(v -> onBackPressed());
        // on click listener for edit button
        findViewById(R.id.detailitem_edit_button).setOnClickListener(v -> {
            Intent intent = new Intent(ItemsDetailActivity.this, ItemsEditActivity.class);
            intent.putExtra("item", itemToDisplay);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setDetailViewContents() {
        if (itemToDisplay != null) {
            // Load image using Glide
            Glide.with(this)
                    .load(itemToDisplay.getImagePath())
                    .into((ImageView) findViewById(R.id.item_detailcard_image));
            ((TextView) findViewById(R.id.item_detailcard_title)).setText(itemToDisplay.getTitle());
            ((TextView) findViewById(R.id.item_detailcard_sideheader)).setText(String.valueOf(itemToDisplay.getNfcTag()));
        } else {
            Log.e("ItemsDetailActivity", "itemToDisplay is null in setDetailViewContents");
        }
    }

    @Override
    public void onItemsFetched(List<Item> items) {
        itemList.clear();
        itemList.addAll(items);
        horizontalAdapter.notifyDataSetChanged();
    }
}