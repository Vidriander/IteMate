package iteMate.project.repositories;

import android.util.Log;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import iteMate.project.models.Item;
import iteMate.project.repositories.listeners.OnMultipleDocumentsFetchedListener;
import iteMate.project.repositories.listeners.OnSingleDocumentFetchedListener;

/**
 * Repository class for items
 * This class is responsible for handling all interactions with database items
 * It contains methods for adding, fetching, deleting and updating items
 */
public class ItemRepository extends GenericRepository<Item> {

    /**
     * Constructor for the ItemRepository class
     */
    public ItemRepository() {
        super(Item.class);
    }

    /**
     * Gets an item from the database by its NFC tag
     * @param nfcTag the NFC tag of the item
     * @param listener the listener to be called when the item is fetched
     */
    public void getItemByNfcTagFromDatabase(String nfcTag, OnSingleDocumentFetchedListener<Item> listener) {
        db.collection("items")
                .whereEqualTo("nfcTag", nfcTag)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Item item = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            item = document.toObject(Item.class);
                            item.setId(document.getId());
                            // setContainedAndAssociatedItems(item);
                            break; // Assuming NFC tag is unique, so we take the first result
                        }
                        listener.onDocumentFetched(item);
                    } else {
                        Log.w("Database", "Error fetching item by NFC tag", task.getException());
                        listener.onDocumentFetched(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Database", "Error fetching item by NFC tag", e);
//                    listener.onItemFetched(null);
                });
    }

    /**
     * Gets an item from the database by its title
     * @param itemIDs list of item IDs
     * @return list of item objects
     */
    private ArrayList<Item> getItemslistByListOfIDsFromDatabase(List<String> itemIDs) {
        ArrayList<Item> items = new ArrayList<>();
        for (String itemID : itemIDs) {
            db.collection("items").document(itemID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Item item = task.getResult().toObject(Item.class);
                            if (item != null) {
                                item.setId(task.getResult().getId()); // Set the database document ID
                                items.add(item);
                            }
                        } else {
                            Log.w("Database", "Error getting document: " + itemID, task.getException());
                        }
                    });
        }
        return items;
    }

    /**
     * Gets all available items from the database (items that are not in a track)
     */
    public void getAllAvailableItemsFromDatabase(OnMultipleDocumentsFetchedListener<Item> listener) {
        List<Item> items = new ArrayList<>();
        db.collection("items").whereEqualTo("activeTrackID", null) // Only get items that are not in a track
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Item item = document.toObject(Item.class);
                            item.setId(document.getId());
                            items.add(item);
                        }
                        listener.onDocumentsFetched(items);
                        Log.w("Database", "Items fetched successfully!" + items);
                    } else {
                        Log.w("Database", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Method to set the contained and associated items of an item.
     *
     * @param item the item whose contained and associated items are to be set
     */
    public void setContainedAndAssociatedItems(Item item) {
        // Fetch contained items
        if (item.getContainedItemIDs() != null && !item.getContainedItemIDs().isEmpty()) {
            item.setContainedItems(getItemslistByListOfIDsFromDatabase(item.getContainedItemIDs()));
        }
        // Fetch associated items
        if (item.getAssociatedItemIDs() != null && !item.getAssociatedItemIDs().isEmpty()) {
            item.setAssociatedItems(getItemslistByListOfIDsFromDatabase(item.getAssociatedItemIDs()));
        }
    }

    @Override
    protected List<Item> manipulateResults(List<Item> items, OnMultipleDocumentsFetchedListener<Item> listener) {
        for (Item item : items) {
            setContainedAndAssociatedItems(item);
        }
        listener.onDocumentsFetched(items);
        return items;
    }
}