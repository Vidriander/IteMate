package iteMate.project.uiActivities.scanScreen;

import android.nfc.NfcAdapter;
import android.os.Bundle;

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
import iteMate.project.uiActivities.adapter.ItemAdapter;

/**
 * Activity for managing the inner items of a track with a NFC scan
 */
public class ManageScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_scan);

        initializeNfcAdapter();

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerViewItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve the list of items from the Intent
        List<Item> itemList = TrackController.getControllerInstance().getCurrentTrack().getLentItemsList(); // Change by David on 15.10. (no more parcel stuff)

        // Initialize the adapter and set it to the RecyclerView
        ItemAdapter itemAdapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(itemAdapter);

        // On click listener for the close button
        findViewById(R.id.close_button).setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeNfcAdapter() {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            finish();
        }
    }
}