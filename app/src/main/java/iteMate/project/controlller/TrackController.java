package iteMate.project.controlller;
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
    Track currentObject;

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
        setCurrentObject(track);
        trackRepository.updateDocumentInFirestore(getCurrentObject());

    }

    /**
     * Returns the current object
     * @return the current object
     */
    public Track getCurrentObject() {
        return currentObject;
    }

    /**
     * Sets the current object
     * @param currentObject the object to be set as current
     * @throws NullPointerException if the object is null
     */
    public void setCurrentObject(Track currentObject) throws NullPointerException {
        if (currentObject == null) {
            throw new NullPointerException("Current object cannot be null");
        }
        this.currentObject = currentObject;
    }
}
