package iteMate.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationBarView;

public abstract class MainActivity extends AppCompatActivity {

    protected int layoutResID;
    protected static int bottomNavID;

    abstract void setLayoutResID();
    abstract void setBottomNavID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutResID();
        setBottomNavID();
        setContentView(layoutResID);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the menu button
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

        // Bottom Navigation setup
        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigation);
        navigationBarView.setSelectedItemId(bottomNavID);

        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        if (bottomNavID == R.id.home) return true;
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.items:
                        if (bottomNavID == R.id.items) return true;
                        startActivity(new Intent(getApplicationContext(), ItemsActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.track:
                        if (bottomNavID == R.id.track) return true;
                        startActivity(new Intent(getApplicationContext(), TrackActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    private void showPopupMenu(View view) {
        // Create a PopupMenu
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_drawer, popupMenu.getMenu());

        // Set click listener for menu items
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_contacts:
                        // startActivity(new Intent(MainActivity.this, FirstActivity.class)); // Replace FirstActivity with the activity you want to start
                        return true;
                    case R.id.nav_settings:
                        // startActivity(new Intent(MainActivity.this, SecondActivity.class)); // Replace SecondActivity with the activity you want to start
                        return true;
                    case R.id.nav_logout:
                        // startActivity(new Intent(MainActivity.this, ThirdActivity.class)); // Replace ThirdActivity with the activity you want to start
                        return true;
                    default:
                        return false;
                }
            }
        });

        // Show the popup menu
        popupMenu.show();
    }
}