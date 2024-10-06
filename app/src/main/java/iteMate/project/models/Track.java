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
public class Track implements Parcelable, DocumentEquivalent {
    // region Attributes

    /**
     * Collection name in Firestore
     */
    private static final String collectionPath = "tracks";
    /**
     * ID of the track in the database
     */
    private String id;
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
    /**
     * Additional information about the track
     */
    private String additionalInfo;
    // endregion

    // region Constructors
    public Track() {
        // Default constructor
    }

    public Track(Timestamp giveOutDate, Timestamp returnDate, Contact contact, String contactID, List<Item> lentItemsList, List<String> lentItemIDs, String ownerID, String additionalInfo) {
        this.giveOutDate = giveOutDate;
        this.returnDate = returnDate;
        setContact(contact);
        this.contactID = contactID;
        setLentItemsList(lentItemsList);
        setLentItemIDs(lentItemIDs);
        setOwnerID(ownerID);
        setAdditionalInfo(additionalInfo);
    }
    // endregion

    //region Getters

    /**
     * Getter for the date the item was given out
     * @return the giveOutDate
     */
    public Timestamp getGiveOutDate() {
        return giveOutDate;
    }

    /**
     * Getter for the date the item is to be returned
     * @return the returnDate
     */
    public Timestamp getReturnDate() {
        return returnDate;
    }

    /**
     * Getter for the contact to whom the item is given
     * @return the contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Getter for the ID of the contact to whom the item is given
     * @return the contactID
     */
    public String getContactID() {
        return contactID;
    }

    /**
     * Getter for the list of items lent out
     * @return the lentItemsList
     */
    public List<Item> getLentItemsList() {
        return lentItemsList;
    }

    /**
     * Getter for the list of IDs of items lent out
     * @return the lentItemIDs
     */
    public List<String> getLentItemIDs() {
        return lentItemIDs;
    }

    /**
     * Getter for the ID of the owner of the item
     * @return the ownerID
     */
    public String getOwnerID() {
        return ownerID;
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
            return 0;
        }
        long millisecondsLeft = returnDate.toDate().getTime() - new Date().getTime();
        return (int) (millisecondsLeft / (1000 * 60 * 60 * 24));
    }

    /**
     * Getter for the additional information about the track
     * @return the additionalInfo
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Method to get the path of the collection
     * @return the ID of the document
     */
    @Override
    public String getCollectionPath() {
        return collectionPath;
    }

    @Override
    public String getId() {
        return null;
    }
    // endregion

    // region Setters
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Method to set the Contact
     * @param contact the contact to set
     * @throws NullPointerException if contact is null
     */
    public void setContact(Contact contact) throws NullPointerException {
        if (contact == null) {
            throw new NullPointerException("Contact cannot be null");
        }
        this.contact = contact;
    }

    /**
     * Setter for the ownerID
     * @param ownerID the ownerID to set
     * @throws NullPointerException if ownerID is null
     * @throws IllegalArgumentException if ownerID is empty
     */
    public void setOwnerID(String ownerID) throws NullPointerException, IllegalArgumentException {
        if (ownerID == null) {
            throw new NullPointerException("OwnerID cannot be null");
        }
        if (ownerID.isEmpty()) {
            throw new IllegalArgumentException("OwnerID cannot be empty");
        }
        this.ownerID = ownerID;
    }

    /**
     * Setter for the list of lentItemIDs
     * @param lentItemIDs the lentItemIDs to set
     * @throws NullPointerException if lentItemIDs is null
     */
    public void setLentItemIDs(List<String> lentItemIDs) throws NullPointerException {
        if (lentItemIDs == null) {
            throw new NullPointerException("LentItemIDs cannot be null");
        }
        this.lentItemIDs = lentItemIDs;
    }

    /**
     * Setter for the list of items lent out
     * @param itemList the list of items to set
     * @throws NullPointerException if itemList is null
     */
    public void setLentItemsList(List<Item> itemList) throws NullPointerException {
        if (itemList == null) {
            throw new NullPointerException("LentItemsList cannot be null");
        }
        this.lentItemsList = itemList;
    }

    /**
     * Setter for the additional information about the track
     * @param additionalInfo the additionalInfo to set
     * @throws NullPointerException if additionalInfo is null
     */
    private void setAdditionalInfo(String additionalInfo) throws NullPointerException {
        if (additionalInfo == null) {
            throw new NullPointerException("AdditionalInfo cannot be null");
        }
        this.additionalInfo = additionalInfo;
    }
    // endregion

    //region Parcelable implementation
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
        dest.writeString(contactID);
        dest.writeString(additionalInfo);
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
        contactID = in.readString();
        additionalInfo = in.readString();
    }
    // endregion
}