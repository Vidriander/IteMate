package iteMate.project;
import java.io.Serializable;

public class Item implements Serializable {
    private String title;
    private String description;

    // Konstruktor
    public Item(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Getter-Methoden
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    // Setter-Methoden
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
