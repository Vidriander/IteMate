package iteMate.project.repositories;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

import iteMate.project.models.User;

/**
 * Repository class for users
 * This class is responsible for handling all interactions with Firestore
 * It contains methods for adding, fetching, deleting and updating users
 */
public class UserRepository extends GenericRepository<User> {

    public UserRepository() {
        super(User.class);
    }
}