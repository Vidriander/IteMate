package iteMate.project.uiActivities;

import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import iteMate.project.R;
import iteMate.project.repositories.ItemRepository;

/**
 * Activity for scanning NFC tags
 * This activity is used to scan NFC tags in NDEF or MIFARE Classic format.
 * It uses Reader Mode API to handle NFC tags.
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

            long tagId = extractTagId(tag);
            updateTagIdTextView(tagId); // Display tag ID for testing
            fetchItemByNfcTagId(tagId); // Fetch item by tag ID
        }

        /**
         * Update the tag ID TextView with the tag ID
         * for testing purposes
         * @param tagId Tag ID as a long
         */
        private void updateTagIdTextView(long tagId) {
            runOnUiThread(() -> {
                TextView tagIdTextView = findViewById(R.id.showNfcTagID);
                tagIdTextView.setText(String.valueOf(tagId));
            });
        }

        /**
         * Fetch item by NFC tag ID
         * @param tagId Tag ID as a long
         */
        private void fetchItemByNfcTagId(long tagId) {
            itemRepository.getItemByNfcTag((int) tagId, item -> {
                if (item != null) {
                    Log.d("ScanActivity", "Item found: " + item.getTitle());
                } else {
                    Log.d("ScanActivity", "Item not found");
                }
            });
        }

        /**
         * Extract the tag ID from the tag and return it as a long
         * @param tag NFC tag
         * @return Tag ID as a long
         */
        private long extractTagId(Tag tag) {
            byte[] tagId = tag.getId();
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            if (tagId.length < Long.BYTES) {
                // Pad with leading zeros if the tag ID is shorter than 8 bytes
                byte[] paddedTagId = new byte[Long.BYTES];
                System.arraycopy(tagId, 0, paddedTagId, Long.BYTES - tagId.length, tagId.length);
                buffer.put(paddedTagId);
            } else {
                // Truncate to the first 8 bytes if the tag ID is longer than 8 bytes
                buffer.put(tagId, 0, Long.BYTES);
            }
            buffer.flip();
            return buffer.getLong();
        }
    }

