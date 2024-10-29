package iteMate.project.uiActivities.scanScreen;

import static iteMate.project.uiActivities.ScanController.extractTagId;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

import iteMate.project.R;
import iteMate.project.controller.ItemController;
import iteMate.project.controller.TrackController;
import iteMate.project.models.Item;
import iteMate.project.models.Track;
import iteMate.project.repositories.GenericRepository;
import iteMate.project.uiActivities.ScanController;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;
import iteMate.project.uiActivities.itemScreens.ItemsEditActivity;
import iteMate.project.uiActivities.trackScreens.TrackDetailActivity;
import iteMate.project.uiActivities.trackScreens.TrackEditActivity;


/**
 * Activity for scanning NFC tags
 * It displays the scanned item and track.
 */
public class ScanActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    private NfcAdapter nfcAdapter;
    private ScanController scanController;
    private TrackController trackController;
    private ItemController itemController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        initializeNfcAdapter();

        // initialize controller
        itemController = ItemController.getControllerInstance();
        trackController = TrackController.getControllerInstance();
        scanController = ScanController.getControllerInstance();

        // Set item details for track card view
        findViewById(R.id.track_card_view_scan).setOnClickListener(v -> {
            Intent intent = new Intent(this, TrackDetailActivity.class);
            trackController.setCurrentTrack(trackController.getCurrentTrack());
            startActivity(intent);
        });

        // Set on click listener for item card
        findViewById(R.id.item_card_view_scan).setOnClickListener(v -> {
            Intent intent;
            if (itemController.getCurrentItem() == null) {
                // if no item exists, navigate to item edit screen and create new item
                Item newItem = new Item();
                newItem.setNfcTag(scanController.getNfcTagId());
                itemController.setCurrentItem(newItem);
                intent = new Intent(this, ItemsEditActivity.class);
            } else {
                // if item exists navigate to item detail screen to display item details
                intent = new Intent(this, ItemsDetailActivity.class);
            }
            startActivity(intent);
        });

        // Set on click listener for track card
        findViewById(R.id.track_card_view_scan).setOnClickListener(v -> {
            Intent intent = new Intent(this, TrackDetailActivity.class);
            trackController.setCurrentTrack(trackController.getCurrentTrack());
            startActivity(intent);
        });

        // Set on click listener for lend button
        findViewById(R.id.lend_button).setOnClickListener(v -> {

            if (itemController.getCurrentItem().isAvailable()) {
                // create new track
                Track track = new Track();

                // add item to track (add item to track: setLendItemsList & setPendingItemsList)
                List<Item> itemList = new ArrayList<>();
                itemList.add(itemController.getCurrentItem());
                track.setLentItemsList(itemList);
                track.setPendingItemsList(itemList);

                // set active trackID to item and mark as lent out
                itemController.getCurrentItem().setActiveTrackID(track.getId());

                // open track edit activity
                trackController.setCurrentTrack(track);
                Intent intent = new Intent(this, TrackEditActivity.class);
                startActivity(intent);
            }
        });

        // Set on click listener for return button
        findViewById(R.id.return_button).setOnClickListener(v -> {

            Track currentTrack = trackController.getCurrentTrack();
            if (currentTrack != null) {
                // remove item from pending list in the track & update track in database
                currentTrack.getPendingItemIDs().remove(itemController.getCurrentItem().getId());
                currentTrack.getReturnedItemIDs().add(itemController.getCurrentItem().getId());
                trackController.saveChangesToDatabase(currentTrack);

                // reset active track id in item & update item in database
                itemController.getCurrentItem().setActiveTrackID(null);
                itemController.saveChangesToDatabase();

                Toast.makeText(this, "Item returned", Toast.LENGTH_SHORT).show();

                // reload fragment
                Intent intent = new Intent(this, ScanActivity.class);
                startActivity(intent);
            }
        });

        // on click listener for close button
        findViewById(R.id.close_nfcscan).setOnClickListener(v -> finish());
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
        enableNfcReaderMode();

        // hiding all elements
        findViewById(R.id.item_card_view_scan).setVisibility(View.GONE);
        findViewById(R.id.track_card_view_scan).setVisibility(View.GONE);
        greyoutButtons();
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
        scanController.resetCurrentScan();

        // extract the tag ID
        String tagId = extractTagId(tag);
        scanController.setNfcTagId(tagId);
        Log.d("ScanActivity", "Tag ID: " + tagId);

        // fetch the item (and track) by the tag ID from the database
        fetchItemByNfcTagId(tagId);
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

            // if the item is lent, fetch the track
            if (item.getActiveTrackID() != null) {
                fetchTrackByItemTrackID(item.getActiveTrackID());
            } else {
                trackController.resetCurrentTrack();
                // hide track card view
                updateTrackCardView(null);
            }
        } else {
            Log.d("ScanActivity", "Item not found");
            updateItemCardView(null);
            updateTrackCardView(null);
            greyoutButtons();
        }
    }

    /**
     * Fetches the track by the item's track ID
     *
     * @param trackID the track ID
     */
    private void fetchTrackByItemTrackID(String trackID) {
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
        CardView itemCardView = findViewById(R.id.item_card_view_scan);

        itemCardView.setVisibility(View.VISIBLE);
        if (item != null) {
            updateItemCardContent(itemCardView, item);
        } else {
            showCreateNewItemCard(itemCardView);
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
        cardContent.setText("Create New Item");
        TextView cardSubContent = itemCardView.findViewById(R.id.itemcard_subheader_text_scan);
        cardSubContent.setText("");
        ImageView cardImage = itemCardView.findViewById(R.id.itemcard_image_scan);
        cardImage.setImageResource(R.drawable.baseline_add_circle_outline_24);
        greyoutButtons();
    }

    /**
     * Sets the visability of the buttons to visible
     */
    private void showButtons() {
        Button lendButton = findViewById(R.id.lend_button);
        lendButton.setVisibility(View.VISIBLE);
        lendButton.setAlpha(1f);
        lendButton.setClickable(true);
        Button returnButton = findViewById(R.id.return_button);
        returnButton.setVisibility(View.VISIBLE);
        returnButton.setAlpha(1f);
        returnButton.setClickable(true);
    }

    /**
     * Sets the visability of the buttons to gone
     */
    private void greyoutButtons() {
        Button lendButton = findViewById(R.id.lend_button);
        lendButton.setAlpha(0.4f);
        lendButton.setClickable(false);
        Button returnButton = findViewById(R.id.return_button);
        returnButton.setAlpha(0.4f);
        returnButton.setClickable(false);
    }

    /**
     * Updates the track card view
     *
     * @param track the track to display
     */
    private void updateTrackCardView(Track track) {
        CardView trackCardView = findViewById(R.id.track_card_view_scan);
        if (track == null) {
            trackCardView.setVisibility(View.GONE);
        } else {
            trackCardView.setVisibility(View.VISIBLE);
            updateTrackCardContent(trackCardView, track);
        }
        updateButtonTransparency();
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
        Button returnButton = findViewById(R.id.return_button);
        Button lendButton = findViewById(R.id.lend_button);

        if (trackController.getCurrentTrack() == null) {
            returnButton.setAlpha(0.4f);
            returnButton.setClickable(false);
            lendButton.setAlpha(1f);
            lendButton.setClickable(true);
        } else {
            returnButton.setAlpha(1f);
            returnButton.setClickable(true);
            lendButton.setAlpha(0.4f);
            lendButton.setClickable(false);
        }

    }
}