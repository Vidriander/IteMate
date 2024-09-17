package iteMate.project.uiActivities.itemScreens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import iteMate.project.models.Item;
import iteMate.project.R;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.uiActivities.utils.InnerItemsAdapter;

public class ItemsDetailActivity extends AppCompatActivity {

    private Item itemToDisplay;
    private RecyclerView containedItemsRecyclerView;
    private InnerItemsAdapter containedItemsAdapter;
    private RecyclerView associatedItemsRecyclerView;
    private InnerItemsAdapter associatedItemsAdapter;

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
        itemToDisplay = getIntent().getParcelableExtra("item");

        setDetailViewContents();

        // Initialize RecyclerViews and Adapters
        containedItemsRecyclerView = findViewById(R.id.itemdetailview_containeditems_recyclerview);
        containedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        associatedItemsRecyclerView = findViewById(R.id.itemdetailview_associateditems_recyclerview);
        associatedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        setUpRecyclerAdapters();

        // on click listener for back button
        findViewById(R.id.detailitem_back_button).setOnClickListener(v -> onBackPressed());

        // on click listener for edit button
        findViewById(R.id.detailitem_edit_button).setOnClickListener(v -> {
            Intent intent = new Intent(ItemsDetailActivity.this, ItemsEditActivity.class);
            intent.putExtra("item", itemToDisplay);
            startActivity(intent);
        });
    }

    /**
     * Set up the recycler adapters for the contained and associated items
     */
    private void setUpRecyclerAdapters() {
        containedItemsAdapter = new InnerItemsAdapter(itemToDisplay.getContainedItems(), this, false);
        containedItemsRecyclerView.setAdapter(containedItemsAdapter);
        associatedItemsAdapter = new InnerItemsAdapter(itemToDisplay.getAssociatedItems(), this, false);
        associatedItemsRecyclerView.setAdapter(associatedItemsAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setDetailViewContents() {
        if (itemToDisplay != null) {
            GenericRepository.setImageForView(this, itemToDisplay.getImage(), findViewById(R.id.item_detailcard_image));
            ((TextView) findViewById(R.id.item_detailcard_title)).setText(itemToDisplay.getTitle());
            ((TextView) findViewById(R.id.item_detailcard_sideheader)).setText(String.valueOf(itemToDisplay.getNfcTag()));
            ((TextView) findViewById(R.id.itemdetailcard_itemdescription)).setText(String.valueOf(itemToDisplay.getDescription()));
        } else {
            Log.e("ItemsDetailActivity", "itemToDisplay is null in setDetailViewContents");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemToDisplay = ItemsEditActivity.getItemToDisplay() != null ? ItemsEditActivity.getItemToDisplay() : itemToDisplay;
        setDetailViewContents();
        setUpRecyclerAdapters();
    }
}