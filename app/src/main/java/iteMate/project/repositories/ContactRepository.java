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
}
