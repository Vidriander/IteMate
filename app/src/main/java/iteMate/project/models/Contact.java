package iteMate.project.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Contact class to store the details of a contact
 */
public class Contact implements Parcelable, DocumentEquivalent {

    /**
     * Collection name in Firestore
     */
    private static final String collectionPath = "contacts";

    /**
     * database ID of the contact
     */
    private String id;

    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String street;
    private String city;
    private int zip;
    private String ownerID;  // #TODO setter for ownerID

    // Default Constructor
    public Contact() {
        firstName = "Select";
        lastName = "Contact";
    }

    // Constructor
    public Contact(String firstName, String lastName, String phone, String email, String street, String city, int zip, String ownerID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.street = street;
        this.city = city;
        this.zip = zip;
        this.ownerID = ownerID;
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public String getPhoneNumber() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public int getZip() {
        return zip;
    }

    // Setter
    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    // Parcelable implementation for the Intent
    protected Contact(Parcel in) {
        id = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        phone = in.readString();
        email = in.readString();
        street = in.readString();
        city = in.readString();
        zip = in.readInt();
        ownerID = in.readString();
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(street);
        dest.writeString(city);
        dest.writeInt(zip);
        dest.writeString(ownerID);
    }

    @Override
    public String getCollectionPath() {
        return collectionPath;
    }
}