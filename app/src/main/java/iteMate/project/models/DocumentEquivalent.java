package iteMate.project.models;

public interface DocumentEquivalent {

    /**
     * Collection name in Firestore
     */
    public String getCollectionPath();

    /**
     * ID of the document
     * @return the ID of the document
     */
    public String getId();

    /**
     * Set the ID of the document
     * @param id the ID of the document
     */
    public void setId(String id);
}
