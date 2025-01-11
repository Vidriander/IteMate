package iteMate.project.databaseManager;

import iteMate.project.model.User;

/**
 * Repository class for users
 * This class is responsible for handling all interactions with the database
 * It contains methods for adding, fetching, deleting and updating users
 */
public class UserRepository extends GenericRepository<User> {

    public UserRepository() {
        super(User.class);
    }
}