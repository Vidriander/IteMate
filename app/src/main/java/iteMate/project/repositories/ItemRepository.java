package iteMate.project.repositories;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MemoryCacheSettings;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;

import iteMate.project.models.Item;

/**
 * Repository class for items
 * This class is responsible for handling all interactions with Firestore
 * It contains methods for adding, fetching, deleting and updating items
 */
public class ItemRepository {
    private FirebaseFirestore db;

    // Default constructor
    public ItemRepository() {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        // Enable offline persistence
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setLocalCacheSettings(
//                        PersistentCacheSettings.newBuilder().build()  // Enables persistent disk storage
//                )
//                .build();
//        db.setFirestoreSettings(settings);

    }

    // Constructor to allow injecting Firestore instance for testing
    public ItemRepository(FirebaseFirestore firestore) {
        this.db = firestore;
    }

    // Setter that accepts a Firestore instance for testing
    public void setDb(FirebaseFirestore firestore) {
        this.db = firestore;
    }

    /**
     * Adds an item to Firestore
     * @param item the item to be added
     */
    public void addItemToFirestore(Item item) {
        db.collection("items").add(item)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Item added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding item", e);
                });
    }

    /**
     * Fetches all items from Firestore
     * @param listener the listener to be called when the items are fetched
     */
    public void getAllItemsFromFirestore(OnItemsFetchedListener listener) {
        db.collection("items")
                .get(Source.CACHE)  // Use local cache first
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        List<Item> itemList = task.getResult().toObjects(Item.class);
                        listener.onItemsFetched(itemList);
                    } else {
                        // Fallback to server if cache is empty or failed
                        db.collection("items")
                                .get(Source.SERVER)
                                .addOnCompleteListener(serverTask -> {
                                    if (serverTask.isSuccessful()) {
                                        List<Item> itemList = serverTask.getResult().toObjects(Item.class);
                                        listener.onItemsFetched(itemList);
                                    } else {
                                        Log.w("Firestore", "Error getting documents.", serverTask.getException());
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Failed to fetch items from cache", e);
                });
    }

    /**
     * Fetches an item from Firestore
     * @param itemId the id of the item to be fetched
     * @param listener the listener to be called when the item is fetched
     */
    public void getItemFromFirestore(String itemId, OnItemsFetchedListener listener) {
        db.collection("items").whereEqualTo("itemId", itemId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Item> itemList = task.getResult().toObjects(Item.class);
                        listener.onItemsFetched(itemList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Updates an item in Firestore
     * @param itemId the id of the item to be updated
     */
    public void updateItemInFirestore(String itemId) {
        db.collection("items").whereEqualTo("itemId", itemId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("items").document(document.getId())
                                    .update("status", "lent out")
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Item updated successfully!"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating item", e));
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Deletes an item from Firestore
     * @param itemId the id of the item to be deleted
     */
    public void deleteItemFromFirestore(String itemId) {
        db.collection("items").whereEqualTo("itemId", itemId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("items").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Item deleted successfully!"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting item", e));
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Listener interface for fetching items
     */
    public interface OnItemsFetchedListener {
        void onItemsFetched(List<Item> items);
    }

    /**
     * Uploads an image to Firebase Storage and updates the item in Firestore with the image URL
     * @param fileUri the URI of the image file
     * @param itemId the id of the item to be updated
     */
    public void uploadImageAndUpdateItem(Uri fileUri, String itemId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("itemImages/" + fileUri.getLastPathSegment());
        UploadTask uploadTask = imageRef.putFile(fileUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                updateItemImageInFirestore(itemId, uri.toString());
            });
        }).addOnFailureListener(exception -> {
            Log.w("Storage", "Error uploading image", exception);
        });
    }

    /**
     * Updates the image of an item in Firestore
     * @param itemId the id of the item to be updated
     * @param imageUrl the URL of the image
     */
    public void updateItemImageInFirestore(String itemId, String imageUrl) {
        db.collection("items").whereEqualTo("itemId", itemId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("items").document(document.getId())
                                    .update("image", imageUrl)
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Item image updated successfully!"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating item image", e));
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }
}