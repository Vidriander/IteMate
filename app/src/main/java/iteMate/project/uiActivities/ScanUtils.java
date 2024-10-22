package iteMate.project.uiActivities;

import android.nfc.Tag;

import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.repositories.TrackRepository;

public class ScanUtils {

    private static final ItemRepository itemRepository = new ItemRepository();
    private static final TrackRepository trackRepository = new TrackRepository();

    public static String extractTagId(Tag tag) {
        byte[] tagId = tag.getId();
        StringBuilder hexString = new StringBuilder();
        for (byte b : tagId) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }

    public static void fetchItemByNfcTagId(String tagId, GenericRepository.OnDocumentsFetchedListener<Item> listener) {
        itemRepository.getItemByNfcTagFromFirestore(tagId, listener);
    }

    public static void fetchTrackByItemTrackID(String trackID, GenericRepository.OnDocumentsFetchedListener<Track> listener) {
        trackRepository.getOneDocumentFromFirestore(trackID, listener);
    }
}