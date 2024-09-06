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

    /**
     * Fetches all contacts from Firestore
     * @param listener the listener to be called when the contacts are fetched
     */
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

    /**
     * Fetches a contact from Firestore
     * @param contactId the id of the contact to be fetched
     * @param listener the listener to be called when the contact is fetched
     */
    public void getContactFromFirestore(String contactId, OnContactsFetchedListener listener) {
        db.collection("contacts").whereEqualTo("contactId", contactId)
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

    /**
     * Updates a contact in Firestore
     * @param contactId the id of the contact to be updated
     */
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
     * Deletes a contact from Firestore
     * @param contactId the id of the contact to be deleted
     */
    public void deleteContactFromFirestore(String contactId) {
        db.collection("contacts").whereEqualTo("contactId", contactId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("contacts").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Contact deleted with ID: " + document.getId());
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error deleting contact", e);
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
