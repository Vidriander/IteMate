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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import iteMate.project.R;

/**
 * Activity for scanning NFC tags
 * This activity is used to scan NFC tags in NDEF or MIFARE Classic format.
 * It uses Reader Mode API to handle NFC tags.
 */
public class ScanActivity extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
        // This method will be called whenever a tag is discovered
        Log.d("ScanActivity", "NFC Tag discovered!");

        // For testing
        // Extract the tag ID and update the TextView
        String tagIdString = extractTagId(tag);
        runOnUiThread(() -> {
            TextView tagIdTextView = findViewById(R.id.showNfcTagID);
            tagIdTextView.setText(tagIdString);
        });

        // Check if the tag is NDEF format
        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
            handleNdefTag(ndef);
        } else {
            // Check if the tag is MIFARE Classic and read it
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
     * @return Tag ID as a HEX coded string
     */
    private String extractTagId(Tag tag) {
        // Get the tag ID
        byte[] tagId = tag.getId();
        return bytesToHex(tagId);
    }

    /**
     * Converts a byte array to a HEX coded string
     * @param bytes Byte array
     * @return HEX coded string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
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
                // Show the data in a Toast
                runOnUiThread(() -> Toast.makeText(this, "MIFARE Classic Data: " + rfidData, Toast.LENGTH_SHORT).show());
            } else {
                Log.d("ScanActivity", "MIFARE Classic authentication failed.");
                runOnUiThread(() -> Toast.makeText(this, "MIFARE Classic authentication failed.", Toast.LENGTH_SHORT).show());
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
                        runOnUiThread(() -> Toast.makeText(this, "NDEF Text: " + text, Toast.LENGTH_SHORT).show());
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
