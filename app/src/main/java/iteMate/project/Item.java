package iteMate.project;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Item class to store the details of an item
 */
public class Item implements Parcelable {
    private int nfcTag;
    private String title;
    private String description;
    private int imageID;
    private boolean available;
    private boolean container;

    // Default Constructor
    public Item() {
    }

    // Constructor
    public Item(int nfcTag, String title, String description, int imageID, boolean available, boolean container) {
        this.nfcTag = nfcTag;
        this.title = title;
        this.description = description;
        this.imageID = imageID;
        this.available = available;
        this.container = container;
    }

    // Parcelable implementation
    protected Item(Parcel in) {
        nfcTag = in.readInt();
        title = in.readString();
        description = in.readString();
        imageID = in.readInt();
        available = in.readByte() != 0;
        container = in.readByte() != 0;
    }

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(nfcTag);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(imageID);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeByte((byte) (container ? 1 : 0));
    }

    // Getter
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImage() {
        return imageID;
    }

    public int getNfcTag() {
        return nfcTag;
    }

    public boolean getIsAvailable() {
        return available;
    }

    public boolean isContainer() {
        return container;
    }

    // Setter
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}