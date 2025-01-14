package iteMate.project.uiActivities.itemScreens;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;

import iteMate.project.R;
import iteMate.project.documentController.ItemController;
import iteMate.project.model.Item;
import iteMate.project.uiActivities.adapter.InnerItemsAdapter;

/**
 * Activity for editing an item.
 */
public class ItemsEditActivity extends AppCompatActivity {

    private final ItemController itemController = ItemController.getControllerInstance();
    private static Item itemToDisplay;  //TODO @dave check if this can be replaced by itemController.getCurrentItem()
    private static Item legacyItem;
    private RecyclerView containedItemsRecyclerView;
    private RecyclerView associatedItemsRecyclerView;

    private Uri photoURI;
    private ActivityResultLauncher<Intent> captureImageLauncher;
    private ActivityResultLauncher<Intent> pickPhotoLauncher;

    private TextView title;
    private TextView description;

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

        // Request for camera permission
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
        }

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

        // Setting up the image upload
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        handleImageUpload(photoURI);
                    }
                }
        );

        // Setting up the image picker
        pickPhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        handleImageUpload(selectedImage);
                    }
                }
        );

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
            itemController.saveChangesToItem(title.getText().toString(), description.getText().toString());
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
            Intent intent = new Intent(this, ItemsDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // setting on click listener for the update image button
        findViewById(R.id.upload_image_card).setOnClickListener(click -> showImagePickerDialog());
    }

    /**
     * Shows a dialog to choose between taking a photo or selecting one from the gallery
     */
    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Delete Image"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        dispatchTakePictureIntent();
                    } else if (which == 1) {
                        dispatchPickPictureIntent();
                    } else if (which == 2) {
                        deleteImage();
                    }
                })
                .show();
    }

    /**
     * Dispatches an intent to take a picture
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = itemController.createImageFile(this);
            } catch (IOException ex) {
                Log.e("ItemsEditActivity", "Error occurred while creating the file", ex);
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this, "iteMate.project.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                captureImageLauncher.launch(takePictureIntent);
            }
        }
    }

    /**
     * Dispatches an intent to pick a picture from the gallery
     */
    private void dispatchPickPictureIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhotoLauncher.launch(pickPhoto);
    }

    private void deleteImage() {
        // TODO default image not shown after deletion
        itemController.setCurrentItem(itemToDisplay);
        itemController.deleteImageFromStorage(itemToDisplay.getImage());
        itemController.setImageForView(this, itemToDisplay.getImage(), findViewById(R.id.editItemMainImage));
    }

    /**
     * Handles the image upload
     *
     * @param imageUri the URI of the image to upload
     */
    private void handleImageUpload(Uri imageUri) {
        itemController.handleImageUpload(imageUri, this, findViewById(R.id.editItemMainImage));
    }

    /**
     * Sets up the recycler adapters for the contained and associated items
     */
    private void setUpRecyclerAdapters() {
        InnerItemsAdapter containedItemsAdapter = new InnerItemsAdapter(itemToDisplay.getContainedItems(), this);
        containedItemsRecyclerView.setAdapter(containedItemsAdapter);
        InnerItemsAdapter associatedItemsAdapter = new InnerItemsAdapter(itemToDisplay.getAssociatedItems(), this);
        associatedItemsRecyclerView.setAdapter(associatedItemsAdapter);
    }

    /**
     * Sets the contents of the edit view
     */
    private void setEditViewContents() {
        // Setting the image
        itemController.setImageForView(this, itemToDisplay.getImage(), findViewById(R.id.editItemMainImage));
        // Setting the title and description
        title = findViewById(R.id.itemEditItemname);
        title.setText(itemToDisplay.getTitle());
        // Setting the description
        description = findViewById(R.id.itemeditcard_itemdescription);
        description.setText(String.valueOf(itemToDisplay.getDescription()));
        // Setting the delete button
        if (itemToDisplay.getId() != null) {
            findViewById(R.id.item_edit_delete_btn).setVisibility(android.view.View.VISIBLE);
        } else {
            findViewById(R.id.item_edit_delete_btn).setVisibility(android.view.View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        itemToDisplay = itemController.getCurrentItem();
        setUpRecyclerAdapters();
    }
}