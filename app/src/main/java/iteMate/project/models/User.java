package iteMate.project.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User class to store the details of a user
 */
public class User implements Parcelable, DocumentEquivalent {

    /**
     * Collection name in Firestore
     */
    private static final String collectionPath = "users";

    private String email;
    private String password;

    // Default Constructor
    public User() {
    }

    // Constructor
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    // Parcelable implementation
    protected User(Parcel in) {
        email = in.readString();
        password = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(password);
    }

    @Override
    public String getCollectionPath() {
        return collectionPath;
    }
}