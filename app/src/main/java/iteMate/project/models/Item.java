package iteMate.project.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.R;

/**
 * Item class to store the details of an item
 */
public class Item implements Parcelable, DocumentEquivalent {

    /**
     * Collection name in Firestore
     */
    private static final String collectionPath = "items";

    /**
     * database ID of the item
     */
    private String id;
    /**
     * Fields of an item
     */
    private String nfcTag;
    /**
     * Title of the item
     */
    private String title;
    /**
     * Description of the item
     */
    private String description;
    /**
     * Image path of the item
     */
    private String image;  // needs to have the same name as field in database!
    /**
     * Availability of the item
     */
    private boolean available;  //  unnecessary as this is indicated by having a trackID - #TODO removed
    /**
     * List of items contained in this item
     */
    private ArrayList<Item> containedItems = new ArrayList<Item>();
    /**
     * List of IDs of items contained in this item
     */
    private ArrayList<String> containedItemIDs = new ArrayList<String>();
    /**
     * List of items associated with this item, not necessary to list any
     */
    private ArrayList<Item> associatedItems = new ArrayList<Item>();
    /**
     * List of IDs of items associated with this item
     */
    private ArrayList<String> associatedItemIDs = new ArrayList<String>();
    /**
     * ID of the user who owns the item
     */
    private String ownerID;  // #TODO set ownerID = userID
    /**
     * ID of the track the item is in
     */
    private String activeTrackID;

    private int defaultImage = R.drawable.gradient_background;

    /**
     * Default constructor for an item
     */
    public Item() {
        nfcTag = "0";
        title = "Add Item";
        description = "This is a blob of antimatter. Please handle with care.";
        image = "";
        available = true;
    }

    /**
     * Constructor for an item
     * @param nfcTag NFC Tag of the item
     * @param title Title of the item
     * @param description Description of the item
     * @param image Image path of the item
     * @param available Availability of the item
     * @param containedItems List of items contained in this item
     * @param associatedItems List of items associated with this item, not necessary to list any
     */
    public Item(String id, String nfcTag, String title, String description, String image, boolean available, ArrayList<Item> containedItems, ArrayList<Item> associatedItems) {
        this.id = id;
        this.nfcTag = nfcTag;
        this.title = title;
        this.description = description;
        this.image = image;
        this.available = available;
        if (containedItems != null && !containedItems.isEmpty()) {
            this.containedItems.addAll(containedItems);
        }
        if (associatedItems != null && !associatedItems.isEmpty()) {
            this.associatedItems.addAll(associatedItems);
        }
    }

    /**
     * Constructor for an item from a parcel
     * @param in Parcel to read from
     */
    protected Item(Parcel in) {
        id = in.readString();
        nfcTag = in.readString();
        title = in.readString();
        description = in.readString();
        image = in.readString();
        available = in.readByte() != 0;
        containedItems = in.createTypedArrayList(Item.CREATOR);
        containedItemIDs = in.createStringArrayList();
        associatedItems = in.createTypedArrayList(Item.CREATOR);
        associatedItemIDs = in.createStringArrayList();
        ownerID = in.readString();
        activeTrackID = in.readString();
        defaultImage = in.readInt();
    }

    /**
     * Creator for an item
     */
    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the item to a parcel
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nfcTag);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeTypedList(containedItems);
        dest.writeStringList(containedItemIDs);
        dest.writeTypedList(associatedItems);
        dest.writeStringList(associatedItemIDs);
        dest.writeString(ownerID);
        dest.writeString(activeTrackID);
        dest.writeInt(defaultImage);
    }

    /**
     * Returns the ID of the item
     * @return ID of the item
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * Returns the title of the item
     * @return Title of the item
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the NFC Tag of the item
     * @return NFC Tag of the item
     */
    public String getImage() {
        return image;
    }

    /**
     * Returns the NFC Tag of the item
     * @return NFC Tag of the item
     */
    public String getNfcTag() {
        return nfcTag;
    }

    /**
     * Returns the description of the item
     * @return Description of the item
     */
    public String getDescription() {
        return description;
    }

    public int getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the list of items contained in this item
     * @return List of items contained in this item
     */
    public List<Item> getContainedItems() {
        Log.w("Debugging", "getContainedItems Aufruf: " + containedItems);
        return containedItems;
    }

    /**
     * Returns the list of items associated with this item
     * @return List of items associated with this item
     */
    public List<Item> getAssociatedItems() {
        Log.w("Debugging", "getAssociatedItems Aufruf: " + associatedItems);
        return associatedItems;
    }

    public String getActiveTrackID() {
        return activeTrackID;
    }

    /**
     * Setter for the ID of the item
     * @param id ID of the item
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Setter for the NFC Tag of the item
     * @param description NFC Tag of the item
     * @throws NullPointerException if description is null
     */
    public void setDescription(String description) throws NullPointerException {
        if (description == null) {
            throw new NullPointerException("Description must not be null");
        }
        this.description = description;
    }

    /**
     * Setter for the title of the item
     * @param title title of the item
     * @throws NullPointerException if title is null
     */
    public void setTitle(String title) throws NullPointerException {
        if (title == null) {
            throw new NullPointerException("Title must not be null");
        }
        this.title = title;
    }

    /**
     * setter for the ID of the track the item is in
     * @param activeTrackID ID of the track the item is in
     */
    public void setActiveTrackID(String activeTrackID) {
        this.activeTrackID = activeTrackID;
    }

    /**
     * Setter for the Items contained in this item
     * @param containedItems List of items contained in this item
     */
    public void setContainedItems(ArrayList<Item> containedItems) {
        this.containedItems = containedItems;
        Log.d("Item", "setContainedItems size: " + containedItems.size());
    }

    public void setContainedItemIDs(ArrayList<String> containedItemIDs) {
        this.containedItemIDs = containedItemIDs;
    }

    /**
     * Setter for the items associated with this item
     * @param associatedItems List of items associated with this item
     */
    public void setAssociatedItems(ArrayList<Item> associatedItems) {
        this.associatedItems = associatedItems;
        Log.d("Item", "setAssociatedItems size: " + associatedItems.size());
    }

    public void setAssociatedItemIDs(ArrayList<String> associatedItemIDs) {
        this.associatedItemIDs = associatedItemIDs;
    }

    /**
     * Getter for the contained item IDs
     * @return List of IDs of items contained in this item
     */
    public List<String> getContainedItemIDs() {
        return containedItemIDs;
    }

    /**
     * Getter for the associated item IDs
     * @return List of IDs of items associated with this item
     */
    public List<String> getAssociatedItemIDs() {
        return associatedItemIDs;
    }

    @Override
    public String getCollectionPath() {
        return collectionPath;
    }
}