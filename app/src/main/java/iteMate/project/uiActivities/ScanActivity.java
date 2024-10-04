package iteMate.project.uiActivities;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.repositories.ItemRepository;
import iteMate.project.repositories.TrackRepository;

/**
 * Activity for scanning NFC tags
 */
public class ScanActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback, GenericRepository.OnDocumentsFetchedListener<Track> {

    private NfcAdapter nfcAdapter;
    private ItemRepository itemRepository;
    private TrackRepository trackRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Log.d("ScanActivity", "NFC is not available on this device.");
            finish();
            return;
        }

        // Initialize Repositories
        itemRepository = new ItemRepository();
        trackRepository = new TrackRepository();

        // Set functionality of close button
        findViewById(R.id.close_nfcscan).setOnClickListener(v -> finish());

        // Add Fragments to the fragment container
        if (savedInstanceState == null) {
            ScanItemFragment scanItemFragment = new ScanItemFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, scanItemFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            // Enable Reader Mode when activity is resumed
            nfcAdapter.enableReaderMode(this, this,
                    NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                    null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            // Disable Reader Mode when the activity is paused
            nfcAdapter.disableReaderMode(this);
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.d("ScanActivity", "NFC Tag discovered!");
        String tagId = extractTagId(tag);
        updateTagIdTextView(tagId); // Display tag ID for testing
        fetchItemByNfcTagId(tagId);
        try {
            fetchTrackByItemTrackID(tagId);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                 InstantiationException e) {
            Log.e("ScanActivity", "Error fetching track from Firestore", e);
        }
    }

    /**
     * Update the tag ID TextView with the tag ID
     * for testing purposes TODO: Remove this method
     *
     * @param tagId Tag ID as a long
     */
    private void updateTagIdTextView(String tagId) {
        runOnUiThread(() -> {
            TextView tagIdTextView = findViewById(R.id.showNfcTagID);
            tagIdTextView.setText(tagId);
        });
    }

    /**
     * Fetch item by NFC tag ID and update the item card view
     *
     * @param tagId Tag ID as a Hex String
     */
    private void fetchItemByNfcTagId(String tagId) {
        itemRepository.getItemByNfcTag(tagId, item -> {
            if (item != null) {
                Log.d("ScanActivity", "Item found: " + item.getTitle());
                updateItemCardView(item);
                fetchTrackByItemTrackID(item.getActiveTrackID());
            } else {
                Log.d("ScanActivity", "Item not found");
            }
        });
    }

    private void fetchTrackByItemTrackID(String trackID) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        trackRepository.getDocumentFromFirestore(trackID, this);
        // trackRepository.fetchTrackByID(trackID, this);
    }


    /**
     * Update the item card view with the item details
     *
     * @param item Item object
     */
    private void updateItemCardView(Item item) {
        runOnUiThread(() -> {
            ScanItemFragment fragment = (ScanItemFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment != null && fragment.getView() != null) {
                CardView itemCardView = fragment.getView().findViewById(R.id.item_card_view_scan);
                if (itemCardView != null) {
                    itemCardView.setVisibility(View.VISIBLE);
                    // Update item card content
                    TextView cardContent = itemCardView.findViewById(R.id.itemcard_header_text_scan);
                    cardContent.setText(item.getTitle());
                    TextView cardSubContent = itemCardView.findViewById(R.id.itemcard_subheader_text_scan);
                    cardSubContent.setText(item.getDescription());
                    GenericRepository.setImageForView(this, item.getImage(), itemCardView.findViewById(R.id.itemcard_image_scan));
                }
            }
        });
    }

    private void updateTrackCardView(Track track) {
        runOnUiThread(() -> {
            ScanItemFragment fragment = (ScanItemFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment != null && fragment.getView() != null) {
                CardView trackCardView = fragment.getView().findViewById(R.id.track_card_view_scan);
                if (trackCardView != null) {
                    trackCardView.setVisibility(View.VISIBLE);
                    // Update track card content
                    TextView cardContent = trackCardView.findViewById(R.id.trackcard_contactname_scan);
                    cardContent.setText(track.getContact().getName());
                    TextView cardSubContent = trackCardView.findViewById(R.id.trackcard_daycounter_scan);
                    cardSubContent.setText(track.getDaysLeft());
                }

            }
        });
    }

    /**
     * Extract tag ID from the tag
     *
     * @param tag Tag object
     * @return Tag ID as a HEX String
     */
    private String extractTagId(Tag tag) {
        byte[] tagId = tag.getId(); // convert tag ID to byte array
        StringBuilder hexString = new StringBuilder();
        for (byte b : tagId) {
            hexString.append(String.format("%02X", b)); // convert byte to HEX
        }
        return hexString.toString();
    }

    @Override
    public void onDocumentFetched(Track document) {
        updateTrackCardView(document);
    }

    @Override
    public void onDocumentsFetched(List<Track> documents) {
        // pass
    }
}

