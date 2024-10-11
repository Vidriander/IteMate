package iteMate.project.uiActivities.scanScreen;

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.uiActivities.utils.ItemAdapter;

/**
 * Activity for returning items from a Track by an NFC scan
 */
public class ReturnScanActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ItemRepository itemRepository;
    private List<Item> itemList;

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

        // Initialize the adapter and set it to the RecyclerView
        itemAdapter = new ItemAdapter(itemList, this);
        recyclerView.setAdapter(itemAdapter);

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
}