package iteMate.project.model;

public interface DocumentEquivalent {

    /**
     * Collection name in database
     */
    String getCollectionPath();

    /**
     * ID of the document
     * @return the ID of the document
     */
    String getId();

    /**
     * Set the ID of the document
     * @param id the ID of the document
     */
    void setId(String id);
}
