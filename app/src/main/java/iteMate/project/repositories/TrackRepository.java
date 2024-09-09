package iteMate.project.repositories;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.models.Item;
import iteMate.project.models.Track;

/**
 * Repository class for tracks
 */
public class TrackRepository {

    private FirebaseFirestore db;

    public TrackRepository() {
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Adds a track to Firestore
     * @param track the track to be added
     */
    public void addTrackToFirestore(Track track) {
        db.collection("tracks").add(track)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Track added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding track", e);
                });
    }

    /**
     * Fetches all tracks from Firestore
     * @param listener the listener to be called when the tracks are fetched
     */
    public void getAllTracksFromFirestore(OnTracksFetchedListener listener) {
        db.collection("tracks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Track> trackList = task.getResult().toObjects(Track.class);
                        listener.onTracksFetched(trackList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Fetches a track from Firestore
     * @param trackId the id of the track to be fetched
     * @param listener the listener to be called when the track is fetched
     */
    public void getTrackFromFirestore(String trackId, OnTracksFetchedListener listener) {
        db.collection("tracks").whereEqualTo("trackId", trackId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Track> trackList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Track track = document.toObject(Track.class);
                            trackList.add(track);
                        }
                        listener.onTracksFetched(trackList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
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

    /**
     * Listener interface for fetching tracks
     */
    public interface OnTracksFetchedListener {
        void onTracksFetched(List<Track> tracks);
    }
}