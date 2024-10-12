package iteMate.project.repositories;

import iteMate.project.models.Contact;

public class ContactRepository extends GenericRepository<Contact> {

    public ContactRepository() {
        super(Contact.class);
    }
}
