package iteMate.project.controller;

import iteMate.project.models.Contact;
import iteMate.project.repositories.ContactRepository;

public class ContactController {
    public static ContactController contactController;

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
}
