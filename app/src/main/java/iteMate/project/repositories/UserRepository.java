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

    /**
     * Fetches all users from Firestore
     * @param listener the listener to be called when the users are fetched
     */
    public void getAllUsersFromFirestore(OnUsersFetchedListener listener) {
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<User> userList = task.getResult().toObjects(User.class);
                        listener.onUsersFetched(userList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Fetches a user from Firestore
     * @param userId the id of the user to be fetched
     * @param listener the listener to be called when the user is fetched
     */
    public void getUserFromFirestore(String userId, OnUsersFetchedListener listener) {
        db.collection("users").whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<User> userList = task.getResult().toObjects(User.class);
                        listener.onUsersFetched(userList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Updates a user in Firestore
     * @param userId the id of the user to be updated
     */
    public void updateUserInFirestore(String userId) {
        db.collection("users").whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("users").document(document.getId())
                                    .update("status", "active")
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "User updated successfully!"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating user", e));
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Deletes a user from Firestore
     * @param userId the id of the user to be deleted
     */
    public void deleteUserFromFirestore(String userId) {
        db.collection("users").whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            db.collection("users").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "User deleted successfully!"))
                                    .addOnFailureListener(e -> Log.w("Firestore", "Error deleting user", e));
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Listener interface for fetching users
     */
    public interface OnUsersFetchedListener {
        void onUsersFetched(List<User> users);
    }
}