package iteMate.project.repositories;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.models.DocumentEquivalent;
import iteMate.project.repositories.listeners.OnMultipleDocumentsFetchedListener;
import iteMate.project.repositories.listeners.OnSingleDocumentFetchedListener;

/**
 * A generic repository for Firestore that can be used to fetch, add, update, and delete documents of a certain type
 *
 * @param <T> the type of the document to be fetched, added, updated, or deleted
 */
public class GenericRepository<T extends DocumentEquivalent> {

    protected FirebaseFirestore db;
    /**
     * The class of the document to be fetched, added, updated, or deleted (Type DocumentEquivalent)
     */
    protected Class<T> tClass;
    /**
     * A flag to remember if offline persistence has been enabled
     */
    private boolean isOfflinePersistenceEnabled = false;

    /**
     * Initializes the Firestore database and remembers the class of the document
     *
     * @param tClass the class of the document to be fetched, added, updated, or deleted
     */
    public GenericRepository(Class<T> tClass) {
        // Initialize Firestore as the database
        db = FirebaseFirestore.getInstance();

        // Enable offline persistence once per app lifecycle
        enableOfflinePersistence();

        // remember the class of the document
        this.tClass = tClass;
    }

    /**
     * Enables offline persistence for Firestore
     */
    private void enableOfflinePersistence() {
        if (isOfflinePersistenceEnabled) {
            return;
        }
        // Enable offline persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                // Enables persistent disk storage
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                .build();
        db.setFirestoreSettings(settings);
        // Remember that offline persistence has been enabled
        isOfflinePersistenceEnabled = true;
    }

    /**
     * Fetches a document from Firestore by its ID
     *
     * @param documentId the ID of the document to be fetched
     * @param listener   the listener to be called when the document is fetched
     */
    public void getOneDocumentFromDatabase(String documentId, OnSingleDocumentFetchedListener<T> listener) {
        try {
            db.collection(tClass.getDeclaredConstructor().newInstance().getCollectionPath()).document(documentId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            T object = task.getResult().toObject(tClass);
                            assert object != null;
                            object.setId(document.getId());
                            Log.d("GenericRepository", "Document fetched: " + object);
                            manipulateResult(object, listener);
                        } else {
                            Log.w("Firestore", "Error getting document.", task.getException());
                        }
                    });
        } catch (Exception e) {
            Log.e("GenericRepository", "Error getting document", e);
        }
    }

    /**
     * Fetches all documents from Firestore of the type of the class
     *
     * @param listener the listener to be called when the documents are fetched
     */
    public void getAllDocumentsFromDatabase(OnMultipleDocumentsFetchedListener<T> listener) {
        try {
            db.collection(tClass.getDeclaredConstructor().newInstance().getCollectionPath())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot documentList = task.getResult();
                            List<T> objects = new ArrayList<>();
                            for (QueryDocumentSnapshot document : documentList) {
                                T object = document.toObject(tClass);
                                object.setId(document.getId());
                                objects.add(object);
                            }
                            manipulateResults(objects, listener);
                        } else {
                            Log.w("Firestore", "Error getting documents.", task.getException());
                        }
                    });
        } catch (Exception e) {
            Log.e("GenericRepository", "Error getting all documents", e);
        }
    }

    /**
     * Adds a document to Firestore
     *
     * @param element the document to be added
     */
    public void addDocumentToDatabase(T element) {
        db.collection(element.getCollectionPath())
                .add(element)
                .addOnSuccessListener(documentReference ->
                        Log.d("Firestore", "Element added with ID: " + documentReference.getId())
                )
                .addOnFailureListener(e ->
                        Log.w("Firestore", "Error adding item", e)
                );
    }

    /**
     * Updates a document in Firestore
     *
     * @param document the document to be updated
     */
    public void updateDocumentInDatabase(T document) {
        try {
            db.collection(tClass.getDeclaredConstructor().newInstance().getCollectionPath()).document(document.getId())
                    .set(document)
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                        Log.d("ContactRepository", "Contact successfully updated!");
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Log.w("ContactRepository", "Error updating contact", e);
                    });
        } catch (Exception e) {
            Log.e("GenericRepository", "Error updating document", e);
        }
    }

    /**
     * Deletes a document from Firestore
     *
     * @param document the document to be deleted
     */
    public void deleteDocumentFromDatabase(T document) {
        try {
            db.collection(tClass.getDeclaredConstructor().newInstance().getCollectionPath()).document(document.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Handle success
                        Log.d("ContactRepository", "Contact successfully deleted!");
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                        Log.w("ContactRepository", "Error deleting contact", e);
                    });
        } catch (Exception e) {
            Log.e("GenericRepository", "Error deleting document", e);
        }
    }

    /**
     * Fetches the URL of an image from Firebase Storage
     *
     * @param imagePath the path of the image in Firebase Storage
     * @param listener  the listener to be called when the URL is fetched
     */
    public void fetchImageUrl(String imagePath, OnSingleDocumentFetchedListener<String> listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference().child(imagePath);
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> listener.onDocumentFetched(uri.toString())).addOnFailureListener(e -> listener.onDocumentFetched(null));
    }

    /*public static void setImageForView(Context context, String imagePath, ImageView imageView) {
        // Get the StorageReference of the image
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference().child(imagePath);

        // Check if image exists in the local cache
        File localFile = new File(context.getCacheDir(), "images/" + imagePath);
        File localDir = localFile.getParentFile();
        if (localDir != null && !localDir.exists()) {
            localDir.mkdirs();  // Create the directory if it doesn't exist
        }

        if (localFile.exists()) {
            // Load the image from the local file
            Glide.with(context)
                    .load(localFile)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .placeholder(R.drawable.placeholder_image)  // image in drawables
                    .error(R.drawable.error_image)  // image in drawables
                    .into(imageView);
        } else {
            // Download the image from Firebase Storage and save it locally
            imageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                // Once downloaded, load the image from the local file
                Glide.with(context)
                        .load(localFile)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into(imageView);
                Log.d("ItemsDetailActivity", "Image downloaded and cached: " + localFile.getPath());
            }).addOnFailureListener(exception -> {
                // Handle the error gracefully, e.g., show a placeholder image
                Glide.with(context)
                        .load(R.drawable.error_image)
                        .into(imageView);
                Log.e("ItemsDetailActivity", "Error getting download URL", exception);
            });
        }
    }*/

    /**
     * Allows to subclass to manipulate the result of the fetch before returning it by overriding this method
     *
     * @param document the document to be manipulated
     */
    protected void manipulateResult(T document, OnSingleDocumentFetchedListener<T> listener) {
        listener.onDocumentFetched(document);
    }

    /**
     * Allows to subclass to manipulate the results of the fetch before returning them by overriding this method
     *
     * @param documents the documents to be manipulated
     */
    protected void manipulateResults(List<T> documents, OnMultipleDocumentsFetchedListener<T> listener) {
        listener.onDocumentsFetched(documents);
    }

}
