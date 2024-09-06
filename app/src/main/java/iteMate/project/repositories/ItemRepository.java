package iteMate.project.repositories;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

import iteMate.project.models.Item;

/**
 * Repository class for items
 * This class is responsible for handling all interactions with Firestore
 * It contains methods for adding, fetching and updating items
 */
public class ItemRepository {
    private FirebaseFirestore db;

    public ItemRepository() {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
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
     * Listener interface for fetching items
     */
    public interface OnItemsFetchedListener {
        void onItemsFetched(List<Item> items);
    }
}