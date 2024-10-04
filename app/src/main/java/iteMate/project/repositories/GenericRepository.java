package iteMate.project.repositories;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import iteMate.project.R;
import iteMate.project.models.DocumentEquivalent;

public class GenericRepository<T extends DocumentEquivalent> {

    protected static FirebaseFirestore db;
    protected Class<T> tClass;

    public GenericRepository(Class<T> tClass) {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        // remember the class of the document
        this.tClass = tClass;

        // Enable offline persistence
//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setLocalCacheSettings(
//                        PersistentCacheSettings.newBuilder().build()  // Enables persistent disk storage
//                )
//                .build();
//        db.setFirestoreSettings(settings);
    }

    /**
     * Adds a document to Firestore
     *
     * @param element the document to be added
     */
    public void addDocumentToFirestore(T element) {
        db.collection(element.getCollectionPath()).add(element)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Element added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding item", e);
                });
    }

    /**
     * Fetches a document from Firestore by its ID
     *
     * @param documentId the ID of the document to be fetched
     * @param listener   the listener to be called when the document is fetched
     * @throws NoSuchMethodException     if the constructor of the class does not exist
     * @throws InvocationTargetException if the constructor of the class cannot be invoked
     * @throws IllegalAccessException    if the class or its nullary constructor is not accessible
     * @throws InstantiationException    if the class that declares the underlying constructor represents an abstract class
     */
    public void getDocumentFromFirestore(String documentId, OnDocumentsFetchedListener<T> listener) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        db.collection(tClass.getDeclaredConstructor().newInstance().getCollectionPath()).document(documentId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        T document = task.getResult().toObject(tClass);
                        document = manipulateResult(document, listener);
                        listener.onDocumentFetched(document);
                    } else {
                        Log.w("Firestore", "Error getting document.", task.getException());
                    }
                });

    }

    /**
     * Allows to subclass to manipulate the result of the fetch before returning it by overriding this method
     *
     * @param document the document to be manipulated
     * @return the manipulated document
     */
    protected T manipulateResult(T document, OnDocumentsFetchedListener<T> listener) {
        return document;
    }

    /**
     * Fetches all documents from Firestore of the type of the class
     *
     * @param listener the listener to be called when the documents are fetched
     * @throws NoSuchMethodException     if the constructor of the class does not exist
     * @throws InvocationTargetException if the constructor of the class cannot be invoked
     * @throws IllegalAccessException    if the class or its nullary constructor is not accessible
     * @throws InstantiationException    if the class that declares the underlying constructor represents an abstract class
     */
    public void getAllDocumentsFromFirestore(OnDocumentsFetchedListener<T> listener) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        db.collection(tClass.getDeclaredConstructor().newInstance().getCollectionPath())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<T> documentList = task.getResult().toObjects(tClass);
                        documentList = manipulateResults(documentList, listener);
                        listener.onDocumentsFetched(documentList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Allows to subclass to manipulate the results of the fetch before returning them by overriding this method
     *
     * @param documents the documents to be manipulated
     * @return the manipulated documents
     */
    protected List<T> manipulateResults(List<T> documents, OnDocumentsFetchedListener<T> listener) {
        List<T> manipulatedDocuments = new ArrayList<T>(documents.size()) {
        };
        for (T document : documents) {
            manipulatedDocuments.add(manipulateResult(document, null)); // TODO
        }
        return manipulatedDocuments;
    }

    /**
     * Sets an image for an ImageView from Firebase Storage
     *
     * @param context   the context of the activity that calls this method
     * @param imagePath the path to the image in Firebase Storage
     * @param imageView the ImageView to set the image for
     */
    public static void setImageForView(Context context, String imagePath, ImageView imageView) {
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
                    .into((ImageView) imageView);
        } else {
            // Download the image from Firebase Storage and save it locally
            imageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                // Once downloaded, load the image from the local file
                Glide.with(context)
                        .load(localFile)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .into((ImageView) imageView);
                Log.d("ItemsDetailActivity", "Image downloaded and cached: " + localFile.getPath());
            }).addOnFailureListener(exception -> {
                // Handle the error gracefully, e.g., show a placeholder image
                Glide.with(context)
                        .load(R.drawable.error_image)
                        .into((ImageView) imageView);
                Log.e("ItemsDetailActivity", "Error getting download URL", exception);
            });
        }
    }

    /**
     * Listener interface for fetching an single item
     */
    public interface OnDocumentsFetchedListener<T> {
        void onDocumentFetched(T document);

        void onDocumentsFetched(List<T> documents);
    }
}
