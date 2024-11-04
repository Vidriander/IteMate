package iteMate.project.uiActivities.appScreens;

import android.nfc.NfcAdapter;
import android.os.Bundle;

import iteMate.project.R;
import iteMate.project.controller.ItemController;
import iteMate.project.controller.TrackController;
import iteMate.project.uiActivities.MainActivity;

/**
 * HomeActivity class is the main screen of the application.
 */
public class HomeActivity extends MainActivity {

    private final ItemController itemController = ItemController.getControllerInstance();
    private final TrackController trackController = TrackController.getControllerInstance();
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcAdapter.disableReaderMode(this);

        //nfcAdapter.disableReaderMode(this);
        // content of main-screen follows here
    }

    @Override
    public void setLayoutResID() {
        layoutResID = R.layout.activity_main;
    }

    @Override
    public void setBottomNavID() {
        bottomNavID = R.id.home;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
