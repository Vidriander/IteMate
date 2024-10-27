package iteMate.project.uiActivities;

import android.nfc.Tag;

import java.util.List;

import iteMate.project.controller.ItemController;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.repositories.TrackRepository;
import iteMate.project.uiActivities.scanScreen.ScanActivity;

public class ScanController {

    public static ScanController scanController;

    private final ScanActivity scanActivity;
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
        this.scanActivity = new ScanActivity();
        this.itemController = ItemController.getControllerInstance();
        this.trackController = TrackController.getControllerInstance();
        this.tagId = "";
    }

    /**
     * Returns the singleton instance of the ScanController
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
     * @return the NFC tag ID
     */
    public String getNfcTagId() {
        return tagId;
    }

    /**
     * Setter for the NFC tag ID
     * @param tagId NFC Tag of the item
     */
    public void setNfcTagId(String tagId) {
        this.tagId = tagId;
    }

    /**
     * Extracts the tag ID from the tag
     * @param tag the tag to extract the ID from
     * @return the tag ID as a string
     */
    public static String extractTagId(Tag tag) {
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
     * @param tagId the NFC tag ID to search for
     * @param listener the listener to call when the item is fetched
     */
    public static void fetchItemByNfcTagId(String tagId, GenericRepository.OnDocumentsFetchedListener<Item> listener) {
        itemRepository.getItemByNfcTagFromDatabase(tagId, listener);
    }

    /**
     * Fetches the track from the database by the item track ID
     * @param trackID the item track ID to search for
     * @param listener the listener to call when the track is fetched
     */
    public static void fetchTrackByItemTrackID(String trackID, GenericRepository.OnDocumentsFetchedListener<Track> listener) {
        trackRepository.getOneDocumentFromDatabase(trackID, listener);
    }

    /**
     * Handles the fetched item
     * If the item is available and not in the lent items list, it will be added
     * If the item is in the lent items list, it will be removed
     *
     * @param item The fetched item
     */
    public void handleItemFetched(Item item) {
        if (item != null && item.getActiveTrackID() == null) {
            List<Item> lendList = trackController.getCurrentTrack().getLentItemsList();
            if (lendList.removeIf(lentItem -> lentItem.getId().equals(item.getId()))) {
                // Item was removed
            } else {
                lendList.add(item);
            }
            trackController.getCurrentTrack().setLentItemsList(lendList);

            if (!trackController.getCurrentTrack().getReturnedItemsList().contains(item)) {
                trackController.getCurrentTrack().getPendingItemsList().add(item);
            }
            trackController.getCurrentTrack().setPendingItemsList(trackController.getCurrentTrack().getPendingItemsList());
        }
    }
}