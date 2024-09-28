package iteMate.project.uiActivities;

import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
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
            // Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_SHORT).show();
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
            nfcAdapter.enableReaderMode(this,
                    this,  // This activity implements NfcAdapter.ReaderCallback
                    NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,  // Skip NDEF check if desired
                    null); // Optional Bundle with additional options
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
        updateTagIdTextView(tagId); // For testing purposes - delete later
        fetchItemByNfcTagId(tagId);
        handleTagType(tag);
    }

    /**
     * Update the tag ID TextView with the tag ID
     * for testing purposes - TODO delete later
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
     * Handle the tag based on its type
     * @param tag NFC tag
     */
    private void handleTagType(Tag tag) {
        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
            handleNdefTag(ndef);
        } else {
            MifareClassic mifareClassic = MifareClassic.get(tag);
            if (mifareClassic != null) {
                handleMifareClassicTag(mifareClassic);
            } else {
                Log.d("ScanActivity", "Tag type is *NOT* NDEF or MIFARE Classic.");
            }
        }
    }
    
    /**
     * Extract the tag ID from the tag
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

    /**
     * Handle MIFARE Classic tag using 3 default keys
     * @param  mifareClassic MIFARE Classic tag
     */
    private void handleMifareClassicTag(MifareClassic mifareClassic) {
        // Try to authenticate with the default keys
        try {
            mifareClassic.connect();
            boolean auth = false;
            byte[][] keys = {
                    MifareClassic.KEY_DEFAULT,
                    MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY,
                    MifareClassic.KEY_NFC_FORUM
            };

            // Try to authenticate with each key
            for (byte[] key : keys) {
                auth = mifareClassic.authenticateSectorWithKeyA(0, key);
                if (auth) {
                    break;
                }
            }

            // If authentication is successful, read the block
            if (auth) {
                // Read block 0 from sector 0
                byte[] data = mifareClassic.readBlock(0);
                String rfidData = new String(data, StandardCharsets.UTF_8);
                Log.d("ScanActivity", "MIFARE Classic Data: " + rfidData);
                // runOnUiThread(() -> Toast.makeText(this, "MIFARE Classic Data: " + rfidData, Toast.LENGTH_SHORT).show());
            } else {
                Log.d("ScanActivity", "MIFARE Classic authentication failed.");
                // runOnUiThread(() -> Toast.makeText(this, "MIFARE Classic authentication failed.", Toast.LENGTH_SHORT).show());
            }
        } catch (IOException e) {
            Log.e("ScanActivity", "Error reading MIFARE Classic tag", e);
        } finally {
            try {
                mifareClassic.close();
            } catch (IOException e) {
                Log.e("ScanActivity", "Error closing MIFARE Classic connection", e);
            }
        }
    }

    /**
     * Handle NDEF tag
     * @param ndef NDEF tag
     */
    private void handleNdefTag(Ndef ndef) {
        // Read the NDEF message
        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            if (ndefMessage != null) {
                // Iterate over all NDEF records
                for (NdefRecord ndefRecord : ndefMessage.getRecords()) {
                    // handles only text type
                    if (Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                        byte[] payload = ndefRecord.getPayload();
                        String text = new String(payload, StandardCharsets.UTF_8);
                        Log.d("ScanActivity", "NDEF Text: " + text);
                        // runOnUiThread(() -> Toast.makeText(this, "NDEF Text: " + text, Toast.LENGTH_SHORT).show());
                    }
                }
            } else {
                Log.d("ScanActivity", "NDEF message is null.");
            }
        } catch (IOException e) {
            Log.e("ScanActivity", "Error reading NDEF tag", e);
        } catch (FormatException e) {
            Log.d("ScanActivity", "Error reading NDEF tag", e);
        } finally {
            try {
                ndef.close();
            } catch (IOException e) {
                Log.e("ScanActivity", "Error closing NDEF connection", e);
            }
        }
    }
}
