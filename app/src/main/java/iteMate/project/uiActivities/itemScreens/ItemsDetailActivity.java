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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import iteMate.project.models.Item;
import iteMate.project.R;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.uiActivities.utils.ContainedItemAdapter;

public class ItemsDetailActivity extends AppCompatActivity{

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

        // Initialize RecyclerViews and Adapters
        horizontalRecyclerView = findViewById(R.id.itemdetailview_containeditems_recyclerview);
        horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        horizontalAdapter = new ContainedItemAdapter(itemToDisplay.getContainedItems(), this, false);
        horizontalRecyclerView.setAdapter(horizontalAdapter);

        associatedItemsRecyclerView = findViewById(R.id.itemdetailview_associateditems_recyclerview);
        associatedItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        horizontalAdapterAssociatedItems = new ContainedItemAdapter(itemToDisplay.getAssociatedItems(), this, false);
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
            // Get the StorageReference of the image
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference imageRef = storage.getReference().child(itemToDisplay.getImage());

            // Check if image exists in the local cache
            File localFile = new File(getCacheDir(), "images/" + itemToDisplay.getImage());
            File localDir = localFile.getParentFile();
            if (localDir != null && !localDir.exists()) {
                localDir.mkdirs();  // Create the directory if it doesn't exist
            }

            if (localFile.exists()) {
                // Load the image from the local file
                Glide.with(this)
                        .load(localFile)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .placeholder(R.drawable.placeholder_image)  // image in drawables
                        .error(R.drawable.error_image)  // image in drawables
                        .into((ImageView) findViewById(R.id.item_detailcard_image));
            } else {
                // Download the image from Firebase Storage and save it locally
                imageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    // Once downloaded, load the image from the local file
                    Glide.with(this)
                            .load(localFile)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .into((ImageView) findViewById(R.id.item_detailcard_image));
                    Log.d("ItemsDetailActivity", "Image downloaded and cached: " + localFile.getPath());
                }).addOnFailureListener(exception -> {
                    // Handle the error gracefully, e.g., show a placeholder image
                    Glide.with(this)
                            .load(R.drawable.error_image)
                            .into((ImageView) findViewById(R.id.item_detailcard_image));
                    Log.e("ItemsDetailActivity", "Error getting download URL", exception);
                });
            }

            ((TextView) findViewById(R.id.item_detailcard_title)).setText(itemToDisplay.getTitle());
            ((TextView) findViewById(R.id.item_detailcard_sideheader)).setText(String.valueOf(itemToDisplay.getNfcTag()));
            ((TextView) findViewById(R.id.itemdetailcard_itemdescription)).setText(String.valueOf(itemToDisplay.getDescription()));

        } else {
            Log.e("ItemsDetailActivity", "itemToDisplay is null in setDetailViewContents");
        }
    }
}