package iteMate.project.uiActivities.scanScreen;

import static iteMate.project.documentController.ScanController.extractNfcTagId;

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
import iteMate.project.documentController.ItemController;
import iteMate.project.documentController.TrackController;
import iteMate.project.model.Item;
import iteMate.project.documentController.ScanController;
import iteMate.project.uiActivities.adapter.ManageScanAdapter;
import iteMate.project.utils.NfcScanner;

/**
 * Activity for managing the inner items of a track with a NFC scan
 */
public class ManageScanActivity extends AppCompatActivity implements NfcScanner.NfcScanListener {

    private NfcScanner nfcScanner;
    ManageScanAdapter manageScanAdapter;
    private final TrackController trackController = TrackController.getControllerInstance();
    private final ScanController scanController = ScanController.getControllerInstance();
    private final ItemController itemController = ItemController.getControllerInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_scan);

        // Initialize NFC Scanner
        nfcScanner = new NfcScanner(this, this);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and set it to the RecyclerView
        List<Item> listOfLentItems = trackController.getCurrentTrack().getLentItemsList();
        manageScanAdapter = new ManageScanAdapter(listOfLentItems, this);
        recyclerView.setAdapter(manageScanAdapter);

        // On click listener for the close button
        findViewById(R.id.close_button).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcScanner.enableReaderMode(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcScanner.disableReaderMode(this);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        String tagId = extractNfcTagId(tag);
        scanController.setNfcTagId(tagId);
        itemController.fetchItemByNfcTagId(tagId, item -> {
            scanController.toggleAddToLendList(item);
            manageScanAdapter.notifyDataSetChanged();
        });
    }
}
