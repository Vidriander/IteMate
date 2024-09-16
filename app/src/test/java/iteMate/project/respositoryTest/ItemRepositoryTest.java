

package iteMate.project.respositoryTest;

import iteMate.project.repositories.ItemRepository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.Tasks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

import iteMate.project.models.Item;

@RunWith(RobolectricTestRunner.class)  // Use Robolectric to simulate Android environment
public class ItemRepositoryTest {

    @Mock
    private FirebaseFirestore mockFirestore;
    @Mock
    private CollectionReference mockCollection;
    @Mock
    private QuerySnapshot mockQuerySnapshot;
    @Mock
    private QueryDocumentSnapshot mockQueryDocumentSnapshot;

    private ItemRepository itemRepository;

    @Before
    public void setUp() {
        // Initialize Mockito annotations
        MockitoAnnotations.initMocks(this);

        // Mock Firestore instance and its collection
        mockFirestore = mock(FirebaseFirestore.class);
        mockCollection = mock(CollectionReference.class);
        mockQuerySnapshot = mock(QuerySnapshot.class);

        // Inject mock Firestore into the repository
        itemRepository = new ItemRepository();
        itemRepository.setDb(mockFirestore);

        // Mock Firestore collection reference
        when(mockFirestore.collection("items")).thenReturn(mockCollection);
    }

    @Test
    public void testGetAllItemsFromFirestore() {
        // Prepare mock data for the test
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item());  // Add a mock Item object
        when(mockQuerySnapshot.toObjects(Item.class)).thenReturn(itemList);

        // Simulate a successful Firestore get task
        when(mockCollection.get()).thenReturn(Tasks.forResult(mockQuerySnapshot));

        // Prepare a mock listener
        ItemRepository.OnItemsFetchedListener mockListener = mock(ItemRepository.OnItemsFetchedListener.class);

        // Invoke method and verify the listener is called with correct data
        itemRepository.getAllItemsFromFirestore(mockListener);
        verify(mockListener).onItemsFetched(itemList);  // Verify that the listener received the itemList
    }
}
