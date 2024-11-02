package iteMate.project.uiActivities.scanScreen;

import static iteMate.project.controller.ScanController.extractNfcTagId;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iteMate.project.R;
import iteMate.project.controller.ItemController;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.controller.ScanController;
import iteMate.project.uiActivities.adapter.ReturnScanAdapter;

/**
 * Activity for returning items from a Track by an NFC scan
 */
public class ReturnScanActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    private NfcAdapter nfcAdapter;
    private List<Item> listOfPendingItems;
    private List<Item> listOfReturnedItems;
    private ReturnScanAdapter returnScanAdapter;
    private final TrackController trackController = TrackController.getControllerInstance();
    private final ScanController scanController = ScanController.getControllerInstance();
    private final ItemController itemController = ItemController.getControllerInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_return_scan);

        initializeNfcAdapter();

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the list of items fro the controller
        // TODO also move to controller class?
        listOfPendingItems = trackController.getCurrentTrack().getPendingItemsList();
        listOfReturnedItems = trackController.getCurrentTrack().getReturnedItemsList();
        List<Item> listOfLentItems = trackController.getCurrentTrack().getLentItemsList();

        // Initialize the adapter and set it to the RecyclerView
        returnScanAdapter = new ReturnScanAdapter(listOfLentItems, this);
        recyclerView.setAdapter(returnScanAdapter);

        // On click listener for the close button
        findViewById(R.id.close_button).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Initializes the NFC adapter
     */
    private void initializeNfcAdapter() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            finish();
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        String tagId = extractNfcTagId(tag);
        scanController.setNfcTagId(tagId);
        handleTag(tagId);
    }

    /**
     * Handles the NFC tag
     *
     * @param tagId the NFC tag ID
     */
    public void handleTag(String tagId) {
        for (Item item : listOfPendingItems) {
            if (item.getNfcTag().equals(tagId)) {
                itemController.setCurrentItem(item);
                scanController.returnItem();
                // Run the UI update on the main thread
                runOnUiThread(() -> {
                    returnScanAdapter.notifyDataSetChanged(); // Or refreshAdapter() if necessary
                    Toast.makeText(this, item.getTitle() + " was returned.", Toast.LENGTH_LONG).show();
                });
                return;
            }
        }
        Toast.makeText(this, "Item not in Track", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableNfcReaderMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableNfcReaderMode();
    }

    /**
     * Enables the NFC reader mode
     */
    private void enableNfcReaderMode() {
        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(this, this,
                    NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                    null);
        }
    }

    /**
     * Disables the NFC reader mode
     */
    private void disableNfcReaderMode() {
        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(this);
        }
    }
}
