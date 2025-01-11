package iteMate.project.documentController;

import android.nfc.Tag;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.model.Item;
import iteMate.project.model.Track;

/**
 * Controller for managing the scanning of NFC tags
 */
public class ScanController {

    /**
     * Singleton instance of the ScanController
     */
    private static ScanController scanController;

    private final ItemController itemController = ItemController.getControllerInstance();
    private final TrackController trackController = TrackController.getControllerInstance();
    private String tagId;

    /**
     * Constructor for ScanController
     */
    private ScanController() {
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

    /**
     * Resets the last scan
     */
    public void resetCurrentScan() {
        tagId = "";
    }

    /**
     * Creates a new item with the given NFC tag ID
     *
     * @param nfcTagId the NFC tag ID
     * @return the newly created item
     */
    public Item createNewItem(String nfcTagId) {
        //TODO move to item controller?
        Item newItem = new Item();
        newItem.setNfcTag(nfcTagId);
        itemController.setCurrentItem(newItem);
        return newItem;
    }

    /**
     * Creates a new track
     *
     * @return the newly created track
     */
    public Track createNewTrack() {
        //TODO move to track controller?
        Track newTrack = new Track();
        trackController.setCurrentTrack(newTrack);
        return newTrack;
    }

    /**
     * Lends the current item
     */
    public void lendItem() {
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

    /**
     * Returns the current item
     */
    public void returnItem() {
        Track currentTrack = trackController.getCurrentTrack();
        if (currentTrack != null) {
            // remove item from pending list in the track and add it to the returned list
            String itemID = itemController.getCurrentItem().getId();
            currentTrack.getPendingItemIDs().remove(itemID);
            currentTrack.getReturnedItemIDs().add(itemID);
            for (Item item: currentTrack.getLentItemsList()) {
                if (item.getId().equals(itemID)) {
                    item.setActiveTrackID(null);
                }
            }
            trackController.saveChangesToDatabase(currentTrack);

            // reset active track id in item & update item in database
            itemController.getCurrentItem().setActiveTrackID(null);
            itemController.saveChangesToDatabase();
        }
    }
}
