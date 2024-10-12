package iteMate.project.repositories;

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