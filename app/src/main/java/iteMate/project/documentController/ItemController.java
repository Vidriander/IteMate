package iteMate.project.documentController;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import iteMate.project.R;
import iteMate.project.model.Item;
import iteMate.project.model.Track;
import iteMate.project.databaseManager.GenericRepository;
import iteMate.project.databaseManager.ItemRepository;
import iteMate.project.databaseManager.listeners.OnSingleDocumentFetchedListener;
import iteMate.project.databaseManager.TrackRepository;

/**
 * Controller for managing an item
 */
public class ItemController {

    // region Attributes
    /**
     * The current item that is being displayed or edited
     */
    private Item currentItem;

    /**
     * List of all items (that belong to the user)
     */
    private List<Item> currentItemList = new ArrayList<>();

    /**
     * Repository for managing items
     */
    private final ItemRepository itemRepository;

    /**
     * Repository for managing tracks
     */
    private final TrackRepository trackRepository;

    /**
     * Singleton instance of the ItemController
     */
    private static ItemController controllerInstance;
    // endregion

    // region Constructor and Singleton
    private ItemController() {
        itemRepository = new ItemRepository();
        trackRepository = new TrackRepository();
        refreshCurrentItemList();
    }

    /**
     * Returns the singleton instance of the ItemController
     *
     * @return the singleton instance of the ItemController
     */
    public static synchronized ItemController getControllerInstance() {
        if (controllerInstance == null) {
            controllerInstance = new ItemController();
        }
        return controllerInstance;
    }
    // endregion

    // region Getters and Setters

    /**
     * Getter for the current item
     *
     * @return the current item
     */
    public Item getCurrentItem() {
        return currentItem;
    }

    /**
     * Getter for the current item list
     *
     * @return the current item list
     */
    public List<Item> getCurrentItemList() {
        return currentItemList;
    }

    /**
     * Setter for the current item
     *
     * @param currentItem the item to set as the current item
     * @throws NullPointerException if the item is null
     */
    public void setCurrentItem(Item currentItem) throws NullPointerException {
        if (currentItem == null) {
            throw new NullPointerException("Item cannot be null");
        }
        this.currentItem = currentItem;
    }

    /**
     * Resets the current item to null
     */
    public void resetCurrentItem() {
        currentItem = null;
    }
    // endregion

    // region Item Database Management

    /**
     * Checks if the current item is ready for upload. Currently, an item is ready for upload if it has a title.
     *
     * @return true if the item is ready for upload, false otherwise
     */
    public boolean isReadyForUpload() {
        return
                currentItem.getTitle() != null && !currentItem.getTitle().isEmpty();
    }

    /**
     * Saves the changes to the current item to the database
     */
    public void saveChangesToDatabase() {
        if (Objects.equals(currentItem.getId(), null) || currentItem.getId().isEmpty()) {
            itemRepository.addDocumentToDatabase(currentItem);
        } else {
            itemRepository.updateDocumentInDatabase(currentItem);
        }
    }

    /**
     * Saves the changes to the current item to the database
     *
     * @param title       the new title of the item
     * @param description the new description of the item
     */
    public void saveChangesToItem(String title, String description) {
        currentItem.setTitle(title);
        currentItem.setDescription(description);
    }

    /**
     * Sets the image for the given ImageView
     *
     * @param context   the context of the activity
     * @param imagePath the path of the image
     * @param imageView the ImageView to set the image to
     */
    public void setImageForView(Context context, String imagePath, ImageView imageView) {
        GenericRepository<Item> repository = new GenericRepository<>(Item.class);
        repository.fetchImageUrl(imagePath, imageUrl -> {
            if (imageUrl != null) {
                Glide.with(context)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.error_image)
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load(R.drawable.error_image)
                        .into(imageView);
            }
        });
    }

    public void deleteImageFromStorage(String imagePath) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child(imagePath);
        imageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    currentItem.resetImageToDefault();
                    saveChangesToDatabase();
                })
                .addOnFailureListener(e -> {
                            Log.e("ItemController", "Error deleting image: " + e.getMessage());
                        }
                );
    }

    /**
     * Handles the image upload of the current item
     *
     * @param imageUri  the URI of the image to upload
     * @param context   the context of the activity
     * @param imageView the ImageView to set the image to
     */
    public void handleImageUpload(Uri imageUri, Context context, ImageView imageView) {
        if (imageUri != null) {
            String imagePath = "itemImages/" + imageUri.getLastPathSegment();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child(imagePath);
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        currentItem.setImage(imagePath);
                        setCurrentItem(currentItem);
                        saveChangesToDatabase();
                        this.setImageForView(context, currentItem.getImage(), imageView);
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    /**
     * Creates a new image file for the current item
     *
     * @param context the context of the activity
     * @return the new image file
     * @throws IOException if the file creation fails
     */
    public File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * Deletes the current item from the database
     */
    public void deleteItemFromDatabase() {
        itemRepository.deleteDocumentFromDatabase(currentItem);
    }

    /**
     * Fetches the tack corresponding to the current item
     *
     * @param trackListener listener that is notified when the track is ready
     */
    public void getTrackOfCurrentItem(OnSingleDocumentFetchedListener<Track> trackListener) {
        trackRepository.getOneDocumentFromDatabase(currentItem.getActiveTrackID(), trackListener);
    }

    /**
     * Fetches all items from the database
     *
     * @param listener listener that is notified when the items are ready
     */
    public void fetchItemByNfcTagId(String nfcTagId, OnSingleDocumentFetchedListener<Item> listener) {
        itemRepository.getItemByNfcTagFromDatabase(nfcTagId, listener);
    }

    /**
     * Fetches all items from database and sets the current item list
     */
    public void refreshCurrentItemList() {
        itemRepository.getAllDocumentsFromDatabase(documents -> currentItemList = documents);
    }
    // endregion
}
