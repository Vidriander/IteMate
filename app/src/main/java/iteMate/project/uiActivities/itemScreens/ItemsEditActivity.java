package iteMate.project.uiActivities.itemScreens;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private Uri photoURI;
    private ActivityResultLauncher<Intent> captureImageLauncher;


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
            showImagePickerDialog();
        });
    }

    /**
     * Show a dialog to choose between taking a photo or picking one from the gallery.
     */
    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        dispatchTakePictureIntent();
                    } else if (which == 1) {
                        dispatchPickPictureIntent();
                    }
                })
                .show();
    }

    /**
     * Dispatch an intent to take a picture.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
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
     * Create a file to store the image.
     *
     * @return The created file.
     * @throws IOException If an error occurs while creating the file.
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * Dispatch an intent to pick a picture from the gallery.
     */
    private void dispatchPickPictureIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Handle the photo taken
                handleImageUpload(photoURI);
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                // Handle the image picked from gallery
                Uri selectedImage = data.getData();
                handleImageUpload(selectedImage);
            }
        }
    }

    /**
     * Handle the image upload to Firebase Storage.
     *
     * @param imageUri The URI of the image to upload.
     */
    private void handleImageUpload(Uri imageUri) {
        if (imageUri != null) {
            String imagePath = "itemImages/" + imageUri.getLastPathSegment();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child(imagePath);
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Save the path in Firestore
                        itemToDisplay.setImage(imagePath);
                        itemController.setCurrentItem(itemToDisplay);
                        itemController.saveChangesToDatabase();
                        GenericRepository.setImageForView(this, itemToDisplay.getImage(), findViewById(R.id.editItemMainImage));
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
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

    /**
     * Save the changes to the item.
     */
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