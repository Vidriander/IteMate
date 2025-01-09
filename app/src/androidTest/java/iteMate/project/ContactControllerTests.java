package iteMate.project;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.Test;
import org.junit.jupiter.api.Order;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import iteMate.project.controller.ContactController;
import iteMate.project.models.Contact;

public class ContactControllerTests {

    // region Constants [IMPORTANT: constants might need to be changed manually when database is updated]
    private static final int NUMBER_OF_CONTACTS = 8;
    /**
     * Timeout for async operations in seconds
     */
    private static final int ASYNC_TIMEOUT_SECONDS = 4;
    // endregion

    /**
     * Test that the singleton instance of the ContactController is returned when calling getControllerInstance multiple times
     */
    @Order(1)
    @Test
    public void singletonBehaviorTest() {
        ContactController controller1 = ContactController.getControllerInstance();
        ContactController controller2 = ContactController.getControllerInstance();
        assertEquals(controller1, controller2);
    }

    /**
     * Test that the current contact is set to null when setCurrentContact is called with a null contact.
     * No exception should be thrown.
     */
    @Order(2)
    @Test
    public void setCurrentContactAcceptNullContactTest() {
        ContactController controller = ContactController.getControllerInstance();
        controller.setCurrentContact(null);
        assertNull(controller.getCurrentContact());
    }

    /**
     * Test that the current contact is set to the contact that is passed to setCurrentContact.
     * The call to getCurrentContact should return the same contact.
     */
    @Order(3)
    @Test
    public void setAndGetCurrentContactTest() {
        ContactController controller = ContactController.getControllerInstance();
        Contact contact = new Contact();
        controller.setCurrentContact(contact);
        assertEquals(contact, controller.getCurrentContact());
    }

    /**
     * Test that the fetchAllContactsFromDatabase method fetches all contacts from the database.
     * The fetched documents should be of the correct type and not null.
     * @throws ExecutionException if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws TimeoutException if the wait timed out
     */
    @Order(4)
    @Test
    public void fetchAllContactsFromDatabaseTest() throws ExecutionException, InterruptedException, TimeoutException {
        ContactController controller = ContactController.getControllerInstance();
        CompletableFuture<Void> future = new CompletableFuture<>();
        controller.fetchAllContactsFromDatabase(documents -> {
            try {
                // Check that the correct number of documents are fetched
                assertEquals(NUMBER_OF_CONTACTS, documents.size());
                for (Contact contact : documents) {
                    // Check that the fetched documents are of the correct type
                    assertInstanceOf(Contact.class, contact);
                    // Check that the fetched documents are not null
                    assertNotNull(contact);
                }
                future.complete(null);
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });
        // This will block until the future is completed or times out
        future.get(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Test that a new contact can be saved to the database and then deleted.
     * The number of contacts should increase by 1 after saving and decrease by 1 after deleting.
     * The new contact should be in the list after saving and not in the list after deleting.
     * @throws ExecutionException thrown when attempting to retrieve the result of a task that aborted by throwing an exception
     * @throws InterruptedException thrown when a thread is waiting, sleeping, or otherwise occupied, and the thread is interrupted
     * @throws TimeoutException thrown when a blocking operation times out
     */
    @Order(5)
    @Test
    public void saveAndDeleteNewContactTest() throws ExecutionException, InterruptedException, TimeoutException {
        ContactController controller = ContactController.getControllerInstance();
        Contact contact = new Contact("John", "Doe", "1234567890", "doe@doe.doe", "Doe Street", "Doe City", "12345", "ownerID");
        controller.setCurrentContact(contact);
        controller.saveContactToDatabase();
        AtomicReference<String> contactId = new AtomicReference<>();

        // testing, if the number of contacts has increased by 1 and the new contact is in the list
        CompletableFuture<Void> future = new CompletableFuture<>();
        controller.fetchAllContactsFromDatabase(documents -> {
            try {
                // Check that the correct number of documents are fetched
                assertEquals(NUMBER_OF_CONTACTS + 1, documents.size());
                // checking of the new contact is in the list
                boolean found = false;
                for (Contact c : documents) {
                    if (c.getEmail().equals(contact.getEmail())) {
                        found = true;
                        contactId.set(c.getId());
                        break;
                    }
                }
                assertTrue(found);
                future.complete(null);
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });
        // This will block until the future is completed or times out
        future.get(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        contact.setId(contactId.get());
        controller.deleteContactFromDatabase(contact);
        Thread.sleep(1000); // wait for the database to update
        // testing, if the number of contacts has decreased by 1 and the contact is not in the list anymore
        CompletableFuture<Void> future2 = new CompletableFuture<>();
        controller.fetchAllContactsFromDatabase(documents -> {
            try {
                // Check that the correct number of documents are fetched
                assertEquals(NUMBER_OF_CONTACTS, documents.size());
                // checking of the new contact is in the list
                boolean found = false;
                for (Contact c : documents) {
                    if (c.getEmail().equals(contact.getEmail())) {
                        found = true;
                        break;
                    }
                }
                assertFalse(found);
                future2.complete(null);
            } catch (Throwable e) {
                future2.completeExceptionally(e);
            }
        });
        // This will block until the future is completed or times out
        future2.get(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }
}
