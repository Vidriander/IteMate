package iteMate.project.controller;

import android.nfc.Tag;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.repositories.TrackRepository;

public class ScanController {

    /**
     * Singleton instance of the ScanController
     */
    public static ScanController scanController;

    private final ItemController itemController;
    private final TrackController trackController;
    private static final ItemRepository itemRepository = new ItemRepository();
    private static final TrackRepository trackRepository = new TrackRepository();
    private String tagId;

    /**
     * Constructor for ScanController
     * TODO make private for singleton
     */
    private ScanController() {
        this.itemController = ItemController.getControllerInstance();
        this.trackController = TrackController.getControllerInstance();
        this.tagId = "";
    }

    /**
     * Returns the singleton instance of the ScanController
     *
     * @return the singleton instance of the ScanController
     */
    public static synchronized ScanController getControllerInstance() {
        if (scanController == null) {
            scanController = new ScanController();
        }
        return scanController;
    }

    /**
     * Getter for the NFC tag ID
     *
     * @return the NFC tag ID
     */
    public String getNfcTagId() {
        return tagId;
    }

    /**
     * Setter for the NFC tag ID
     *
     * @param tagId NFC Tag of the item
     */
    public void setNfcTagId(String tagId) {
        this.tagId = tagId;
    }

    /**
     * Extracts the tag ID from the tag
     *
     * @param tag the tag to extract the ID from
     * @return the tag ID as a string
     */
    public static String extractNfcTagId(Tag tag) {
        byte[] tagId = tag.getId();
        StringBuilder hexString = new StringBuilder();
        for (byte b : tagId) {
            hexString.append(String.format("%02X", b));
        }
        scanController.setNfcTagId(hexString.toString());
        return hexString.toString();
    }

    /**
     * Fetches the item from the database by the NFC tag ID
     *
     * @param tagId    the NFC tag ID to search for
     * @param listener the listener to call when the item is fetched
     */
    public void fetchItemByNfcTagId(String tagId, GenericRepository.OnDocumentsFetchedListener<Item> listener) {
        itemRepository.getItemByNfcTagFromDatabase(tagId, listener);
    }

    /**
     * Fetches the track from the database by the item track ID
     *
     * @param trackID  the item track ID to search for
     * @param listener the listener to call when the track is fetched
     */
    public static void fetchTrackByItemTrackID(String trackID, GenericRepository.OnDocumentsFetchedListener<Track> listener) {
        trackRepository.getOneDocumentFromDatabase(trackID, listener);
    }

    /**
     * Toggles Add / Remove from the lent items list
     * If the item is available and not in the lent items list, it will be added
     * If the item is in the lent items list, it will be removed
     *
     * @param item The fetched item
     */
    public void toggleAddToLendList(Item item) {
        if (item != null && item.getActiveTrackID() == null && trackController.getCurrentTrack().getReturnedItemIDs().isEmpty()) {
            List<Item> lendList = trackController.getCurrentTrack().getLentItemsList();
            // Toggle: If the item is in the lent list, remove it else add it
            if (lendList.removeIf(lentItem -> lentItem.getId().equals(item.getId()))) {
                // Item was removed
            } else {
                lendList.add(item);
            }
            trackController.getCurrentTrack().setLentItemsList(lendList);
            trackController.getCurrentTrack().setPendingItemsList(lendList);
        }
    }

    public void resetCurrentScan() {
        tagId = "";
    }

    public Item createNewItem(String nfcTagId) {
        //TODO move to item controller?
        Item newItem = new Item();
        newItem.setNfcTag(nfcTagId);
        itemController.setCurrentItem(newItem);
        return newItem;
    }

    public Track createNewTrack() {
        //TODO move to track controller?
        Track newTrack = new Track();
        trackController.setCurrentTrack(newTrack);
        return newTrack;
    }

    public void lendItem() {
        //TODO move to track controller?
        if (itemController.getCurrentItem().isAvailable()) {
            // create new track
            scanController.createNewTrack();

            // add item to track (add item to track: setLendItemsList & setPendingItemsList)
            List<Item> itemList = new ArrayList<>();
            itemList.add(itemController.getCurrentItem());
            trackController.getCurrentTrack().setLentItemsList(itemList);
            trackController.getCurrentTrack().setPendingItemsList(itemList);

            // set active trackID to item and mark as lent out
            itemController.getCurrentItem().setActiveTrackID(trackController.getCurrentTrack().getId());

            // open track edit activity
            trackController.setCurrentTrack(trackController.getCurrentTrack());
        }
    }

    public void returnItem() {
        Track currentTrack = trackController.getCurrentTrack();
        if (currentTrack != null) {
            // remove item from pending list in the track & update track in database
            currentTrack.getPendingItemIDs().remove(itemController.getCurrentItem().getId());
            currentTrack.getReturnedItemIDs().add(itemController.getCurrentItem().getId());
            trackController.saveChangesToDatabase(currentTrack);

            // reset active track id in item & update item in database
            itemController.getCurrentItem().setActiveTrackID(null);
            itemController.saveChangesToDatabase();
        }
    }

    public interface OnItemFetchedListener {
        void onItemFetched(Item item);
    }

    public void fetchItemByNfcTagId(String tagId, OnItemFetchedListener listener) {
        itemRepository.getItemByNfcTagFromDatabase(tagId, new GenericRepository.OnDocumentsFetchedListener<Item>() {
            @Override
            public void onDocumentFetched(Item item) {
                listener.onItemFetched(item);
            }

            @Override
            public void onDocumentsFetched(List<Item> items) {
                // No implementation needed
            }
        });
    }

}
