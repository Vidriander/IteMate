package iteMate.project.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Contact class to store the details of a contact
 */
public class Contact implements Parcelable {

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
    }

    // Constructor
    public Contact(String firstName, String lastName, String phone, String email, String street, String city, int zip ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.street = street;
        this.city = city;
        this.zip = zip;
    }

    // Getter
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

    // Parcelable implementation
    protected Contact(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        phone = in.readString();
        email = in.readString();
        street = in.readString();
        city = in.readString();
        zip = in.readInt();
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
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(street);
        dest.writeString(city);
        dest.writeInt(zip);
    }
}