package iteMate.project.repositories;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.models.Item;

/**
 * Repository class for items
 * This class is responsible for handling all interactions with Firestore
 * It contains methods for adding, fetching, deleting and updating items
 */
public class ItemRepository {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                .get(Source.SERVER) // TODO: add condition to use Cache in certain cases
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        List<Item> itemList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Item item = document.toObject(Item.class);
                            item.setId(document.getId()); // Set the Firestore document ID

                            // Fetch contained items
                            if (item.getContainedItemIDs() != null && !item.getContainedItemIDs().isEmpty()) {
                                item.setContainedItems(getItemslistFromListOfIDs(item.getContainedItemIDs()));
                            }
                            // Fetch associated items
                            if (item.getAssociatedItemIDs() != null && !item.getAssociatedItemIDs().isEmpty()) {
                                item.setAssociatedItems(getItemslistFromListOfIDs(item.getAssociatedItemIDs()));
                            }

                            itemList.add(item); // Add item to the list
                        }

                        listener.onItemsFetched(itemList);
                    } else {
                        // Fallback to server if cache is empty or failed
                        db.collection("items")
                                .get(Source.SERVER)
                                .addOnCompleteListener(serverTask -> {
                                    if (serverTask.isSuccessful()) {
                                        List<Item> itemList = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : serverTask.getResult()) {
                                            Item item = document.toObject(Item.class);
                                            item.setId(document.getId()); // Set the Firestore document ID

                                            setContainedAndAssociatedItems(item);

                                            itemList.add(item); // Add item to the list
                                        }
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

    public static ArrayList<Item> getItemslistFromListOfIDs(List<String> itemIDs) {
        ArrayList<Item> items = new ArrayList<>();
        for (String itemID : itemIDs) {
            db.collection("items").document(itemID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Item item = task.getResult().toObject(Item.class);
                            if (item != null) {
                                item.setId(task.getResult().getId()); // Set the Firestore document ID
                                items.add(item);
                            }
                        } else {
                            Log.w("Firestore", "Error getting document: " + itemID, task.getException());
                        }
                    });
        }
        return items;
    }

    /**
     * Method to set the contained and associated items of an item.
     * @param item the item whose contained and associated items are to be set
     */
    public static void setContainedAndAssociatedItems(Item item) {
        // Fetch contained items
        if (item.getContainedItemIDs() != null && !item.getContainedItemIDs().isEmpty()) {
            item.setContainedItems(getItemslistFromListOfIDs(item.getContainedItemIDs()));
        }
        // Fetch associated items
        if (item.getAssociatedItemIDs() != null && !item.getAssociatedItemIDs().isEmpty()) {
            item.setAssociatedItems(getItemslistFromListOfIDs(item.getAssociatedItemIDs()));
        }
    }

    /**
     * Updates an item in Firestore
     * @param item the item to be updated
     */
    public static void updateItemInFirestore(Item item) {
        db.collection("items").whereEqualTo("nfcTag", item.getNfcTag())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("items").document(document.getId())
                                    .update(
                                            "title", item.getTitle(),
                                            "nfcTag", item.getNfcTag(),
                                            "description", item.getDescription(),
                                            "image", item.getImage(),
                                            "containedItemIDs", item.getContainedItemIDs(),
                                            "associatedItemIDs", item.getAssociatedItemIDs()
                                    )
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Item updated successfully!"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating item", e));
                        }
                    } else {
                        Log.w("Firestore", "No matching document found.");
                    }
                });
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

    // ItemRepository.java
    public void getItemByNfcTag(String nfcTag, OnItemFetchedListener listener) {
        db.collection("items")
                .whereEqualTo("nfcTag", nfcTag)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Item item = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            item = document.toObject(Item.class);
                            item.setId(document.getId());
                            break; // Assuming NFC tag is unique, so we take the first result
                        }
                        listener.onItemFetched(item);
                    } else {
                        listener.onItemFetched(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error fetching item by NFC tag", e);
                    listener.onItemFetched(null);
                });
    }


    /**
     * Listener interface for fetching an single item
     */
    public interface OnItemFetchedListener {
        void onItemFetched(Item item);
    }

    /**
     * Listener interface for fetching multiple items
     */
    public interface OnItemsFetchedListener {
        void onItemsFetched(List<Item> items);
    }
}