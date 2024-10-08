package iteMate.project.uiActivities.scanScreen;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
    private ScanItemFragment scanItemFragment;
    private ScanIdleFragment scanIdleFragment;

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
            setUpFragments();
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
        Log.d("ScanActivity", "Tag ID: " + tagId);
        fetchItemByNfcTagId(tagId);transitionToItemFragment();
    }

    /**
     * Set up the fragments
     */
    private void setUpFragments() {
        scanIdleFragment = new ScanIdleFragment();
        scanItemFragment = new ScanItemFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, ScanIdleFragment.class, null);
        fragmentTransaction.commit();
    }

    /**
     * Transition to the item fragment
     */
    private void transitionToItemFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, scanItemFragment);
        fragmentTransaction.commit();
    }

    /**
     * Fetch item by NFC tag ID and update the item card view
     *
     * @param tagId Tag ID as a Hex String
     */
    private void fetchItemByNfcTagId(String tagId) {
        itemRepository.getItemByNfcTagFromFirestore(tagId, new GenericRepository.OnDocumentsFetchedListener<Item>() {
            @Override
            public void onDocumentFetched(Item item) {
                if (item != null) {
                    Log.d("ScanActivity", "Item found: " + item.getTitle());
                    updateItemCardView(item);
                    itemRepository.setContainedAndAssociatedItems(item);
                    scanItemFragment.setItemToDisplay(item);
                    if (item.getActiveTrackID() != null) {
                        fetchTrackByItemTrackID(item.getActiveTrackID());
                        TrackRepository trackRepository = new TrackRepository();
                        trackRepository.getOneDocumentFromFirestore(item.getActiveTrackID(), new GenericRepository.OnDocumentsFetchedListener<Track>() {
                            @Override
                            public void onDocumentFetched(Track document) {
                                scanItemFragment.setTrackToDisplay(document);
                            }
                            @Override
                            public void onDocumentsFetched(List<Track> documents) {
                            }
                        });
                    }
                } else {
                    Log.d("ScanActivity", "Item not found");
                }
            }

            @Override
            public void onDocumentsFetched(List<Item> documents) {
                // Not used in this context
            }
        });
    }

    /**
     * Fetch track by item track ID
     * @param trackID Track ID as a String
     */
    private void fetchTrackByItemTrackID(String trackID) {
        trackRepository.getOneDocumentFromFirestore(trackID, this);
    }

    /**
     * Update the item card view with the item details
     *
     * @param item Item object
     */
    private void updateItemCardView(Item item) {
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
    }

    /**
     * Update the track card view with the track details
     *
     * @param track Track object
     */
    private void updateTrackCardView(Track track) {
        ScanItemFragment fragment = (ScanItemFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.getView() != null) {
            CardView trackCardView = fragment.getView().findViewById(R.id.track_card_view_scan);
            Button returnButton = fragment.getView().findViewById(R.id.return_button);
            Button lendButton = fragment.getView().findViewById(R.id.lend_button);

            if (trackCardView != null) {
                trackCardView.setVisibility(View.VISIBLE);
                // Update track card content
                TextView cardContent = trackCardView.findViewById(R.id.trackcard_contactname_scan);
                cardContent.setText(track.getContact().getName());
                TextView cardSubContent = trackCardView.findViewById(R.id.trackcard_daycounter_scan);
                cardSubContent.setText(String.valueOf(track.getDaysLeft()));
                TextView cardSubContent2 = trackCardView.findViewById(R.id.trackcard_numberofitems_scan);
                cardSubContent2.setText(String.valueOf(track.getNumberOfItems()));
            }

            // Manage Button transparency of return & lend button
            if (returnButton != null) {
                if (track == null) {
                    returnButton.setAlpha(0.5f);
                    lendButton.setAlpha(1f);
                } else {
                    returnButton.setAlpha(1f);
                    lendButton.setAlpha(0.5f);
                }
            }
        }
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
            hexString.append(String.format("%02X", b)); // convert byte to HEX, caps-lock on
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

