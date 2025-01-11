package iteMate.project.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User class to store the details of a user
 */
public class User implements Parcelable, DocumentEquivalent {

    /**
     * Collection name in database
     */
    private static final String collectionPath = "users";

    private String id;
    private String email;
    private String password;
    private Settings settings;


    // Default Constructor
    public User() {
    }

    // Constructor
    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.settings = new Settings();
    }

    // Getter
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    // Parcelable implementation
    protected User(Parcel in) {
        email = in.readString();
        password = in.readString();
        //settings = in.readByte() != 0;
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
        //dest.writeByte((byte) (settings ? 1 : 0));
    }

    @Override
    public String getCollectionPath() {
        return collectionPath;
    }
}