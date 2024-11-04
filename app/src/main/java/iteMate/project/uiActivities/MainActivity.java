package iteMate.project.uiActivities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

import iteMate.project.R;
import iteMate.project.uiActivities.appScreens.HomeActivity;
import iteMate.project.uiActivities.appScreens.LoginActivity;
import iteMate.project.uiActivities.appScreens.SettingsActivity;
import iteMate.project.uiActivities.contactScreens.ContactActivity;
import iteMate.project.uiActivities.itemScreens.ItemsActivity;
import iteMate.project.uiActivities.scanScreen.ScanActivity;
import iteMate.project.uiActivities.trackScreens.TrackActivity;
import iteMate.project.utils.NfcScanner;

public abstract class MainActivity extends AppCompatActivity {

    protected int layoutResID;
    protected static int bottomNavID;

    public abstract void setLayoutResID();

    public abstract void setBottomNavID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutResID();
        setBottomNavID();
        setContentView(layoutResID);

        // disable landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        // Set up the menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(this::showPopupMenu);

        // Set up the scan button
        FloatingActionButton scanButton = findViewById(R.id.scan_button);
        if (scanButton != null) {
            scanButton.setOnClickListener(v -> {
                startActivity(new Intent(getApplicationContext(), ScanActivity.class));
                overridePendingTransition(0, 0);
            });
        }

        // Bottom Navigation setup
        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigation);
        navigationBarView.setSelectedItemId(bottomNavID);

        navigationBarView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                if (bottomNavID == R.id.home) return true;
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.items) {
                if (bottomNavID == R.id.items) return true;
                startActivity(new Intent(getApplicationContext(), ItemsActivity.class));
                finish();
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.track) {
                if (bottomNavID == R.id.track) return true;
                startActivity(new Intent(getApplicationContext(), TrackActivity.class));
                finish();
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void showPopupMenu(View view) {
        // Create a PopupMenu
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_drawer, popupMenu.getMenu());

        // Set click listener for menu items
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.nav_contacts) {
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_logout) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
            }
            return false;
        });

        // Show the popup menu
        popupMenu.show();
    }
}