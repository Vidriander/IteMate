package iteMate.project.controller;

import java.util.Objects;

import iteMate.project.models.Contact;
import iteMate.project.repositories.ContactRepository;
import iteMate.project.repositories.listeners.OnMultipleDocumentsFetchedListener;

/**
 * Controller for managing contacts
 */
public class ContactController {

    /**
     * Singleton instance of the ContactController
     */
    private Contact currentContact;
    private static ContactController controllerInstance;
    private final ContactRepository contactRepository;

    /**
     * Constructor for a Singleton instance of the ContactController
     */
    private ContactController() {
        this.contactRepository = new ContactRepository();
    }

    /**
     * Returns the singleton instance of the ContactController
     * @return the singleton instance of the ContactController
     */
    public static synchronized ContactController getControllerInstance() {
        if (controllerInstance == null) {
            controllerInstance = new ContactController();
        }
        return controllerInstance;
    }

    /**
     * Sets the current contact that is being displayed or edited
     * @param currentContact the current contact
     */
    public void setCurrentContact(Contact currentContact) {
        this.currentContact = currentContact;
    }

    /**
     * Returns the current contact that is being displayed or edited
     * @return the current contact
     */
    public Contact getCurrentContact() {
        return currentContact;
    }

    /**
     * Fetches all contacts from the database
     * @param listener the listener that will be called when the contacts are fetched
     */
    public void fetchAllContactsFromDatabase(OnMultipleDocumentsFetchedListener<Contact> listener) {
        contactRepository.getAllDocumentsFromDatabase(listener);
    }

    /**
     * Saves the current contact in the database
     */
    public void saveContactToDatabase(Contact contact) {
        if (Objects.equals(currentContact.getId(), null) || currentContact.getId().isEmpty()) {
            contactRepository.addDocumentToDatabase(currentContact);
        } else {
            contactRepository.updateDocumentInDatabase(currentContact);
        }
    }

    /**
     * Deletes the current contact from the database
     */
    public void deleteContactFromDatabase(Contact contact) {
        contactRepository.deleteDocumentFromDatabase(contact);
    }
}
