package iteMate.project.repositories;

import android.util.Log;

import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.models.Contact;
import iteMate.project.models.Item;
import iteMate.project.models.Track;

/**
 * Repository class for tracks
 */
public class TrackRepository extends GenericRepository<Track> {

    public TrackRepository() {
        super(Track.class);
    }

    @Override
    protected Track manipulateResult(Track track, OnDocumentsFetchedListener<Track> listener) {
        fetchAttributesForTrack(track, listener);
        return track;
    }

    /**
     * Fetches all tracks from Firestore
     * @param listener the listener to be called when the tracks are fetched
     */
    public void getAllTracksFromFirestore(OnDocumentsFetchedListener listener) {
        db.collection("tracks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Track> trackList = task.getResult().toObjects(Track.class);
                        for (Track track : trackList) {
                            fetchAttributesForTrack(track, listener);
                        }
                        listener.onDocumentsFetched(trackList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Fetches the attributes for a track from Firestore
     * @param track the track for which the attributes are to be fetched
     */
    public void fetchAttributesForTrack(Track track, OnDocumentsFetchedListener<Track> listener) {
        db.collection("contacts").document(track.getContactID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Contact contact = task.getResult().toObject(Contact.class);
                        track.setContact(contact);
                        listener.onDocumentFetched(track);
                    } else {
                        Log.w("Firestore", "Error getting contact.", task.getException());
                    }
                });
        db.collection("items").whereIn(FieldPath.documentId(), track.getLentItemIDs())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Item> itemList = task.getResult().toObjects(Item.class);
                        track.setLentItemsList(itemList);
                        listener.onDocumentFetched(track);
                    } else {
                        Log.w("Firestore", "Error getting items.", task.getException());
                    }
                });
    }

    public void fetchTrackByID(String trackID, OnDocumentsFetchedListener<Track> listener) {
        db.collection("tracks").whereEqualTo("trackID", trackID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Track track = task.getResult().toObjects(Track.class).get(0);
                        fetchAttributesForTrack(track, listener);
                        listener.onDocumentFetched(track);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    public List<Item> getContainedItemsOfTrack(Track track) {
        List<Item> itemList = new ArrayList<>();
        db.collection("items").whereIn(FieldPath.documentId(), track.getLentItemIDs())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        itemList.addAll(task.getResult().toObjects(Item.class));
                    } else {
                        Log.w("Firestore", "Error getting items.", task.getException());
                    }
                });
        return itemList;
    }

    /**
     * Updates a track in Firestore
     * @param trackId the id of the track to be updated
     */
    public void updateTrackInFirestore(String trackId) {
        db.collection("tracks").whereEqualTo("trackId", trackId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("tracks").document(document.getId())
                                    .update("status", "lent out")
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Track updated successfully!"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating track", e));
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Deletes a track from Firestore
     * @param trackId the id of the track to be deleted
     */
    public void deleteTrackFromFirestore(String trackId) {
        db.collection("tracks").whereEqualTo("trackId", trackId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("tracks").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Track deleted successfully!"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting track", e));
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }
}