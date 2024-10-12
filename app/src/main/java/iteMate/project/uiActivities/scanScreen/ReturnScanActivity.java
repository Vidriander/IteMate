package iteMate.project.uiActivities.scanScreen;

import static iteMate.project.uiActivities.utils.ScanUtils.extractTagId;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.uiActivities.utils.ItemAdapter;

/**
 * Activity for returning items from a Track by an NFC scan
 */
public class ReturnScanActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    private NfcAdapter nfcAdapter;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private List<Item> itemList;
    private List<Item> listOfItemsToReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_return_scan);

        initializeNfcAdapter();

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the list of items from the Intent
        itemList = getIntent().getParcelableArrayListExtra("itemList");
        listOfItemsToReturn = new ArrayList<>(itemList);

        // Initialize the adapter and set it to the RecyclerView
        itemAdapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(itemAdapter);

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
        String tagId = extractTagId(tag);
        removeItemFromReturnList(tagId);

    }

    /**
     * Removes an item from the list of items to return
     *
     * @param tagId the tag ID of the item to remove
     */
    private void removeItemFromReturnList(String tagId) {
        Toast.makeText(this, "Tag ID: " + tagId, Toast.LENGTH_SHORT).show();
        Log.d("ReturnScanActivity", "Tag ID: " + tagId);
        for (Item item : listOfItemsToReturn) {
            if (item.getNfcTag().equals(tagId)) {
                listOfItemsToReturn.remove(item);
                makeItemAvailable(item);
            } else {
                Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void makeItemAvailable(Item item) {
        item.setActiveTrackID(null);
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
