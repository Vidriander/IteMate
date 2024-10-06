package iteMate.project.repositories;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

import iteMate.project.models.Contact;
import iteMate.project.models.Item;

public class ContactRepository extends GenericRepository<Contact> {

    public ContactRepository() {
        super(Contact.class);
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

    public void updateContactInFirestore(Contact contact) {
        db.collection("contacts").document(contact.getId())
                .set(contact)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    Log.d("ContactRepository", "Contact successfully updated!");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.w("ContactRepository", "Error updating contact", e);
                });
    }

    public void deleteContactFromFirestore(Contact contact) {
        db.collection("contacts").document(contact.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    Log.d("ContactRepository", "Contact successfully deleted!");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.w("ContactRepository", "Error deleting contact", e);
                });
    }

    /**
     * Listener interface for fetching items
     */
    public interface OnContactsFetchedListener {
        void onContactsFetched(List<Contact> contacts);
    }
}
