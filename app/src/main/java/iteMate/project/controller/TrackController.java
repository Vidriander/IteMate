package iteMate.project.controller;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.repositories.OnMultipleDocumentsFetchedListener;
import iteMate.project.repositories.OnSingleDocumentFetchedListener;
import iteMate.project.repositories.TrackRepository;

public class TrackController {

    /**
     * Singleton instance of the TrackController
     */
    private static TrackController trackController;

    /**
     * The current object that is being displayed or edited
     */
    private Track currentTrack;
    private final TrackRepository trackRepository;
    private final ItemRepository itemRepository;

    private TrackController() {
        trackRepository = new TrackRepository();
        itemRepository = new ItemRepository();
    }

    /**
     * Returns the singleton instance of the TrackController
     * @return the singleton instance of the TrackController
     */
    public static synchronized TrackController getControllerInstance() {
        if (trackController == null) {
            trackController = new TrackController();
        }
        return trackController;
    }

    /**
     * Sets the current object
     * @param currentTrack the object to be set as current
     * @throws NullPointerException if the object is null
     */
    public void setCurrentTrack(Track currentTrack) throws NullPointerException {
        if (currentTrack == null) {
            throw new NullPointerException("Current object cannot be null");
        }
        this.currentTrack = currentTrack;
    }

    /**
     * Returns the current object
     * @return the current object
     */
    public Track getCurrentTrack() {
        return currentTrack;
    }

    /**
     * Updates the current object with the given track and saves it to database
     * @param track the track to be saved
     */
    public void saveChangesToDatabase(Track track) {
        setCurrentTrack(track);
        String trackId = currentTrack.getId();

        if (trackId == null || trackId.isEmpty()) {
            trackRepository.addDocumentToDatabase(getCurrentTrack());
        } else {
            trackRepository.updateDocumentInDatabase(getCurrentTrack());
        }
    }

    /**
     * Checks if the current object is ready for upload and the edit activity can be closed
     * @return true if the object is ready for upload, false otherwise
     */
    public boolean isReadyForUpload() {
        boolean b1 = currentTrack != null;
        boolean b2 = currentTrack.getContact() != null;
        boolean b3 = currentTrack.getLentItemsList() != null;
        boolean b4 = !currentTrack.getLentItemIDs().isEmpty();
        boolean b5 = currentTrack.getReturnDate() != null;
        boolean b6 = currentTrack.getGiveOutDate().compareTo(currentTrack.getReturnDate()) < 0;

        return b1 && b2 && b3 && b4 && b5 && b6;
    }

    /**
     * Returns a list of items that can be lend or are already lend in the current track
     */
    public void getLendableItemsList(OnMultipleDocumentsFetchedListener<Item> listener) {
        List<Item> lendableItems = new ArrayList<>();
        itemRepository.getAllAvailableItemsFromDatabase(documents -> {
            lendableItems.addAll(documents);

            if (currentTrack != null) {
                List<Item> itemsInTrack = currentTrack.getLentItemsList();
                if (itemsInTrack != null) {
                    lendableItems.addAll(itemsInTrack);
                }
            }
            listener.onDocumentsFetched(lendableItems);
        });
    }


    /**
     * Resets the current object to null
     */
    public void resetCurrentTrack() {
        currentTrack = null;
    }

    /**
     * Fetches the track with the given ID from database
     * @param trackID the ID of the track to be fetched
     * @param listener listener that is notified when the track is ready
     */
    public void fetchOneTrackFromDatabase(String trackID, OnSingleDocumentFetchedListener<Track> listener) {
        trackRepository.getOneDocumentFromDatabase(trackID, track -> {
            track.setId(trackID);
            listener.onDocumentFetched(track);
        });
    }

    public void fetchAllTracksFromDatabase(OnMultipleDocumentsFetchedListener<Track> listener) {
        trackRepository.getAllDocumentsFromDatabase(listener);
    }

    // add methode to set availability of items



}
