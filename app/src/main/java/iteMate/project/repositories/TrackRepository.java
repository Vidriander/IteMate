package iteMate.project.repositories;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
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

    /*@Override
    public void updateDocumentInFirestore(Track track) {
        db.collection("tracks").document(track.getId()).set(track);
    }*/

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
        List<Item> lentItemsList = new ArrayList<>();
        db.collection("items").whereIn(FieldPath.documentId(), track.getLentItemIDs()) // TODO: might be empty
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Item item = document.toObject(Item.class);
                            item.setId(document.getId());
                            lentItemsList.add(item);
                        }
                        track.setLentItemsList(lentItemsList);
                    } else {
                        Log.w("Firestore", "Error getting items.", task.getException());
                    }
                    itemsTaskSource.setResult(null);
                });
        if (!track.getPendingItemIDs().isEmpty()) {
            List<Item> pendingItemsList = new ArrayList<>();
            db.collection("items").whereIn(FieldPath.documentId(), track.getPendingItemIDs()) // TODO: might be empty
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Item item = document.toObject(Item.class);
                                item.setId(document.getId());
                                pendingItemsList.add(item);
                            }
                            track.setPendingItemsList(pendingItemsList);
                        } else {
                            Log.w("Firestore", "Error getting items.", task.getException());
                        }
                        pendingItemsTaskSource.setResult(null);
                    });
        } else {
            pendingItemsTaskSource.setResult(null);
        }

        Tasks.whenAll(contactTaskSource.getTask(), itemsTaskSource.getTask(), pendingItemsTaskSource.getTask())
                .addOnCompleteListener(task -> listener.onDocumentFetched(track));
    }

    @Override
    public void addDocumentToFirestore(Track track) {
        db.collection("tracks").add(track).addOnSuccessListener(documentReference -> {
            String documentId = documentReference.getId();
            new ItemRepository().getAllDocumentsFromFirestore(new OnDocumentsFetchedListener<Item>() {
                @Override
                public void onDocumentFetched(Item document) {
                    // Do nothing
                }
                @Override
                public void onDocumentsFetched(List<Item> documents) {
                    for (Item item : documents) {
                        if (track.getPendingItemIDs().contains(item.getId())) {
                            item.setActiveTrackID(documentId);
                            new ItemRepository().updateDocumentInFirestore(item);
                        }
                    }
                }
                // TODO: change ItemRepository to singleton to prevent multiple instances (for each item to update)
            });
        });
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