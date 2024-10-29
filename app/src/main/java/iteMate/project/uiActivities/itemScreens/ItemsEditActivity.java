package iteMate.project.uiActivities.itemScreens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import iteMate.project.R;
import iteMate.project.controller.ItemController;
import iteMate.project.models.Item;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.uiActivities.adapter.InnerItemsAdapter;

public class ItemsEditActivity extends AppCompatActivity {

    /**
     * The item to display in the activity.
     */
    private static Item itemToDisplay;
    private static Item legacyItem;
    private RecyclerView containedItemsRecyclerView;
    private RecyclerView associatedItemsRecyclerView;

    private TextView title;
    private TextView description;

    private final ItemController itemController = ItemController.getControllerInstance();

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
        itemToDisplay = itemController.getCurrentItem();
        legacyItem = itemToDisplay.getDeepCopy();

        // Setting the contents of the edit view:
        setEditViewContents();

        // Setting up the recycler views
        containedItemsRecyclerView = findViewById(R.id.itemdetailview_containeditems_recyclerview);
        containedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        associatedItemsRecyclerView = findViewById(R.id.itemdetailview_associateditems_recyclerview);
        associatedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        setUpRecyclerAdapters();

        // Setting on click listener for managing contained items
        findViewById(R.id.manageContainedItemsButton).setOnClickListener(click -> {
            Intent intent = new Intent(this, ManageInnerItemsActivity.class);
            intent.putExtra("isContainedItems", true);
            startActivity(intent);
        });
        // Setting on click listener for managing associated items
        findViewById(R.id.manageAssociatedItemsButton).setOnClickListener(click -> {
            Intent intent = new Intent(this, ManageInnerItemsActivity.class);
            intent.putExtra("isContainedItems", false);
            startActivity(intent);
        });
        // Setting on click listener for save button
        findViewById(R.id.item_edit_save).setOnClickListener(click -> {
            saveChangesToItem();
            if (itemController.isReadyForUpload()) {
                itemController.saveChangesToDatabase();
                finish();
            } else {
                Toast toast = Toast.makeText(this, "Please add at least a name to your item.", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        // Setting on click listener for cancel button
        findViewById(R.id.item_edit_cancel).setOnClickListener(click -> {
            itemController.setCurrentItem(legacyItem);
            finish();
        });
        // Setting on click listener for delete button
        findViewById(R.id.item_edit_delete_btn).setOnClickListener(click -> {
            itemController.deleteItemFromDatabase();
            // Beende die nächste Activity
            Intent intent = new Intent(this, ItemsDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // setting on click listener for the delete image button
        findViewById(R.id.delete_image_card).setOnClickListener(click -> {
            itemToDisplay.resetImageToDefault();
            itemController.setCurrentItem(itemToDisplay);
            GenericRepository.setImageForView(this, itemToDisplay.getImage(), findViewById(R.id.editItemMainImage));
        });

        // setting on click listener for the update image button
        findViewById(R.id.upload_image_card).setOnClickListener(click -> {
            // TODO: Implement image upload
        });
    }

    /**
     * Set up the recycler adapters for the contained and associated items.
     */
    private void setUpRecyclerAdapters() {
        // contained Items
        InnerItemsAdapter containedItemsAdapter = new InnerItemsAdapter(itemToDisplay.getContainedItems(), this);
        containedItemsRecyclerView.setAdapter(containedItemsAdapter);
        // associated Items
        InnerItemsAdapter associatedItemsAdapter = new InnerItemsAdapter(itemToDisplay.getAssociatedItems(), this);
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
        // Setting the description
        description = findViewById(R.id.itemeditcard_itemdescription);
        description.setText(String.valueOf(itemToDisplay.getDescription()));

        // setting visibility of the delete button
        if (itemToDisplay.getId() != null) {
            findViewById(R.id.item_edit_delete_btn).setVisibility(android.view.View.VISIBLE);
        } else {
            findViewById(R.id.item_edit_delete_btn).setVisibility(android.view.View.GONE);
        }
    }

    private void saveChangesToItem() {
        itemToDisplay.setTitle(title.getText().toString());
        itemToDisplay.setDescription(description.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemToDisplay = itemController.getCurrentItem();
        setUpRecyclerAdapters();
    }
}