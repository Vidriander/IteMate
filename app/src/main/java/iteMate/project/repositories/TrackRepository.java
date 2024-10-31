package iteMate.project.repositories;

import android.util.Log;

import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.models.Contact;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.listeners.Listener;
import iteMate.project.repositories.listeners.OnMultipleDocumentsFetchedListener;
import iteMate.project.repositories.listeners.OnSingleDocumentFetchedListener;

/**
 * Repository class for tracks
 */
public class TrackRepository extends GenericRepository<Track> {

    public TrackRepository() {
        super(Track.class);
    }

    /**
     * Fetches the attributes for a track from database
     * @param track the track for which the attributes are to be fetched
     */
    public void fetchAttributesForTrack(Track track, Listener listener) {
        TaskCompletionSource<Void> contactTaskSource = new TaskCompletionSource<>();
        TaskCompletionSource<Void> itemsTaskSource = new TaskCompletionSource<>();
        TaskCompletionSource<Void> pendingItemsTaskSource = new TaskCompletionSource<>();


        db.collection("contacts").document(track.getContactID())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        Contact contact = document.toObject(Contact.class);
                        contact.setId(document.getId());
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
                .addOnCompleteListener(task -> {
                    if (listener instanceof OnSingleDocumentFetchedListener) {
                        ((OnSingleDocumentFetchedListener<Track>) listener).onDocumentFetched(track);
                    } else if (listener instanceof OnMultipleDocumentsFetchedListener) {
                        ((OnMultipleDocumentsFetchedListener<Track>) listener).onDocumentsFetched(new ArrayList<>(List.of(track)));
                    }
                });
    }

    @Override
    public void addDocumentToDatabase(Track track) {
        db.collection("tracks").add(track).addOnSuccessListener(documentReference -> {
            String documentId = documentReference.getId();
            // TODO: change ItemRepository to singleton to prevent multiple instances (for each item to update)
            new ItemRepository().getAllDocumentsFromDatabase(documents -> {
                for (Item item : documents) {
                    if (track.getPendingItemIDs().contains(item.getId())) {
                        item.setActiveTrackID(documentId);
                        new ItemRepository().updateDocumentInDatabase(item);
                    }
                }
            });
        });
    }

    @Override
    protected Track manipulateResult(Track track, OnSingleDocumentFetchedListener<Track> listener) {
        if (track != null) {
            fetchAttributesForTrack(track, listener);
        }
        return track;
    }

    @Override
    protected List<Track> manipulateResults(List<Track> tracks, OnMultipleDocumentsFetchedListener<Track> listener) {
        for (Track track : tracks) {
            // TODO think about if the listener is notified twice for the same track
            fetchAttributesForTrack(track, listener);
        }
        listener.onDocumentsFetched(tracks);
        return tracks;
    }
}