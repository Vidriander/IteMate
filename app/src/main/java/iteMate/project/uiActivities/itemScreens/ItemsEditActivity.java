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
import iteMate.project.uiActivities.utils.InnerItemsAdapter;

public class ItemsEditActivity extends AppCompatActivity {

    /**
     * The item to display in the activity.
     */
    private Item itemToDisplay;
    private RecyclerView containedItemsRecyclerView;
    private InnerItemsAdapter containedItemsAdapter;
    private RecyclerView associatedItemsRecyclerView;
    private InnerItemsAdapter associatedItemsAdapter;

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

        // Initialize RecyclerViews and Adapters
        containedItemsRecyclerView = findViewById(R.id.itemdetailview_containeditems_recyclerview);
        containedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        containedItemsAdapter = new InnerItemsAdapter(itemToDisplay.getContainedItems(), this, false);
        containedItemsRecyclerView.setAdapter(containedItemsAdapter);

        associatedItemsRecyclerView = findViewById(R.id.itemdetailview_associateditems_recyclerview);
        associatedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        associatedItemsAdapter = new InnerItemsAdapter(itemToDisplay.getAssociatedItems(), this, false);
        associatedItemsRecyclerView.setAdapter(associatedItemsAdapter);

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
    }

    private void setEditViewContents() {
        // Setting the image:
        GenericRepository.setImageForView(this, itemToDisplay.getImage(), findViewById(R.id.editItemMainImage));
        // Setting the name
        ((TextView) findViewById(R.id.itemEditItemname)).setText(itemToDisplay.getTitle());
        // Setting the NfcTag
        ((TextView) findViewById(R.id.item_edit_sideheader)).setText(String.valueOf(itemToDisplay.getNfcTag()));
        // Setting the description
        ((TextView) findViewById(R.id.itemeditcard_itemdescription)).setText(String.valueOf(itemToDisplay.getDescription()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEditViewContents();
        containedItemsAdapter.notifyDataSetChanged();
        associatedItemsAdapter.notifyDataSetChanged();
    }
}