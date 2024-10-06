package iteMate.project.repositories;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import iteMate.project.models.Item;

/**
 * Repository class for items
 * This class is responsible for handling all interactions with Firestore
 * It contains methods for adding, fetching, deleting and updating items
 */
public class ItemRepository extends GenericRepository<Item> {

    // Default constructor
    public ItemRepository() {
        super(Item.class);
    }

    @Override
    protected void manipulateResults(List<Item> items, OnDocumentsFetchedListener<Item> listener) {
        for (Item item : items) {
            setContainedAndAssociatedItems(item);
        }
        listener.onDocumentsFetched(items);
    }

    /**
     * Method to set the contained and associated items of an item.
     *
     * @param item the item whose contained and associated items are to be set
     */
    public void setContainedAndAssociatedItems(Item item) {
        // Fetch contained items
        if (item.getContainedItemIDs() != null && !item.getContainedItemIDs().isEmpty()) {
            item.setContainedItems(getItemslistFromListOfIDs(item.getContainedItemIDs()));
        }
        // Fetch associated items
        if (item.getAssociatedItemIDs() != null && !item.getAssociatedItemIDs().isEmpty()) {
            item.setAssociatedItems(getItemslistFromListOfIDs(item.getAssociatedItemIDs()));
        }
    }

    private static ArrayList<Item> getItemslistFromListOfIDs(List<String> itemIDs) {
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
     * Updates an item in Firestore
     *
     * @param item the item to be updated
     */
    @Override
    public void updateDocumentInFirestore(Item item) {
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
                        try {
                            listener.onItemFetched(item);
                        } catch (InvocationTargetException | NoSuchMethodException |
                                 IllegalAccessException | InstantiationException e) {
                            Log.w("Firestore", "Error fetching item by NFC tag", e);
                        }
                    } else {
//                        listener.onItemFetched(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error fetching item by NFC tag", e);
//                    listener.onItemFetched(null);
                });
    }

    /**
     * Listener interface for fetching an single item
     */
    public interface OnItemFetchedListener {
        void onItemFetched(Item item) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException;
    }

    /**
     * Listener interface for fetching multiple items
     */
    public interface OnItemsFetchedListener {
        void onItemsFetched(List<Item> items);
    }
}