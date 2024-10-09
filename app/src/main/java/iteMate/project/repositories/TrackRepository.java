package iteMate.project.repositories;

import android.util.Log;

import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
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

    /**
     * Fetches the attributes for a track from Firestore
     * @param track the track for which the attributes are to be fetched
     */
    public void fetchAttributesForTrack(Track track, OnDocumentsFetchedListener<Track> listener) {
        TaskCompletionSource<Void> contactTaskSource = new TaskCompletionSource<>();
        TaskCompletionSource<Void> itemsTaskSource = new TaskCompletionSource<>();
        TaskCompletionSource<Void> pendingItemsTaskSource = new TaskCompletionSource<>();


        db.collection("contacts").document(track.getContactID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Contact contact = task.getResult().toObject(Contact.class);
                        track.setContact(contact);
                    } else {
                        Log.w("Firestore", "Error getting contact.", task.getException());
                    }
                    contactTaskSource.setResult(null);
                });
        db.collection("items").whereIn(FieldPath.documentId(), track.getLentItemIDs())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Item> itemList = task.getResult().toObjects(Item.class);
                        track.setLentItemsList(itemList);
                    } else {
                        Log.w("Firestore", "Error getting items.", task.getException());
                    }
                    itemsTaskSource.setResult(null);
                });
        db.collection("items").whereIn(FieldPath.documentId(), track.getPendingItemIDs())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Item> itemList = task.getResult().toObjects(Item.class);
                        track.setPendingItemsList(itemList);
                    } else {
                        Log.w("Firestore", "Error getting items.", task.getException());
                    }
                    pendingItemsTaskSource.setResult(null);
                });

        Tasks.whenAll(contactTaskSource.getTask(), itemsTaskSource.getTask(), pendingItemsTaskSource.getTask())
                .addOnCompleteListener(task -> listener.onDocumentFetched(track));
    }

    @Override
    protected Track manipulateResult(Track track, OnDocumentsFetchedListener<Track> listener) {
        fetchAttributesForTrack(track, listener);
        return track;
    }

    @Override
    protected List<Track> manipulateResults(List<Track> tracks, OnDocumentsFetchedListener<Track> listener) {
        for (Track track : tracks) {
            fetchAttributesForTrack(track, listener);
        }
        listener.onDocumentsFetched(tracks);
        return tracks;
    }
}