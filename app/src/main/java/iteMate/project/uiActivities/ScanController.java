package iteMate.project.uiActivities;

import android.nfc.Tag;

import iteMate.project.controller.ItemController;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.repositories.TrackRepository;
import iteMate.project.uiActivities.scanScreen.ScanActivity;

public class ScanController {

    private final ScanActivity scanActivity;
    private final ItemController itemController;
    private final TrackController trackController;
    private static final ItemRepository itemRepository = new ItemRepository();
    private static final TrackRepository trackRepository = new TrackRepository();

    public ScanController(ScanActivity scanActivity) {
        this.scanActivity = scanActivity;
        this.itemController = ItemController.getControllerInstance();
        this.trackController = TrackController.getControllerInstance();
    }

    public static String extractTagId(Tag tag) {
        byte[] tagId = tag.getId();
        StringBuilder hexString = new StringBuilder();
        for (byte b : tagId) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }



    public static void fetchItemByNfcTagId(String tagId, GenericRepository.OnDocumentsFetchedListener<Item> listener) {
        itemRepository.getItemByNfcTagFromDatabase(tagId, listener);
    }

    public static void fetchTrackByItemTrackID(String trackID, GenericRepository.OnDocumentsFetchedListener<Track> listener) {
        trackRepository.getOneDocumentFromDatabase(trackID, listener);
    }
}