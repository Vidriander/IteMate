package iteMate.project;
import java.io.Serializable;

/**
 * Item class to store the details of an item
 */
public class Item implements Serializable {
    private int nfcTag;
    private String title;
    private String description;
    private int imageID;
    private boolean available;
    private boolean container;

    //Default Constructor
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
