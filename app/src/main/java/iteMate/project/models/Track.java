package iteMate.project.models;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Track class to store detail of a lend out item.
 */
public class Track implements Parcelable {

    /**
     * Date on which the item was given out
     */
    private Timestamp giveOutDate;
    /**
     * Date on which the item is to be returned
     */
    private Timestamp returnDate;
    /**
     * Contact to whom the item is given
     */
    private Contact contact;
    /**
     * ID of the contact to whom the item is given
     */
    private String contactID;
    /**
     * List of items lent out
     */
    private List<Item> lentItemsList;
    /**
     * List of item IDs lent out
     */
    private List<String> lentItemIDs = new ArrayList<>();
    /**
     * ID of the owner of the item
     */
    private String ownerID;

    public Track() {
        // Default constructor
    }

    public Track(Timestamp giveOutDate, Timestamp returnDate, Contact contact, String contactID, List<Item> lentItemsList, List<String> lentItemIDs, String ownerID) {
        this.giveOutDate = giveOutDate;
        this.returnDate = returnDate;
        this.contact = contact;
        this.contactID = contactID;
        this.lentItemsList = lentItemsList;
        this.lentItemIDs = lentItemIDs;
        this.ownerID = ownerID;
    }

    public Track(Timestamp giveOutDate, Timestamp returnDate, Contact contact, List<Item> lentItemsList) {
        this.giveOutDate = giveOutDate;
        this.returnDate = returnDate;
        this.contact = contact;
        this.lentItemsList = lentItemsList;
    }

    public Timestamp getGiveOutDate() {
        return giveOutDate;
    }

    public Timestamp getReturnDate() {
        return returnDate;
    }

    public Contact getContact() {
        return contact;
    }

    public String getContactID() {
        return contactID;
    }

    public List<Item> getLentItemsList() {
        return lentItemsList;
    }

    public List<String> getLentItemIDs() {
        return lentItemIDs;
    }

    public void setLentItemIDs(List<String> lentItemIDs) {
        this.lentItemIDs = lentItemIDs;
    }

    public void setLentItemsList(List<Item> itemList) {
        this.lentItemsList = itemList;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    /**
     * Method to get the number of items in the list
     * @return the number of items
     */
    public int getNumberOfItems() {
        return lentItemIDs.size();
    }

    /**
     * Method to get the number of days left for the item to be returned
     * @return the number of days left
     */
    public int getDaysLeft() {
        if (returnDate == null) {
            return 0; // Adjust handling of null returnDate
        }
        long millisecondsLeft = returnDate.toDate().getTime() - new Date().getTime();
        return (int) (millisecondsLeft / (1000 * 60 * 60 * 24));
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Writing each field to the Parcel
        dest.writeLong(giveOutDate != null ? giveOutDate.toDate().getTime() : -1);
        dest.writeLong(returnDate != null ? returnDate.toDate().getTime() : -1);
        dest.writeParcelable(contact, flags);
        dest.writeStringList(lentItemIDs);
        dest.writeTypedList(lentItemsList);
        dest.writeString(ownerID);
    }

    /**
     * Constructor for creating a Track from a Parcel
     * @param in Parcel to read from
     */
    protected Track(Parcel in) {
        long giveOutDateLong = in.readLong();
        giveOutDate = giveOutDateLong != -1 ? new Timestamp(new Date(giveOutDateLong)) : null;
        long returnDateLong = in.readLong();
        returnDate = returnDateLong != -1 ? new Timestamp(new Date(returnDateLong)) : null;
        contact = in.readParcelable(Contact.class.getClassLoader());
        lentItemIDs = in.createStringArrayList();
        lentItemsList = in.createTypedArrayList(Item.CREATOR);
        ownerID = in.readString();
    }
}