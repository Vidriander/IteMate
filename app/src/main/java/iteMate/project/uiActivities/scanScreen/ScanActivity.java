package iteMate.project.uiActivities.scanScreen;

import static iteMate.project.uiActivities.ScanUtils.extractTagId;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

import iteMate.project.R;
import iteMate.project.controller.ItemController;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.GenericRepository;


/**
 * Activity for scanning NFC tags
 * It displays the scanned item and track.
 */
public class ScanActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    private NfcAdapter nfcAdapter;
    private TrackController trackController;
    private ItemController itemController;
    private ScanItemFragment scanItemFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initializeNfcAdapter();
        // initialize controller
        itemController = ItemController.getControllerInstance();
        trackController = TrackController.getControllerInstance();

        // on click listener for close button
        findViewById(R.id.close_nfcscan).setOnClickListener(v -> finish());

        // Add Fragments to the fragment container
        if (savedInstanceState == null) {
            setUpFragments();
        }
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
    protected void onResume() {
        super.onResume();
        transitionToIdleFragment();
        enableNfcReaderMode();
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
     * Transitions to the idle fragment
     */
    private void transitionToIdleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, ScanIdleFragment.class, null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableNfcReaderMode();
    }

    /**
     * Disables the NFC reader mode
     */
    private void disableNfcReaderMode() {
        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(this);
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        // reset the current item and track
        itemController.resetCurrentItem();
        trackController.resetCurrentTrack();

        // extract the tag ID
        String tagId = extractTagId(tag);
        Log.d("ScanActivity", "Tag ID: " + tagId);

        // fetch the item (and track) by the tag ID from the database
        fetchItemByNfcTagId(tagId);

        // transition to the item fragment
        transitionToItemFragment(tagId);
    }

    /**
     * Sets up the fragments
     */
    private void setUpFragments() {
        ScanIdleFragment scanIdleFragment = new ScanIdleFragment();
        scanItemFragment = new ScanItemFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, ScanIdleFragment.class, null);
        fragmentTransaction.commit();
    }

    /**
     * Transitions to the item fragment
     */
    private void transitionToItemFragment(String TagId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, scanItemFragment, TagId);
        fragmentTransaction.commit();
    }

    /**
     * Fetches an item by its NFC tag ID
     *
     * @param tagId the NFC tag ID
     */
    private void fetchItemByNfcTagId(String tagId) {
        itemController.fetchItemByNfcTagId(tagId, new GenericRepository.OnDocumentsFetchedListener<Item>() {
            @Override
            public void onDocumentFetched(Item item) {
                handleItemFetched(item);
            }

            @Override
            public void onDocumentsFetched(List<Item> item) {

            }
        });
    }

    /**
     * Handles the fetched item
     *
     * @param item the fetched item
     */
    private void handleItemFetched(Item item) {
        if (item != null) {

            // set the current item in the controller
            itemController.setCurrentItem(item);
            Log.d("ScanActivity", "Item found: " + item.getTitle());

            // update UI
            updateItemCardView(item);
            scanItemFragment.setNfcTagId(item.getNfcTag());

            // if the item is lent, fetch the track
            if (item.getActiveTrackID() != null) {
                fetchTrackByItemTrackID(item.getActiveTrackID(), item);;
            } else {
                trackController.resetCurrentTrack();
                // hide track card view
                updateTrackCardView(null);
            }
        } else {
            Log.d("ScanActivity", "Item not found");
            updateItemCardView(null);
            updateTrackCardView(null);
        }
    }

    /**
     * Fetches the track by the item's track ID
     *
     * @param trackID the track ID
     * @param item    the item
     */
    private void fetchTrackByItemTrackID(String trackID, Item item) {
        trackController.fetchTrackFromDatabase(trackID, new GenericRepository.OnDocumentsFetchedListener<Track>() {
            @Override
            public void onDocumentFetched(Track track) {
                trackController.setCurrentTrack(track);
                updateTrackCardView(track);
            }

            @Override
            public void onDocumentsFetched(List<Track> documents) {
                // pass
            }
        });
    }

    /**
     * Updates the item card view
     *
     * @param item the item to display
     */
    private void updateItemCardView(Item item) {
        ScanItemFragment fragment = (ScanItemFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.getView() != null) {
            CardView itemCardView = fragment.getView().findViewById(R.id.item_card_view_scan);
            if (itemCardView != null) {
                itemCardView.setVisibility(View.VISIBLE);
                if (item != null) {
                    updateItemCardContent(itemCardView, item);
                } else {
                    showCreateNewItemCard(itemCardView);
                }
            }
        }
    }

    /**
     * Updates the item card content
     *
     * @param itemCardView the item card view
     * @param item         the item to display
     */
    private void updateItemCardContent(CardView itemCardView, Item item) {
        TextView cardContent = itemCardView.findViewById(R.id.itemcard_header_text_scan);
        cardContent.setText(item.getTitle());
        TextView cardSubContent = itemCardView.findViewById(R.id.itemcard_subheader_text_scan);
        cardSubContent.setText(item.getDescription());
        GenericRepository.setImageForView(this, item.getImage(), itemCardView.findViewById(R.id.itemcard_image_scan));
        showButtons();
    }

    /**
     * Shows the "create new item" card
     *
     * @param itemCardView the item card view
     */
    private void showCreateNewItemCard(CardView itemCardView) {
        TextView cardContent = itemCardView.findViewById(R.id.itemcard_header_text_scan);
        cardContent.setText("Create new item");
        TextView cardSubContent = itemCardView.findViewById(R.id.itemcard_subheader_text_scan);
        cardSubContent.setText("");
        ImageView cardImage = itemCardView.findViewById(R.id.itemcard_image_scan);
        cardImage.setImageResource(R.drawable.add_button);
        hideButtons();
    }

    /**
     * Sets the visability of the buttons to visible
     */
    private void showButtons() {
        ScanItemFragment fragment = (ScanItemFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.getView() != null) {
            Button lendButton = fragment.getView().findViewById(R.id.lend_button);
            lendButton.setVisibility(View.VISIBLE);
            Button returnButton = fragment.getView().findViewById(R.id.return_button);
            returnButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets the visability of the buttons to gone
     */
    private void hideButtons() {
        ScanItemFragment fragment = (ScanItemFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.getView() != null) {
            Button lendButton = fragment.getView().findViewById(R.id.lend_button);
            lendButton.setVisibility(View.GONE);
            Button returnButton = fragment.getView().findViewById(R.id.return_button);
            returnButton.setVisibility(View.GONE);
        }
    }

    /**
     * Updates the track card view
     *
     * @param track the track to display
     */
    private void updateTrackCardView(Track track) {
        ScanItemFragment fragment = (ScanItemFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.getView() != null) {
            CardView trackCardView = fragment.getView().findViewById(R.id.track_card_view_scan);
            if (trackCardView != null) {
                if (track == null) {
                    trackCardView.setVisibility(View.GONE);
                } else {
                    trackCardView.setVisibility(View.VISIBLE);
                    updateTrackCardContent(trackCardView, track);
                }
            }
            updateButtonTransparency();
        }
    }

    /**
     * Updates the track card content
     *
     * @param track         the track to display
     * @param trackCardView the track card view
     */
    private void updateTrackCardContent(CardView trackCardView, Track track) {
        trackCardView.setVisibility(View.VISIBLE);
        TextView cardContent = trackCardView.findViewById(R.id.trackcard_contactname_scan);
        cardContent.setText(track.getContact().getName());
        TextView cardSubContent = trackCardView.findViewById(R.id.trackcard_daycounter_scan);
        cardSubContent.setText(String.valueOf(track.getDaysLeft()));
        TextView cardSubContent2 = trackCardView.findViewById(R.id.trackcard_numberofitems_scan);
        cardSubContent2.setText(String.valueOf(track.getNumberOfItems()));
    }

    /**
     * Manages the transparency of the buttons
     */
    private void updateButtonTransparency() {
        ScanItemFragment fragment = (ScanItemFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null && fragment.getView() != null) {
            Button returnButton = fragment.getView().findViewById(R.id.return_button);
            Button lendButton = fragment.getView().findViewById(R.id.lend_button);
            if (returnButton != null) {
                if (trackController.getCurrentTrack() == null) {
                    returnButton.setAlpha(0.4f);
                    lendButton.setAlpha(1f);
                } else {
                    returnButton.setAlpha(1f);
                    lendButton.setAlpha(0.4f);
                }
            }
        }
    }

}