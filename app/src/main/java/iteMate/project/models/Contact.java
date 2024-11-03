package iteMate.project.models;

import com.google.firebase.firestore.Exclude;

/**
 * Contact class to store the details of a contact
 */
public class Contact implements DocumentEquivalent {

    /**
     * Collection name in database
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
    private String zip;
    private String ownerID;

    // Default Constructor
    public Contact() {
        firstName = "";
        lastName = "";
        id = "";
    }

    // Constructor
    public Contact(String firstName, String lastName, String phone, String email, String street, String city, String zip, String ownerID) {
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
    @Exclude
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

    public String getZip() {
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

    public void setZip(String zip) {
        this.zip = zip;
    }

    @Override
    public String getCollectionPath() {
        return collectionPath;
    }
}