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
    private String address;
    private String city;

    // Default Constructor
    public Contact() {
    }

    // Constructor
    public Contact(String firstName, String lastName, String phone, String email, String address, String city) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.city = city;
    }

    // Getter
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    // Parcelable implementation
    protected Contact(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        phone = in.readString();
        email = in.readString();
        address = in.readString();
        city = in.readString();
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
        dest.writeString(address);
        dest.writeString(city);
    }
}