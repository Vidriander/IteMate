package iteMate.project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.repositories.OnSingleDocumentFetchedListener;
import iteMate.project.repositories.TrackRepository;

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
     * @return the current item
     */
    public Item getCurrentItem() {
        return currentItem;
    }

    /**
     * Getter for the current item list
     * @return the current item list
     */
    public List<Item> getCurrentItemList() {
        return currentItemList;
    }

    /**
     * Setter for the current item
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
     * Deletes the current item from the database
     */
    public void deleteItemFromDatabase() {
        itemRepository.deleteDocumentFromDatabase(currentItem);
    }

    /**
     * Fetches the tack corresponding to the current item
     * @param trackListener listener that is notified when the track is ready
     */
    public void getTrackOfCurrentItem(OnSingleDocumentFetchedListener<Track> trackListener) {
        trackRepository.getOneDocumentFromDatabase(currentItem.getActiveTrackID(), document -> trackListener.onDocumentFetched(document));
    }

    public void fetchItemByNfcTagId(String nfcTagId, OnSingleDocumentFetchedListener<Item> listener) {
        itemRepository.getItemByNfcTagFromDatabase(nfcTagId, document -> listener.onDocumentFetched(document));
    }

    /**
     * Fetches all items from database and sets the current item list
     */
    public void refreshCurrentItemList() {
        itemRepository.getAllDocumentsFromDatabase(documents -> currentItemList = documents);
    }
    // endregion
}
