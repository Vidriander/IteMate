package iteMate.project.controller;
import iteMate.project.models.Track;
import iteMate.project.repositories.TrackRepository;

public class TrackController {

    /**
     * Singleton instance of the TrackController
     */
    private static TrackController trackController;

    /**
     * The current object that is being displayed or edited
     */
    Track currentTrack;

    private final TrackRepository trackRepository;

    private TrackController() {
        trackRepository = new TrackRepository();
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
     * Updates the current object with the given track and saves it to Firestore
     * @param track the track to be saved
     */
    public void saveChangesToTrack(Track track) {
        setCurrentTrack(track);
        trackRepository.updateDocumentInFirestore(getCurrentTrack());
    }

    /**
     * Returns the current object
     * @return the current object
     */
    public Track getCurrentTrack() {
        return currentTrack;
    }

    /**
     * Checks if the current object is ready for upload and the edit activity can be closed
     * @return true if the object is ready for upload, false otherwise
     */
    public boolean isReadyForUpload() {
        return
                currentTrack != null &&
                currentTrack.getContact() != null &&
                currentTrack.getLentItemsList() != null &&
                currentTrack.getReturnDate() != null &&
                currentTrack.getGiveOutDate().compareTo(currentTrack.getReturnDate()) < 0 &&
                !currentTrack.getLentItemIDs().isEmpty();
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


}
