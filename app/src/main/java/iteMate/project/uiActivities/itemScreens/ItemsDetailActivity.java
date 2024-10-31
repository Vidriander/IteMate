package iteMate.project.uiActivities.itemScreens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import iteMate.project.controller.ItemController;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.R;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.uiActivities.trackScreens.TrackDetailActivity;
import iteMate.project.uiActivities.adapter.InnerItemsAdapter;

public class ItemsDetailActivity extends AppCompatActivity {

    private Item itemToDisplay;
    private RecyclerView containedItemsRecyclerView;
    private RecyclerView associatedItemsRecyclerView;
    private final TrackController trackController = TrackController.getControllerInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_items_detail);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the item to display from the intent:
        itemToDisplay = ItemController.getControllerInstance().getCurrentItem();
        if (getIntent().getFlags() == Intent.FLAG_ACTIVITY_CLEAR_TOP) {
            finish();
        }

        if (itemToDisplay != null) {
            // Set the view contents
            setDetailViewContents();

            // Initialize RecyclerViews and Adapters
            containedItemsRecyclerView = findViewById(R.id.itemdetailview_containeditems_recyclerview);
            containedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            associatedItemsRecyclerView = findViewById(R.id.itemdetailview_associateditems_recyclerview);
            associatedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            setUpRecyclerAdapters();

            // on click listener for back button
            findViewById(R.id.detailitem_back_button).setOnClickListener(v -> finish());

            // on click listener for edit button
            findViewById(R.id.detailitem_edit_button).setOnClickListener(v -> {
                Intent intent = new Intent(ItemsDetailActivity.this, ItemsEditActivity.class);
                startActivity(intent);
            });
        }
    }

    /**
     * Set up the recycler adapters for the contained and associated items
     */
    private void setUpRecyclerAdapters() {
        InnerItemsAdapter containedItemsAdapter = new InnerItemsAdapter(itemToDisplay.getContainedItems(), this);
        containedItemsRecyclerView.setAdapter(containedItemsAdapter);
        InnerItemsAdapter associatedItemsAdapter = new InnerItemsAdapter(itemToDisplay.getAssociatedItems(), this);
        associatedItemsRecyclerView.setAdapter(associatedItemsAdapter);
    }

    private void setDetailViewContents() {

        // Setting the image
        GenericRepository.setImageForView(this, itemToDisplay.getImage(), findViewById(R.id.item_detailcard_image));
        // Setting the title
        ((TextView) findViewById(R.id.item_detailcard_title)).setText(itemToDisplay.getTitle());
        // Setting the description
        ((TextView) findViewById(R.id.itemdetailcard_itemdescription)).setText(String.valueOf(itemToDisplay.getDescription()));
        // Setting availability
        String availability;
        int color;
        TextView availabilityTextView = findViewById(R.id.available_text);
        // if item is in a track
        if (itemToDisplay.getActiveTrackID() != null && !itemToDisplay.getActiveTrackID().isEmpty()) {
            availability = "track";
            color = getResources().getColor(R.color.unavailable_red, null);
            // on click listener if the item is in a track
            availabilityTextView.setOnClickListener(v -> {
                ItemController.getControllerInstance().getTrackOfCurrentItem(document -> {
                    Intent intent = new Intent(ItemsDetailActivity.this, TrackDetailActivity.class);
                    trackController.setCurrentTrack(document);
                    startActivity(intent);
                });
            });
        // if item is available
        } else {
            availability = "lend";
            color = getResources().getColor(R.color.available_green, null);
        }
        availabilityTextView.setText(availability);
        availabilityTextView.setTextColor(color);
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemToDisplay = ItemController.getControllerInstance().getCurrentItem();
        setDetailViewContents();
        setUpRecyclerAdapters();
    }
}