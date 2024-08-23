package iteMate.project.repositories;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

import iteMate.project.models.Contact;
import iteMate.project.models.Item;

public class ContactRepository {
    private FirebaseFirestore db;

    public ContactRepository() {
        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
    }

    public void addContactToFirestore(Contact contact) {
        db.collection("contacts").add(contact)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Contact added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding contact", e);
                });
    }

    public void getAllContactsFromFirestore(OnContactsFetchedListener listener) {
        db.collection("contacts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Contact> contactList = task.getResult().toObjects(Contact.class);
                        listener.onContactsFetched(contactList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    public void updateContactInFirestore(String contactId) {
        db.collection("contacts").whereEqualTo("contactId", contactId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("contacts").document(document.getId())
                                    .update("contactId", contactId)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Contact updated with ID: " + document.getId());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error updating contact", e);
                                    });
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Listener interface for fetching items
     */
    public interface OnContactsFetchedListener {
        void onContactsFetched(List<Contact> contacts);
    }
}
