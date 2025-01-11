package iteMate.project;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import iteMate.project.model.Item;
import iteMate.project.databaseManager.GenericRepository;
import iteMate.project.databaseManager.ItemRepository;

public class ItemRepositoryTests {

    // region Constants [IMPORTANT: constants might need to be changed manually when database is updated]
    /**
     * Timeout for async operations in seconds
     */
    private static final int ASYNC_TIMEOUT_SECONDS = 8;
    // endregion

    private ItemRepository itemRepository;

    @Spy
    private ItemRepository itemRepositorySpy;

    @Before
    public void setUp() {
        itemRepository = new ItemRepository();
        itemRepositorySpy = spy(new ItemRepository());
    }

    /**
     * Test that the ItemRepository class extends the GenericRepository class and therefore inherits its methods and is of the correct type
     */
    @Test
    public void itemRepositoryExtendsGenericRepository() {
        assertInstanceOf(GenericRepository.class, itemRepository);
    }

    /**
     * Test the method getItemByNfcTagFromDatabase in the ItemRepository class by fetching an item from the database by its NFC tag
     * and checking that the correct item is fetched.
     */
    @Test
    public void getItemByNfcTagFromDatabaseTest() {
        CompletableFuture<Item> future = new CompletableFuture<>();
        itemRepository.getItemByNfcTagFromDatabase("1D69F440021080", item -> {
            assertNotNull(item);
            assertEquals("1D69F440021080", item.getNfcTag());
            assertEquals("Holy Grail", item.getTitle());
            assertEquals("MBQWsdyrOngXibyhMwQ2",item.getId());
            future.complete(item);
        });
        try {
            future.get(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    /**
     * Test that the method setContainedAndAssociatedItems is called when fetching all documents from the database
     * This indicates that the overridden method manipulateResults is called when fetching all documents from the database.
     * The latter is the correct behavior as the method manipulateResults is overridden in the ItemRepository class.
     */
    @Test
    public void rightManipulateResultsMethodIsCalledTest() throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<Void> future = new CompletableFuture<>();
        itemRepositorySpy.getAllDocumentsFromDatabase(fetchedItems -> {
            future.complete(null);
        });
        future.get(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        verify(itemRepositorySpy, Mockito.atLeastOnce()).setContainedAndAssociatedItems(any());
    }

    /**
     * Test the method getAllAvailableItemsFromDatabase in the ItemRepository class by fetching all available items from the database
     * and checking that the correct number of items is fetched. The number of items fetched should be equal to the number of items in the database that are available.
     * An item is considered available if the activeTrackId is null.
     */
    @Test
    public void getAllAvailableItemsFromDatabaseTest() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        itemRepository.getAllAvailableItemsFromDatabase(items -> {
            assertNotNull(items);
            assertEquals(8, items.size());
            future.complete(null);
        });
        try {
            future.get(ASYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            fail("Exception occurred: " + e.getMessage());
        }
    }

    @Test
    public void setContainedAndAssociatedItemsTest() throws InterruptedException {
        Item fahrradwerkzeug = new Item();
        // manually replicating a test item from the database
        fahrradwerkzeug.setContainedItemIDs(new ArrayList<>(Arrays.asList("B0jdJ2Juhalub7rItfFs","lpB3VZoBgA2J64SwXmjM")));
        fahrradwerkzeug.setAssociatedItemIDs(new ArrayList<>(Arrays.asList("kNtJ95ZDMgBGRi5ip8mb","pVkV17BSPsyS1W9LxRSl")));

        // implicitly calling the private method getItemslistByListOfIDsFromDatabase and therefore testing it as well
        itemRepository.setContainedAndAssociatedItems(fahrradwerkzeug);

        // check that the correct number of items are fetched
        assertEquals(2, fahrradwerkzeug.getContainedItems().size());
        assertEquals(2, fahrradwerkzeug.getAssociatedItems().size());
        // check that the correct items have been fetched
        assertEquals("B0jdJ2Juhalub7rItfFs", fahrradwerkzeug.getContainedItems().get(0).getId());
        assertEquals("lpB3VZoBgA2J64SwXmjM", fahrradwerkzeug.getContainedItems().get(1).getId());
        assertEquals("kNtJ95ZDMgBGRi5ip8mb", fahrradwerkzeug.getAssociatedItems().get(0).getId());
        assertEquals("pVkV17BSPsyS1W9LxRSl", fahrradwerkzeug.getAssociatedItems().get(1).getId());
    }
}
