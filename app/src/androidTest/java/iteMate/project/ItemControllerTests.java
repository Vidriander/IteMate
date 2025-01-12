package iteMate.project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Order;

import iteMate.project.documentController.ItemController;
import iteMate.project.model.Item;

public class ItemControllerTests {

    ItemController itemController;

    @Before
    public void setUp() {
        itemController = ItemController.getControllerInstance();
    }

    @Order(0)
    /**
     * Test if the singleton behaviour is working correctly
     */
    @Test
    public void singletonBehaviourTest() {
        ItemController itemController2 = ItemController.getControllerInstance();
        assert(itemController == itemController2);
    }

    @Order(0)
    @Test
    public void getCurrentItemTest() {
        assert(itemController.getCurrentItem() == null);
    }

    @Order(1)
    @Test
    public void setCurrentItemTest() {
        assertThrows(NullPointerException.class, () -> itemController.setCurrentItem(null));
        Item item = new Item();
        itemController.setCurrentItem(item);
        assert(itemController.getCurrentItem() == item);
    }

    @Order(2)
    @Test
    public void resetCurrentItemTest() {
        itemController.resetCurrentItem();
        assert(itemController.getCurrentItem() == null);
        itemController.setCurrentItem(new Item());
        itemController.resetCurrentItem();
        assert(itemController.getCurrentItem() == null);
    }

    @Order(3)
    @Test
    public void itemIsReadyForUploadTest() {
        Item item = new Item();
        itemController.setCurrentItem(item);
        assert(!itemController.isReadyForUpload());
        item.setTitle("Title");
        assert(itemController.isReadyForUpload());
    }

    @Order(4)
    @Test
    public void saveChangesToItemTest() {
        Item item = new Item();
        itemController.setCurrentItem(item);
        itemController.saveChangesToItem("Title","Description");
        assertEquals(item.getTitle(), "Title");
        assertEquals(item.getDescription(), "Description");
    }

    @Order(5)
    @Test
    public void saveChangesToDatabaseTest() throws InterruptedException {
        Item temporaryItem = new Item();
        temporaryItem.setTitle("Only for Testing");
        itemController.setCurrentItem(temporaryItem);
        Thread.sleep(1000);
        assert(itemController.getCurrentItemList().stream().map(Item::getTitle).noneMatch("Only for Testing"::equals));
        Thread t = new Thread(() -> itemController.saveChangesToDatabase());
        t.start();
        Thread.sleep(1000);
        t = new Thread(() -> itemController.refreshCurrentItemList());
        t.start();
        Thread.sleep(1000);
        assert(itemController.getCurrentItemList().stream().map(Item::getTitle).anyMatch("Only for Testing"::equals));
    }

    @Order(6)
    @Test
    public void deleteItemFromDatabaseTest() throws InterruptedException {
        Thread.sleep(1000);
        Thread t = new Thread(() -> itemController.refreshCurrentItemList());
        t.start();
        Thread.sleep(2000);
        Item testingItem = itemController.getCurrentItemList().stream().filter(item -> item.getTitle().equals("Only for Testing")).findFirst().orElse(null);
        assert(testingItem != null);
        itemController.setCurrentItem(testingItem);
        t = new Thread(() -> itemController.deleteItemFromDatabase());
        t.start();
        Thread.sleep(1000);
        t = new Thread(() -> itemController.refreshCurrentItemList());
        t.start();
        Thread.sleep(1000);
        assert(itemController.getCurrentItemList().stream().map(Item::getTitle).noneMatch("Only for Testing"::equals));
    }
}
