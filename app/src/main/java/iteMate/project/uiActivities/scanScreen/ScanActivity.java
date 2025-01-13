package iteMate.project.uiActivities.scanScreen;

import static iteMate.project.documentController.ScanController.extractNfcTagId;

import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.Locale;

import iteMate.project.R;
import iteMate.project.documentController.ItemController;
import iteMate.project.documentController.TrackController;
import iteMate.project.documentController.ScanController;
import iteMate.project.model.Item;
import iteMate.project.model.Track;
import iteMate.project.uiActivities.itemScreens.ItemsDetailActivity;
import iteMate.project.uiActivities.itemScreens.ItemsEditActivity;
import iteMate.project.uiActivities.trackScreens.TrackDetailActivity;
import iteMate.project.uiActivities.trackScreens.TrackEditActivity;
import iteMate.project.utils.NfcScanner;


/**
 * Activity for scanning NFC tags
 * It displays the scanned item and track.
 *
 * @Author: CHRIS
 */
public class ScanActivity extends AppCompatActivity implements NfcScanner.NfcScanListener {

    private NfcScanner nfcScanner;
    private ScanController scanController;
    private TrackController trackController;
    private ItemController itemController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // initialize NFC scanner
        nfcScanner = new NfcScanner(this, this);

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
                scanController.createNewItem(scanController.getNfcTagId());
                intent = new Intent(this, ItemsEditActivity.class);
            } else {
                // if item exists navigate to item detail screen to display item details
                intent = new Intent(this, ItemsDetailActivity.class);
            }
            startActivity(intent);
        });

        // Set on click listener for track card
        findViewById(R.id.track_card_view_scan).setOnClickListener(v -> {
            trackController.setCurrentTrack(trackController.getCurrentTrack());
            Intent intent = new Intent(this, TrackDetailActivity.class);
            startActivity(intent);
        });

        // Set on click listener for lend button
        findViewById(R.id.lend_button).setOnClickListener(v -> {
            scanController.lendItem();
            Intent intent = new Intent(this, TrackEditActivity.class);
            startActivity(intent);

        });

        // Set on click listener for return button
        findViewById(R.id.return_button).setOnClickListener(v -> {
            scanController.returnItem();
            Toast.makeText(this, "Item returned", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);
        });

        // on click listener for close button
        findViewById(R.id.close_nfcscan).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();  //TODO understand this and remove if not needed
        nfcScanner.enableReaderMode(this);

        // hiding all elements
        findViewById(R.id.item_card_view_scan).setVisibility(View.GONE);
        findViewById(R.id.track_card_view_scan).setVisibility(View.GONE);
        greyoutButtons();
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcScanner.disableReaderMode(this);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        // reset the current item and track
        itemController.resetCurrentItem();
        trackController.resetCurrentTrack();
        scanController.resetCurrentScan();

        // extract the tag ID
        String tagId = extractNfcTagId(tag);
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
        itemController.fetchItemByNfcTagId(tagId, this::handleItemFetched);
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
        trackController.fetchOneTrackFromDatabase(trackID, track -> {
            trackController.setCurrentTrack(track);
            updateTrackCardView(track);
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
        itemController.setImageForView(this, item.getImage(), itemCardView.findViewById(R.id.itemcard_image_scan));
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
     * Sets the visibility of the buttons to visible
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
     * Sets the visibility of the buttons to gone
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
        String daysLeft = String.valueOf(track.getDaysLeft()) + "d";
        if (track.getDaysLeft() > 0) {
            daysLeft = "+" + daysLeft;
        }
        cardSubContent.setText(daysLeft);
        TextView cardSubContent2 = trackCardView.findViewById(R.id.trackcard_scan_return_date);
        cardSubContent2.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(track.getReturnDate().toDate()));
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