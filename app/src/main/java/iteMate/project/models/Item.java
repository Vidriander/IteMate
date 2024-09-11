package iteMate.project.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.R;

/**
 * Item class to store the details of an item
 */
public class Item implements Parcelable {
    /**
     * Fields of an item
     */
    private int nfcTag;
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
    private boolean available;
    /**
     * List of items contained in this item
     */
    private ArrayList<Item> containedItems = new ArrayList<Item>();
    /**
     * List of items associated with this item, not necessary to list any
     */
    private ArrayList<Item> associatedItems = new ArrayList<Item>();
    /**
     * ID of the user who owns the item
     */
    private String ownerID;  // #TODO set ownerID = userID
    /**
     * ID of the track the item is in
     */
    private int trackID = -1;

    private int defaultImage = R.drawable.gradient_background;

    /**
     * Default constructor for an item
     */
    public Item() {
        nfcTag = 0;
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
    public Item(int nfcTag, String title, String description, String image, boolean available, ArrayList<Item> containedItems, Item... associatedItems) {
        this.nfcTag = nfcTag;
        this.title = title;
        this.description = description;
        this.image = image;
        this.available = available;
        if (containedItems != null && !containedItems.isEmpty()) {
            this.containedItems.addAll(containedItems);
        }
    }

    /**
     * Constructor for an item from a parcel
     * @param in Parcel to read from
     */
    protected Item(Parcel in) {
        nfcTag = in.readInt();
        title = in.readString();
        description = in.readString();
        image = in.readString();
        available = in.readByte() != 0;
        containedItems = in.createTypedArrayList(Item.CREATOR);
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
        dest.writeInt(nfcTag);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeTypedList(containedItems);
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
    public int getNfcTag() {
        return nfcTag;
    }

    /**
     * Returns the description of the item
     * @return Description of the item
     */
    public String getDescription() {
        return description;
    }

    /**
     * Tells if the item is a container
     * @return True if the item has subitems in it (containedItems is not empty), false otherwise
     */
    public boolean isContainer() {
        return !containedItems.isEmpty();
    }

    public int getDefaultImage() {
        return defaultImage;
    }

    /**
     * Returns the list of items contained in this item
     * @return List of items contained in this item
     */
    public List<Item> getContainedItems() {
        return containedItems;
    }

    /**
     * Returns the list of items associated with this item
     * @return List of items associated with this item
     */
    public List<Item> getAssociatedItems() {
        return associatedItems;
    }

    /**
     * setter for the ID of the track the item is in
     * @param trackID ID of the track the item is in
     */
    public void setTrackID(int trackID) {
        this.trackID = trackID;
    }
}