package iteMate.project.models;

public class Settings {

    // Allows manipulation of advanced feature settings
    private boolean advancedMode;

    private boolean manualItemCreation;
    private boolean manualTrackCreation;
    private boolean manualTrackManagementOfItems;
    private boolean hideDeleteItemButton;

    public Settings() {
        this.advancedMode = false;
        this.manualItemCreation = false;
        this.manualTrackCreation = false;
        this.manualTrackManagementOfItems = false;
        this.hideDeleteItemButton = false;
    }

    public boolean isAdvancedMode() {
        return advancedMode;
    }

    public boolean isManualItemCreation() {
        return manualItemCreation;
    }

    public boolean isManualTrackCreation() {
        return manualTrackCreation;
    }

    public boolean isManualTrackManagementOfItems() {
        return manualTrackManagementOfItems;
    }

    public boolean isHideDeleteItemButton() {
        return hideDeleteItemButton;
    }

    public void setAdvancedMode(boolean advancedMode) {
        this.advancedMode = advancedMode;
    }

    public void setManualItemCreation(boolean manualItemCreation) {
        this.manualItemCreation = manualItemCreation;
    }

    public void setManualTrackCreation(boolean manualTrackCreation) {
        this.manualTrackCreation = manualTrackCreation;
    }

    public void setManualTrackManagementOfItems(boolean manualTrackManagementOfItems) {
        this.manualTrackManagementOfItems = manualTrackManagementOfItems;
    }

    public void setHideDeleteItemButton(boolean hideDeleteItemButton) {
        this.hideDeleteItemButton = hideDeleteItemButton;
    }
}
