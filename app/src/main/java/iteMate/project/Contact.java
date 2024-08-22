package iteMate.project;
import java.io.Serializable;

/**
 * Contact class to store the details of a contact
 */
public class Contact implements Serializable {
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String address;
    private String image;

    // Default Constructor
    public Contact() {
    }

    // Constructor
    public Contact(String firstName, String lastName, String phone, String email, String address, String image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.image = image;
    }

    // Getter
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getImage() {
        return image;
    }

    // Setter
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

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
