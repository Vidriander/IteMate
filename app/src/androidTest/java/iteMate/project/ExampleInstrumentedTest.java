package iteMate.project;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.util.List;

import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.repositories.ItemRepository;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("iteMate.project", appContext.getPackageName());
    }

    @Test
    public void fetchAllAvailableItemsTest() {
        TrackController trackController = TrackController.getControllerInstance();
        trackController.getLendableItemsList(new GenericRepository.OnDocumentsFetchedListener<Item>(){
            @Override
            public void onDocumentFetched(Item document) {

            }
            @Override
            public void onDocumentsFetched(List<Item> documents) {
                System.out.println("Fetched items: " + documents);
            }
        });
    }
}