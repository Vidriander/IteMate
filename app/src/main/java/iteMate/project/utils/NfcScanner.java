package iteMate.project.utils;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Log;

/**
 * Class for scanning NFC tags
 * Manages the NFC adapter and its lifecycle, ensuring it is only active in scan activities.
 */
public class NfcScanner implements NfcAdapter.ReaderCallback {

    private final NfcAdapter nfcAdapter;
    private final NfcScanListener listener;

    private boolean isReaderEnabled = false;

    /**
     * Constructor for NfcScanner
     *
     * @param activity the activity in which the scanner is used
     * @param listener the listener for the scanner
     */
    public NfcScanner(Activity activity, NfcScanListener listener) {
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        this.listener = listener;
    }

    /**
     * Enables the NFC reader mode
     *
     * @param activity the activity in which the scanner is used
     */
    public void enableReaderMode(Activity activity) {
        if (nfcAdapter != null) {
            nfcAdapter.enableReaderMode(activity, this,
                    NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F | NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                    null);
            isReaderEnabled = true;
            Log.d("NfcScanner", "NFC Reader Mode enabled.");
        }
    }

    /**
     * Disables the NFC reader mode
     *
     * @param activity the activity in which the scanner is used
     */
    public void disableReaderMode(Activity activity) {
        if (nfcAdapter != null && isReaderEnabled) {
            nfcAdapter.disableReaderMode(activity);
            isReaderEnabled = false;
            Log.d("NfcScanner", "NFC Reader Mode disabled.");
        }
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        if (listener != null) {
            listener.onTagDiscovered(tag);
        }
    }

    /**
     * Interface for the NFC scan listener
     */
    public interface NfcScanListener {
        void onTagDiscovered(Tag tag);
    }
}
