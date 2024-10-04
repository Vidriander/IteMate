package iteMate.project.uiActivities;

import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import iteMate.project.R;
import iteMate.project.models.Item;
import iteMate.project.repositories.ItemRepository;

/**
 * Activity for scanning NFC tags
 */
public class ScanActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {

        private NfcAdapter nfcAdapter;
        private ItemRepository itemRepository;

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

            // Initialize ItemRepository
            itemRepository = new ItemRepository();

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
        }

        /**
         * Update the tag ID TextView with the tag ID
         * for testing purposes TODO: Remove this method
         * @param tagId Tag ID as a long
         */
        private void updateTagIdTextView(String tagId) {
            runOnUiThread(() -> {
                TextView tagIdTextView = findViewById(R.id.showNfcTagID);
                tagIdTextView.setText(tagId);
            });
        }

        /**
         * Fetch item by NFC tag ID
         * @param tagId Tag ID as a Hex String
         */
          private void fetchItemByNfcTagId(String tagId) {
               itemRepository.getItemByNfcTag(tagId, item -> {
                   if (item != null) {
                        Toast.makeText(this, "Item found: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        Log.d("ScanActivity", "Item found: " + item.getTitle());
                        updateItemCardView(item);
                   } else {
                       Log.d("ScanActivity", "Item not found");
                   }
               });
          }

        /**
         * Update the item card view with the item details
         * @param item Item object
         */
        private void updateItemCardView(Item item) {
            runOnUiThread(() -> {
                ScanItemFragment fragment = (ScanItemFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (fragment != null && fragment.getView() != null) {
                    CardView itemCardView = fragment.getView().findViewById(R.id.item_card);
                    if (itemCardView != null) {
                        itemCardView.setVisibility(View.VISIBLE);
                        TextView cardContent = itemCardView.findViewById(R.id.card_content);
                        cardContent.setText(item.getTitle()); // Update with item details
                    }
                }
            });
        }

        /**
         * Extract tag ID from the tag
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
    }

