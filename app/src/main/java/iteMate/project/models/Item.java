package iteMate.project.models;

import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

/**
 * Item class to store the details of an item
 */
public class Item implements DocumentEquivalent {
    // region Attributes
    /**
     * Collection name in database
     */
    private static final String collectionPath = "items";

    /**
     * Default image path for an item
     */
    private static final String DEFAULT_IMAGE_PATH = "itemImages/default_image.jpg";

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
    private boolean available;
    /**
     * List of items contained in this item
     */
    private ArrayList<Item> containedItems = new ArrayList<>();
    /**
     * List of IDs of items contained in this item
     */
    private ArrayList<String> containedItemIDs = new ArrayList<>();
    /**
     * List of items associated with this item, not necessary to list any
     */
    private ArrayList<Item> associatedItems = new ArrayList<>();
    /**
     * List of IDs of items associated with this item
     */
    private ArrayList<String> associatedItemIDs = new ArrayList<>();
    /**
     * ID of the user who owns the item
     */
    private final String ownerID;  // #TODO set ownerID = userID
    /**
     * ID of the track the item is in
     */
    private String activeTrackID;
    // endregion

    // region Constructors
    /**
     * Default constructor for an item
     */
    public Item() {
        nfcTag = "";
        title = "";
        description = "";
        resetImageToDefault();
        available = true;
        ownerID = "";
        // activeTrackID = "";
    }

    /**
     * Constructor for an item
     *
     * @param nfcTag          NFC Tag of the item
     * @param title           Title of the item
     * @param description     Description of the item
     * @param image           Image path of the item
     * @param available       Availability of the item
     * @param containedItems  List of items contained in this item
     * @param associatedItems List of items associated with this item, not necessary to list any
     */
    public Item(String id, String nfcTag, String title, String description, String image, boolean available,
                ArrayList<Item> containedItems, ArrayList<Item> associatedItems, String ownerID, String activeTrackID) {
        setId(id);
        setNfcTag(nfcTag);
        setTitle(title);
        setDescription(description);
        this.image = image;
        setAvailable(available);
        setAssociatedItems(associatedItems); // IDs are not set here. willingly?
        setContainedItems(containedItems);
        this.ownerID = ownerID;
        this.activeTrackID = activeTrackID;
    }
    // endregion

    // region Getter
    /**
     * Returns the ID of the item
     *
     * @return ID of the item
     */
    @Override
    @Exclude
    public String getId() {
        return id;
    }

    /**
     * Returns the title of the item
     *
     * @return Title of the item
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the NFC Tag of the item
     *
     * @return NFC Tag of the item
     */
    public String getImage() {
        return image;
    }

    /**
     * Returns the NFC Tag of the item
     *
     * @return NFC Tag of the item
     */
    public String getNfcTag() {
        return nfcTag;
    }

    /**
     * Returns the availability of the item
     *
     * @return Availability of the item
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Returns the description of the item
     *
     * @return Description of the item
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the list of items contained in this item
     *
     * @return List of items contained in this item
     */
    @Exclude
    public List<Item> getContainedItems() {
        return containedItems;
    }

    /**
     * Returns the list of items associated with this item
     *
     * @return List of items associated with this item
     */
    @Exclude
    public List<Item> getAssociatedItems() {
        return associatedItems;
    }

    public String getActiveTrackID() {
        return activeTrackID;
    }

    /**
     * Getter for the contained item IDs
     *
     * @return List of IDs of items contained in this item
     */
    public List<String> getContainedItemIDs() {
        return containedItemIDs;
    }

    /**
     * Getter for the associated item IDs
     *
     * @return List of IDs of items associated with this item
     */
    public List<String> getAssociatedItemIDs() {
        return associatedItemIDs;
    }

    @Override
    @Exclude
    public String getCollectionPath() {
        return collectionPath;
    }

    // endregion

    // region Setter
    /**
     * Setter for the ID of the item
     *
     * @param id ID of the item
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Setter for the NFC Tag of the item
     *
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
     *
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
     * Setter for the nfc tag ID of the item
     *
     * @param tagId NFC Tag of the item
     * @throws NullPointerException     if tagId is null
     * @throws IllegalArgumentException if tagId is empty
     */
    public void setNfcTag(String tagId) throws NullPointerException {
        if (tagId == null) {
            throw new NullPointerException("NFC Tag must not be null");
        }
        this.nfcTag = tagId;
    }

    /**
     * setter for the ID of the track the item is in
     *
     * @param activeTrackID ID of the track the item is in
     */
    public void setActiveTrackID(String activeTrackID) {
        this.activeTrackID = activeTrackID;
        setAvailable(activeTrackID == null || activeTrackID.isEmpty());
    }

    /**
     * Setter for status of the item
     */
    private void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Setter for the Items contained in this item.
     *
     * @param containedItems List of items contained in this item
     */
    public void setContainedItems(ArrayList<Item> containedItems) throws NullPointerException {
        if (containedItems == null) {
            throw new NullPointerException("Contained items must not be null");
        }
        this.containedItems = containedItems;
    }

    /**
     * Setter for the Items contained in this item
     *
     * @param containedItemIDs List of items contained in this item
     */
    public void setContainedItemIDs(ArrayList<String> containedItemIDs) throws NullPointerException {
        if (containedItemIDs == null) {
            throw new NullPointerException("Contained item IDs must not be null");
        }
        this.containedItemIDs = containedItemIDs;
    }

    /**
     * Setter for the items associated with this item
     *
     * @param associatedItems List of items associated with this item
     */
    public void setAssociatedItems(ArrayList<Item> associatedItems) throws NullPointerException {
        if (associatedItems == null) {
            throw new NullPointerException("Associated items must not be null");
        }
        this.associatedItems = associatedItems;
    }

    /**
     * Setter for the items associated with this item
     *
     * @param associatedItemIDs List of items associated with this item
     */
    public void setAssociatedItemIDs(ArrayList<String> associatedItemIDs) throws NullPointerException {
        if (associatedItemIDs == null) {
            throw new NullPointerException("Associated item IDs must not be null");
        }
        this.associatedItemIDs = associatedItemIDs;
    }

    public void setImage(String image) {
        this.image = image;
    }
    // endregion

    /**
     * Reset the image of the item to the default image
     */
    public void resetImageToDefault() {
        image = DEFAULT_IMAGE_PATH;
    }

    /**
     * Returns a deep copy of the item
     * @return deep copy of the item
     */
    @Exclude
    public Item getDeepCopy() {
        Item newItem = new Item(this.id, this.nfcTag, this.title, this.description, this.image, this.available, this.containedItems, this.associatedItems, this.ownerID, this.activeTrackID);
        newItem.setContainedItemIDs(new ArrayList<>(this.containedItemIDs));
        newItem.setAssociatedItemIDs(new ArrayList<>(this.associatedItemIDs));
        return newItem;
    }
}