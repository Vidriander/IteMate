package iteMate.project.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * Track class to store detail of a lend out item.
 * All attributes that should not be stored in databasee are annotated with @Exclude (their getters are annotated)
 */
public class Track implements DocumentEquivalent {
    // region Attributes

    /**
     * Collection name in database
     */
    private static final String collectionPath = "tracks";

    /**
     * ID of the track in the database
     */
    private String id;

    /**
     * Date on which the item was given out
     */
    private final Timestamp giveOutDate;

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
     * Status of the track, true = lent out, false = returned
     */
    private boolean active;

    /**
     * List of all items initially lent out
     */
    private List<Item> lentItemsList;

    /**
     * List of item IDs lent out
     */
    private List<String> lentItemIDs = new ArrayList<>();

    /**
     * List of items yet to be returned
     */
    private List<Item> pendingItemsList;

    /**
     * List of IDs of items yet to be returned
     */
    private List<String> pendingItemIDs = new ArrayList<>();

    private List<Item> returnedItemsList;

    private List<String> returnedItemIDs = new ArrayList<>();

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
        giveOutDate = new Timestamp(new Date());
        returnDate = new Timestamp(new Date());
        setContact(new Contact());
        additionalInfo = "";
        lentItemsList = new ArrayList<>();
        lentItemIDs = new ArrayList<>();
        pendingItemsList = new ArrayList<>();
        pendingItemIDs = new ArrayList<>();
        returnedItemsList = new ArrayList<>();
        returnedItemIDs = new ArrayList<>();
        ownerID = "TODO";
    }

    public Track(Timestamp giveOutDate, Timestamp returnDate, Contact contact, String contactID, List<Item> lentItemsList, List<String> lentItemIDs, String ownerID, String additionalInfo) throws NullPointerException, IllegalArgumentException {
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
     *
     * @return the giveOutDate
     */
    public Timestamp getGiveOutDate() {
        return giveOutDate;
    }

    /**
     * Getter for the date the item was given out in a readable format
     *
     * @return the giveOutDate in a readable format
     */
    @Exclude
    public String getReadableGiveOutDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault());
        return sdf.format(giveOutDate.toDate());
    }

    /**
     * Getter for the date the item is to be returned
     *
     * @return the returnDate
     */
    public Timestamp getReturnDate() {
        return returnDate;
    }

    /**
     * Getter for the contact to whom the item is given
     *
     * @return the contact
     */
    @Exclude
    public Contact getContact() {
        return contact;
    }

    /**
     * Getter for the ID of the contact to whom the item is given
     *
     * @return the contactID
     */
    public String getContactID() {
        return contactID;
    }

    /**
     * Getter for the status active:true = lent out, false = returned
     *
     * @return if the track has items that are not returned
     */
    @Exclude
    public boolean isActive() {
        return active;
    }

    /**
     * Getter for the list of items lent out
     *
     * @return the lentItemsList
     */
    @Exclude
    public List<Item> getLentItemsList() {
        return lentItemsList;
    }

    /**
     * Getter for the list of IDs of items lent out
     *
     * @return the lentItemIDs
     */
    public List<String> getLentItemIDs() {
        return lentItemIDs;
    }

    /**
     * Getter for the list of items yet to be returned
     *
     * @return the pendingItemsList
     */
    @Exclude
    public List<Item> getPendingItemsList() {
        return pendingItemsList;
    }

    /**
     * Getter for the IDs of items yet to be returned
     *
     * @return the pendingItemIDs
     */
    public List<String> getPendingItemIDs() {
        return pendingItemIDs;
    }


    /**
     * Getter for the list of items that have been returned
     *
     * @return the returnedItemsList
     */
    @Exclude
    public List<Item> getReturnedItemsList() {
        return returnedItemsList;
    }

    /**
     * Getter for the IDs of items that have been returned
     *
     * @return the returnedItemIDs
     */
    public List<String> getReturnedItemIDs() {
        return returnedItemIDs;
    }

    /**
     * Getter for the ID of the owner of the item
     *
     * @return the ownerID
     */
    public String getOwnerID() {
        return ownerID;
    }

    /**
     * Method to get the number of items in the list
     *
     * @return the number of items
     */
    @Exclude
    public int getNumberOfItems() {
        return lentItemIDs.size();
    }

    /**
     * Method to get the number of days left for the item to be returned
     *
     * @return the number of days left
     */
    @Exclude
    public int getDaysLeft() {
        if (returnDate == null) {
            return 0;
        }
        long millisecondsLeft = returnDate.toDate().getTime() - new Date().getTime();
        return (int) (millisecondsLeft / (1000 * 60 * 60 * 24)) + 1; // +1 because of the rounding (might produce different error)
    }

    /**
     * Getter for the additional information about the track
     *
     * @return the additionalInfo
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Method to get the path of the collection
     *
     * @return the ID of the document
     */
    @Override
    @Exclude
    public String getCollectionPath() {
        return collectionPath;
    }

    @Override
    @Exclude
    public String getId() {
        return id;
    }

    /**
     * Method to check if the track is done
     * @return true if the track is done aka all items are returned / there are no pending items
     */
    @Exclude
    public boolean isDone() {
        return pendingItemIDs.isEmpty();
    }
    // endregion

    // region Setters
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Method to set the Contact
     *
     * @param contact the contact to set
     * @throws NullPointerException if contact is null
     */
    public void setContact(Contact contact) throws NullPointerException {
        if (contact == null) {
            throw new NullPointerException("Contact cannot be null");
        }
        this.contact = contact;
        setContactID(contact.getId());
    }

    /**
     * Setter for the contactID
     * @param contactID the contactID to set
     * @throws NullPointerException if contactID is null
     */
    public void setContactID(String contactID) throws NullPointerException {
        if (contactID == null) {
            throw new NullPointerException("ContactID cannot be null");
        }
        this.contactID = contactID;
    }

    /**
     * Setter for the returnDate
     *
     * @param date the returnDate to set
     * @throws NullPointerException if date is null
     */
    public void setReturnDate(Timestamp date) throws NullPointerException {
        if (date == null) {
            throw new NullPointerException("ReturnDate cannot be null");
        }
        this.returnDate = date;
    }

    /**
     * Setter for the ownerID
     *
     * @param ownerID the ownerID to set
     * @throws NullPointerException     if ownerID is null
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
     *
     * @param lentItemIDs the lentItemIDs to set
     * @throws NullPointerException     if lentItemIDs is null
     * @throws IllegalArgumentException if lentItemIDs is empty
     */
    private void setLentItemIDs(List<String> lentItemIDs) throws NullPointerException {
        if (lentItemIDs == null) {
            throw new NullPointerException("LentItemIDs cannot be null");
        }
        this.lentItemIDs = lentItemIDs;
    }

    /**
     * Setter for the list of items lent out
     * Also sets the lentItemIDs automatically
     *
     * @param itemList the list of items to set
     * @throws NullPointerException if itemList is null
     */
    public void setLentItemsList(List<Item> itemList) throws NullPointerException {
        if (itemList == null) {
            throw new NullPointerException("LentItemsList cannot be null");
        }
        // also setting the lentItemIDs
        lentItemIDs.clear();
        for (Item item : itemList) {
            lentItemIDs.add(item.getId());
        }
        this.lentItemsList = itemList;
    }

    /**
     * Setter for the list of pending Items
     *
     * @param pendingItemsList the list of pendingItems to set
     * @throws NullPointerException if pendingItemsList is null
     */
    public void setPendingItemsList(List<Item> pendingItemsList) throws NullPointerException {
        if (pendingItemsList == null) {
            throw new NullPointerException("PendingItemsList cannot be null");
        }
        // also setting the pendingItemIDs
        pendingItemIDs.clear();
        for (Item item : pendingItemsList) {
            pendingItemIDs.add(item.getId());
        }
        this.pendingItemsList = pendingItemsList;
        // setting the active status, active = true if there are pending items
        active = !pendingItemsList.isEmpty();
    }

    /**
     * Setter for the list of returned Item
     *
     * @param returnedItemsList the pendingItemIDs to set
     * @throws NullPointerException if pendingItemIDs is null
     */
    public void setReturnedItemsList(List<Item> returnedItemsList) throws NullPointerException {
        // TODO: check if this can be removed
        if (returnedItemsList == null) {
            throw new NullPointerException("ReturnedItemsList cannot be null");
        }
        // also setting the pendingItemIDs
        returnedItemIDs.clear();
        for (Item item : returnedItemsList) {
            returnedItemIDs.add(item.getId());
        }
        this.returnedItemsList = returnedItemsList;
    }

    /**
     * Setter for the list of returned Item IDs
     *
     * @param returnedItemIDs the pendingItemIDs to set
     * @throws NullPointerException if pendingItemIDs is null
     */
    public void setReturnedItemIDs(List<String> returnedItemIDs) throws NullPointerException {
        // TODO: check if this can be removed
        if (returnedItemIDs == null) {
            throw new NullPointerException("ReturnedItemIDs cannot be null");
        }
        this.returnedItemIDs = returnedItemIDs;
    }

    /**
     * Setter for the additional information about the track
     *
     * @param additionalInfo the additionalInfo to set
     * @throws NullPointerException if additionalInfo is null
     */
    public void setAdditionalInfo(String additionalInfo) throws NullPointerException {
        if (additionalInfo == null) {
            throw new NullPointerException("AdditionalInfo cannot be null");
        }
        this.additionalInfo = additionalInfo;
    }

    // endregion

    //region Methods
    //endregion
}