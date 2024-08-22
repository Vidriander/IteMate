package iteMate.project;
import java.io.Serializable;
import java.util.List;
import java.util.Date;

/**
 * Track class to store detail of a lend out item.
 */
public class Track implements Serializable {
    private Date giveOutDate;
    private Date returnDate;
    private Contact contact;
    private List LendList;

    // Default Constructor
    public Track() {
    }

    // Constructor
    public Track(Date giveOutDate, Date returnDate, Contact contact, List LendList) {
        this.giveOutDate = giveOutDate;
        this.returnDate = returnDate;
        this.contact = contact;
        this.LendList = LendList;
    }

    // Getter
    public Date getGiveOutDate() {
        return giveOutDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public Contact getContact() {
        return contact;
    }

    public List getLendList() {
        return LendList;
    }

    // Setter
    public void setGiveOutDate(Date giveOutDate) {
        this.giveOutDate = giveOutDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setLendList(List LendList) {
        this.LendList = LendList;
    }

    public void addLendList(Item item) {
        LendList.add(item);
    }

    public void removeLendList(Item item) {
        LendList.remove(item);
    }

}