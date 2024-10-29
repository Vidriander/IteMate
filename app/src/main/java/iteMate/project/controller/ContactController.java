package iteMate.project.controller;

import iteMate.project.models.Contact;
import iteMate.project.repositories.ContactRepository;
import iteMate.project.repositories.GenericRepository;

public class ContactController {

    /**
     * Singleton instance of the ContactController
     */
    public static ContactController contactController;

    private Contact currentContact;
    private final ContactRepository contactRepository;

    private ContactController() {
        this.contactRepository = new ContactRepository();
    }

    public static synchronized ContactController getControllerInstance() {
        if (contactController == null) {
            contactController = new ContactController();
        }
        return contactController;
    }

    public void setCurrentContact(Contact currentContact) {
        this.currentContact = currentContact;
    }

    public Contact getCurrentContact() {
        return currentContact;
    }

    public void fetchAllContactsFromDatabase(GenericRepository.OnDocumentsFetchedListener<Contact> listener) {
        contactRepository.getAllDocumentsFromDatabase(listener);
    }
}
