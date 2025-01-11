package iteMate.project.databaseManager;

import iteMate.project.model.Contact;

public class ContactRepository extends GenericRepository<Contact> {

    public ContactRepository() {
        super(Contact.class);
    }
}
