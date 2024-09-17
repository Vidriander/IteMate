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

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.uiActivities.utils.InnerItemsAdapter;

public class ItemsEditActivity extends AppCompatActivity {

    /**
     * The item to display in the activity.
     */
    private static Item itemToDisplay;
    private RecyclerView containedItemsRecyclerView;
    private InnerItemsAdapter containedItemsAdapter;
    private RecyclerView associatedItemsRecyclerView;
    private InnerItemsAdapter associatedItemsAdapter;

    private TextView title;
    private TextView nfcTag;
    private TextView description;

    /**
     * Get the item possibly updated in the activity.
     * @return updated item, null if activity not initialized.
     */
    public static Item getItemToDisplay() {
        return itemToDisplay;
    }

    /**
     * Reset the stored item to null.
     */
    public static void resetItemToDisplay() {
        itemToDisplay = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_items_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the item to display from the intent:
        itemToDisplay = getIntent().getParcelableExtra("item");

        // Setting the contents of the edit view:
        setEditViewContents();

        // Setting up the recycler views
        containedItemsRecyclerView = findViewById(R.id.itemdetailview_containeditems_recyclerview);
        containedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        associatedItemsRecyclerView = findViewById(R.id.itemdetailview_associateditems_recyclerview);
        associatedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        setUpRecyclerAdapters();

        // Settting on click listener for managing contained items
        findViewById(R.id.manageContainedItemsButton).setOnClickListener(click -> {
            Intent intent = new Intent(this, ManageInnerItemsActivity.class);
            intent.putExtra("item", itemToDisplay);
            intent.putExtra("isContainedItems", true);
            startActivity(intent);
        });
        // Settting on click listener for managing associated items
        findViewById(R.id.manageAssociatedItemsButton).setOnClickListener(click -> {
            Intent intent = new Intent(this, ManageInnerItemsActivity.class);
            intent.putExtra("item", itemToDisplay);
            intent.putExtra("isContainedItems", false);
            startActivity(intent);
        });
        // Settting on click listener for save button
        findViewById(R.id.item_edit_save).setOnClickListener(click -> {
            saveChangesToItem();
            ItemRepository.updateItemInFirestore(itemToDisplay);
            finish();
        });
    }

    /**
     * Set up the recycler adapters for the contained and associated items.
     */
    private void setUpRecyclerAdapters() {
        containedItemsAdapter = new InnerItemsAdapter(itemToDisplay.getContainedItems(), this, false);
        containedItemsRecyclerView.setAdapter(containedItemsAdapter);

        associatedItemsAdapter = new InnerItemsAdapter(itemToDisplay.getAssociatedItems(), this, false);
        associatedItemsRecyclerView.setAdapter(associatedItemsAdapter);
    }

    /**
     * Set the contents of the edit view but the recycler views.
     */
    private void setEditViewContents() {
        // Setting the image:
        GenericRepository.setImageForView(this, itemToDisplay.getImage(), findViewById(R.id.editItemMainImage));
        // Setting the name
        title = findViewById(R.id.itemEditItemname);
        title.setText(itemToDisplay.getTitle());
        // Setting the NfcTag
        nfcTag = findViewById(R.id.item_edit_sideheader);
        nfcTag.setText(String.valueOf(itemToDisplay.getNfcTag()));
        // Setting the description
        description = findViewById(R.id.itemeditcard_itemdescription);
        description.setText(String.valueOf(itemToDisplay.getDescription()));
    }

    private void saveChangesToItem() {
        itemToDisplay.setTitle(title.getText().toString());
        itemToDisplay.setDescription(description.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemToDisplay = ManageInnerItemsActivity.getUpdatedItem() != null ? ManageInnerItemsActivity.getUpdatedItem(): itemToDisplay;
        setUpRecyclerAdapters();
    }
}