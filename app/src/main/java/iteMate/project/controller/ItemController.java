package iteMate.project.controller;

import java.util.List;
import java.util.Objects;

import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.repositories.TrackRepository;

/**
 * Controller for managing an item
 */
public class ItemController {

    /**
     * The current item that is being displayed or edited
     */
    private Item currentItem;

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

    private ItemController() {
        itemRepository = new ItemRepository();
        trackRepository = new TrackRepository();
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

    /**
     * Getter for the current item
     * @return the current item
     */
    public Item getCurrentItem() {
        return currentItem;
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
     * Saves the changes to the current item to the database
     */
    public void saveChangesToDatabase() {
        if (Objects.equals(currentItem.getId(), "-1")) {
            itemRepository.addDocumentToFirestore(currentItem);
        } else {
            itemRepository.updateDocumentInFirestore(currentItem);
        }
    }

    /**
     * Deletes the current item from the database
     */
    public void deleteItemFromDatabase() {
        itemRepository.deleteDocumentFromFirestore(currentItem);
    }

    /**
     * Fetches the tack corresponding to the current item
     * @param trackListener listener that is notified when the track is ready
     */
    public void getTrackOfCurrentItem(GenericRepository.OnDocumentsFetchedListener<Track> trackListener) {
        trackRepository.getOneDocumentFromFirestore(currentItem.getActiveTrackID(), new GenericRepository.OnDocumentsFetchedListener<Track>() {
            @Override
            public void onDocumentFetched(Track document) {
                trackListener.onDocumentFetched(document);
            }
            @Override
            public void onDocumentsFetched(List<Track> documents) {

            }
        });
    }
}
