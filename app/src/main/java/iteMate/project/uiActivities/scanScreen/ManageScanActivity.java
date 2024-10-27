package iteMate.project.uiActivities.scanScreen;

import static iteMate.project.uiActivities.ScanUtils.extractTagId;
import static iteMate.project.uiActivities.ScanUtils.fetchItemByNfcTagId;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iteMate.project.R;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.uiActivities.adapter.ManageScanAdapter;

/**
 * Activity for managing the inner items of a track with a NFC scan
 */
public class ManageScanActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    private NfcAdapter nfcAdapter;
    private final TrackController trackController = TrackController.getControllerInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_scan);

        initializeNfcAdapter();

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and set it to the RecyclerView
        List<Item> listOfLentItems = trackController.getCurrentTrack().getLentItemsList();
        ManageScanAdapter manageScanAdapter = new ManageScanAdapter(listOfLentItems, this);
        recyclerView.setAdapter(manageScanAdapter);

        // On click listener for the close button
        findViewById(R.id.close_button).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeNfcAdapter() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(this);
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        String tagId = extractTagId(tag);
        fetchItemByNfcTagId(tagId, new GenericRepository.OnDocumentsFetchedListener<Item>() {
            @Override
            public void onDocumentFetched(Item item) {
                handleItemFetched(item);
                updateAdapter();
            }

            // Update the adapter with scanned item
            private void updateAdapter() {
                runOnUiThread(() -> {
                    ManageScanAdapter adapter = (ManageScanAdapter) ((RecyclerView) findViewById(R.id.recyclerViewItems)).getAdapter();
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onDocumentsFetched(List<Item> items) {

            }
        });
    }

    /**
     * Handles the fetched item
     * If the item is already on the list, remove it from the lent list, otherwise add it
     *
     * @param item The fetched item
     */
    public void handleItemFetched(Item item) {
        if (item != null && item.getActiveTrackID() == null) {
            List<Item> lendList = trackController.getCurrentTrack().getLentItemsList();
            boolean itemExists = lendList.stream().anyMatch(lentItem -> lentItem.getId().equals(item.getId()));
            if (itemExists) {
                lendList.removeIf(lentItem -> lentItem.getId().equals(item.getId()));
            } else {
                lendList.add(item);
            }
            trackController.getCurrentTrack().setLentItemsList(lendList);
            // TODO Update the lent items list and lent items in Firestore
        }
    }

}
